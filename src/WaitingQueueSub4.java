import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class WaitingQueueSub4 {
    SubSystem4 owner;
    //TODO: discuss if we should use a priority queue
    private final LinkedList<ProcessSubSystem4> waitingList;


    public LinkedList<ProcessSubSystem4> getWaitingList() {
        return waitingList;
    }


    public void addLast (ProcessSubSystem4 processSubSystem4){
        synchronized (waitingList){
            waitingList.addLast(processSubSystem4);
        }
    }

    public void addAll(List<ProcessSubSystem4> items){
        synchronized (waitingList){
            // appends to end of the list
            waitingList.addAll(items);
        }
    }

    public boolean isEmpty(){
        return waitingList.isEmpty();
    }


    public WaitingQueueSub4(SubSystem4 subSystem){
        owner = subSystem;
        waitingList = new LinkedList<>();
    }

    public Optional<ProcessSubSystem4> getWaitingProcess() {
        ProcessSubSystem4 out = null;
        synchronized (waitingList) {
            Iterator<ProcessSubSystem4> iterator = waitingList.iterator();
            while (iterator.hasNext()) {
                ProcessSubSystem4 temp = iterator.next();
                if ((owner.isPrerequisiteDone(temp) && owner.checkAndAllocate(temp))) { // Condition to remove
                    out = temp;
                    iterator.remove(); // Safe removal
                    break; // Stop iteration
                }
            }
        }
        return Optional.ofNullable(out);
    }

    // return of process that its prerequisite have been met by finish of input process to be added to ready queue
    //TODO: call this function when a task is finished
    //TODO: discuss if we should return all processes or only one or none since we have getWaitingProcess?
    public void backToReadyQueue(ProcessSubSystem4 processSubSystem4) {
        LinkedList<ProcessSubSystem4> backToReadyQueue = new LinkedList<>();
        synchronized (waitingList) {
            Iterator<ProcessSubSystem4> iterator = waitingList.iterator();
            while (iterator.hasNext()) {
                ProcessSubSystem4 temp = iterator.next();
                if (temp.getPrerequisite().equals(processSubSystem4.getName())) {
                    backToReadyQueue.add(temp);
                    iterator.remove();
                }
            }
        }
        owner.readyQueue.addAllProcesses(backToReadyQueue);
    }
}
