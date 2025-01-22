public final class ProcessSubSystem4 extends Process {

    private String prerequisite;

    public String getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        this.prerequisite = prerequisite;
    }

    public boolean runForATimeUnit() {
        burstTime--;
        return burstTime == 0;
    }

    public ProcessSubSystem4(String name, int burstTime, int maxR1, int maxR2, int startTime, String prerequisite) {
        super(name, burstTime, maxR1, maxR2, startTime);
        this.prerequisite = prerequisite;
    }

    public static ProcessSubSystem4 createProcessWithString(String line) {
        String[] parts = line.split(" ");
        return new ProcessSubSystem4(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), parts[5]);
    }
}
