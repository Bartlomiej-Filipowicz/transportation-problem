package controller;

import model.Task;

import java.util.*;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;

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
    private ArrayList<ArrayList<Integer>> mycosts;

    //new below
    private static int[] demand;
    private static int[] supply;
    private static double[][] costs;
    private static Shipment[][] matrix;

    private static class Shipment {
        final double costPerUnit;
        final int r, c;
        double quantity;

        public Shipment(double q, double cpu, int r, int c) {
            quantity = q;
            costPerUnit = cpu;
            this.r = r;
            this.c = c;
        }
    }

    static void init(ArrayList<Integer> podaz, ArrayList<Integer> popyt, ArrayList<ArrayList<Integer>> mcosts) {


            supply = podaz.stream().mapToInt(i -> i).toArray();
            demand = popyt.stream().mapToInt(i -> i).toArray();

            costs = new double[supply.length][demand.length];
            int[][] intcosts = mcosts.stream().map(  u  ->  u.stream().mapToInt(i->i).toArray()  ).toArray(int[][]::new);
            for(int i = 0; i < intcosts.length; i++)
            {
                for(int j = 0; j < intcosts[0].length; j++)
                    costs[i][j] = (double) intcosts[i][j];
            }

            matrix = new Shipment[supply.length][demand.length];

    }

    static void northWestCornerRule() {

        for (int r = 0, northwest = 0; r < supply.length; r++)
            for (int c = northwest; c < demand.length; c++) {

                int quantity = Math.min(supply[r], demand[c]);
                if (quantity > 0) {
                    matrix[r][c] = new Shipment(quantity, costs[r][c], r, c);

                    supply[r] -= quantity;
                    demand[c] -= quantity;

                    if (supply[r] == 0) {
                        northwest = c;
                        break;
                    }
                }
            }
    }

    static void steppingStone() {
        double maxReduction = 0;
        Shipment[] move = null;
        Shipment leaving = null;

        fixDegenerateCase();

        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {

                if (matrix[r][c] != null)
                    continue;

                Shipment trial = new Shipment(0, costs[r][c], r, c);
                Shipment[] path = getClosedPath(trial);

                double reduction = 0;
                double lowestQuantity = Integer.MAX_VALUE;
                Shipment leavingCandidate = null;

                boolean plus = true;
                for (Shipment s : path) {
                    if (plus) {
                        reduction += s.costPerUnit;
                    } else {
                        reduction -= s.costPerUnit;
                        if (s.quantity < lowestQuantity) {
                            leavingCandidate = s;
                            lowestQuantity = s.quantity;
                        }
                    }
                    plus = !plus;
                }
                if (reduction < maxReduction) {
                    move = path;
                    leaving = leavingCandidate;
                    maxReduction = reduction;
                }
            }
        }

        if (move != null) {
            double q = leaving.quantity;
            boolean plus = true;
            for (Shipment s : move) {
                s.quantity += plus ? q : -q;
                matrix[s.r][s.c] = s.quantity == 0 ? null : s;
                plus = !plus;
            }
            steppingStone();
        }
    }

    static LinkedList<Shipment> matrixToList() {
        return stream(matrix)
                .flatMap(row -> stream(row))
                .filter(s -> s != null)
                .collect(toCollection(LinkedList::new));
    }

    static Shipment[] getClosedPath(Shipment s) {
        LinkedList<Shipment> path = matrixToList();
        path.addFirst(s);

        // remove (and keep removing) elements that do not have a
        // vertical AND horizontal neighbor
        while (path.removeIf(e -> {
            Shipment[] nbrs = getNeighbors(e, path);
            return nbrs[0] == null || nbrs[1] == null;
        }));

        // place the remaining elements in the correct plus-minus order
        Shipment[] stones = path.toArray(new Shipment[path.size()]);
        Shipment prev = s;
        for (int i = 0; i < stones.length; i++) {
            stones[i] = prev;
            prev = getNeighbors(prev, path)[i % 2];
        }
        return stones;
    }

    static Shipment[] getNeighbors(Shipment s, LinkedList<Shipment> lst) {
        Shipment[] nbrs = new Shipment[2];
        for (Shipment o : lst) {
            if (o != s) {
                if (o.r == s.r && nbrs[0] == null)
                    nbrs[0] = o;
                else if (o.c == s.c && nbrs[1] == null)
                    nbrs[1] = o;
                if (nbrs[0] != null && nbrs[1] != null)
                    break;
            }
        }
        return nbrs;
    }

    static void fixDegenerateCase() {
        final double eps = Double.MIN_VALUE;

        if (supply.length + demand.length - 1 != matrixToList().size()) {

            for (int r = 0; r < supply.length; r++)
                for (int c = 0; c < demand.length; c++) {
                    if (matrix[r][c] == null) {
                        Shipment dummy = new Shipment(eps, costs[r][c], r, c);
                        if (getClosedPath(dummy).length == 0) {
                            matrix[r][c] = dummy;
                            return;
                        }
                    }
                }
        }
    }

    static int[][] printResult(String filename) {
        System.out.printf("Optimal solution %s%n%n", filename);
        double totalCosts = 0;
        int[][] result = new int[supply.length+1][demand.length]; // +1 coz the array stores totalCost as well

        for (int r = 0; r < supply.length; r++) {
            for (int c = 0; c < demand.length; c++) {

                Shipment s = matrix[r][c];
                if (s != null && s.r == r && s.c == c) {
                    System.out.printf(" %3s ", (int) s.quantity);
                    result[r][c] = (int) s.quantity;
                    totalCosts += (s.quantity * s.costPerUnit);
                } else{
                    System.out.printf("  -  ");
                    result[r][c] = 0;
                }

            }
            System.out.println();
        }
        System.out.printf("%nTotal costs: %s%n%n", totalCosts);
        result[supply.length][0] = (int) totalCosts;
        return result;
    }

    static void algTP() {
        double prevTotalCosts = -1;
        double totalCosts = 0;

        while(prevTotalCosts != totalCosts){

            steppingStone();

            for (int r = 0; r < supply.length; r++) {
                for (int c = 0; c < demand.length; c++) {

                    Shipment s = matrix[r][c];
                    if (s != null && s.r == r && s.c == c) {
                        //System.out.printf(" %3s ", (int) s.quantity);
                        totalCosts += (s.quantity * s.costPerUnit);
                    } //else
                        //System.out.printf("  -  ");
                }
                //System.out.println();
            }
            //System.out.printf("%nTotal costs: %s%n%n", totalCosts);
            if(totalCosts != prevTotalCosts){
                prevTotalCosts = totalCosts;
                totalCosts = 0;
            }
        }


    }

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

    public CPMAlgorithm update(ArrayList<Integer> podaz, ArrayList<Integer> popyt, ArrayList<ArrayList<Integer>> costs) {
        this.podaz = podaz;
        this.popyt = popyt;
        this.mycosts = costs;
        init(podaz,popyt,costs);
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


    public int[][] runTP() {

        northWestCornerRule();
        algTP(); // !!!!!! experiment with number of calling steppingStone()
        return printResult("myfile.txt");

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