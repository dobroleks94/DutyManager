package com.app.controller;

import com.app.services.StageCreationService;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ResultController {

    private static Path PATH_TO_FILE;

    public static void setPath(Path uri) {
        PATH_TO_FILE = uri;
    }

    public void goToMain(ActionEvent actionEvent) throws IOException {

        Stage resultPage = StageCreationService.createStage(new Stage(), "fxml/mainPage.fxml");

        resultPage.show();
        MainPageController.getMainPageStage().close();
        MainPageController.setMainPageStage(resultPage);
    }

    public void openFile(MouseEvent mouseEvent) {
        Desktop desktop = Desktop.getDesktop();

        new Thread(() -> {
            try {
                desktop.browse(PATH_TO_FILE.toUri());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void showInFolder(MouseEvent mouseEvent) {
        Desktop desktop = Desktop.getDesktop();
        new Thread(() -> {
            try {
                desktop.open(new File(
                        PATH_TO_FILE.toString()
                                .substring(0, PATH_TO_FILE.toString().lastIndexOf('/'))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendViaTelegram(MouseEvent mouseEvent) {
    }
}
