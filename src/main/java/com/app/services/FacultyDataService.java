package com.app.services;

import com.app.entity.Faculty;
import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import com.app.entity.Officer;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.rmi.CORBA.Util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FacultyDataService {

    private static FacultyDutyInfo facultyDutyInfo;

    public static void updateFacultyNumber(ComboBox<Integer> facultyNumber) {

        facultyNumber.setValue(Faculty.InformationTechnologies.getNumber());
        facultyNumber.setItems(
                FXCollections.observableArrayList(
                        Arrays.stream(Faculty.values()).map(Faculty::getNumber).collect(Collectors.toList())
                )
        );
    }

    public static FacultyDutyInfo createNewDutyInfo() {
        FacultyDutyInfo empty = new FacultyDutyInfo();
        empty.setDutyPerson(new Officer());
        return empty;
    }


    public static void updateFacultyDutyInfo(FacultyDutyInfo info) {
        setFacultyDutyInfo(info);
    }

    public static void updateFacultyDutyInfo(int facultyNumber, String dutyTime, String dutyDate, Officer dutyOfficer, FacultyDutyInfo facultyDutyInfo) {

        Faculty faculty = Faculty.getByNumber(facultyNumber);

        facultyDutyInfo.setDutyDate(dutyDate);
        facultyDutyInfo.setDutyTime(dutyTime);
        facultyDutyInfo.setDutyPerson(dutyOfficer);
        facultyDutyInfo.setFaculty(faculty);
        facultyDutyInfo.setFacultyNum(FacultyDutyInfo.getFaculty().getNumber());

        setFacultyDutyInfo(facultyDutyInfo);
    }

    public static Officer specifyDutyOfficer(TextField rank, TextField fullName) {
        return new Officer(
                rank.getText(),
                fullName.getText()
        );
    }

    public static void fillFacultyUnits(ComboBox<Integer> eduUnits) {
        int facultyNum = FacultyDutyInfo.getFaculty().getNumber();
        int unitCount = 5;
        Integer currentYear = (LocalDateTime.now().isAfter(ChronoLocalDateTime.from(LocalDateTime.of(LocalDate.now().getYear(), 6, 30, 0, 0))))
                                ? LocalDateTime.now().getYear()
                                : LocalDateTime.now().getYear() - 1;

        List<Integer> facultyUnits = IntStream.iterate(unitCount - 1, i -> i - 1).limit(unitCount)
                .map(i -> Integer.parseInt(
                        String.valueOf(facultyNum).concat(
                                String.valueOf(currentYear - i).substring(currentYear.toString().length() - 1)
                        )
                ))
                .boxed()
                .collect(Collectors.toList());

        eduUnits.setItems(FXCollections.observableArrayList(facultyUnits));
        eduUnits.setValue(eduUnits.getItems().get(0));
    }

    public static FacultyUnit createFacultyUnit(Integer unitNumber) {
        return new FacultyUnit(unitNumber);
    }

    public static FacultyUnit updateFacultyUnit(FacultyUnit currentFacultyUnit, String... fields) {

        currentFacultyUnit.setGeneralCadetCount(Utils.tryParse(fields[0]) ? Integer.parseInt(fields[0]) : 0);
        currentFacultyUnit.setDutyCadets(Utils.getNamesFrom(fields[1]));
        currentFacultyUnit.setIllCadets(Utils.getNamesFrom(fields[2]));
        currentFacultyUnit.setHospitalLocatedCadets(Utils.getNamesFrom(fields[3]));
        currentFacultyUnit.setHospitalVisitCadets(Utils.getNamesFrom(fields[4]));

        if (!Utils.tryParse(fields[5])) {
            currentFacultyUnit.setOnLeaveCadets(Utils.getNamesFrom(fields[5]));
            currentFacultyUnit.setOnLeaveCount(currentFacultyUnit.getOnLeaveCadets().size());
        } else {
            currentFacultyUnit.setOnLeaveCount(Integer.parseInt(fields[5].trim()));
            currentFacultyUnit.setOnLeaveCadets(new ArrayList<>());
        }

        if (!Utils.tryParse(fields[6])) {
            currentFacultyUnit.setVacationsCadets(Utils.getNamesFrom(fields[6]));
            currentFacultyUnit.setVacationsCount(currentFacultyUnit.getVacationsCadets().size());
        } else {
            currentFacultyUnit.setVacationsCount(Integer.parseInt(fields[6].trim()));
            currentFacultyUnit.setVacationsCadets(new ArrayList<>());
        }

        if (!Utils.tryParse(fields[7])) {
            currentFacultyUnit.setDetachedCadets(Utils.getNamesFrom(fields[7]));
            currentFacultyUnit.setDetachedCount(currentFacultyUnit.getDetachedCadets().size());
        } else {
            currentFacultyUnit.setDetachedCount(Integer.parseInt(fields[7].trim()));
            currentFacultyUnit.setDetachedCadets(new ArrayList<>());
        }

        if (!Utils.tryParse(fields[8])) {

            currentFacultyUnit.setOtherAbsentCadets(Utils.getOtherCadetAbsenceInfo(Utils.getNamesFrom(fields[8])));
            currentFacultyUnit.setOtherAbsentCount(
                    currentFacultyUnit.getOtherAbsentCadets()
                            .values()
                            .stream()
                            .reduce(0, Integer::sum)
            );

        } else {
            currentFacultyUnit.setOtherAbsentCount(Integer.parseInt(fields[8].trim()));
            currentFacultyUnit.setOtherAbsentCadets(new LinkedHashMap<>());
        }

        return currentFacultyUnit;

    }


    public static FacultyDutyInfo getFacultyDutyInfo() {
        return facultyDutyInfo;
    }

    public static void setFacultyDutyInfo(FacultyDutyInfo facultyDutyInfo) {
        FacultyDataService.facultyDutyInfo = facultyDutyInfo;
    }

    public static String calculatePresentCadets(FacultyUnit unit) {
        return String.valueOf(
                unit.getGeneralCadetCount() - (unit.getDetachedCount() + unit.getVacationsCount() +
                        unit.getOnLeaveCount() + unit.getOtherAbsentCount() +
                        unit.getDutyCadets().size() + unit.getIllCadets().size() +
                        unit.getHospitalLocatedCadets().size() + unit.getHospitalVisitCadets().size())
        );
    }

    public static String getHospitalRepresentation(FacultyUnit unit, String action) {

        String returnedStatement = "";

        switch (action) {
            case "count":
                int layHosp = unit.getHospitalLocatedCadets().size();
                int goHosp = unit.getHospitalVisitCadets().size();

                returnedStatement = (goHosp == 0) ? String.valueOf(layHosp) : String.format("%d / %d", layHosp, goHosp);
                break;
            case "names":
                String layHospN = Utils.trimList(unit.getHospitalLocatedCadets());
                String goHospN = Utils.trimList(unit.getHospitalVisitCadets());

                returnedStatement = (goHospN.isEmpty()) ? layHospN : String.format("%s / %s", layHospN, goHospN);
                break;

        }
        return "0".equals(returnedStatement) ? "" : returnedStatement;
    }
    public static String getHospitalRepresentation(List<FacultyUnit> unit) {

        int layHospital = unit.stream().map(FacultyUnit::getHospitalLocatedCadets).map(List::size).reduce(0, Integer::sum);
        int goHospital = unit.stream().map(FacultyUnit::getHospitalVisitCadets).map(List::size).reduce(0, Integer::sum);

        String returnedStatement = (goHospital == 0) ? String.valueOf(layHospital) : String.format("%d / %d", layHospital, goHospital);

        return "0".equals(returnedStatement) ? "" : returnedStatement;
    }



    public static String getOtherCadetInfo(FacultyUnit unit) {
        StringBuilder sb = new StringBuilder();
        if (unit.getDetachedCount() > 0) sb.append("відр - ").append(unit.getDetachedCount()).append(";\n");
        unit.getOtherAbsentCadets()
                .forEach((descr, count) -> sb.append(descr.toLowerCase()).append(" - ").append(count).append(";\n"));

        return sb.toString();
    }
    public static String getOtherCadetInfo(List<FacultyUnit> units) {
        int detached = units.stream().map(FacultyUnit::getDetachedCount).reduce(0, Integer::sum);
        int other = units.stream().map(FacultyUnit::getOtherAbsentCount).reduce(0, Integer::sum);
        StringBuilder sb = new StringBuilder();
        if (detached > 0) sb.append("відр - ").append(detached).append(";\n");
        if (other > 0) sb.append("\nінші заходи - ").append(other);


        return sb.toString();
    }
}
