import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class SubSystem1ReadyQueue {
    public final Queue<ProcessSubSystem1> queue;
    private final SubSystem1 owner;
    private final System1Core core;

    public void addOne(ProcessSubSystem1 in) {
        synchronized (queue) {
            queue.add(in);
        }
    }

    public boolean isEmpty(){
        return queue.isEmpty();
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

    public ProcessSubSystem1 tryPulling() {
        synchronized (queue) {
            // if queue is empty then pulling from this queue fails
            if (queue.isEmpty()) {
                return null;
            }
            // if core is idle and there is only 1 process, the core is going to run it I am pretty sure unless it cannot be allocated
            if (core.getCoreState() == SystemCore.CORE_STATE_IDLE && queue.size() == 1) {
                return null;
            }
            return getAndAllocate();
        }
    }

    public SubSystem1ReadyQueue(SubSystem1 owner, System1Core core) {
        this.queue = new LinkedList<>();
        this.owner = owner;
        this.core = core;
    }
}
