import Exceptions.EmptyQueueException;
import Exceptions.NotEnoughResourcesException;

import java.util.PriorityQueue;

public class SubSystem2ReadyQueue {
    private final PriorityQueue<ProcessSubSystem2> priorityQueue;
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
    public boolean isEmpty(){
        return priorityQueue.isEmpty();
    }

    public ProcessSubSystem2 checkIfNewProcessHasHigherPriority(ProcessSubSystem2 processSubSystem2){
        ProcessSubSystem2 head;
        synchronized (priorityQueue) {
            head = priorityQueue.peek();
            if (head == null) {
                return null;
            }
            boolean result = (head.getRemainingTime() < processSubSystem2.getRemainingTime());
            if (!result){
                return null;
            }
            priorityQueue.poll();
            priorityQueue.add(processSubSystem2);
        }
        return head;
    }

    public ProcessSubSystem2 getProcess() throws EmptyQueueException {
        // we will use deadlock prevention, all need resources must be available or the core will stall
        synchronized (priorityQueue){
            ProcessSubSystem2 processSubSystem = priorityQueue.poll();
            if (processSubSystem == null) {
                throw new EmptyQueueException("ready queue is empty");
            }
            return processSubSystem;
        }
    }

    public void retrieveProcess(ProcessSubSystem2 processSubSystem2){
        synchronized (priorityQueue){
            processSubSystem2.setRemainingTime(processSubSystem2.getRemainingTime() - 1);
            owner.deallocate(processSubSystem2);
            if (processSubSystem2.getRemainingTime() != 0){
                priorityQueue.add(processSubSystem2);
            }
        }
    }

    // to print queue content in order, we have to clone it
    public PriorityQueue<ProcessSubSystem2> cloneQueue(){
        return new PriorityQueue<>(priorityQueue);
    }


}
