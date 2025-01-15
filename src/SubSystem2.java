import java.util.concurrent.Semaphore;

public final class SubSystem2 extends SubSystem {

    public Semaphore[] subThreadWait;
    public Semaphore[] coreThreadWait;
    public StringBuilder[] message;
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
        systemIndex = 1;
        this.processes = processes;
        subThreadWait = new Semaphore[2];
        coreThreadWait = new Semaphore[2];
        message = new StringBuilder[2];
        for (int i = 0; i < subThreadWait.length; i++) {
            subThreadWait[i] = new Semaphore(0);
            coreThreadWait[i] = new Semaphore(0);
            message[i] = new StringBuilder();
        }

    }



    @Override
    public void run() {
        try {
            while (true) {
                sleep(300); //TODO: remove with actual job
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("message from system " + (systemIndex+1) + " at time " + owner.time);
                if (owner.time == 3) { //TODO: replace with actual finish statement
                    systemState = STATE_FINISHED;
                    System.out.println("[THREAD] system " + (systemIndex+1) + " is finished");
                    owner.mainThreadWait[systemIndex].release();
                    break;
                };
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            //TODO: task is finished here
            while(true) {
                owner.subSystemWait[systemIndex].acquire();
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("system " + (systemIndex + 1) + " is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
