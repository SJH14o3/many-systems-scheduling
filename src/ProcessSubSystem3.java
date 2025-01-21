public final class ProcessSubSystem3 extends Process {
    private final int period;
    private int reoccurrence;


    public int getPeriod() {
        return period;
    }

    public int getReoccurrence() {
        return reoccurrence;
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
}
