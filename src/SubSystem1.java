import java.util.ArrayList;
import java.util.Queue;

public final class SubSystem1 extends SubSystem {

    public ProcessSubSystem1[] processSubSystem1;
    public WaitingQueueSub1 waitingQueue;
    public System1Core[] cores;

    private void setQuantums(){
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (ProcessSubSystem1 subSystem1 : processSubSystem1) {
            if (subSystem1.burstTime > max) {
                max = subSystem1.burstTime;
            }
            if (subSystem1.burstTime < min) {
                min = subSystem1.burstTime;
            }
        }
        int maxQ=5, minQ=1;
        for (ProcessSubSystem1 subSystem1 : processSubSystem1) {
            if (max == min) {
                subSystem1.setQuantum(maxQ); // If all burst times are equal, assign max quantum
            } else {
                subSystem1.setQuantum((int) Math.round(minQ + ((double) (subSystem1.burstTime - min) / (max - min)) * (maxQ - minQ)));
            }
        }
    }

    // for load Balancing
    public ProcessSubSystem1 pullProcess(int index) {
        ProcessSubSystem1 out = null;
        for (int i = (index+1) % cores.length; i != index; i = (i+1) % cores.length) {
            out = cores[i].readyQueue.tryPulling();
            if (out != null) {
                break;
            }
        }
        return out;
    }

    // if a process has pulled a task, if it's quantum is over it should go back to its original core ready queue
    public void addBackToCoreReadyQueue(ProcessSubSystem1 process) {
        cores[process.getTargetCPU()-1].readyQueue.addOne(process);
    }

    public SubSystem1(int intR1Remain, int intR2Remain, ProcessSubSystem1[] processSubSystem1, int sys1CoresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processSubSystem1, sys1CoresCount, doNotSendReport);
        this.processSubSystem1 = processSubSystem1;
        systemIndex = 0;
        setQuantums();
        //printQs();
        waitingQueue = new WaitingQueueSub1(this);
        cores = new System1Core[sys1CoresCount];
        for (int i = 0; i < sys1CoresCount; i++) {
            cores[i] = new System1Core(this, i);
        }
    }

    public void printQs(){
        for (ProcessSubSystem1 subSystem1 : processSubSystem1) {
            System.out.println(subSystem1.getName() + ":q=" + subSystem1.getQuantum() +",t=" + subSystem1.getBurstTime());
        }
    }


    @Override
    protected void checkForNewProcesses() {
        ArrayList<ProcessSubSystem1> newProcesses = new ArrayList<>();
        for (Process c : notArrivedProcesses){
            if (c.startTime == owner.time) {
                newProcesses.add((ProcessSubSystem1) c);
            } else break;
        }
        for (ProcessSubSystem1 newProcess : newProcesses){
            notArrivedProcesses.remove(newProcess);
            cores[newProcess.getTargetCPU()-1].readyQueue.addOne(newProcess);
        }
    }

    protected boolean isSystemFinished(){
        boolean coresFinished = true;
        boolean isEmptyReadyQueues = true;
        for (System1Core core : cores){
            if (core.getCoreState() != SystemCore.CORE_STATE_IDLE){
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
    protected void reportToMainSystem() {
        owner.message[systemIndex].setLength(0);
        if (dontSendReport) {
            dummyReport();
            return;
        }
        owner.message[systemIndex].append("Sub1:\n")
                .append("    Resources: R1: ").append(R1Remain)
                .append(", R2: ").append(R2Remain).append("\n    Waiting Queue:")
                .append(getQueueContent(waitingQueue.waitingList));
        for (StringBuilder sb: message) {
            owner.message[systemIndex].append(sb.toString());
        }
    }

    public String getQueueContent(Queue<ProcessSubSystem1> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (ProcessSubSystem1 processSubSystem1 : queue) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }
            sb.append(processSubSystem1.getName());
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    protected void runATimeUnitBody() throws InterruptedException {
        // phase 2 set cores tasks and wait for cores assigning tasks
        letCoresRunOnePhase();
        // phase 3 get report from cores
        letCoresRunOnePhase();
        reportToMainSystem();
        // phase 4 now cores actually run
        letCoresRunOnePhase();
    }

    @Override
    public void run() {
        runLoop(cores);
    }
}
