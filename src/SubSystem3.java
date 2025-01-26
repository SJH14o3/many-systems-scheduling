import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public final class SubSystem3 extends SubSystem {
    private ArrayList<ProcessSubSystem3> sortedByR1;
    private ArrayList<ProcessSubSystem3> sortedByR2;
    private int maxR1;
    private int maxR2;
    private final System3Core[] core;
    private final int originalR1;
    private final int originalR2;

    public SubSystem3ReadyQueue subSystem3ReadyQueue;
    private final LinkedList<Resource> resource1;
    private final LinkedList<Resource> resource2;
    public PriorityQueue<ProcessSubSystem3> notArrivedQueue;

    public boolean isOverClocked = true;
    public int subTimeUnit = 1;

    public SubSystem3(int intR1Remain, int intR2Remain, Process[] processes, int sys3CoresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processes, sys3CoresCount, doNotSendReport, 2);
        sortProcessesByResources(processes);
        resource1 = new LinkedList<>();
        resource2 = new LinkedList<>();
        subSystem3ReadyQueue = new SubSystem3ReadyQueue();
        core = new System3Core[1];
        core[0] = new System3Core(0,this);
        notArrivedQueue = new PriorityQueue<>(Comparator.comparingInt(ProcessSubSystem3::getStartTime));
        for (Process process : processes) {
            notArrivedQueue.add((ProcessSubSystem3) process);
        }
        originalR1 = intR1Remain;
        originalR2 = intR2Remain;
    }

    public void sortProcessesByResources(Process[] processes) {
        sortedByR1 = new ArrayList<>();
        sortedByR2 = new ArrayList<>();
        for (Process process : processes) {
            sortedByR1.add((ProcessSubSystem3) process);
            sortedByR2.add((ProcessSubSystem3) process);
        }
        sortedByR1.sort(Comparator.comparingInt(ProcessSubSystem3::getMaxR1).reversed());
        sortedByR2.sort(Comparator.comparingInt(ProcessSubSystem3::getMaxR2).reversed());
        maxR1 = sortedByR1.get(0).maxR1;
        maxR2 = sortedByR2.get(0).maxR2;

    }


    public void lendResource(){
        int neededR1 = (Math.max(maxR1 - R1Remain, 0));
        int neededR2 = (Math.max(maxR2-R2Remain,0));
        LinkedList<Resource> resourceLinkedList = new LinkedList<>();
        resourceLinkedList = owner.lendResourceToSubsystem3(neededR1,neededR2);
        for (Resource resource: resourceLinkedList){
            if (resource.type() == 1){
                resource1.add(resource);
            }else {
                resource2.add(resource);
            }
        }
        R1Remain += resource1.size();
        R2Remain += resource2.size();
    }

    @Override
    protected void checkForNewProcesses() {
        while (true) {
            if (notArrivedQueue.isEmpty()) {
                break;
            }
            ProcessSubSystem3 temp = notArrivedQueue.peek();
            if (temp.startTime == owner.time) {
                notArrivedQueue.poll();
                subSystem3ReadyQueue.addProcess(temp);
            } else {
                break;
            }
        }
    }


    private String readyQueueString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ready Queue: [");
        PriorityQueue<ProcessSubSystem3> temp = subSystem3ReadyQueue.cloneQueue();
        boolean first = true;
        while (!temp.isEmpty()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            ProcessSubSystem3 process = temp.poll();
            sb.append(process.getName());
        }
        sb.append("]\n");
        return sb.toString();
    }

    @Override
    protected void reportToMainSystem() {
        if (dontSendReport) {
            dummyReport();
            return;
        }
        if (isOverClocked){
            owner.message[systemIndex].append("Sub3 at sub time ").append(subTimeUnit).append(":\n");
        }
        else {
            owner.message[systemIndex].append("Sub3:\n");
        }
        owner.message[systemIndex].append("    Resources: R1: ").append(R1Remain)
                .append(", R2: ").append(R2Remain).append("\n   Ready Queue:")
                .append(readyQueueString());
        for (int i = 0; i < CORE_COUNT; i++) {
            owner.message[systemIndex].append(message[i].toString()).append("\n");
        }
    }

    // this function is called when a task is finished and checks if we can lend back additional resources. if we can,
    // it would return the resources.
    public void taskIsFinished(ProcessSubSystem3 process) {
        sortedByR1.remove(process);
        sortedByR2.remove(process);
        LinkedList<Resource> lendBackResources = new LinkedList<>();
        if (!resource1.isEmpty()) {
            int newMaxR1;
            if (sortedByR1.isEmpty()) {
                newMaxR1 = originalR1;
            }
            else {
                newMaxR1 = sortedByR1.get(0).maxR1;
            }
            if (maxR1 > newMaxR1) {
                for (; newMaxR1 != maxR1; maxR1--) {
                    if (resource1.isEmpty()) {
                        break;
                    }
                    lendBackResources.add(resource1.removeFirst());
                    R1Remain--;
                }
            }
        }
        if (!resource2.isEmpty()) {
            int newMaxR2;
            if (sortedByR2.isEmpty()) {
                newMaxR2 = originalR2;
            }
            else {
                newMaxR2 = sortedByR2.get(0).maxR2;
            }
            if (maxR2 > newMaxR2) {
                for (; newMaxR2 != maxR2; maxR2--) {
                    if (resource2.isEmpty()) {
                        break;
                    }
                    lendBackResources.add(resource2.removeFirst());
                    R2Remain--;
                }
            }
        }
        if (!lendBackResources.isEmpty()) {
            owner.lendBackResource(lendBackResources);
        }
        if (resource2.isEmpty() && resource1.isEmpty()) {
            isOverClocked = false;
        }
    }

    @Override
    protected void runATimeUnitBody() throws InterruptedException {
        owner.message[systemIndex].setLength(0);
        if (isOverClocked){
            subTimeUnit = 1;
            letCoresRunOnePhase();
            reportToMainSystem();
            subTimeUnit = 2;
            letCoresRunOnePhase();
        } else {
            subTimeUnit = 0;
        }
        letCoresRunOnePhase();
        reportToMainSystem();
        letCoresRunOnePhase();
    }

    @Override
    protected boolean isSystemFinished() {
        return (subSystem3ReadyQueue.isEmpty() && notArrivedQueue.isEmpty() && core[0].getCoreState() == SystemCore.CORE_STATE_IDLE ) ;
    }

    @Override
    public void run() { //TODO: move this function after implementation to runLoop, like other systems
        runLoop(core);
    }
}
