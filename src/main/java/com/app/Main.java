package com.app;

import com.app.controller.MainPageController;
import javafx.application.Application;
import javafx.stage.Stage;
import com.app.services.StageCreationService;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        StageCreationService.createStage(primaryStage, "fxml/mainPage.fxml", 600, 400);
        primaryStage.show();

        MainPageController.setMainPageStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
