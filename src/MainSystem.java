import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class MainSystem {
    SubSystem[] subSystems;
    public Semaphore[] mainThreadWait;
    public Semaphore[] subSystemWait;
    public StringBuilder[] message;


    public static final int SUBSYSTEM_COUNT = 4;
    public int time;


    public MainSystem(SubSystem[] subSystems){
        time = 0;
        this.subSystems = subSystems;
        mainThreadWait = new Semaphore[SUBSYSTEM_COUNT];
        subSystemWait = new Semaphore[SUBSYSTEM_COUNT];
        message = new StringBuilder[SUBSYSTEM_COUNT];
        for (int i = 0; i < subSystemWait.length; i++) {
            mainThreadWait[i] = new Semaphore(0);
            subSystemWait[i] = new Semaphore(0);
            message[i] = new StringBuilder();
        }
    }

    public LinkedList<Resource> lendResourceToSubsystem3(int neededR1, int neededR2) {
        LinkedList<Resource> out = new LinkedList<>();
        int i = 2;
        for (;neededR1 != 0 ; i = (i+1) % SUBSYSTEM_COUNT) {
            if (i == 2) continue;
            Optional<Resource> temp = subSystems[i].lendResource(1);
            if (temp.isPresent()) {
                out.add(temp.get());
                neededR1--;
            }
        }
        for (;neededR2 != 0 ; i = (i+1) % SUBSYSTEM_COUNT) {
            if (i == 2) continue;
            Optional<Resource> temp = subSystems[i].lendResource(2);
            if (temp.isPresent()) {
                out.add(temp.get());
                neededR2--;
            }
        }
        return out;
    }

    public void lendBackResource(LinkedList<Resource> resources) {
        for (Resource r : resources) {
            subSystems[r.systemIndex()].getResourceBack(r.type());
        }
    }

    public void start() throws InterruptedException {
        int activeSubsystems = subSystems.length;
        // threads (subsystems) starts to run
        for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
            subSystems[i].start();
        }
        while (activeSubsystems != 0) {
            for (Semaphore value : mainThreadWait) {
                value.acquire();
            }
            System.out.println("--------------------");
            System.out.println("Time: " + time);
            for (StringBuilder stringBuilder : message) {
                System.out.println(stringBuilder);
            }
            time++;
            for (SubSystem subSystem : subSystems) {
                if (subSystem.systemState == SubSystem.STATE_FINISHED) {
                    activeSubsystems--;
                    subSystem.setSystemState(SubSystem.STATE_FINISH_REGISTERED);
                }
            }
            for (Semaphore semaphore : subSystemWait) {
                semaphore.release();
            }
        }
        // stopping threads
        for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
            System.out.println("Sub" + (i+1) + " Resources: R1=" + subSystems[i].getR1Remain() +
                    " R2=" + subSystems[i].getR2Remain());
            subSystems[i].setSystemState(SubSystem.STATE_STOPPED);
            subSystemWait[i].release();
        }
        System.out.println("all systems are finished");
    }
}
