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

    public SubSystem3ReadyQueue subSystem3ReadyQueue;
    private LinkedList<Resource> resource1;
    private LinkedList<Resource> resource2;

    private boolean isOverClocked = true;

    public SubSystem3(int intR1Remain, int intR2Remain, Process[] processes, int sys3CoresCount, boolean doNotSendReport) {
        super(intR1Remain, intR2Remain, processes, sys3CoresCount, doNotSendReport, 2);
        sortProcessesByResources(processes);
        resource1 = new LinkedList<>();
        resource2 = new LinkedList<>();
        subSystem3ReadyQueue = new SubSystem3ReadyQueue();
        core = new System3Core[1];
        core[0] = new System3Core(0,this);
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
    }
    @Override
    protected void checkForNewProcesses() {
        ArrayList<Process> newProcesses = new ArrayList<>();
        for (Process notArrivedProcess : notArrivedProcesses){
            if (notArrivedProcess.startTime == owner.time){
                newProcesses.add(notArrivedProcess);
            }else break;
        }
        for (Process newProcess : newProcesses){
            notArrivedProcesses.remove(newProcess);
            subSystem3ReadyQueue.addProcess((ProcessSubSystem3) newProcess);
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
        owner.message[systemIndex].setLength(0);
        if (dontSendReport) {
            dummyReport();
            return;
        }
        owner.message[systemIndex].append("Sub3:\n")
                .append("    Resources: R1: ").append(R1Remain)
                .append(", R2: ").append(R2Remain).append("\n   Ready Queue:")
                .append(readyQueueString());
        for (int i = 0; i < CORE_COUNT; i++) {
            owner.message[systemIndex].append(message[i].toString()).append("\n");
        }
    }

    @Override
    protected void runATimeUnitBody() throws InterruptedException {
        if (isOverClocked){

        }
    }

    @Override
    protected boolean isSystemFinished() {
        return (subSystem3ReadyQueue.isEmpty() && notArrivedProcesses.isEmpty() && core[0].getCoreState() == SystemCore.CORE_STATE_IDLE ) ;
    }

    @Override
    public void run() { //TODO: move this function after implementation to runLoop, like other systems
        runLoop(core);
    }
}
