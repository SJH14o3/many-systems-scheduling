import java.util.*;

public class WaitingQueueSub4 {
    SubSystem4 owner;
    private final PriorityQueue<ProcessSubSystem4> waitingList;


    public PriorityQueue<ProcessSubSystem4> getWaitingList() {
        return waitingList;
    }


    public void addProcess(ProcessSubSystem4 processSubSystem4){
        processSubSystem4.addWaitingStartStamp(owner.owner.time);
        synchronized (waitingList){
            waitingList.add(processSubSystem4);
        }
    }

    public void addAll(List<ProcessSubSystem4> items){
        for (ProcessSubSystem4 item : items) {
            item.addWaitingStartStamp(owner.owner.time);
        }
        synchronized (waitingList){
            // appends to end of the list
            waitingList.addAll(items);
        }
    }

    public boolean isEmpty(){
        return waitingList.isEmpty();
    }

    public void increaseWaitingTimes() {
        for (ProcessSubSystem4 process: waitingList){
            process.incrementWaitingTime();
        }
    }


    public WaitingQueueSub4(SubSystem4 subSystem){
        owner = subSystem;
        waitingList = new PriorityQueue<>(Comparator.comparing(ProcessSubSystem4::getStartTime));
    }

    public Optional<ProcessSubSystem4> getWaitingProcess() {
        ProcessSubSystem4 out = null;
        synchronized (waitingList) {
            PriorityQueue<ProcessSubSystem4> temp = new PriorityQueue<>(waitingList);
            while (!temp.isEmpty()){
                ProcessSubSystem4 processSubSystem4 = temp.poll();
                if (owner.isPrerequisiteDone(processSubSystem4) && owner.checkAndAllocate(processSubSystem4)){
                    out = processSubSystem4;
                    out.addWaitingEndStamp(owner.owner.time - 1);
                    waitingList.remove(processSubSystem4);
                    break;
                }
            }
        }
        return Optional.ofNullable(out);
    }

    // return of process that its prerequisite have been met by finish of input process to be added to ready queue
    public void backToReadyQueue() {
        LinkedList<ProcessSubSystem4> backToReadyQueue = new LinkedList<>();
        synchronized (waitingList) {
            Iterator<ProcessSubSystem4> iterator = waitingList.iterator();
            while (iterator.hasNext()) {
                ProcessSubSystem4 temp = iterator.next();
                if (owner.isPrerequisiteDone(temp) && owner.checkEnoughResource(temp)) {
                    temp.addWaitingEndStamp(owner.owner.time - 1);
                    backToReadyQueue.add(temp);
                    iterator.remove();
                }
            }
        }
        owner.readyQueue.addAllProcesses(backToReadyQueue);
    }
}
