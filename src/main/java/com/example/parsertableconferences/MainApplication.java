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

        stage.setMinHeight(650);
        stage.setMinWidth(950);
        stage.setMaxHeight(650);
        stage.setMaxWidth(950);

        stage.setTitle("Winston Churchill");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}