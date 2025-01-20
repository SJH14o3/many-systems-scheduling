import Exceptions.EmptyQueueException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class SubSystem1ReadyQueue {
    private final Queue<ProcessSubSystem1> queue;
    private final SubSystem1 owner;

    public void addAll(ArrayList<ProcessSubSystem1> in) {
        queue.addAll(in);
    }

    // since other cores might access this queue, it must be synchronized
    public ProcessSubSystem1 getProcess() throws EmptyQueueException {
        synchronized (queue) {
            if (queue.isEmpty()) {
                throw new EmptyQueueException("Ready queue is empty");
            }
            return queue.poll();
        }
    }

    public SubSystem1ReadyQueue(ArrayList<ProcessSubSystem1> in, SubSystem1 owner) {
        this.queue = new LinkedList<>(in);
        this.owner = owner;
    }
}
