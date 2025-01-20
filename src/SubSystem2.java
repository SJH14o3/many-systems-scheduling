import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;

public final class SubSystem2 extends SubSystem {
    private ProcessSubSystem2[] processes;
    // this array list will store processes which have not arrived yet.
    private final SubSystem2ReadyQueue readyQueue;
    private int taskState;
    private final System2Core[] cores;

    public static final int TASK_STATE_NEW_ARRIVED = 1;
    public static final int TASK_STATE_NORMAL = 2;

    public ProcessSubSystem2[] getProcesses() {
        return processes;
    }

    public ProcessSubSystem2 getProcessWithIndex(int i) {
        return processes[i];
    }

    public void setProcesses(ProcessSubSystem2[] processes) {
        this.processes = processes;
    }

    public int getTaskState() {
        return taskState;
    }

    public SubSystem2ReadyQueue getReadyQueue() {
        return readyQueue;
    }

    public SubSystem2(int intR1Remain, int intR2Remain, ProcessSubSystem2[] processes, int coresCount) {
        super(intR1Remain, intR2Remain, processes, coresCount);
        systemIndex = 1;
        this.processes = processes;
        cores = new System2Core[CORE_COUNT];
        readyQueue = new SubSystem2ReadyQueue(this);
        for (int i = 0; i < CORE_COUNT; i++) {
            cores[i] = new System2Core(this, i);
        }
    }

    @Override
    protected void checkForNewProcesses() {
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

    private boolean isSystemFinished() {
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
    public void run() {
        try {
            for (System2Core core : cores) {
                core.start();
            }
            while (true) {
                // phase 1: preparing for running
                taskState = TASK_STATE_NORMAL;
                checkForNewProcesses();

                // phase 2: now cores will assign tasks and send their report
                for (int i = 0; i < CORE_COUNT; i++) {
                    coreThreadWait[i].release();
                }
                // wait for cores finish allocating and reporting
                for (int i = 0; i < CORE_COUNT; i++) {
                    subSystemWait[i].acquire();
                }
                // prepare message for main system
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub2:\n")
                        .append("    Resources: R1: ").append(R1Remain)
                        .append(", R2: ").append(R2Remain).append("\n    ")
                        .append(readyQueueString());
                for (int i = 0; i < CORE_COUNT; i++) {
                    owner.message[systemIndex].append(message[i].toString()).append("\n");
                }
                // now let the cores actually run
                for (int i = 0; i < CORE_COUNT; i++) {
                    coreThreadWait[i].release();
                }
                // wait for cores finish running
                for (int i = 0; i < CORE_COUNT; i++) {
                    subSystemWait[i].acquire();
                }

                // phase 3: subsystem checks if is finished
                if (isSystemFinished()) {
                    systemState = STATE_FINISHED;
                    owner.mainThreadWait[systemIndex].release();
                    break;
                }
                // subsystem will let main know their time unit is finished
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            // system task is finished here
            for (int i = 0; i < CORE_COUNT; i++) {
                cores[i].setFinished();
                coreThreadWait[i].release();
            }
            while(true) {
                owner.subSystemWait[systemIndex].acquire();
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub2 is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
