public final class ProcessSubSystem3 extends Process {
    private final int period;
    private int reoccurrence;
    private int nextDeadline;


    public int getPeriod() {
        return period;
    }

    public int getReoccurrence() {
        return reoccurrence;
    }

    public int getNextDeadline() {
        return nextDeadline;
    }

    public void setNextDeadline(int nextDeadline) {
        this.nextDeadline = nextDeadline;
    }

    public void setReoccurrence(int reoccurrence) {
        this.reoccurrence = reoccurrence;
    }

    public ProcessSubSystem3(String name, int burstTime, int maxR1, int maxR2, int startTime, int period, int reoccurrence) {
        super(name, burstTime, maxR1, maxR2, startTime);
        this.period = period;
        this.reoccurrence = reoccurrence;
    }

    public static ProcessSubSystem3 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem3(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]),
                Integer.parseInt(parts[6]));
    }

    @Override
    public void addRunningStartStamp(int time) {

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
    public String getFinalReport() {
        return name;
    }
}
