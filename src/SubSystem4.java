import java.util.*;

public class SubSystem4 extends SubSystem {

    public final SubSystem4ReadyQueue readyQueue;
    public final WaitingQueueSub4 waitingQueue;

    private final System4Core[] cores;
    private final HashSet<String> finishedTasks; // we will store finished task names here

    public SubSystem4(int intR1Remain, int intR2Remain, Process[] processes, int sys4CoresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processes, sys4CoresCount, doNotSendReport,3);
        readyQueue = new SubSystem4ReadyQueue(this);
        waitingQueue = new WaitingQueueSub4(this);
        cores = new System4Core[sys4CoresCount];
        finishedTasks = new HashSet<>();
        for (int i = 0; i < sys4CoresCount; i++) {
            cores[i] = new System4Core(this,i);
        }
    }

    public boolean isPrerequisiteDone(ProcessSubSystem4 process) {
        if (process.getPrerequisite().equals("-")) return true;
        return finishedTasks.contains(process.getPrerequisite());
    }

    @Override
    protected void checkForNewProcesses() {
        ArrayList<ProcessSubSystem4> newProcesses = new ArrayList<>();
        for (Process c : notArrivedProcesses){
            if (c.startTime == owner.time ){
                newProcesses.add((ProcessSubSystem4) c);
            }else break;
        }
        for (ProcessSubSystem4 newProcess : newProcesses){
            notArrivedProcesses.remove(newProcess);
            if (isPrerequisiteDone(newProcess)) {
                readyQueue.addProcess(newProcess);
            } else {
                waitingQueue.addLast(newProcess);
            }
        }
    }

    private String readyQueueString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ready Queue: [");
        PriorityQueue<ProcessSubSystem4> temp = readyQueue.cloneQueue();
        boolean first = true;
        while (!temp.isEmpty()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            ProcessSubSystem4 process = temp.poll();
            sb.append(process.getName());
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    protected void reportToMainSystem() {
        owner.message[systemIndex].setLength(0);
        if (dontSendReport){
            dummyReport();
            return;
        }
        owner.message[systemIndex].append("Sub4\n")
                .append("   Resources: R1 ").append(R1Remain)
                .append(", R2: ").append(R2Remain).append("\n   Waiting Queue:")
                .append(getQueueContent(waitingQueue.getWaitingList())).append("\n Ready Queue")
                .append(readyQueueString());
        for (StringBuilder sb: message){
            owner.message[systemIndex].append(sb.toString()).append("\n");
        }
    }

    public String getQueueContent(Queue<ProcessSubSystem4> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (ProcessSubSystem4 processSubSystem1 : queue) {
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
        //TODO: implement
    }

    @Override
    protected boolean isSystemFinished() {
        boolean coresFinished = true;
        for (System4Core core : cores){
            if (core.getCoreState() != SystemCore.CORE_STATE_IDLE){
                coresFinished = false;
                break;
            }
        }
        return (notArrivedProcesses.isEmpty() && waitingQueue.isEmpty() && readyQueue.isEmpty() && coresFinished);
    }

    @Override
    public void run() { //TODO: move this function after implementation to runLoop, like other systems
        runLoop(cores);
    }
}
