import Exceptions.NotEnoughResourcesException;

import java.util.PriorityQueue;
import java.util.Queue;

public class SubSystem2ReadyQueue {
    PriorityQueue<ProcessSubSystem2> priorityQueue;
    SubSystem2 owner;
    public SubSystem2ReadyQueue(SubSystem2 owner){
        this.owner = owner;
        priorityQueue = new PriorityQueue<>((t1, t2)-> Integer.compare(t1.getRemainingTime(),t2.getRemainingTime()));
    }
    public void addProcess(ProcessSubSystem2 processSubSystem2){
        synchronized (priorityQueue) {
            priorityQueue.add(processSubSystem2);
        }
    }

    public ProcessSubSystem2 getProcess() throws NotEnoughResourcesException {
        // we will use deadlock prevention, all need resources must be available or the core will stall
        synchronized (priorityQueue){
            ProcessSubSystem2 processSubSystem = priorityQueue.peek();
            boolean result = owner.checkEnoughResource(processSubSystem);
            if (result == false){
                throw new NotEnoughResourcesException("current process there is not enough resources");
            }
            priorityQueue.poll();
            owner.allocate(processSubSystem);
            return processSubSystem;
        }
    }

    public void retrieveProcess(ProcessSubSystem2 processSubSystem2){
        synchronized (priorityQueue){
            processSubSystem2.setRemainingTime(processSubSystem2.getRemainingTime() - 1);
            owner.unAllocate(processSubSystem2);
            if (processSubSystem2.getRemainingTime() != 0){
                priorityQueue.add(processSubSystem2);
            }
        }
    }


}
