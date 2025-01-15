import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class MainSystem {
    SubSystem[] subSystems;
    public Semaphore[] mainThreadWait;
    public Semaphore[] subSystemWait;
    public StringBuilder[] message;

    public static final int SUBSYSTEM_COUNT = 3;
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

        public void start() throws InterruptedException {
            int activeSubsystems = subSystems.length;
            ArrayList<Integer> indexes = new ArrayList<>(subSystems.length);
            for (int i = 0; i < subSystems.length; i++) {
                indexes.add(i);
            }
            // threads (subsystems) starts to run
            for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                subSystems[i].start();
            }
            while (activeSubsystems != 0) {
                for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                    mainThreadWait[i].acquire();
                }
                //TODO: main system print output
                for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                    System.out.println(message[i]);
                }
                System.out.println("\n");
                time++;
                for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                    if (subSystems[i].systemState == SubSystem.STATE_FINISHED) {
                        activeSubsystems--;
                        subSystems[i].setSystemState(SubSystem.STATE_FINISH_REGISTERED);
                    }
                }
                for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                    subSystemWait[i].release();
                }
            }
            // stopping threads
            for (int i = 0; i < SUBSYSTEM_COUNT; i++) {
                subSystems[i].stop();
            }
            System.out.println("all systems are finished");
        }



}
