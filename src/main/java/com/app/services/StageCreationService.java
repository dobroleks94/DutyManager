
package com.app.services;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class StageCreationService {
    public StageCreationService() {
    }

    public static Stage createStage(String fxmlLocation, int sceneWidth, int sceneHeight) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)Objects.requireNonNull(StageCreationService.class.getClassLoader().getResource(fxmlLocation)));
        Stage stage = new Stage();
        stage.setTitle("Duty Manager Generator");
        stage.setScene(new Scene(root, (double)sceneWidth, (double)sceneHeight));
        stage.setResizable(false);
        return stage;
    }

    public static Stage createStage(Stage stage, String fxmlLocation, int sceneWidth, int sceneHeight) throws IOException {
        Parent root = (Parent)FXMLLoader.load((URL)Objects.requireNonNull(StageCreationService.class.getClassLoader().getResource(fxmlLocation)));
        stage.setTitle("Duty Manager Generator");
        stage.setScene(new Scene(root, (double)sceneWidth, (double)sceneHeight));
        stage.setResizable(false);
        return stage;
    }

    public static void showFileNotExist() {
        Alert alert = new Alert(AlertType.ERROR, "", new ButtonType[]{ButtonType.OK});
        alert.setHeaderText("Вибачте, але попереднього розходу, на жаль, не знайдено :(");
        alert.show();
    }
}