import Exceptions.NullWaitingQueueReturn;

import java.util.Iterator;
import java.util.LinkedList;

public class WaitingQueueSub1 {
    SubSystem owner;
    final LinkedList<ProcessSubSystem1> waitingList;

    public void addLast(ProcessSubSystem1 processSubSystem1){
        synchronized (waitingList){
            waitingList.addLast(processSubSystem1);
        }
    }


    public WaitingQueueSub1(SubSystem subSystem){
        waitingList = new LinkedList<>();
        owner = subSystem;
    }


    public ProcessSubSystem1 getWaitingProcess(int targetCPU, boolean affinity) throws NullWaitingQueueReturn {
        ProcessSubSystem1 out = null;
        synchronized (waitingList){
            Iterator<ProcessSubSystem1> iterator = waitingList.iterator();
            while (iterator.hasNext()) {
                ProcessSubSystem1 processSubSystem1 = iterator.next();
                if ((processSubSystem1.getTargetCPU() == targetCPU || !affinity) && owner.checkEnoughResource(processSubSystem1)) { // Condition to remove
                    out = processSubSystem1;
                    iterator.remove(); // Safe removal
                    break; // Stop iteration
                }
            }
        }
        if (out == null){
            throw new NullWaitingQueueReturn();
        }
        return out;
    }
}
