public final class ProcessSubSystem2 extends Process {
    private int remainingTime;

    public int getRemainingTime() {
        return remainingTime;
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

    public boolean runForATimeUnit(int time, int coreNum) {
        remainingTime--;
        addRunningStartStamp(time, coreNum);
        return remainingTime == 0;
    }

    @Override
    public void addRunningStartStamp(int time) {

    }

    public void addRunningStartStamp(int time, int coreNumber) {
        runningReport.append(",").append(time).append(":").append(coreNumber);
    }

    @Override
    public void addRunningEndStamp(int time, int coreNumber) {

    }

    // weird but we use waiting queue report for stall report since we don't have a waiting queue
    @Override
    public void addWaitingStartStamp(int time) {

    }

    public void addWaitingStartStamp(int time, int coreNumber) {
        waitingReport.append(",").append(time).append(":").append(coreNumber);
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
            finalReport.append(", didn't stall");
        }
        else {
            finalReport.append(", total time stalled: ").append(timeInWaitingQueue);
            finalReport.append(", stalled in ");
            finalReport.append(consecutiveDecoder(waitingReport));
        }
        return finalReport.toString();
    }
}
