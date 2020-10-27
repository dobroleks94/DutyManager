package com.app.controller;

import com.app.entity.FacultyUnit;
import javafx.event.ActionEvent;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CourseInfoController implements Initializable {

    @FXML
    private ComboBox<Integer> eduUnits;
    @FXML
    private TextField genCount, dutyNamesStr, illNamesStr, inHospitalNamesStr, gotoHospitalNamesStr, onLeaveNamesStr, vacationNamesStr, detachedNamesStr, otherNamesStr;
    @FXML
    private Label currentDutyOfficer, date, time;

    private static List<FacultyUnit> facultyUnits;

    public static void setFacultyUnits(List<FacultyUnit> facultyUnits) {
        CourseInfoController.facultyUnits = facultyUnits;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FacultyDataService.fillFacultyUnits(eduUnits);
        if(facultyUnits == null || facultyUnits.isEmpty())
            facultyUnits = eduUnits.getItems()
                    .stream()
                    .map(FacultyDataService::createFacultyUnit)
                    .collect(Collectors.toList());
        updateForm(getCurrentFacultyUnit());

    }

    public void updateFields(){
        FacultyUnit currentFacultyUnit = getCurrentFacultyUnit();

        int index = facultyUnits.indexOf(currentFacultyUnit);

        FacultyUnit updated = FacultyDataService.updateFacultyUnit(currentFacultyUnit,
                genCount.getText(),
                dutyNamesStr.getText(),
                illNamesStr.getText(),
                inHospitalNamesStr.getText(),
                gotoHospitalNamesStr.getText(),
                onLeaveNamesStr.getText(),
                vacationNamesStr.getText(),
                detachedNamesStr.getText(),
                otherNamesStr.getText());

        facultyUnits.remove(index);
        facultyUnits.add(index, updated);
    }
    private void updateForm(FacultyUnit unit){
        genCount.setText(String.valueOf(unit.getGeneralCadetCount()));
        dutyNamesStr.setText(Utils.trimList(unit.getDutyCadets()));
        illNamesStr.setText(Utils.trimList(unit.getIllCadets()));
        inHospitalNamesStr.setText(Utils.trimList(unit.getHospitalLocatedCadets()));
        gotoHospitalNamesStr.setText(Utils.trimList(unit.getHospitalVisitCadets()));
        onLeaveNamesStr.setText(unit.getOnLeaveCadets().isEmpty() ? unit.getOnLeaveCount() != 0 ? String.valueOf(unit.getOnLeaveCount()) : Utils.trimList(unit.getOnLeaveCadets()) : Utils.trimList(unit.getOnLeaveCadets()));
        vacationNamesStr.setText(unit.getVacationsCadets().isEmpty() ? unit.getVacationsCount() != 0 ? String.valueOf(unit.getVacationsCount()) : Utils.trimList(unit.getVacationsCadets()) : Utils.trimList(unit.getVacationsCadets()));
        detachedNamesStr.setText(unit.getDetachedCadets().isEmpty() ? unit.getDetachedCount() != 0 ?  String.valueOf(unit.getDetachedCount()) : Utils.trimList(unit.getDetachedCadets()) : Utils.trimList(unit.getDetachedCadets()));
        otherNamesStr.setText(unit.getOtherAbsentCadets().isEmpty() ? unit.getOtherAbsentCount() != 0 ?  String.valueOf(unit.getOtherAbsentCount()) : Utils.trimMap(unit.getOtherAbsentCadets()) : Utils.trimMap(unit.getOtherAbsentCadets()));
        currentDutyOfficer.setText(FacultyDataService.getFacultyDutyInfo().getDutyPerson().getRank() + " " + FacultyDataService.getFacultyDutyInfo().getDutyPerson().getFullName());
        time.setText(FacultyDataService.getFacultyDutyInfo().getDutyTime());
        date.setText(FacultyDataService.getFacultyDutyInfo().getDutyDate());
    }
    private FacultyUnit getCurrentFacultyUnit(){
        return facultyUnits.stream().filter(f -> f.getUnitNumber() == eduUnits.getValue()).findFirst().orElse(new FacultyUnit(Integer.MAX_VALUE));
    }

    public void chosenUnit(){
        updateForm(getCurrentFacultyUnit());
    }


    public void generateDutyList() throws URISyntaxException, IOException {
        updateFields();
        DutyListGeneratorService.generateDutyList(facultyUnits, FacultyDataService.getFacultyDutyInfo());
        Utils.saveJsonRepresentation(facultyUnits, FacultyDataService.getFacultyDutyInfo());
        System.out.println("Successfully saved!");
        goToMain();
    }

    public void goToMain() throws IOException {
        try { updateFields(); }
        catch (Exception e) { e.printStackTrace(); }
        Stage mainPage = StageCreationService.createStage("fxml/mainPage.fxml", 600, 400);

        mainPage.show();
        MainPageController.getMainPageStage().close();
        MainPageController.setMainPageStage(mainPage);
    }
}
