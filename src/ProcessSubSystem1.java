public final class ProcessSubSystem1 extends Process {
    private final int targetCPU;

    private int quantum;
    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public int getTargetCPU() {
        return targetCPU;
    }

    public boolean runForATimeUnit() {
        burstTime--;
        return burstTime == 0;
    }

    public ProcessSubSystem1(String name, int burstTime, int maxR1, int maxR2, int startTime, int targetCPU) {
        super(name, burstTime, maxR1, maxR2, startTime);
        this.targetCPU = targetCPU;
    }

    public static ProcessSubSystem1 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem1(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
    }

    @Override
    public void addRunningStartStamp(int time) {

    }

    @Override
    public void addRunningEndStamp(int time, int coreNumber) {

    }

    @Override
    public void addWaitingStartStamp(int time) {

    }

    @Override
    public void addWaitingEndStamp(int time) {

    }

    @Override
    public String getFinalReport() {
        return name;
    }
}
