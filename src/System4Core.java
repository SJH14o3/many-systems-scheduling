public class System4Core extends SystemCore{
    private final SubSystem4 owner;
    private ProcessSubSystem4 currentTask;



    public System4Core(SubSystem4 owner, int coreIndex) {
        super(coreIndex);
        this.owner = owner;
    }
}
