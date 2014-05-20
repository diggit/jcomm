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
import java.net.URL;


import org.xtech.app.jimcom.*;

public class LoginDialog
{
	Identity localID;
	Identity read=null;
	public LoginDialog(Identity localID)
	{
		this.localID=localID;
	}

	private void setReadIdentity(Identity id)
	{
		this.read=id;
	}
	public Identity login(final boolean verificationNeeded)
	{

		final Stage dialog = new Stage();
		final GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        final Text scenetitle = new Text("Login");
        final Label userName = new Label("User Name:");
        final TextField userTextField = new TextField();
        final Label pw = new Label("Password:");
        final PasswordField pwBox = new PasswordField();
        final Text info = new Text("");
        final Button signBtn = new Button("Sign in");
        final Button newayBtn = new Button("use neway");

        if(localID!=null)
        	userTextField.setText(localID.getNickname());

        newayBtn.setVisible(false);
        
        grid.add(scenetitle, 0, 0, 2, 1);
        grid.add(userName, 0, 1);
        grid.add(userTextField, 1, 1);
        grid.add(pw, 0, 2);
        grid.add(pwBox, 1, 2);
        grid.add(info, 0, 4, 2, 1);
        grid.add(signBtn, 1, 5);
        grid.add(newayBtn, 0, 5);

		newayBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent e)
			{
				if(userTextField.getText().isEmpty() ||pwBox.getText().isEmpty())
				{
					info.setText("username and password must be filled!");
					return;
				}
        //TODO: (10) do not allow non alphanumerical characters in name
				setReadIdentity(new SimpleIdentity(userTextField.getText(),Utils.stringHash(pwBox.getText())));
				dialog.close();

			}
		});

        signBtn.setOnAction(new EventHandler<ActionEvent>()
        {
			@Override
			public void handle(ActionEvent e)
			{
				if(userTextField.getText().isEmpty() ||pwBox.getText().isEmpty())
				{
					info.setText("username and password must be filled!");
					return;
				}

				Identity grabbed=new SimpleIdentity(userTextField.getText(),Utils.stringHash(pwBox.getText()));

				if(localID!=null)
					if(grabbed.equals(localID) || !verificationNeeded)
					{
						setReadIdentity(grabbed);
						dialog.close();
					}
					else
					{
						info.setText("login is not matching");
						newayBtn.setVisible(true);
					}
				else
				{
					setReadIdentity(grabbed);	
					dialog.close();
				}

			}
		});


        
        dialog.initStyle(StageStyle.UTILITY);
       
        dialog.setScene(new Scene(grid));
        dialog.showAndWait();

        return read;
	}

	
}