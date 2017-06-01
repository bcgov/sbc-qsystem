package ru.apertum.qsky.web;

import java.util.ArrayList;

public class User {

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    private String name = "demo";
    private String password = "demo";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private ArrayList<Long> branches = new ArrayList<>();

    public ArrayList<Long> getBranches() {
        return branches;
    }

    public void setBranches(ArrayList<Long> branches) {
        this.branches = branches;
    }
    
    public void addBranch(Long branchId){
        branches.add(branchId);
    }

    public boolean permition(Long branchId) {
        return branches.isEmpty() || branches.contains(branchId);
    }

}
