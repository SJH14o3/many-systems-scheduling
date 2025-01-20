import Exceptions.EmptyQueueException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SubSystem1ReadyQueue {
    private final Queue<ProcessSubSystem1> queue;
    private final SubSystem1 owner;

    public void addAll(ArrayList<ProcessSubSystem1> in) {
        synchronized (queue) {
            queue.addAll(in);
        }
    }
    public void addOne(ProcessSubSystem1 in) {
        synchronized (queue) {
            queue.add(in);
        }
    }

    // since other cores might access this queue, it must be synchronized
    public ProcessSubSystem1 getProcess() {
        synchronized (queue) {
            if (queue.isEmpty()) {
                return null;
            }
            return queue.poll();
        }
    }

    // get and allocate process
    public ProcessSubSystem1 getAndAllocate() {
        synchronized (queue) {
            if (queue.isEmpty()) {
                return null;
            }
            ProcessSubSystem1 out = null;
            Iterator<ProcessSubSystem1> iterator = queue.iterator();
            while (iterator.hasNext()) {
                ProcessSubSystem1 processSubSystem1 = iterator.next();
                if (owner.checkAndAllocate(processSubSystem1)) { // Condition to remove
                    out = processSubSystem1;
                    iterator.remove();
                    break; // Stop iteration
                }
                else { // if it cannot be allocated, then remove from ready queue and add to waiting queue
                    owner.waitingQueue.addLast(processSubSystem1);
                    iterator.remove();
                }
            }
            return out;
        }
    }

    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (ProcessSubSystem1 processSubSystem1 : queue) {
            if (first) {
                first = false;
            }
            else {
                sb.append(",");
            }
            sb.append(processSubSystem1.getName());
        }
        sb.append("]\n");
        return sb.toString();
    }

    public SubSystem1ReadyQueue(ArrayList<ProcessSubSystem1> in, SubSystem1 owner) {
        this.queue = new LinkedList<>(in);
        this.owner = owner;
    }
}
