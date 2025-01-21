public final class SubSystem3 extends SubSystem {
    public SubSystem3(int intR1Remain, int intR2Remain, Process[] processes, int sys3CoresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processes, sys3CoresCount, doNotSendReport);
        systemIndex = 2;
    }

    @Override
    protected void checkForNewProcesses() {
        //TODO: implement
    }

    @Override
    protected void reportToMainSystem() {
        //TODO: implement
    }

    @Override
    protected void runATimeUnitBody() throws InterruptedException {
        //TODO: implement
    }

    @Override
    protected boolean isSystemFinished() {
        //TODO: implement
        return false;
    }

    @Override
    public void run() { //TODO: move this function after implementation to runLoop, like other systems
        try {
            while (true) {
                //TODO: remove with actual job
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub3:");
                if (owner.time == 6) { //TODO: replace with actual finish statement
                    systemState = STATE_FINISHED;
                    owner.mainThreadWait[systemIndex].release();
                    break;
                }
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            //TODO: task is finished here
            while(true) {
                owner.subSystemWait[systemIndex].acquire();
                if (systemState == STATE_STOPPED) {
                    break;
                }
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub3 is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
