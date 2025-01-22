import Exceptions.EmptyQueueException;

import java.util.Comparator;
import java.util.PriorityQueue;

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


    public PriorityQueue<ProcessSubSystem4> cloneQueue(){
        return new PriorityQueue<>(priorityQueue);
    }


}
