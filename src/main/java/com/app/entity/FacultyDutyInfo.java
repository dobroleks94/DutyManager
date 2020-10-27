package com.app.entity;

public class FacultyDutyInfo {

    private String dutyTime;
    private String dutyDate;

    private int facultyNum;

    private static Faculty faculty;

    private Officer dutyPerson;


    public FacultyDutyInfo(){}
    public String getDutyTime() {
        return dutyTime;
    }
    public void setDutyTime(String dutyTime) {
        this.dutyTime = dutyTime;
    }

    public String getDutyDate() {
        return dutyDate;
    }
    public void setDutyDate(String dutyDate) {
        this.dutyDate = dutyDate;
    }

    public static Faculty getFaculty() {
        return faculty;
    }
    public void setFaculty(Faculty unit) {
        faculty = unit;
    }

    public Officer getDutyPerson() {
        return dutyPerson;
    }
    public void setDutyPerson(Officer dutyPerson) {
        this.dutyPerson = dutyPerson;
    }

    public int getFacultyNum() {
        return facultyNum;
    }
    public void setFacultyNum(int facultyNum) {
        this.facultyNum = facultyNum;
    }

    @Override
    public String toString() {
        return "FacultyDutyInfo{" +
                "dutyTime='" + dutyTime + '\'' +
                ", dutyDate='" + dutyDate + '\'' +
                ", faculty=" + faculty +
                ", dutyPerson=" + dutyPerson +
                '}';
    }
}
