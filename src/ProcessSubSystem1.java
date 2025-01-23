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

    public boolean runForATimeUnit(int time, int coreNum) {
        burstTime--;
        addRunningStartStamp(time, coreNum);
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

    public void addRunningStartStamp(int time, int coreNumber) {
        runningReport.append(",").append(time).append(":").append(coreNumber);
    }

    @Override
    public void addWaitingStartStamp(int time) {
        waitingReport.append(",").append(time);
    }

    @Override
    public void addWaitingEndStamp(int time) {

    }

    @Override
    public String getFinalReport() {
        StringBuilder finalReport = new StringBuilder(reportStartingDetails());
        finalReport.append(", ran in ");
        finalReport.append(consecutiveDecoder(runningReport));
        if (waitingReport.isEmpty()) {
            finalReport.append(", no time in waiting queue");
        }
        else {
            finalReport.append(", total time in waiting queue: ").append(timeInWaitingQueue);
            finalReport.append(", appeared in waiting in ");
            finalReport.append(consecutiveDecoderNoCore(waitingReport));
        }
        return finalReport.toString();
    }
}
