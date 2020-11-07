package com.app.services;

import com.app.entity.*;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static void updateFacultyDutyInfo(int facultyNumber, String dutyTime, String dutyDate, Officer dutyOfficer, FacultyDutyInfo facultyDutyInfo, String womenAdj) {

        Faculty faculty = Faculty.getByNumber(facultyNumber);

        facultyDutyInfo.setDutyDate(dutyDate);
        facultyDutyInfo.setDutyTime(dutyTime);
        facultyDutyInfo.setDutyPerson(dutyOfficer);
        facultyDutyInfo.setFaculty(faculty);
        facultyDutyInfo.setFacultyNum(FacultyDutyInfo.getFaculty().getNumber());
        facultyDutyInfo.setWomenAdj(
                womenAdj.equals("general") ? 0 : womenAdj.equals("withWomen") ? 1 : 2
        );

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
        FacultyUnit newUnit = new FacultyUnit(unitNumber);
        newUnit.setWomen(new FacultyUnitWomen(newUnit));
        return newUnit;
    }

    public static FacultyUnit updateFacultyUnit(boolean women, FacultyUnit currentFacultyUnit, String... fields) {
        if(women){
            currentFacultyUnit.setWomen(
                    (FacultyUnitWomen) doGeneralUpdate(currentFacultyUnit.getWomen(), fields)
            );
            return currentFacultyUnit;
        }

        return doGeneralUpdate(currentFacultyUnit, fields);
    }
    private static FacultyUnit doGeneralUpdate(FacultyUnit currentFacultyUnit, String[] fields){
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

    public static String processHospRepres(String hosp, String hospW, boolean names, boolean general) {
        if(names){
            ArrayList<String> menInGo = new ArrayList<>(Arrays.asList(hosp.split("/")));
            ArrayList<String> womenInGo = new ArrayList<>(Arrays.asList(hospW.split("/")));
            if (menInGo.size() < 2) menInGo.add("");
            if(womenInGo.size() < 2) womenInGo.add("");

            StringBuilder result = new StringBuilder();
            result.append(menInGo.get(0))
                    .append("".equals(womenInGo.get(0).trim()) ? "" : ",")
                    .append((general) ? String.format(" %s", womenInGo.get(0)) : !"".equals(womenInGo.get(0).trim()) ? String.format(" (%s) ", womenInGo.get(0)) : "")
                    .append("".equals(womenInGo.get(1).trim()) & "".equals(menInGo.get(1).trim()) ? "" : " / ")
                    .append(menInGo.get(1))
                    .append("".equals(menInGo.get(1).trim()) ? "" : ",")
                    .append((general) ? String.format(" %s", womenInGo.get(1)) : !"".equals(womenInGo.get(1).trim()) ? String.format(" (%s) ", womenInGo.get(1)) : "");
            return result.toString();
        }
        else {
            List<Integer> menValues = getEvaluations(hosp);
            List<Integer> womenValues = getEvaluations(hospW);
            int menInHosp = menValues.get(0);
            int menGoHosp = menValues.size() > 1 ? menValues.get(1) : 0;
            int womenInHosp = womenValues.get(0);
            int womenGoHosp = womenValues.size() > 1 ? womenValues.get(1) : 0;

            if (menGoHosp == 0 & womenGoHosp == 0)
                return String.valueOf(menInHosp + womenInHosp);

            return (general) ? (menInHosp + womenInHosp) + "/" + (menGoHosp + womenGoHosp)
                             : (womenInHosp == 0) ? String.format("%d / %d (%d)", menInHosp + womenInHosp, menGoHosp + womenGoHosp, womenGoHosp)
                             : (womenGoHosp == 0) ? String.format("%d (%d) / %d", menInHosp + womenInHosp, womenInHosp, menGoHosp + womenGoHosp)
                             : String.format("%d (%d) / %d (%d)", menInHosp + womenInHosp, womenInHosp, menGoHosp + womenGoHosp, womenGoHosp);
        }
    }

    private static List<Integer> getEvaluations(String hosp) {
        List<Integer> list = new ArrayList<>();
        if(!"".equals(hosp) & Utils.tryParse(hosp))
            list.add(Integer.parseInt(hosp.trim()));
        else if (!"".equals(hosp) & hosp.contains("/")){
            List<Integer> ingo =  Arrays.stream(hosp.split("/")).map(item -> Integer.parseInt(item.trim())).collect(Collectors.toList());
            list.add(ingo.get(0));
            list.add(ingo.get(1));
        }
        else list.add(0);
        return list;
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

    public static String processOtherRepres(String other, String otherW, int womenAdj) {

        Map<String, Integer> otherMen = getKeyValEvaluations(other);
        Map<String, Integer> otherWomen = getKeyValEvaluations(otherW);

        StringBuilder sb = new StringBuilder();
        String detached = "0", oth = "0";
        switch (womenAdj){
            case 0:
                detached = String.valueOf(otherMen.get("відр") + otherWomen.get("відр"));
                oth = String.valueOf(otherMen.get("інші заходи") + otherWomen.get("інші заходи"));
                break;
            case 1:
                detached = String.format("%d (%d)", (otherMen.get("відр") + otherWomen.get("відр")), otherWomen.get("відр"));
                oth = String.format("%d (%d)", (otherMen.get("інші заходи") + otherWomen.get("інші заходи")), otherWomen.get("інші заходи"));
                break;
        }

        if (otherMen.get("відр") > 0 || otherWomen.get("відр") > 0) sb.append("відр - ").append(detached).append(";\n");
        if (otherMen.get("інші заходи") > 0 || otherWomen.get("інші заходи") > 0) sb.append("\nінші заходи - ").append(oth);

        return sb.toString();
    }

    public static String processOtherRepres(Map<String, Integer> other, Map<String, Integer> otherW, int womenAdj) {

        Map<String, String> res = new HashMap<>();
        other.forEach((key, value) -> res.putIfAbsent(key, ""));
        otherW.forEach((key, value) -> res.putIfAbsent(key, ""));

        switch (womenAdj){
            case 0:
                res.entrySet().forEach(item -> {
                    int men = other.getOrDefault(item.getKey(), 0);
                    int women = otherW.getOrDefault(item.getKey(), 0);
                    item.setValue(String.format("%d", men + women));
                });
                break;
            case 1:
                res.entrySet().forEach(item -> {
                    int men = other.getOrDefault(item.getKey(), 0);
                    int women = otherW.getOrDefault(item.getKey(), 0);
                    item.setValue(String.format("%d (%d)", men + women, women));
                });
                break;
        }

        return Utils.trimMap(res);
    }

    private static Map<String, Integer> getKeyValEvaluations(String other) {
        HashMap<String, Integer> data = new HashMap<>();
        data.put("відр", 0);
        data.put("інші заходи", 0);

        Pattern pattern = Pattern.compile("[іа-я]+\\s\\-\\s\\d+");
        Matcher m = pattern.matcher(other);
        List<String> val = new ArrayList<>();
        while (m.find()){
            if(m.group().contains("відр"))
                data.merge("відр",
                        Utils.tryParse(m.group().split("-")[1]) ? Integer.parseInt(m.group().split("-")[1].trim()) : 0,
                        Integer::sum);
            else if(m.group().contains("заходи"))
                data.merge("інші заходи",
                        Utils.tryParse(m.group().split("-")[1]) ? Integer.parseInt(m.group().split("-")[1].trim()) : 0,
                        Integer::sum);
        }


        return data;

    }

    public static void groupInfoTogether(FacultyUnit unit){
        try{

            unit.getDutyCadets().addAll(unit.getWomen().getDutyCadets());
            unit.getIllCadets().addAll(unit.getWomen().getIllCadets());
            unit.getHospitalLocatedCadets().addAll(unit.getWomen().getHospitalLocatedCadets());
            unit.getHospitalVisitCadets().addAll(unit.getWomen().getHospitalVisitCadets());
            unit.getOnLeaveCadets().addAll(unit.getWomen().getOnLeaveCadets());
            unit.getVacationsCadets().addAll(unit.getWomen().getVacationsCadets());
            unit.getDetachedCadets().addAll(unit.getWomen().getDetachedCadets());
            unit.getOtherAbsentCadets().putAll(unit.getWomen().getOtherAbsentCadets());

        } catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("There is no explicit object of women is found!");
        }
    }
}
