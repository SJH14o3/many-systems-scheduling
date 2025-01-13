public abstract class SubSystem {
    protected Integer intR1Remain;
    protected Integer intR2Remain;

    public int getIntR1Remain() {
        return intR1Remain;
    }

    public void setIntR1Remain(int intR1Remain) {
        this.intR1Remain = intR1Remain;
    }

    public int getIntR2Remain() {
        return intR2Remain;
    }

    public void setIntR2Remain(int intR2Remain) {
        this.intR2Remain = intR2Remain;
    }

    public SubSystem(int intR1Remain, int intR2Remain) {
        this.intR1Remain = intR1Remain;
        this.intR2Remain = intR2Remain;
    }

    public boolean checkEnoughResource(Process process){
        boolean out = false;
        synchronized (intR1Remain){
            if (intR1Remain >= process.getMaxR1() && intR2Remain >= process.getMaxR2()){
                out = true;
            }
        }
        return out;
    }
}
