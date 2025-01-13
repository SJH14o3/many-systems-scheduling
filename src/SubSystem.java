public abstract class SubSystem {
    protected int intR1Remain;
    protected int intR2Remain;

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
}
