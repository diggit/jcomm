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

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import org.xtech.app.jimcom.Status;
import java.util.ResourceBundle;
import java.net.URL;
import javafx.fxml.Initializable;



public class GuiFXController implements Initializable
{
	@FXML protected ComboBox<Status> statusBox;
	

	@Override
	public void initialize(URL url, ResourceBundle rb)
	{
		statusBox.getItems().addAll(
			Status.Online,
			Status.Offline);
		statusBox.getSelectionModel().select(Status.Offline);
		//statusBox.setSelectedIndex(0);
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
