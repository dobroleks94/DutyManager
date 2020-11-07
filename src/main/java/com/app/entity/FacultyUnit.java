package com.app.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FacultyUnit {

    private int unitNumber;


    private int generalCadetCount, onLeaveCount,
            vacationsCount,detachedCount, otherAbsentCount;

    private List<String> dutyCadets, illCadets, hospitalLocatedCadets, hospitalVisitCadets,
                            onLeaveCadets, vacationsCadets, detachedCadets;
    private Map<String, Integer> otherAbsentCadets;

    private FacultyUnitWomen women;


    public FacultyUnit(){
        this.dutyCadets = new ArrayList<>();
        this.illCadets = new ArrayList<>();
        this.hospitalLocatedCadets = new ArrayList<>();
        this.hospitalVisitCadets = new ArrayList<>();
        this.onLeaveCadets = new ArrayList<>();
        this.vacationsCadets = new ArrayList<>();
        this.detachedCadets = new ArrayList<>();
        this.otherAbsentCadets = new LinkedHashMap<>();
    }
    public FacultyUnit(int unitNumber){
        this();
        this.unitNumber = unitNumber;
    }

    public int getGeneralCadetCount() {
        return generalCadetCount;
    }
    public void setGeneralCadetCount(int generalCadetCount) {
        this.generalCadetCount = generalCadetCount;
    }


    public int getUnitNumber() {
        return unitNumber;
    }

    public void setDutyCadets(List<String> dutyCadets) {
        this.dutyCadets = dutyCadets;
    }
    public void setIllCadets(List<String> illCadets) {
        this.illCadets = illCadets;
    }
    public void setHospitalLocatedCadets(List<String> hospitalLocatedCadets) {
        this.hospitalLocatedCadets = hospitalLocatedCadets;
    }
    public void setHospitalVisitCadets(List<String> hospitalVisitCadets) {
        this.hospitalVisitCadets = hospitalVisitCadets;
    }
    public void setOnLeaveCadets(List<String> onLeaveCadets) {
        this.onLeaveCadets = onLeaveCadets;
    }
    public void setVacationsCadets(List<String> vacationsCadets) {
        this.vacationsCadets = vacationsCadets;
    }
    public void setDetachedCadets(List<String> detachedCadets) {
        this.detachedCadets = detachedCadets;
    }
    public void setOtherAbsentCadets(Map<String, Integer> otherAbsentCadets) { this.otherAbsentCadets = otherAbsentCadets; }

    public List<String> getDutyCadets() {
        return dutyCadets;
    }
    public List<String> getIllCadets() {
        return illCadets;
    }
    public List<String> getHospitalLocatedCadets() {
        return hospitalLocatedCadets;
    }
    public List<String> getHospitalVisitCadets() {
        return hospitalVisitCadets;
    }
    public List<String> getOnLeaveCadets() {
        return onLeaveCadets;
    }
    public List<String> getVacationsCadets() {
        return vacationsCadets;
    }
    public List<String> getDetachedCadets() {
        return detachedCadets;
    }
    public Map<String, Integer> getOtherAbsentCadets() {
        return otherAbsentCadets;
    }

    public int getOnLeaveCount() {
        return onLeaveCount;
    }
    public void setOnLeaveCount(int onLeaveCount) {
        this.onLeaveCount = onLeaveCount;
    }

    public int getVacationsCount() {
        return vacationsCount;
    }
    public void setVacationsCount(int vacationsCount) {
        this.vacationsCount = vacationsCount;
    }

    public int getDetachedCount() {
        return detachedCount;
    }
    public void setDetachedCount(int detachedCount) {
        this.detachedCount = detachedCount;
    }

    public int getOtherAbsentCount() {
        return otherAbsentCount;
    }
    public void setOtherAbsentCount(int otherAbsentCount) {
        this.otherAbsentCount = otherAbsentCount;
    }

    public FacultyUnitWomen getWomen() {
        return women;
    }
    public void setWomen(FacultyUnitWomen women) {
        this.women = women;
    }
}
