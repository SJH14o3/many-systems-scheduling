public final class SubSystem2 extends SubSystem {
    private ProcessSubSystem2[] processes;

    public ProcessSubSystem2[] getProcesses() {
        return processes;
    }

    public ProcessSubSystem2 getProcessWithIndex(int i) {
        return processes[i];
    }

    public void setProcesses(ProcessSubSystem2[] processes) {
        this.processes = processes;
    }

    public SubSystem2(int intR1Remain, int intR2Remain, ProcessSubSystem2[] processes) {
        super(intR1Remain, intR2Remain);
    }



}
