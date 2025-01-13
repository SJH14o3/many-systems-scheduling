import java.util.Arrays;
import java.util.PriorityQueue;

public class WaitingQueue {

    private SubSystem owner;

    private final Object lock = new Object();
    PriorityQueue<Process> queue;

    private void increaseAge(){
        Process[] temp = queue.toArray(new Process[0]);
        queue.clear();
        for (int i = 0; i < temp.length; i++) {
            temp[i].increaseAge();
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

    public Process getProcess(){
        Process temp;
        synchronized (lock){
            PriorityQueue<Process> copy = new PriorityQueue<>(queue);
            while (true){
                temp = copy.poll();
                if (owner.checkEnoughResource(temp)) break; //TODO: handle if no process cannot be allocated
            }
        }
        return temp;
    }
}

