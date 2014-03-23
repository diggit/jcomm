
package com.xtech.app.jimcom;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;

public class GuiFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            VBox page = (VBox) FXMLLoader.load(GuiFX.class.getResource("../../../../mainWindow.fxml"));
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JIMcom");
            primaryStage.show();
        } catch (Exception ex) {
            Logger.getLogger(GuiFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void show() {
        Application.launch(GuiFX.class, (java.lang.String[])null); //blocking
  }
}