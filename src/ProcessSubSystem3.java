public final class ProcessSubSystem3 extends Process {
    private final int period;
    private int reoccurrence;
    public final int originalBurstTime;

    public int getPeriod() {
        return period;
    }

    public int getReoccurrence() {
        return reoccurrence;
    }

    public void setReoccurrence(int reoccurrence) {
        this.reoccurrence = reoccurrence;
    }

    public boolean decrementReoccurrence() {
        reoccurrence--;
        return reoccurrence == 0;
    }

    public void setStartTime(int newTime) {
        startTime = newTime;
    }

    public void setBurstTime(int newTime) {
        burstTime = newTime;
    }


    public ProcessSubSystem3(String name, int burstTime, int maxR1, int maxR2, int startTime, int period, int reoccurrence) {
        super(name, burstTime, maxR1, maxR2, startTime);
        this.period = period;
        this.reoccurrence = reoccurrence;
        originalBurstTime = burstTime;
    }

    public static ProcessSubSystem3 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem3(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
                Integer.parseInt(parts[6]));
    }

    public boolean runForATimeUnit() {
        burstTime--;
        return burstTime == 0;
    }

    @Override
    public void addRunningStartStamp(int time) {

    }

    public void addSubTimeUnitStartStamp(int time, int sub, int coreNumber) {
        runningReport.append(",").append(time).append(".").append(sub).append(":").append(coreNumber);
    }


    @Override
    public void addRunningEndStamp(int time,  int coreNumber) {

    }

    @Override
    public void addWaitingStartStamp(int time) {

    }

    @Override
    public void addWaitingEndStamp(int time) {

    }

    @Override
    public String consecutiveDecoder(StringBuilder in) {
        String[] entries = in.substring(1).split(",");
        StringBuilder result = new StringBuilder("[ ");
        double start = -1;
        double end = -1;
        int prevCore = -1;
        boolean firstEntry = true;

        for (String entry : entries) {
            if (entry.isEmpty()) continue;

            String[] parts = entry.split(":");
            double time = Double.parseDouble(parts[0]);
            int core = Integer.parseInt(parts[1]);

            if (firstEntry) {
                start = time;
                prevCore = core;
                firstEntry = false;
            }

            if (core != prevCore || (time != end + 1 && time != end + 0.1 )) {
                if (end != -1) {
                    result.append(start).append(" to ").append(end).append(" in core ").append(prevCore).append(", ");
                }
                start = time;
                prevCore = core;
            }

            end = time;
        }

        if (start != -1 && end != -1) {
            result.append(start).append(" to ").append(end).append(" in core ").append(prevCore);
        }

        return result + " ]";
    }

    @Override
    public String getFinalReport() {
        StringBuilder sb = new StringBuilder(reportStartingDetails());
        sb.append(", ran in ");
        sb.append(consecutiveDecoder(runningReport));
        return sb.toString();
    }
}
