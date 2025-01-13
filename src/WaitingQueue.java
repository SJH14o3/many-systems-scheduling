import Exceptions.AllProcessesNotEnoughResourceException;
import Exceptions.EmptyQueueException;

import java.util.Arrays;
import java.util.PriorityQueue;

public class WaitingQueue {

    private SubSystem owner;

    private final Object lock = new Object();
    PriorityQueue<Process> queue;

    // NOTE THAT this function MUST be called inside a synchronized block
    private void increaseAge(){
        Process[] temp = queue.toArray(new Process[0]);
        queue.clear();
        for (Process process : temp) {
            process.increaseAge();
        }
        queue.addAll(Arrays.asList(temp));
    }

    public void addProcess(Process process){
        synchronized (lock){
            process.setWaitingQueuePriority(0);
            increaseAge();
            queue.add(process);
        }
    }

    public Process getProcess() throws EmptyQueueException, AllProcessesNotEnoughResourceException {
        Process temp;
        synchronized (lock){
            if (queue.isEmpty()){
                throw new EmptyQueueException("waiting queue is empty");
            }
            PriorityQueue<Process> copy = new PriorityQueue<>(queue);
            do {
                if (copy.isEmpty()){
                    throw new AllProcessesNotEnoughResourceException("No process can be allocated because there are not" +
                            " enough resources for any of them");
                }
                temp = copy.poll();
                assert temp != null;
            } while (!owner.checkEnoughResource(temp));
        }
        queue.remove(temp);
        return temp;
    }
}

