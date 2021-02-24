package com.app.controller;

import com.app.entity.FacultyDutyInfo;
import com.app.entity.FacultyUnit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.app.services.DutyListGeneratorService;
import com.app.services.FacultyDataService;
import com.app.services.StageCreationService;
import com.app.services.Utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CourseInfoController implements Initializable {

    @FXML
    private ComboBox<Integer> eduUnits;
    @FXML
    private TextField genCount, dutyNamesStr, illNamesStr, inHospitalNamesStr, gotoHospitalNamesStr, onLeaveNamesStr, vacationNamesStr, detachedNamesStr, otherNamesStr,
            womenCount, dutyNamesStrW, illNamesStrW, inHospitalNamesStrW, gotoHospitalNamesStrW, onLeaveNamesStrW, vacationNamesStrW, detachedNamesStrW, otherNamesStrW;
    @FXML
    private Label currentDutyOfficer, date, time, womenAdj;

    private static List<FacultyUnit> facultyUnits;

    public static void setFacultyUnits(List<FacultyUnit> facultyUnits) {
        CourseInfoController.facultyUnits = facultyUnits;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        womenAdj.setText("Розрахунок о/с факультету" + (FacultyDataService.getFacultyDutyInfo().getWomenAdj() == 0 ? " (загальний)" : FacultyDataService.getFacultyDutyInfo().getWomenAdj() == 1 ? " (з ЖВС)" : " (без ЖВС)"));
        FacultyDataService.fillFacultyUnits(eduUnits);
        if(facultyUnits == null || facultyUnits.isEmpty() || (facultyUnits.get(0).getUnitNumber() / 10) != FacultyDutyInfo.getFaculty().getNumber())
            facultyUnits = eduUnits.getItems()
                    .stream()
                    .map(FacultyDataService::createFacultyUnit)
                    .collect(Collectors.toList());
        FacultyUnit current = getCurrentFacultyUnit();
        updateForm(current);


    }

    public void updateFields(){
        FacultyUnit currentFacultyUnit = getCurrentFacultyUnit();

        int index = facultyUnits.indexOf(currentFacultyUnit);

        FacultyUnit updated = FacultyDataService.updateFacultyUnit(false, currentFacultyUnit,
                genCount.getText(),
                dutyNamesStr.getText(),
                illNamesStr.getText(),
                inHospitalNamesStr.getText(),
                gotoHospitalNamesStr.getText(),
                onLeaveNamesStr.getText(),
                vacationNamesStr.getText(),
                detachedNamesStr.getText(),
                otherNamesStr.getText());

        updated = FacultyDataService.updateFacultyUnit(true, updated,
                womenCount.getText(),
                dutyNamesStrW.getText(),
                illNamesStrW.getText(),
                inHospitalNamesStrW.getText(),
                gotoHospitalNamesStrW.getText(),
                onLeaveNamesStrW.getText(),
                vacationNamesStrW.getText(),
                detachedNamesStrW.getText(),
                otherNamesStrW.getText());

        facultyUnits.remove(index);
        facultyUnits.add(index, updated);
    }

    private void updateForm(FacultyUnit unit, TextField ... fields) {

        fields[0].setText(String.valueOf(unit.getGeneralCadetCount()));
        fields[1].setText(Utils.trimList(unit.getDutyCadets()));
        fields[2].setText(Utils.trimList(unit.getIllCadets()));
        fields[3].setText(Utils.trimList(unit.getHospitalLocatedCadets()));
        fields[4].setText(Utils.trimList(unit.getHospitalVisitCadets()));
        fields[5].setText(unit.getOnLeaveCadets().isEmpty() ? unit.getOnLeaveCount() != 0 ? String.valueOf(unit.getOnLeaveCount()) : Utils.trimList(unit.getOnLeaveCadets()) : Utils.trimList(unit.getOnLeaveCadets()));
        fields[6].setText(unit.getVacationsCadets().isEmpty() ? unit.getVacationsCount() != 0 ? String.valueOf(unit.getVacationsCount()) : Utils.trimList(unit.getVacationsCadets()) : Utils.trimList(unit.getVacationsCadets()));
        fields[7].setText(unit.getDetachedCadets().isEmpty() ? unit.getDetachedCount() != 0 ? String.valueOf(unit.getDetachedCount()) : Utils.trimList(unit.getDetachedCadets()) : Utils.trimList(unit.getDetachedCadets()));
        fields[8].setText(unit.getOtherAbsentCadets().isEmpty() ? unit.getOtherAbsentCount() != 0 ? String.valueOf(unit.getOtherAbsentCount()) : Utils.trimMap(unit.getOtherAbsentCadets()) : Utils.trimMap(unit.getOtherAbsentCadets()));

        currentDutyOfficer.setText(FacultyDataService.getFacultyDutyInfo().getDutyPerson().getRank() + " " + FacultyDataService.getFacultyDutyInfo().getDutyPerson().getFullName());
        time.setText(FacultyDataService.getFacultyDutyInfo().getDutyTime());
        date.setText(FacultyDataService.getFacultyDutyInfo().getDutyDate());
    }
    private void updateForm(FacultyUnit unit){

        FacultyUnit women = unit.getWomen();

        genCount.setText(String.valueOf(unit.getGeneralCadetCount()));
        dutyNamesStr.setText(Utils.trimList(unit.getDutyCadets()));
        illNamesStr.setText(Utils.trimList(unit.getIllCadets()));
        inHospitalNamesStr.setText(Utils.trimList(unit.getHospitalLocatedCadets()));
        gotoHospitalNamesStr.setText(Utils.trimList(unit.getHospitalVisitCadets()));
        onLeaveNamesStr.setText(unit.getOnLeaveCadets().isEmpty() ? unit.getOnLeaveCount() != 0 ? String.valueOf(unit.getOnLeaveCount()) : Utils.trimList(unit.getOnLeaveCadets()) : Utils.trimList(unit.getOnLeaveCadets()));
        vacationNamesStr.setText(unit.getVacationsCadets().isEmpty() ? unit.getVacationsCount() != 0 ? String.valueOf(unit.getVacationsCount()) : Utils.trimList(unit.getVacationsCadets()) : Utils.trimList(unit.getVacationsCadets()));
        detachedNamesStr.setText(unit.getDetachedCadets().isEmpty() ? unit.getDetachedCount() != 0 ?  String.valueOf(unit.getDetachedCount()) : Utils.trimList(unit.getDetachedCadets()) : Utils.trimList(unit.getDetachedCadets()));
        otherNamesStr.setText(unit.getOtherAbsentCadets().isEmpty() ? unit.getOtherAbsentCount() != 0 ?  String.valueOf(unit.getOtherAbsentCount()) : Utils.trimMap(unit.getOtherAbsentCadets()) : Utils.trimMap(unit.getOtherAbsentCadets()));

        womenCount.setText(String.valueOf(women.getGeneralCadetCount()));
        dutyNamesStrW.setText(Utils.trimList(women.getDutyCadets()));
        illNamesStrW.setText(Utils.trimList(women.getIllCadets()));
        inHospitalNamesStrW.setText(Utils.trimList(women.getHospitalLocatedCadets()));
        gotoHospitalNamesStrW.setText(Utils.trimList(women.getHospitalVisitCadets()));
        onLeaveNamesStrW.setText(women.getOnLeaveCadets().isEmpty() ? women.getOnLeaveCount() != 0 ? String.valueOf(women.getOnLeaveCount()) : Utils.trimList(women.getOnLeaveCadets()) : Utils.trimList(women.getOnLeaveCadets()));
        vacationNamesStrW.setText(women.getVacationsCadets().isEmpty() ? women.getVacationsCount() != 0 ? String.valueOf(women.getVacationsCount()) : Utils.trimList(women.getVacationsCadets()) : Utils.trimList(women.getVacationsCadets()));
        detachedNamesStrW.setText(women.getDetachedCadets().isEmpty() ? women.getDetachedCount() != 0 ?  String.valueOf(women.getDetachedCount()) : Utils.trimList(women.getDetachedCadets()) : Utils.trimList(women.getDetachedCadets()));
        otherNamesStrW.setText(women.getOtherAbsentCadets().isEmpty() ? women.getOtherAbsentCount() != 0 ?  String.valueOf(women.getOtherAbsentCount()) : Utils.trimMap(women.getOtherAbsentCadets()) : Utils.trimMap(women.getOtherAbsentCadets()));

        currentDutyOfficer.setText(FacultyDataService.getFacultyDutyInfo().getDutyPerson().getRank() + " " + FacultyDataService.getFacultyDutyInfo().getDutyPerson().getFullName());
        time.setText(FacultyDataService.getFacultyDutyInfo().getDutyTime());
        date.setText(FacultyDataService.getFacultyDutyInfo().getDutyDate());

    }
    private FacultyUnit getCurrentFacultyUnit(){
        return facultyUnits.stream().filter(f -> f.getUnitNumber() == eduUnits.getValue()).findFirst().orElse(new FacultyUnit(Integer.MAX_VALUE));
    }

    public void chosenUnit(){
        System.out.println(getCurrentFacultyUnit().getWomen());
        FacultyUnit current = getCurrentFacultyUnit();
        updateForm(current, genCount, dutyNamesStr, illNamesStr, inHospitalNamesStr, gotoHospitalNamesStr, onLeaveNamesStr,
                                            vacationNamesStr, detachedNamesStr, otherNamesStr);
        updateForm(current.getWomen(), womenCount, dutyNamesStrW, illNamesStrW, inHospitalNamesStrW, gotoHospitalNamesStrW, onLeaveNamesStrW,
                vacationNamesStrW, detachedNamesStrW, otherNamesStrW);
    }


    public void generateDutyList() throws URISyntaxException, IOException {
        updateFields();
        Path path = DutyListGeneratorService.generateDutyList(facultyUnits, FacultyDataService.getFacultyDutyInfo());
        Utils.saveJsonRepresentation(facultyUnits, FacultyDataService.getFacultyDutyInfo());
        System.out.println("Successfully saved!");
        ResultController.setPath(path);

        goToResult();
    }

    public void goToResult() throws IOException {
        try { updateFields(); }
        catch (Exception e) { e.printStackTrace(); }
        Stage resultPage = StageCreationService.createStage(new Stage(), "fxml/resultPage.fxml");

        resultPage.show();
        MainPageController.getMainPageStage().close();
        MainPageController.setMainPageStage(resultPage);
    }
}
