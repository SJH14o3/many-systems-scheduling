public abstract class Process {
    protected final String name;
    protected int burstTime;
    protected int maxR1;
    protected int maxR2;
    protected int startTime;
    protected int state;
    protected int endTime;
    protected StringBuilder runningReport;
    protected StringBuilder waitingReport;


    protected int timeInWaitingQueue;


    public static int STATE_READY = 0;
    public static int STATE_RUNNING = 1;
    public static int STATE_WAITING = 2;


    public String getName() {
        return name;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getMaxR1() {
        return maxR1;
    }

    public int getMaxR2() {
        return maxR2;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getTimeInWaitingQueue() {
        return timeInWaitingQueue;
    }

    public void setTimeInWaitingQueue(int timeInWaitingQueue) {
        this.timeInWaitingQueue = timeInWaitingQueue;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public Process(String name, int burstTime, int maxR1, int maxR2, int startTime) {
        this.name = name;
        this.burstTime = burstTime;
        this.maxR1 = maxR1;
        this.maxR2 = maxR2;
        this.startTime = startTime;
        runningReport = new StringBuilder();
        waitingReport = new StringBuilder();
    }

    public int[] getMaxResourcesAsArray() {
        return new int[]{maxR1, maxR2};
    }

    public void incrementWaitingTime() {
        timeInWaitingQueue++;
    }

    private String createPartialReportString(int start, int end , int core) {
        return " " + start + " to " + end + " in core " + core;
    }

    public String consecutiveDecoder(StringBuilder in) {
        String[] entries = in.substring(1).split(",");
        StringBuilder result = new StringBuilder("[ ");

        int start = -1;
        int end = -1;
        int prevCore = -1;
        boolean firstEntry = true;

        for (String entry : entries) {
            if (entry.isEmpty()) continue;

            String[] parts = entry.split(":");
            int time = Integer.parseInt(parts[0]);
            int core = Integer.parseInt(parts[1]);

            if (firstEntry) {
                start = time;
                prevCore = core;
                firstEntry = false;
            }

            if (core != prevCore || time != end + 1) {
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

    public String consecutiveDecoderNoCore(StringBuilder in) {
        String[] entries = in.substring(1).split(",");
        StringBuilder result = new StringBuilder("[ ");

        int start = -1;
        int end = -1;
        boolean firstEntry = true;

        for (String entry : entries) {
            if (entry.isEmpty()) continue;
            int time = Integer.parseInt(entry);

            if (firstEntry) {
                start = time;
                firstEntry = false;
            }

            if (time != end + 1) {
                if (end != -1) {
                    result.append(start).append(" to ").append(end).append(", ");
                }
                start = time;
            }

            end = time;
        }

        if (start != -1 && end != -1) {
            result.append(start).append(" to ").append(end);
        }
        return result + " ]";
    }

    public String listToReport(StringBuilder in) {
        String[] reports = in.substring(1).split("x");
        StringBuilder out = new StringBuilder("[");
        boolean first = true;
        for (String report : reports) {
            if (first) {
                first = false;
            }
            else {
                out.append(", ");
            }
            String[] times = report.split("-");
            out.append(times[0]).append(" to ").append(times[1]);
            if (times.length > 2) {
                out.append(" in core ").append(times[2]);
            }
        }
        out.append("]");
        return out.toString();
    }

    public String reportStartingDetails() {
        return name + ": " +
                "arrived at " + startTime +
                ", ended at " + endTime;
    }

    public static Process createProcessWithString(String line) {
        return null;
    }
    public abstract void addRunningStartStamp(int time);
    public abstract void addRunningEndStamp(int time, int coreNumber);
    public abstract void addWaitingStartStamp(int time);
    public abstract void addWaitingEndStamp(int time);
    public abstract String getFinalReport();
}
