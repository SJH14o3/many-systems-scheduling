public class System1Core extends Thread{
    private final SubSystem1 owner;
    private final int coreIndex;
    private int coreState;
    private ProcessSubSystem1 currentTask;
    public SubSystem1ReadyQueue readyQueue;
    private int remainingQuantum;

    public static final int CORE_STATE_IDLE = 0;
    public static final int CORE_STATE_RUNNING = 1;
    public static final int CORE_STATE_STALLED = 2;
    public static final int CORE_STATE_TASK_FINISHED = 3;
    public static final int CORE_STATE_STOPPED = 4;

    @Override
    public void run() {
        while(true) {
            if (coreState == CORE_STATE_IDLE) {
                break;
            }
            /* phase 1: assign a process from queues
             at first, we try to assign a process from waiting queue from this core processes to prevent starvation
             if we couldn't then we try to assign a process from ready queue
             if we couldn't again if they were empty, we will try to poll from another cores ready queues
             if they were not empty then core will be idle.
            */
            boolean flag = false;
            ProcessSubSystem1 task;
        }
    }

    public System1Core(SubSystem1 owner, int coreIndex) {
        this.owner = owner;
        this.coreIndex = coreIndex;
    }
}
