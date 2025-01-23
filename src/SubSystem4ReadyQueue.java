import Exceptions.EmptyQueueException;

import java.util.*;

public class SubSystem4ReadyQueue {
    SubSystem4 owner;

    private final PriorityQueue<ProcessSubSystem4> priorityQueue;


    public SubSystem4ReadyQueue(SubSystem4 owner){
        this.owner = owner;
        priorityQueue = new PriorityQueue<>(Comparator.comparingInt(ProcessSubSystem4::getStartTime));
    }

    public void addProcess(ProcessSubSystem4 processSubSystem4){
        synchronized (priorityQueue){
            priorityQueue.add(processSubSystem4);
        }
    }

    public void addAllProcesses(List<ProcessSubSystem4> processes){
        synchronized (priorityQueue){
            priorityQueue.addAll(processes);
        }
    }

    public boolean isEmpty(){
        return priorityQueue.isEmpty();
    }

    public ProcessSubSystem4 getProcess() throws EmptyQueueException {
        synchronized (priorityQueue){
            ProcessSubSystem4 processSubSystem4 = priorityQueue.poll();
            if (processSubSystem4 == null){
                throw new EmptyQueueException("ready queue is empty");
            }
            return processSubSystem4;
        }
    }

    /* this function will check if process is runnable first by checking if it's prerequisite is finished
    * and then checks if it can be allocated. if two conditions are met, inside checkAndAllocate function , it
    * will be allocated. if any of the conditions are not met, it will be removed and it will be added to waiting queue*/
    public Optional<ProcessSubSystem4> getRunnableProcess() {
        ProcessSubSystem4 out = null;
        List<ProcessSubSystem4> addToWaitingQueue = new LinkedList<>();
        synchronized (priorityQueue) {
            while (!priorityQueue.isEmpty()) {
                out = priorityQueue.poll();
                if (owner.isPrerequisiteDone(out) && owner.checkAndAllocate(out)) {
                    break;
                } else {
                    addToWaitingQueue.add(out);
                }
            }
        }
        owner.waitingQueue.addAll(addToWaitingQueue);
        return Optional.ofNullable(out);
    }

    public PriorityQueue<ProcessSubSystem4> cloneQueue(){
        return new PriorityQueue<>(priorityQueue);
    }


}
