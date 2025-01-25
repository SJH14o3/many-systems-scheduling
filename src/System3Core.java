import java.util.Optional;

public class System3Core extends SystemCore {
    SubSystem3 owner;
    private ProcessSubSystem3 currentTask;
    public System3Core(int coreIndex, SubSystem3 owner ) {
        super(coreIndex, owner.systemIndex);
        this.owner = owner;
    }



    @Override
    public void run() {
        try {
            while (true){
                try {
                    owner.coreThreadWait[coreIndex].acquire();
                    if (coreState == CORE_STATE_STOPPED) {
                        break;
                    }
                    if (coreState == CORE_STATE_IDLE){
                        currentTask = owner.subSystem3ReadyQueue.pollProcess();
                        if (currentTask != null){
                            coreState = CORE_STATE_RUNNING;
                        }
                    }else{
                        Optional<ProcessSubSystem3> temp = owner.subSystem3ReadyQueue.checkForHigherPriority(currentTask);
                        if (temp.isPresent()){
                            currentTask = temp.get();
                        }
                    }



                    owner.message[coreIndex].setLength(0);
                    owner.message[coreIndex].append("    Core").append((coreIndex+1)).append(":\n")
                            .append("       Running Task: ");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
