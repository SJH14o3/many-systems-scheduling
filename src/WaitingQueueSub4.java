import java.util.Iterator;
import java.util.LinkedList;

public class WaitingQueueSub4 {
    SubSystem owner;

    private final LinkedList<ProcessSubSystem4> waitingList;


    public LinkedList<ProcessSubSystem4> getWaitingList() {
        return waitingList;
    }


    public void addLast (ProcessSubSystem4 processSubSystem4){
        synchronized (waitingList){
            waitingList.addLast(processSubSystem4);
        }
    }

    public boolean isEmpty(){
        return waitingList.isEmpty();
    }


    public WaitingQueueSub4(SubSystem subSystem){
        owner = subSystem;
        waitingList = new LinkedList<>();
    }


    // return of process that its prerequisite have been met by finish of input process to be added to ready queue
    public ProcessSubSystem4 backToReadyQueue(ProcessSubSystem4 processSubSystem4){
        for (ProcessSubSystem4 process: waitingList){
            if (process.getPrerequisite().equals(processSubSystem4.getName())){
                return process;
            }
        }
        return null;
    }


}
