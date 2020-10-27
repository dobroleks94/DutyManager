package com.app.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class StageCreationService {

    public static Stage createStage(String fxmlLocation, int sceneWidth, int sceneHeight) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(StageCreationService.class.getClassLoader().getResource(fxmlLocation)));

        Stage stage = new Stage();
        stage.setTitle("Duty Manager Generator");
        stage.setScene(new Scene(root, sceneWidth, sceneHeight));
        stage.setResizable(false);

        return stage;
    }

    public static Stage createStage(Stage stage, String fxmlLocation, int sceneWidth, int sceneHeight) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(StageCreationService.class.getClassLoader().getResource(fxmlLocation)));

        stage.setTitle("Duty Manager Generator");
        stage.setScene(new Scene(root, sceneWidth, sceneHeight));
        stage.setResizable(false);

        return stage;
    }

    public static void showFileNotExist() {
        Alert alert = new Alert(Alert.AlertType.ERROR, "",  ButtonType.OK);
        alert.setHeaderText("Вибачте, але попереднього розходу, на жаль, не знайдено :(");
        alert.show();
    }
}
