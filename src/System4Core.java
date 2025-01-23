import java.util.Optional;
import java.util.Random;

public class System4Core extends SystemCore{
    private final SubSystem4 owner;
    private ProcessSubSystem4 currentTask;



    public System4Core(SubSystem4 owner, int coreIndex) {
        super(coreIndex, owner.systemIndex);
        this.owner = owner;
    }

    @Override
    public void run() {
        try {
            while (true){
                owner.coreThreadWait[coreIndex].acquire();
                if (coreState == CORE_STATE_STOPPED){
                    break;
                }
                //phase one: assign task and report of this time unit
                owner.message[coreIndex].setLength(0);
                owner.message[coreIndex].append("    Core").append((coreIndex+1)).append(":\n")
                        .append("       Running Task: ");
                if (coreState == CORE_STATE_IDLE){
                    Optional<ProcessSubSystem4> temp = owner.readyQueue.getRunnableProcess();
                    if (!temp.isPresent()){
                        temp = owner.waitingQueue.getWaitingProcess();
                    }
                    if (temp.isPresent()){
                        coreState = CORE_STATE_RUNNING;
                        currentTask = temp.get();
                    }
                }
                if (currentTask != null){
                    owner.message[coreIndex].append(currentTask.getName());
                    owner.subSystemWait[coreIndex].release();
                    owner.coreThreadWait[coreIndex].acquire();
                    if (currentTask.runForATimeUnit()){
                        Random random = new Random();
                        int randomNum = random.nextInt(10);
                        if (randomNum <= 2){
                            System.out.println("\u001B[31m" + currentTask.getName() + " failed at time " + owner.owner.time + " in core " + (coreIndex+1) + "\u001B[0m");
                            currentTask.setRemainingTime(currentTask.burstTime);
                        }else {
                            owner.addFinishedTask(currentTask);
                            coreState = CORE_STATE_IDLE;
                            owner.deallocate(currentTask);
                            currentTask = null;
                        }
                    }
                }else {
                    owner.message[coreIndex].append("IDLE");
                    owner.subSystemWait[coreIndex].release();
                    owner.coreThreadWait[coreIndex].acquire();
                }
                owner.subSystemWait[coreIndex].release();
            }
        }catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
