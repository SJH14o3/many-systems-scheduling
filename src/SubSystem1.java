import java.util.ArrayList;

public final class SubSystem1 extends SubSystem {

    public ProcessSubSystem1[] processSubSystem1;
    public WaitingQueueSub1 waitingQueue;
    public System1Core[] cores;

    private int taskState;

    public static final int TASK_STATE_NEW_ARRIVED = 1;
    public static final int TASK_STATE_NORMAL = 2;

    private void setQuantums(){
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < processSubSystem1.length; i++) {
            if (processSubSystem1[i].burstTime > max){
                max = processSubSystem1[i].burstTime;
            }
            if (processSubSystem1[i].burstTime < min){
                min = processSubSystem1[i].burstTime;
            }
        }
        int maxQ=5, minQ=1;
        for (int i = 0; i < processSubSystem1.length; i++) {
            if (max == min) {
                processSubSystem1[i].setQuantum(maxQ); // If all burst times are equal, assign max quantum
            } else {
                processSubSystem1[i].setQuantum((int) Math.round(minQ + ((double)(processSubSystem1[i].burstTime - min) / (max - min)) * (maxQ - minQ)));
            }
        }
    }

    // for load Balancing
    public ProcessSubSystem1 pullProcess(int index) {
        ProcessSubSystem1 out = null;
        for (int i = (index+1) % cores.length; i != index; i = (i+1) % cores.length) {
            out = cores[i].readyQueue.getAndAllocate();
            if (out != null) {
                break;
            }
        }
        return out;
    }

    public SubSystem1(int intR1Remain, int intR2Remain, ProcessSubSystem1[] processSubSystem1, int sys1CoresCount) {
        super(intR1Remain, intR2Remain, processSubSystem1, sys1CoresCount);
        this.processSubSystem1 = processSubSystem1;
        systemIndex = 0;
        setQuantums();
        printQs();
        waitingQueue = new WaitingQueueSub1(this);
    }

    public void printQs(){
        for (int i = 0; i < processSubSystem1.length; i++) {
            System.out.println(processSubSystem1[i].getName() + ":" + processSubSystem1[i].getQuantum());
        }
    }


    @Override
    protected void checkForNewProcesses() {
        ArrayList<ProcessSubSystem1> newProcesses = new ArrayList<>();
        for (Process notArrivedProsess : notArrivedProcesses){
            if (notArrivedProsess.startTime == owner.time) {
                newProcesses.add((ProcessSubSystem1) notArrivedProsess);
                taskState = TASK_STATE_NEW_ARRIVED;
            } else break;
        }
        for (ProcessSubSystem1 newProcess : newProcesses){
            notArrivedProcesses.remove(newProcess);
            cores[newProcess.getTargetCPU()-1].readyQueue.addOne(newProcess);
        }
    }

    private boolean isSystemFinished(){
        boolean coresFinished = true;
        boolean isEmptyReadyQueues = true;
        for (System1Core core : cores){
            if (core.getCoreState() != System1Core.CORE_STATE_IDLE){
                coresFinished = false;
                break;
            }
        }
        for (System1Core core : cores){
            if (!core.readyQueue.isEmpty()){
                isEmptyReadyQueues = false ;
                break;
            }
        }
        return (notArrivedProcesses.isEmpty() && waitingQueue.isEmpty() && isEmptyReadyQueues && coresFinished);
    }

    @Override
    public void run() {
        try {
            for (System1Core core : cores) {
                core.start();
            }
            while (true) {
                //TODO: remove with actual job
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub1:");
                if (owner.time == 2) { //TODO: replace with actual finish statement
                    systemState = STATE_FINISHED;
                    owner.mainThreadWait[systemIndex].release();
                    break;
                }
                ;
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            //TODO: task is finished here
            while (true) {
                owner.subSystemWait[systemIndex].acquire();
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub1 is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }




}
