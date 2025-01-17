import java.sql.SQLOutput;
import java.util.Scanner;

public final class SubSystem1 extends SubSystem {

    public ProcessSubSystem1[] processSubSystem1;

    private void setQuantums(){
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < processSubSystem1.length; i++) {
            if (processSubSystem1[i].burstTime > max){
                max = processSubSystem1[i].burstTime;
            }
            if (processSubSystem1[i].burstTime < min){
                min = processSubSystem1[i].burstTime;
            }
        }
        int maxQ=5, minQ=1;
        for (int i = 0; i < processSubSystem1.length; i++) {
            if (max == min) {
                processSubSystem1[i].setQuantum(maxQ); // If all burst times are equal, assign max quantum
            } else {
                processSubSystem1[i].setQuantum((int) Math.round(minQ + ((double)(processSubSystem1[i].burstTime - min) / (max - min)) * (maxQ - minQ)));
            }
        }
    }

    public SubSystem1(int intR1Remain, int intR2Remain, ProcessSubSystem1[] processSubSystem1) {
        super(intR1Remain, intR2Remain);
        this.processSubSystem1 = processSubSystem1;
        systemIndex = 0;
        setQuantums();
        printQs();
    }

    public void printQs(){
        for (int i = 0; i < processSubSystem1.length; i++) {
            System.out.println(processSubSystem1[i].getName() + ":" + processSubSystem1[i].getQuantum());
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                 //TODO: remove with actual job
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub1:");
                if (owner.time == 2) { //TODO: replace with actual finish statement
                    systemState = STATE_FINISHED;
                    owner.mainThreadWait[systemIndex].release();
                    break;
                };
                owner.mainThreadWait[systemIndex].release();
                owner.subSystemWait[systemIndex].acquire();
            }
            //TODO: task is finished here
            while(true) {
                owner.subSystemWait[systemIndex].acquire();
                owner.message[systemIndex].setLength(0);
                owner.message[systemIndex].append("Sub1 is finished");
                owner.mainThreadWait[systemIndex].release();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
