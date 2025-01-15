import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {


    public static final int systemCount = 3;
    public static void main(String[] args) {
        ArrayList<ProcessSubSystem1> subSystem1Processes = new ArrayList<>();
        ArrayList<ProcessSubSystem2> subSystem2Processes = new ArrayList<>();
        ArrayList<ProcessSubSystem3> subSystem3Processes = new ArrayList<>();
        SubSystem1 system1;
        SubSystem2 system2;
        SubSystem3 system3;
        Scanner sc = new Scanner(System.in);
        int[][] resources = new int[systemCount][2];
        for (int i = 0; i < systemCount; i++) {
            resources[i][0] = sc.nextInt();
            resources[i][1] = sc.nextInt();
        }
        sc.nextLine();
        for (int i = 0; i < systemCount;) {
            String line = sc.nextLine();
            if (line.equals("$")) i++;
            else {
                switch (i) {
                  case 0:
                      subSystem1Processes.add(ProcessSubSystem1.createProcessWithString(line));
                      break;
                  case 1:
                      subSystem2Processes.add(ProcessSubSystem2.createProcessWithString(line));
                      break;
                  case 2:
                      subSystem3Processes.add(ProcessSubSystem3.createProcessWithString(line));
                      break;
                }
            }
        }
        system1 = new SubSystem1(resources[0][0], resources[0][1]);
        system2 = new SubSystem2(resources[1][0], resources[1][1], subSystem2Processes.toArray(new ProcessSubSystem2[0]));
        system3 = new SubSystem3(resources[2][0], resources[2][1]);
        SubSystem[] subSystems = {system1, system2, system3};
        MainSystem mainSystem = new MainSystem(subSystems);
        for (int i = 0; i < subSystems.length; i++) {
            subSystems[i].setOwner(mainSystem);
        }
        try {
            mainSystem.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}