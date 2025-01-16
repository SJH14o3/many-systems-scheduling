import Exceptions.NotEnoughResourcesException;

public abstract class SubSystem extends Thread{
    protected Integer R1Remain;
    protected Integer R2Remain;

    protected MainSystem owner;

    protected int systemState;
    protected int systemIndex;

    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_FINISH_REGISTERED = 3;

    public int getR1Remain() {
        return R1Remain;
    }

    public void setR1Remain(int r1Remain) {
        this.R1Remain = r1Remain;
    }

    public int getR2Remain() {
        return R2Remain;
    }

    public void setR2Remain(int r2Remain) {
        this.R2Remain = r2Remain;
    }

    public int getSystemState() {
        return systemState;
    }

    public void setSystemState(int systemState) {
        this.systemState = systemState;
    }

    public void setOwner(MainSystem instance) {
        owner = instance;
    }

    public SubSystem(int intR1Remain, int intR2Remain) {
        this.R1Remain = intR1Remain;
        this.R2Remain = intR2Remain;
        systemState = 1;
    }

    public void allocate(Process process) throws NotEnoughResourcesException {
        synchronized (this) {
            int newR1Remain = R1Remain - process.getMaxR1();
            int newR2Remain = R2Remain - process.getMaxR2();
            /*System.out.println("-- task: " + process.getName() + " --");
            System.out.println("-- Resources before allocation: R1:" + R1Remain + ", R2:" + R2Remain + " --");
            System.out.println("-- Resources after allocation: R1:" + newR1Remain + ", R2:" + newR2Remain + " --");*/
            if (newR1Remain < 0 || newR2Remain < 0) {
                //System.out.println("-- a core is stalled --");
                throw new NotEnoughResourcesException("task cannot be allocated");
            }
            R1Remain = newR1Remain;
            R2Remain = newR2Remain;
        }
    }

    public void deallocate(Process process) {
        synchronized (this) {
            //System.out.println("-- Resources before de-allocation: R1:" + R1Remain + ", R2:" + R2Remain + " --");
            R1Remain += process.getMaxR1();
            R2Remain += process.getMaxR2();
            //System.out.println("-- Resources after de-allocation: R1:" + R1Remain + ", R2:" + R2Remain + " --");
        }
    }

    public boolean checkEnoughResource(Process process){
        boolean out = false;
        synchronized (this){
            if (R1Remain >= process.getMaxR1() && R2Remain >= process.getMaxR2()){
                out = true;
            }
        }
        return out;
    }
}
