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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
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
	@FXML protected Stage primaryStage,stage;
	@FXML protected Button hider;
	@FXML protected ListView contactList;
	@FXML protected TextArea typingArea;
	@FXML protected TextArea messageArea;

	Contact lastSelectedContact;

    private Status status;

    private Roster roster;

    public void setRoster(Roster roster)
    {
        this.roster=roster;
    }


	@Override
	public void initialize(URL url, ResourceBundle rb)
	{
		statusBox.getItems().addAll(
			Status.Online,
			Status.Offline);
		statusBox.getSelectionModel().select(Status.Online);
        status=Status.Online;
		//statusBox.setSelectedIndex(0);
        lastSelectedContact=null;
	}

    @FXML
    public void handleQuitAction(ActionEvent event)
    {
        shout("oh, you wanna quit?");
        shout("ok, then...");
        roster.exit();
        try
        {roster.join();}
        catch(InterruptedException ex)
        {
            shout("unable to join roster thread!");
        }
        shout("roster terminated");

        Platform.exit();//teminates GUI
    }

    @FXML
    public void handleSetLocalIdentity(ActionEvent event)
    {
        //TODO: (10) open local identity window and commit changes
        Stage stage = new Stage();
        stage.setScene(new Scene(new Group(new Text(10,10, "my second window"))));
        stage.show();
    }

    @FXML
    public void handleAddContact(ActionEvent event)
    {
        //TODO: (10) open new contact window (only address)
        ;
    }

    public synchronized void updateContactListView(List<Contact> contacts)
    {
        shout(("updating contact list..."));
        contactList.setItems(FXCollections.observableArrayList(contacts));
        resolveTypingAvailability();
        //TODO: (40) what to do when something is already typed?
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        shout("You clicked me!");
        if(lastSelectedContact!=null)
        {
            if(lastSelectedContact.getConnectionState()==Status.Online)
                lastSelectedContact.setConnectionState(Status.Offline);
            else
                lastSelectedContact.setConnectionState(Status.Online);
        }
       
    }

    @FXML
    private void handleKeyAction( KeyEvent keyEvent)
    {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            // setPressed(keyEvent.getEventType()
            //     == KeyEvent.KEY_PRESSED);

            keyEvent.consume();
            shout("ENTER PRESSED");

            String text=typingArea.getText();
            //messageArea.appendText(typingArea.getText());
            typingArea.clear();
            lastSelectedContact.sendMessage(text);

        }
    }

    @FXML
    private void handleStatuchChosen(ActionEvent event)
    {
        if(statusBox.getValue()!=status)
        {
            status=statusBox.getValue();
            shout("status changed to: "+status);
        }
    }



    @FXML
    private void handleContactSelection( MouseEvent event)
    {
        Contact selectedContact=(Contact)contactList.getSelectionModel().getSelectedItem();
        if(selectedContact!=lastSelectedContact)//proceed if chosen contact was changed only
        {
            shout("contact selected: "+selectedContact);
            lastSelectedContact=selectedContact;
            messageArea.setDisable(false);

            String buffer="";
            for (Message msg : selectedContact.getMessages())
            {
                    buffer+=msg+"\n";
            }
            messageArea.setText(buffer);

            resolveTypingAvailability();

        }
    }

    private void resolveTypingAvailability()
    {
        Contact selectedContact=(Contact)contactList.getSelectionModel().getSelectedItem();

        if(selectedContact==null)
        {
            shout("no contact chosen, nothing to resolve");
            return;
        }

        shout("RESOLVING AVAILABILITY");

        if(selectedContact.isConnected())
            typingArea.setDisable(false);
        else
            typingArea.setDisable(true);
    }

    public synchronized void handleMessageEvent(Identity local,Contact msgId)
    {
        if(msgId.equals(lastSelectedContact))
        {
            //event came to selected contact
            messageArea.appendText("\n"+msgId.getLastMessage().toString());

        }
    }

    private static void shout(String text)
    {
        //opt TODO: (90) use logger
        System.out.println("FXCONTROLLER: "+text);
    }
}
