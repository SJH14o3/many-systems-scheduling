import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;

public class SubSystem3ReadyQueue {
    // since we only have one core, we don't need to synchronize
    private PriorityQueue<ProcessSubSystem3> queue;

    public SubSystem3ReadyQueue() {
        queue = new PriorityQueue<>(Comparator.comparingInt(ProcessSubSystem3::getPeriod).reversed());
    }

    public ProcessSubSystem3 pollProcess() {
        return queue.poll();
    }

    public void addProcess(ProcessSubSystem3 process) {
        queue.add(process);
    }
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public Optional<ProcessSubSystem3> checkForHigherPriority(ProcessSubSystem3 process) {
        if (queue.isEmpty()) {
            return Optional.empty();
        }
        ProcessSubSystem3 top = queue.peek();
        if (top.getNextDeadline() < process.getNextDeadline()) {
            queue.poll();
            queue.add(process);
            return Optional.of(top);
        }
        return Optional.empty();
    }
    public PriorityQueue<ProcessSubSystem3> cloneQueue(){
        return new PriorityQueue<>(queue);
    }

}
