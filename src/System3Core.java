import Exceptions.NotEnoughResourcesException;

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
                    // phase one: assign a task if we don't have any and send a report
                    if (coreState == CORE_STATE_IDLE){
                        currentTask = owner.subSystem3ReadyQueue.pollProcess();
                        if (currentTask != null){
                            coreState = CORE_STATE_RUNNING;
                            owner.allocate(currentTask);
                        }
                    }else{
                        Optional<ProcessSubSystem3> temp = owner.subSystem3ReadyQueue.checkForHigherPriority(currentTask);
                        if (temp.isPresent()){
                            owner.deallocate(currentTask);
                            currentTask = temp.get();
                            owner.allocate(currentTask);
                        }
                    }
                    owner.message[coreIndex].setLength(0);
                    owner.message[coreIndex].append("    Core").append((coreIndex+1)).append(":\n")
                            .append("       Running Task: ");
                    // phase two: run now
                    if (coreState == CORE_STATE_IDLE){
                        owner.message[coreIndex].append("IDLE");
                        owner.subSystemWait[coreIndex].release();
                        owner.coreThreadWait[coreIndex].acquire();
                    } else {
                        owner.message[coreIndex].append(currentTask.getName());
                        owner.subSystemWait[coreIndex].release();
                        currentTask.addSubTimeUnitStartStamp(owner.owner.time, owner.subTimeUnit, coreIndex+1);
                        owner.coreThreadWait[coreIndex].acquire();
                        if (currentTask.runForATimeUnit()) {
                            if (currentTask.decrementReoccurrence()) {
                                owner.deallocate(currentTask);
                                owner.taskIsFinished(currentTask);
                            }
                            else {
                                currentTask.setStartTime(currentTask.getStartTime()+ currentTask.getPeriod());
                                System.out.println("next start time for " + currentTask.getName() + " is : " + currentTask.getStartTime());
                                currentTask.setBurstTime(currentTask.originalBurstTime);
                                owner.notArrivedQueue.add(currentTask);
                                owner.deallocate(currentTask);
                            }
                            coreState = CORE_STATE_IDLE;
                            currentTask = null;
                        }
                    }
                    owner.subSystemWait[coreIndex].release();
                } catch (InterruptedException | NotEnoughResourcesException e) { //NotEnoughResourcesException does not happen
                    throw new RuntimeException(e);
                }

            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
