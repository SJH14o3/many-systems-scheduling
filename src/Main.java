import java.util.ArrayList;
import java.util.Scanner;

public class Main {


    public static final int systemCount = 4;
    public static final int Sys1CoresCount = 3;
    public static final int Sys2CoresCount = 2;
    public static final int Sys3CoresCount = 1;
    public static final int Sys4CoresCount = 2;
    public static void main(String[] args) {
        ArrayList<ProcessSubSystem1> subSystem1Processes = new ArrayList<>();
        ArrayList<ProcessSubSystem2> subSystem2Processes = new ArrayList<>();
        ArrayList<ProcessSubSystem3> subSystem3Processes = new ArrayList<>();
        ArrayList<ProcessSubSystem4> subSystem4Processes = new ArrayList<>();
        SubSystem1 system1;
        SubSystem2 system2;
        SubSystem3 system3;
        SubSystem4 system4;
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
                  case 3:
                      subSystem4Processes.add(ProcessSubSystem4.createProcessWithString(line));
                }
            }
        }
        system1 = new SubSystem1(resources[0][0], resources[0][1], subSystem1Processes.toArray(new ProcessSubSystem1[0]), Sys1CoresCount, true);
        system2 = new SubSystem2(resources[1][0], resources[1][1], subSystem2Processes.toArray(new ProcessSubSystem2[0]), Sys2CoresCount, true);
        system3 = new SubSystem3(resources[2][0], resources[2][1], subSystem3Processes.toArray(new ProcessSubSystem3[0]), Sys3CoresCount, true);
        system4 = new SubSystem4(resources[3][0], resources[3][1], subSystem4Processes.toArray(new ProcessSubSystem4[0]), Sys4CoresCount, false);
        SubSystem[] subSystems = {system1, system2, system3, system4};
        MainSystem mainSystem = new MainSystem(subSystems);
        for (SubSystem subSystem : subSystems) {
            subSystem.setOwner(mainSystem);
        }
        try {
            mainSystem.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}