package com.app.entity;

import java.util.Arrays;

public enum Faculty {

    TelecommunicationSystems(1),
    InformationTechnologies(2),
    ManagementSystemsCombatApplying(3);


    private final Integer number;
    Faculty(Integer number){
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }

    public static Faculty getByNumber(int number){
        return Arrays.stream(Faculty.values())
                .filter(i -> i.getNumber() == number)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Faculty has not been found!")
                );
    }

    @Override
    public String toString() {
        return "Faculty{" +
                "number=" + number +
                '}';
    }
}
