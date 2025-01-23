import Exceptions.IdleSystemException;

public class System1Core extends SystemCore{
    private final SubSystem1 owner;
    private ProcessSubSystem1 currentTask;
    public SubSystem1ReadyQueue readyQueue;
    private int remainingQuantum;

    private void assignTask() {
        ProcessSubSystem1 task = owner.waitingQueue.getWaitingProcess(coreIndex+1, true);
        if (task == null) { // waiting queue could not return a suitable process
            task = readyQueue.getAndAllocate();
        }
        if (task == null) { // ready queue is empty ( if it wasn't and no task could be allocated then it will be empty)
            task = owner.pullProcess(coreIndex);
        }
        if (task == null) { // was not able to pull a task, this is the last resort to get any process from waiting queue
            task = owner.waitingQueue.getWaitingProcess(coreIndex+1, false);
        }
        currentTask = task;
    }

    private void runForATimeUnit() {
        remainingQuantum--;
        boolean taskIsFinished = currentTask.runForATimeUnit();
        boolean bool = remainingQuantum == 0 || taskIsFinished;
        if (bool) {
            if (!taskIsFinished) {
                owner.addBackToCoreReadyQueue(currentTask);
            }
            owner.deallocate(currentTask);
            currentTask = null;
            coreState = CORE_STATE_IDLE;
            //System.out.println("core" + (coreIndex+1) + " switching context");
        }
    }

    private void reportToOwner() {
        owner.message[coreIndex].setLength(0);
        String taskName;
        if (currentTask != null) {
            taskName = currentTask.getName();
        }
        else {
            taskName = "IDLE";
        }
        owner.message[coreIndex].append("    Core").append(coreIndex + 1).append(":\n")
                .append("       Running Task:").append(taskName).append("\n")
                .append("       Ready Queue: ").append(owner.getQueueContent(readyQueue.queue));
    }

    @Override
    public void run() {
        try {
            while (true) {
                owner.coreThreadWait[coreIndex].acquire();
                if (coreState == CORE_STATE_STOPPED) {
                    break;
                }
                try {
                 /* phase 1: assign a process from queues if currently none is assigned
                 at first, we try to assign a process from waiting queue from this core processes to prevent starvation
                 if we couldn't then we try to assign a process from ready queue.
                 if we couldn't again if they were empty, we will try to poll from another cores ready queues.
                 last resort is to get any process from waiting queue if possible.
                 if nothing could be assigned, then unfortunately core will be idle.
                */
                    if (coreState == CORE_STATE_IDLE) {
                        assignTask();
                        if (currentTask != null) {
                            remainingQuantum = currentTask.getQuantum();
                            coreState = CORE_STATE_RUNNING;
                        }
                    }
                    if (coreState == CORE_STATE_IDLE) { // could not assign new task
                        throw new IdleSystemException();
                    }
                    owner.subSystemWait[coreIndex].release();
                    // phase two: report back to system, since all queues content can be changed from all cores
                    owner.coreThreadWait[coreIndex].acquire();
                    reportToOwner();
                    owner.subSystemWait[coreIndex].release();
                    // phase three: now actually run
                    owner.coreThreadWait[coreIndex].acquire();
                    runForATimeUnit();
                    owner.subSystemWait[coreIndex].release();
                } catch (IdleSystemException e) {
                    owner.subSystemWait[coreIndex].release();
                    owner.coreThreadWait[coreIndex].acquire();
                    reportToOwner();
                    owner.subSystemWait[coreIndex].release();
                    owner.coreThreadWait[coreIndex].acquire();
                    owner.subSystemWait[coreIndex].release();
                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public System1Core(SubSystem1 owner, int coreIndex) {
        super(coreIndex, owner.systemIndex);
        this.owner = owner;
        readyQueue = new SubSystem1ReadyQueue(this.owner, this);
    }
}
