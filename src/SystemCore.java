public class SystemCore extends Thread {
    protected final int coreIndex;
    protected int coreState;

    public static final int CORE_STATE_IDLE = 0;
    public static final int CORE_STATE_RUNNING = 1;
    public static final int CORE_STATE_STALLED = 2;
    public static final int CORE_STATE_TASK_FINISHED = 3;
    public static final int CORE_STATE_STOPPED = 4;

    public SystemCore(int coreIndex, int ownerIndex) {
        this.coreIndex = coreIndex;
        coreState = CORE_STATE_IDLE;
        setName("Core " + (coreIndex + 1) + "-Subsystem " + (ownerIndex+1));
    }

    public int getCoreState() {
        return coreState;
    }

    public void setFinished() {
        coreState = CORE_STATE_STOPPED;
    }

    public String getStateAsString() {
        return switch (coreState) {
            case CORE_STATE_IDLE -> "IDLE";
            case CORE_STATE_RUNNING -> "RUNNING";
            case CORE_STATE_STALLED -> "STALLED";
            case CORE_STATE_TASK_FINISHED -> "TASK_FINISHED";
            case CORE_STATE_STOPPED -> "STOPPED";
            default -> "UNKNOWN";
        };
    }
}
