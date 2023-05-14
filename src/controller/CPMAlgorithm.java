package controller;

import model.Task;

import java.util.*;

public class CPMAlgorithm {
    private int numTasks;
    private List<Task> tasks;
    private int[] earliestStart;
    private int[] earliestFinish;
    private int[] latestStart;
    private int[] latestFinish;
    private int time;
    private String criticalPath;

    private ArrayList<Integer> popyt;
    private ArrayList<Integer> podaz;

    public CPMAlgorithm() {
        /*
        this.earliestStart = new int[numTasks+1]; // to wpisuje
        this.earliestFinish = new int[numTasks+1]; // to co wyzej + czas trwania czynnosci
        this.latestStart = new int[numTasks+1];
        this.latestFinish = new int[numTasks+1];
        this.time = -1;
        this.criticalPath = "";
         */
    }

    public CPMAlgorithm update(ArrayList<Integer> podaz, ArrayList<Integer> popyt) {
        this.podaz = podaz;
        this.popyt = popyt;
        /*
        this.numTasks = numTasks;
        this.tasks = tasks; // LISTA zadan
        this.earliestStart = new int[numTasks+1]; // to wpisuje
        this.earliestFinish = new int[numTasks+1]; // to co wyzej + czas trwania czynnosci
        this.latestStart = new int[numTasks+1];
        this.latestFinish = new int[numTasks+1];
        this.time = -1;
        this.criticalPath = "";
        */
        return this;
    }


    public void runTP() {

        int rowNum = podaz.size();
        int colNum = popyt.size();
        int minim,colStart = 0;
        ArrayList<ArrayList<Integer>> northWestTable = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < rowNum; i++){
            northWestTable.add(new ArrayList<Integer>());
            for(int j = 0; j < colNum; j++) northWestTable.get(i).add(0);
        }

        for(int i = 0; i < rowNum; i++){
            for(int j = colStart; j < colNum; j++){
                minim = Math.min(podaz.get(i),popyt.get(j));
                podaz.set(i,podaz.get(i)-minim);
                popyt.set(j,popyt.get(j)-minim);
                northWestTable.get(i).set(j,minim);

                if(podaz.get(i) == 0){
                    colStart = j;
                    break;
                }
            }
        }
        System.out.println(northWestTable);
        /*
        // Step 0: check if there are nodes/activities with 0 next
        if (!tasks.get(tasks.size()-1).getName().equals("")) {
            tasks.add(new Task("", 0));
            numTasks++;
        }
//        System.out.println("size==========" +tasks.size());
//        for (var elem : tasks) {
//            System.out.println(elem.getName());
//        }
        for (int i = 0; i < numTasks-1; i++) {
            if (tasks.get(i).getDependents().isEmpty()) {
                // System.out.println("Last " + tasks.get(i).getName());
                int intValue = tasks.get(i).getName().charAt(0) - 65;
                tasks.get(tasks.size()-1).addDependency(intValue);
                tasks.get(intValue).addDependent(tasks.size()-1);
            }
        }

        //System.out.println("Ostatni: " + tasks.get(tasks.size()-1).getDependencies().size());

        // Step 1: Find the earliest start and finish time for each task
        for (int i = 0; i < numTasks; i++) {
            Task task = tasks.get(i);
            if (task.getDependencies().isEmpty()) { // pierwsze zadanie - brak poprzednikow
                earliestStart[i] = 0;
            } else { // ma poprzednikow
                int maxFinishTime = -1; // najpozniejszy czas zakonczenia kazdego z poprzednikow
                for (int dependency : task.getDependencies()) { // iteruje po poprzednikach
                    maxFinishTime = Math.max(maxFinishTime, earliestFinish[dependency]); // jesli jakis z poprzednikow ma dluzszy najkrotszy czas wykonania to zamien
                }
                earliestStart[i] = maxFinishTime;
            }
            earliestFinish[i] = earliestStart[i] + task.getTime();
        }

        // Step 2: Find the latest start and finish time for each task
        // licze dla ostatniego - dziala tylko jak jest jedna czynnosc konczaca
        latestFinish[numTasks - 1] = earliestFinish[numTasks - 1];
        latestStart[numTasks - 1] = earliestStart[numTasks - 1];
        // licze dla wszytskich poza osttanim
        for (int i = numTasks - 2; i >= 0; i--) {
            Task task = tasks.get(i);
            if (task.getDependents().isEmpty()) { // jesli zawiera dependency na przedostatnia czynnosc (to ostatnia czynnosc
                System.out.println(i);
                latestFinish[i] = latestFinish[numTasks - 1];//???????????
            } else {
                int minStartTime = Integer.MAX_VALUE;
                for (int dependents : task.getDependents()) {
                    minStartTime = Math.min(minStartTime, latestStart[dependents] - task.getTime());
                }
                latestFinish[i] = minStartTime + task.getTime();
            }
            latestStart[i] = latestFinish[i] - task.getTime();
        }

        // Step 3: set parametres
        for (int i = 0; i < numTasks; i++) {
            if (earliestFinish[i] > this.time) {
                this.time = earliestFinish[i];
            }
            Task task = tasks.get(i);
            boolean isCritical = (earliestStart[i] == latestStart[i] && earliestFinish[i] == latestFinish[i]);
            if (isCritical) {
                this.criticalPath += task.getName() + " "; // change to string builder?
            }
        }
        return this.criticalPath;
        */
    }

    public void printResults() {
        System.out.println("Task\tTime\tEarliest Start\tEarliest Finish\tLatest Start\tLatest Finish\tCritical Path");
        for (int i = 0; i < numTasks; i++) {
            Task task = tasks.get(i);
            boolean isCritical = (earliestStart[i] == latestStart[i] && earliestFinish[i] == latestFinish[i]);
            System.out.printf("%s\t\t%d\t\t%d\t\t\t\t%d\t\t\t\t%d\t\t\t\t%d\t\t\t\t%s\n", task.getName(), task.getTime(), earliestStart[i], earliestFinish[i], latestStart[i], latestFinish[i], isCritical ? "Yes" : "No");
        }
    }

    public int getTime() {
        return time;
    }

    public String getCriticalPath() {
        return criticalPath;
    }
}