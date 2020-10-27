package com.app.entity;

import java.util.List;

public class JsonGenEntity {
    private FacultyDutyInfo dutyInformation;
    private List<FacultyUnit> facultyUnits;

    public JsonGenEntity(){}

    public JsonGenEntity(FacultyDutyInfo dutyInformation, List<FacultyUnit> facultyUnits) {
        this.dutyInformation = dutyInformation;
        this.facultyUnits = facultyUnits;
    }

    public FacultyDutyInfo getDutyInformation() {
        return dutyInformation;
    }

    public List<FacultyUnit> getFacultyUnits() {
        return facultyUnits;
    }

    @Override
    public String toString() {
        return "JsonGenEntity{" +
                "dutyInformation=" + dutyInformation +
                ", facultyUnits=" + facultyUnits +
                '}';
    }
}
