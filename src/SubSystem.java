import Exceptions.NotEnoughResourcesException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public abstract class SubSystem extends Thread{
    protected Integer R1Remain;
    protected Integer R2Remain;

    protected MainSystem owner;

    protected int systemState;
    protected int systemIndex;

    public Semaphore[] subSystemWait;
    public Semaphore[] coreThreadWait;

    public StringBuilder[] message;
    public final int CORE_COUNT;

    protected final ArrayList<Process> notArrivedProcesses;


    public Process getProcessWithName(String name){
        for (Process process: notArrivedProcesses){
            if (process.getName().equals(name)){
                return process;
            }
        }
        return null;
    }
    protected final boolean dontSendReport;


    public static final int STATE_RUNNING = 1;
    public static final int STATE_FINISHED = 2;
    public static final int STATE_FINISH_REGISTERED = 3;
    public static final int STATE_STOPPED = 4;

    public int getR1Remain() {
        return R1Remain;
    }

    public int getR2Remain() {
        return R2Remain;
    }

    public int getSystemState() {
        return systemState;
    }

    public void setSystemState(int systemState) {
        this.systemState = systemState;
    }

    public void setOwner(MainSystem instance) {
        owner = instance;
    }

    public SubSystem(int intR1Remain, int intR2Remain, Process[] processes, int coresCount, boolean dontSendReport, int systemIndex) {
        this.R1Remain = intR1Remain;
        this.R2Remain = intR2Remain;
        CORE_COUNT = coresCount;
        this.dontSendReport = dontSendReport;
        systemState = STATE_RUNNING;
        notArrivedProcesses = new ArrayList<>(Arrays.asList(processes));
        notArrivedProcesses.sort(Comparator.comparing(process -> process.startTime));
        subSystemWait = new Semaphore[CORE_COUNT];
        coreThreadWait = new Semaphore[CORE_COUNT];
        message = new StringBuilder[CORE_COUNT];
        this.systemIndex = systemIndex;
        setName("SubSystem " + (systemIndex+1) + " Thread");
        for (int i = 0; i < CORE_COUNT; i++) {
            subSystemWait[i] = new Semaphore(0);
            coreThreadWait[i] = new Semaphore(0);
            message[i] = new StringBuilder();
        }
    }

    public void allocate(Process process) throws NotEnoughResourcesException {
        synchronized (this) {
            int newR1Remain = R1Remain - process.getMaxR1();
            int newR2Remain = R2Remain - process.getMaxR2();
            if (newR1Remain < 0 || newR2Remain < 0) {
                throw new NotEnoughResourcesException("task cannot be allocated");
            }
            R1Remain = newR1Remain;
            R2Remain = newR2Remain;
        }
    }

    public void deallocate(Process process) {
        synchronized (this) {
            R1Remain += process.getMaxR1();
            R2Remain += process.getMaxR2();
        }
    }

    // this function will check if process can be allocated and if it can be, it would allocate it.
    public boolean checkAndAllocate(Process process) {
        synchronized (this) {
            if (R1Remain >= process.getMaxR1() && R2Remain >= process.getMaxR2()) {
                R1Remain -= process.getMaxR1();
                R2Remain -= process.getMaxR2();
                return true;
            }
            return false;
        }
    }

    public boolean checkEnoughResource(Process process){
        boolean out = false;
        synchronized (this){
            if (R1Remain >= process.getMaxR1() && R2Remain >= process.getMaxR2()){
                out = true;
            }
        }
        return out;
    }

    // is called when cores are not running so no synchronization is required
    public Optional<Resource> lendResource(int resource) {
        if (resource == 1) {
            if (R1Remain > 0) {
                R1Remain--;
                return Optional.of(new Resource(1, systemIndex));
            }
        }
        else {
            if (R2Remain > 0) {
                R2Remain--;
                return Optional.of(new Resource(2, systemIndex));
            }
        }
        return Optional.empty();
    }

    public void getResourceBack(int resource) {
        synchronized (this) {
            if (resource == 1) {
                R1Remain++;
            }
            else {
                R2Remain++;
            }
        }
    }

    protected void dummyReport() {
        owner.message[systemIndex].append("Sub").append(systemIndex+1);
    }

    protected void signalAllThreads() {
        for (Semaphore semaphore : coreThreadWait) {
            semaphore.release();
        }
    }

    protected void acquireFromThreads() throws InterruptedException {
        for (Semaphore semaphore : subSystemWait) {
            semaphore.acquire();
        }
    }

    protected void letCoresRunOnePhase() throws InterruptedException {
        signalAllThreads();
        acquireFromThreads();
    }

    protected void runLoop(SystemCore[] cores) {
        try {
            for (SystemCore core : cores) {
                core.start();
            }
            while (true) {
                // phase 1: preparing for running
                checkForNewProcesses();
                runATimeUnitBody();
                // final phase: subsystem checks if is finished
                if (isSystemFinished()) {
                    systemState = STATE_FINISHED;
                    owner.mainThreadWait[systemIndex].release();
                    break;
                }
                // subsystem will let main know their time unit is finished
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            // system task is finished here
            for (int i = 0; i < CORE_COUNT; i++) {
                cores[i].setFinished();
                coreThreadWait[i].release();
            }
            while(true) {
                owner.subSystemWait[systemIndex].acquire();
                if (systemState == STATE_STOPPED) {
                    //System.out.println("System stopped");
                    break;
                }
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub").append(systemIndex + 1).append(" is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void checkForNewProcesses();
    protected abstract void reportToMainSystem();
    protected abstract void runATimeUnitBody() throws InterruptedException;
    protected abstract boolean isSystemFinished();
}
