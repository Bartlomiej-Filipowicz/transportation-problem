package model;

import java.util.ArrayList;
import java.util.List;

public class Task {
    private String name;
    private int time;
    private List<Integer> dependencies; // trzyma inty (np 0 = czynnosc A) które sa poprzednikami
    private List<Integer> dependents; // trzyma nastepniki tej czynnosci

    public Task(String name, int time) {
        this.name = name;
        this.time = time;
        this.dependencies = new ArrayList<>();
        this.dependents = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public List<Integer> getDependencies() {
        return dependencies;
    }

    public List<Integer> getDependents() {
        return dependents;
    }

    public void addDependency(int taskIndex) {
        dependencies.add(taskIndex);
    }

    public int getDependency(int taskIndex) {
        return dependencies.get(taskIndex);
    }

    public void addDependent(int taskIndex) {
        dependents.add(taskIndex);
    }
}