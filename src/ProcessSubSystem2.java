public final class ProcessSubSystem2 extends Process {
    private int remainingTime;

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public ProcessSubSystem2(String name, int burstTime, int maxR1, int maxR2, int startTime) {
        super(name, burstTime, maxR1, maxR2, startTime);
        remainingTime = burstTime;
    }

    public static ProcessSubSystem2 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem2(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
    }
}
