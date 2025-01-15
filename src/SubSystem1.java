public final class SubSystem1 extends SubSystem {

    public SubSystem1(int intR1Remain, int intR2Remain) {
        super(intR1Remain, intR2Remain);
        systemIndex = 0;
    }

    @Override
    public void run() {
        try {
            while (true) {
                sleep(300); //TODO: remove with actual job
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("message from system " + (systemIndex+1) + " at time " + owner.time);
                if (owner.time == 2) { //TODO: replace with actual finish statement
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
