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
import javafx.application.Platform;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.event.EventHandler;

import javafx.collections.*;


import java.util.*;
import java.net.*;


import org.xtech.app.jimcom.*;

public class NewContact
{
	Contact newID;

	private void setNewContact(Contact id)
	{
		this.newID=id;
	}

	public Contact get(final Roster roster)
	{

		final Stage dialog = new Stage();
		final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        final Text scenetitle = new Text("New contact");
        final Label address = new Label("address:");
        final TextField addressField = new TextField();
        final Label port = new Label("port:");
        final TextField portField = new TextField();
        final Text info = new Text("");
        final Button addBtn = new Button("Add");
        final Button cancelBtn = new Button("Cancel");
        
        grid.add(scenetitle, 0, 0, 2, 1);
        grid.add(address, 0, 1);
        grid.add(addressField, 1, 1);
        grid.add(port, 0, 2);
        grid.add(portField, 1, 2);
        grid.add(info, 0, 4, 2, 1);
        grid.add(addBtn, 1, 5);
        grid.add(cancelBtn, 0, 5);

        addBtn.setOnAction(new EventHandler<ActionEvent>()
        {
			@Override
			public void handle(ActionEvent e)
			{
				dialog.close();	
			}
		});

        addBtn.setOnAction(new EventHandler<ActionEvent>()
        {
			@Override
			public void handle(ActionEvent e)
			{
				if(addressField.getText().isEmpty() ||portField.getText().isEmpty())
				{
					info.setText("address and port must be set");
				}
				else
				{
					int port;
					InetAddress ip;
					try
					{
						port=Integer.parseInt(portField.getText());
						if(port>65535)
							throw new NumberFormatException("port out fo range");
					}
					catch (NumberFormatException ex)
					{
						info.setText("port id number, maximum 65535");	
						return;
					}

					try
					{
						ip= InetAddress.getByName(addressField.getText());
					}
					catch(UnknownHostException ex)
					{
						info.setText("ivalid address format");
						return;
					}
					setNewContact(new Contact(roster,"unknown",Utils.stringHash(""+Calendar.getInstance().getTimeInMillis()),ip,port));
					//TODO: (10) create every new contant somehow unique, to add more waiting contacts into list
					dialog.close();
					
				}

			}
		});


        
        dialog.initStyle(StageStyle.UTILITY);
       
        dialog.setScene(new Scene(grid));
        dialog.showAndWait();

        return newID;
	}

	
}