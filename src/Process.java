public abstract class Process {
    protected String name;
    protected int burstTime;
    protected int maxR1;
    protected int maxR2;
    protected int startTime;
    protected int state;


    protected int waitingQueuePriority;


    public static int STATE_READY = 0;
    public static int STATE_RUNNING = 1;
    public static int STATE_WAITING = 2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getMaxR1() {
        return maxR1;
    }

    public void setMaxR1(int maxR1) {
        this.maxR1 = maxR1;
    }

    public int getMaxR2() {
        return maxR2;
    }

    public void setMaxR2(int maxR2) {
        this.maxR2 = maxR2;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getWaitingQueuePriority() {
        return waitingQueuePriority;
    }

    public void setWaitingQueuePriority(int waitingQueuePriority) {
        this.waitingQueuePriority = waitingQueuePriority;
    }

    public Process(String name, int burstTime, int maxR1, int maxR2, int startTime) {
        this.name = name;
        this.burstTime = burstTime;
        this.maxR1 = maxR1;
        this.maxR2 = maxR2;
        this.startTime = startTime;
    }

    public void increaseAge(){
        waitingQueuePriority++;
    }

    public static Process createProcessWithString(String line) {
        return null;
    }
}
