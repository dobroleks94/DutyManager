package com.app.entity;

public class Officer {

    private String rank;
    private String fullName;


    public Officer(){}
    public Officer(String rank, String fullName) {
        this.rank = rank;
        this.fullName = fullName;
    }

    public String getRank() {
        return rank;
    }
    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return "Officer{" +
                "rank='" + rank + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
