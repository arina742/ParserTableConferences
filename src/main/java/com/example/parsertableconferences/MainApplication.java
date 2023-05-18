package com.example.parsertableconferences;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        stage.setMinHeight(600);
        stage.setMinWidth(900);
        stage.setMaxHeight(601);
        stage.setMaxWidth(901);

        stage.setTitle("Winston Churchill");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}