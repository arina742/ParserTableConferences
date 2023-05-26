//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package com.example.parsertableconferences;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    public MainApplication() {
    }

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("form.fxml"));
        Scene scene = new Scene((Parent)fxmlLoader.load(), 1080.0, 600.0);

        stage.setMaxHeight(650.0);
        stage.setMaxWidth(950.0);
        stage.setMinHeight(650.0);
        stage.setMinWidth(950.0);

        stage.setTitle("Winston Churchill");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(new String[0]);
    }
}
