import java.util.ArrayList;
import java.util.PriorityQueue;

public final class SubSystem2 extends SubSystem {
    // this array list will store processes which have not arrived yet.
    private final SubSystem2ReadyQueue readyQueue;
    private int taskState;
    private final System2Core[] cores;

    public static final int TASK_STATE_NEW_ARRIVED = 1;
    public static final int TASK_STATE_NORMAL = 2;

    public int getTaskState() {
        return taskState;
    }

    public SubSystem2ReadyQueue getReadyQueue() {
        return readyQueue;
    }

    public SubSystem2(int intR1Remain, int intR2Remain, ProcessSubSystem2[] processes, int coresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processes, coresCount, doNotSendReport);
        systemIndex = 1;
        cores = new System2Core[CORE_COUNT];
        readyQueue = new SubSystem2ReadyQueue(this);
        for (int i = 0; i < CORE_COUNT; i++) {
            cores[i] = new System2Core(this, i);
        }
    }

    @Override
    protected void checkForNewProcesses() {
        taskState = TASK_STATE_NORMAL;
        ArrayList<Process> newProcesses = new ArrayList<>();
        for (Process notArrivedProcess : notArrivedProcesses) {
            if (notArrivedProcess.startTime == owner.time) {
                newProcesses.add(notArrivedProcess);
                taskState = TASK_STATE_NEW_ARRIVED;
            } else break;
        }
        for (Process newProcess : newProcesses) {
            notArrivedProcesses.remove(newProcess);
            readyQueue.addProcess((ProcessSubSystem2) newProcess);
        }
    }

    private String readyQueueString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ready Queue: [");
        PriorityQueue<ProcessSubSystem2> temp = readyQueue.cloneQueue();
        boolean first = true;
        while (!temp.isEmpty()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            ProcessSubSystem2 process = temp.poll();
            sb.append(process.getName());
        }
        sb.append("]\n");
        return sb.toString();
    }

    protected boolean isSystemFinished() {
        boolean coresFinished = true;
        for (System2Core core : cores) {
            if (core.getCoreState() != System2Core.CORE_STATE_IDLE) {
                coresFinished = false;
                break;
            }
        }
        return (notArrivedProcesses.isEmpty() && readyQueue.isEmpty() && coresFinished);
    }

    @Override
    protected void reportToMainSystem() {
        owner.message[systemIndex].setLength(0);
        if (dontSendReport) {
            dummyReport();
            return;
        }
        owner.message[systemIndex].append("Sub2:\n")
                .append("    Resources: R1: ").append(R1Remain)
                .append(", R2: ").append(R2Remain).append("\n    ")
                .append(readyQueueString());
        for (int i = 0; i < CORE_COUNT; i++) {
            owner.message[systemIndex].append(message[i].toString()).append("\n");
        }
    }

    @Override
    protected void runATimeUnitBody() throws InterruptedException {
        // phase 2: now cores will assign tasks and send their report, wait for cores finish allocating and reporting
        letCoresRunOnePhase();
        // prepare message for main system
        reportToMainSystem();
        // now let the cores actually run
        letCoresRunOnePhase();
    }

    @Override
    public void run() {
        runLoop(cores);
    }
}
