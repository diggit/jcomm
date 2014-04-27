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
import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.util.*;
import java.net.URL;


import org.xtech.app.jimcom.Status;



public class GuiFXController implements Initializable
{
	@FXML protected ComboBox<Status> statusBox;
    @FXML protected GridPane grid;
    @FXML protected VBox topBox;
    @FXML protected Stage primaryStage;
    @FXML protected Button hider;
    @FXML protected ListView contactList;
	

	@Override
	public void initialize(URL url, ResourceBundle rb)
	{
		statusBox.getItems().addAll(
			Status.Online,
			Status.Offline);
		statusBox.getSelectionModel().select(Status.Offline);
		//statusBox.setSelectedIndex(0);
	}

    public synchronized void updateContactListView(List<Contact> contacts)
    {
        contactList.setItems(FXCollections.observableArrayList(contacts));
    }

    @FXML
    private void clicked(MouseEvent event) {
        System.out.println("CLICKED: You clicked me!");       
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
    }
}