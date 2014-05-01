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

//package imports
import org.xtech.app.jimcom.*;

//various imports
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.*;
import java.net.URL;

//javafx imports (oh jeez...)

import javafx.application.Application;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.VBox;
import javafx.collections.*;

//wooo, we are getting deeper!
public class GuiFX extends Application
{
    private Roster roster;
    private Logger logger;

    // public GuiFX()
    // {
    //     ;
    // }

    // public GuiFX(Logger logger)
    // {
    //     this.logger=logger;
    // }
    private static List<String> args;//OK, when no more than one instance is created and setter is not static, its ok... I hope...
    public void setArgs(List<String> args)
    {
        this.args=args;
    }
    

    public void show()
    {
        Application.launch(GuiFX.class, (java.lang.String[])null); //blocking
    }

    private GuiFXController controller;

    //private ObservableList<Contact> contacts = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        //logger.log("loading GUI contents...",Severity.INFO);
        
        URL style=getClass().getResource("../../../../mainWindow.fxml");
        VBox page=null;

        FXMLLoader fxmlLoader = new FXMLLoader();
        // fxmlLoader.setLocation(getClass().getResource("../../../../mainWindow.fxml"));
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        
        
        try
        {
            page = (VBox) fxmlLoader.load(style.openStream());
        }
        catch (IOException ioe)
        {
            //logger.log("----UNABLE TO LOAD LAYOUT FILE----",Severity.ERR);
            ioe.printStackTrace();
        }

        controller=fxmlLoader.getController();
        if(controller==null)
            throw new NullPointerException("refference to controller was null!");

        roster=new Roster(controller,args);
        roster.start();

        

        primaryStage.setTitle("JIMcom");

        Scene scene = new Scene(page);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}