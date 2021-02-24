package com.app.controller;

import com.app.entity.FacultyDutyInfo;
import com.app.entity.Officer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.app.services.FacultyDataService;
import com.app.services.StageCreationService;
import com.app.services.Utils;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    //private FacultyDutyInfo facultyDutyInfo;
    private static Stage mainPageStage;

    public static void setMainPageStage(Stage mainPageStage) {
        MainPageController.mainPageStage = mainPageStage;
    }
    public static Stage getMainPageStage() {
        return mainPageStage;
    }

    @FXML
    private ComboBox<Integer> facultyNumber;
    @FXML
    private Label dutyDate;
    @FXML
    private TextField dutyTime, rank, fullName;
    @FXML
    private RadioButton genList, withWomen, withoutWomen;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(FacultyDataService.getFacultyDutyInfo() == null)
            FacultyDataService.setFacultyDutyInfo(FacultyDataService.createNewDutyInfo());
        FacultyDataService.updateFacultyNumber(facultyNumber);

        updateFields(FacultyDataService.getFacultyDutyInfo());

        LocalDateTime currentDate = LocalDateTime.now();
        dutyDate.setText(String.format( "\"%d\" %s %d",
                currentDate.getDayOfMonth(),
                Utils.monthValueConverter(currentDate.getMonthValue()),
                currentDate.getYear()));
    }

    private void updateFields(FacultyDutyInfo facultyDutyInfo) {
        if(facultyDutyInfo.getFacultyNum() != 0)
            facultyNumber.setValue(facultyDutyInfo.getFacultyNum());
        dutyTime.setText(facultyDutyInfo.getDutyTime());
        rank.setText(facultyDutyInfo.getDutyPerson().getRank());
        fullName.setText(facultyDutyInfo.getDutyPerson().getFullName());

        if(facultyDutyInfo.getWomenAdj() == 0){
            genList.setSelected(true);
            withoutWomen.setSelected(false);
            withWomen.setSelected(false);
        } else if (facultyDutyInfo.getWomenAdj() == 1){
            genList.setSelected(false);
            withoutWomen.setSelected(false);
            withWomen.setSelected(true);
        } else if (facultyDutyInfo.getWomenAdj() == 2) {
            genList.setSelected(false);
            withoutWomen.setSelected(true);
            withWomen.setSelected(false);
        }
    }

    public void start() throws IOException {

        Officer dutyOfficer = FacultyDataService.specifyDutyOfficer(rank, fullName);
        String womenAdj = genList.isSelected() ? "general" : withWomen.isSelected() ? "withWomen" : "withoutWomen";

        FacultyDataService.updateFacultyDutyInfo(facultyNumber.getValue(), dutyTime.getText(), dutyDate.getText(), dutyOfficer, FacultyDataService.getFacultyDutyInfo(), womenAdj);
        System.out.println(FacultyDataService.getFacultyDutyInfo());

        Stage courseInfoStage = StageCreationService.createStage("fxml/courseInfo.fxml", 865, 570);

        courseInfoStage.show();
        mainPageStage.close();
        MainPageController.setMainPageStage(courseInfoStage);

    }


    public void openPrevious(ActionEvent actionEvent) throws IOException {
        boolean success = Utils.getEntitiesFromJsonRepresentation();
        if (success){

            Stage updatedMain = StageCreationService.createStage(new Stage(), "fxml/mainPage.fxml");

            updatedMain.show();
            mainPageStage.close();
            setMainPageStage(updatedMain);
        }
    }

}
