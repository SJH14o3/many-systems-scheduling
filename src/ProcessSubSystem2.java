public final class ProcessSubSystem2 extends Process {
    private int remainingTime;
    private boolean isStalled = false;

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

    public boolean runForATimeUnit() {
        remainingTime--;
        return remainingTime == 0;
    }

    public boolean isStalledJustNow() {
        if (!isStalled) {
            isStalled = true;
            return true;
        }
        return false;
    }

    @Override
    public void addRunningStartStamp(int time) {
        runningReport.append("x").append(time);
    }

    @Override
    public void addRunningEndStamp(int time, int coreNumber) {
        runningReport.append("-").append(time).append("-").append(coreNumber);
    }

    // weird but we use waiting queue report for stall report since we don't have a waiting queue
    @Override
    public void addWaitingStartStamp(int time) {
        waitingReport.append("x").append(time);
    }

    public void addWaitingEndStamp(int time, int coreNumber) {
        waitingReport.append("-").append(time).append("-").append(coreNumber);
        isStalled = false;
    }

    @Override
    public void addWaitingEndStamp(int time) {
        waitingReport.append("-").append(time);
    }

    @Override
    public String getFinalReport() {
        StringBuilder finalReport = new StringBuilder(reportStartingDetails());
        finalReport.append(", running details: ");
        if (name.equals("T22")) {
            System.out.println(runningReport);
        }
        finalReport.append(listToReport(runningReport));
        if (waitingReport.isEmpty()) {
            finalReport.append(", didn't stall");
        }
        else {
            finalReport.append(", total time stalled: ").append(timeInWaitingQueue);
            finalReport.append(", stalled in ");
            finalReport.append(listToReport(waitingReport));
        }
        return finalReport.toString();
    }
}
