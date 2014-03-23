//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.

//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.

//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

//  author bachapat aka diggit

package org.xtech.app.jimcom;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;

import java.io.IOException;
import org.xtech.app.jimcom.GuiFXController;
//import javafx.fxml.FXML;

//wooo, we are getting deeper!
public class GuiFX extends Application {

    private GuiFXController controller=null;

    @Override
    public void start(Stage primaryStage) {
        
        VBox page=null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../../../../mainWindow.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());

        controller=fxmlLoader.getController();

        try
        {
            page = (VBox) fxmlLoader.load();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }





        primaryStage.setTitle("JIMcom");
        //primaryStage.setWidth(320);
        //primaryStage.setHeight(200);

        Scene scene = new Scene(page);
        primaryStage.setScene(scene);

        primaryStage.show();


        controller=fxmlLoader.getController();


    }

    public static void show() {
        Application.launch(GuiFX.class, (java.lang.String[])null); //blocking
  }
}