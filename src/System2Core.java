import Exceptions.EmptyQueueException;
import Exceptions.NotEnoughResourcesException;

public class System2Core extends SystemCore {
    private final SubSystem2 owner;
    private ProcessSubSystem2 currentTask;
    private int allocationState = ALLOCATION_STATE_NOT_ALLOCATED;

    public static final int ALLOCATION_STATE_ALLOCATED = 1;
    public static final int ALLOCATION_STATE_NOT_ALLOCATED = 2;

    public System2Core(SubSystem2 owner, int coreIndex) {
        super(coreIndex);
        this.owner = owner;
        coreState = CORE_STATE_IDLE;
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    // phase one: check if subsystem is finished, if not start making a report of this time unit
                    owner.coreThreadWait[coreIndex].acquire();
                    if (coreState == CORE_STATE_STOPPED) {
                        break;
                    }

                    owner.message[coreIndex].setLength(0);
                    owner.message[coreIndex].append("    Core").append((coreIndex+1)).append(":\n")
                            .append("       Running Task: ");
                    boolean flag = true; // this is how I fixed the bug...
                    if (coreState == CORE_STATE_IDLE) {
                        currentTask = owner.getReadyQueue().getProcess();
                        owner.allocate(currentTask);
                        allocationState = ALLOCATION_STATE_ALLOCATED;
                        coreState = CORE_STATE_RUNNING;
                        flag = false;
                    } else if (owner.getTaskState() == SubSystem2.TASK_STATE_NEW_ARRIVED && (coreState == CORE_STATE_RUNNING || coreState == CORE_STATE_STALLED)) {
                        // if a new task has arrived, core will check if it has a higher priority
                        // if new task is better, temp won't be null
                        ProcessSubSystem2 temp = owner.getReadyQueue().checkIfNewProcessHasHigherPriority(currentTask);
                        if (temp != null) {
                            if (allocationState == ALLOCATION_STATE_ALLOCATED) {
                                owner.deallocate(currentTask);
                            }
                            currentTask = temp;
                            owner.allocate(currentTask); // might throw an exception if not enough resources are available
                            allocationState = ALLOCATION_STATE_ALLOCATED;
                            coreState = CORE_STATE_RUNNING;
                            flag = false;
                        }
                    }
                    if (flag && coreState == CORE_STATE_STALLED) { // checking if task can be allocated again, or it will remain stalled
                        if (allocationState == ALLOCATION_STATE_NOT_ALLOCATED) {
                            owner.allocate(currentTask);
                        }
                        coreState = CORE_STATE_RUNNING;
                    }
                    owner.message[coreIndex].append(currentTask.getName());
                    owner.subSystemWait[coreIndex].release(); // phase one finished

                    // phase two : now actually run
                    owner.coreThreadWait[coreIndex].acquire();
                    // simulate running and checking if task is finished
                    if (currentTask.runForATimeUnit()) {
                        coreState = CORE_STATE_IDLE;
                        owner.deallocate(currentTask);
                        allocationState = ALLOCATION_STATE_NOT_ALLOCATED;
                        currentTask = null;
                    }
                } catch (NotEnoughResourcesException e) {
                    owner.subSystemWait[coreIndex].release();
                    coreState = CORE_STATE_STALLED;
                    allocationState = ALLOCATION_STATE_NOT_ALLOCATED;
                    owner.message[coreIndex].append("STALLED-").append(currentTask.getName());
                    owner.coreThreadWait[coreIndex].acquire();
                } catch (EmptyQueueException e) {
                    owner.subSystemWait[coreIndex].release();
                    coreState = CORE_STATE_IDLE;
                    allocationState = ALLOCATION_STATE_NOT_ALLOCATED;
                    owner.message[coreIndex].append("IDLE");
                    owner.coreThreadWait[coreIndex].acquire();
                }
                owner.subSystemWait[coreIndex].release(); // phase two finished
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
