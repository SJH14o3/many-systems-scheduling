public final class ProcessSubSystem4 extends Process {

    private final String prerequisite;
    private int remainingTime;
    private int failedCount;

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }


    public String getPrerequisite() {
        return prerequisite;
    }

    public void incrementFailedCount() {
        failedCount++;
    }

    public boolean runForATimeUnit() {
        remainingTime--;
        return remainingTime == 0;
    }

    public ProcessSubSystem4(String name, int burstTime, int maxR1, int maxR2, int startTime, String prerequisite) {
        super(name, burstTime, maxR1, maxR2, startTime);
        this.prerequisite = prerequisite;
        remainingTime = burstTime;
    }

    public static ProcessSubSystem4 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem4(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
    }

    @Override
    public void addRunningStartStamp(int time) {
        runningReport.append(time);
    }

    public void addRunningEndStamp(int time, int coreNumber) {
        runningReport.append("-").append(time).append("-").append(coreNumber);
    }

    @Override
    public void addWaitingStartStamp(int time) {
        waitingReport.append("x").append(time);
    }

    @Override
    public void addWaitingEndStamp(int time) {
        waitingReport.append("-").append(time);
    }

    @Override
    public String getFinalReport() {
        StringBuilder finalReport = new StringBuilder(reportStartingDetails());
        String[] runTimes = runningReport.toString().split("-");
        finalReport.append(", ran from ").append(runTimes[0]).append(" to ").append(runTimes[1]).append(" by core ").append(runTimes[2]);
        finalReport.append(", failed ").append(failedCount).append(" time");
        if (failedCount > 1) finalReport.append("s");
        if (waitingReport.isEmpty()) {
            finalReport.append(", no waiting time");
        }
        else {
            finalReport.append(", total time in waiting queue: ").append(timeInWaitingQueue);
            finalReport.append(", waited in ");
            finalReport.append(listToReport(waitingReport));
        }
        return finalReport.toString();
    }
}
