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



public class GuiFXController implements Initializable, EventHandler<WindowEvent>
{
	@FXML protected ComboBox<Status> statusBox;
	@FXML protected GridPane grid;
	@FXML protected VBox topBox;
	@FXML protected Stage primaryStage,stage;
	@FXML protected Button hider;
	@FXML protected ListView contactList;
	@FXML protected TextArea typingArea;
	@FXML protected TextArea messageArea;
    @FXML protected Menu menuContact;

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
        exit();
    }


    //handle window closing
    @FXML
    public void handle(WindowEvent ev) {
        exit();
    }

    private void exit()
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
        LoginDialog login=new LoginDialog(roster.getIdentity());
        Identity newID=login.login(false);
        if(newID!=null)
            roster.setLocalIdentity(newID);
        else
            shout("newID was NULL!");
    }

    @FXML
    public void handleAddContact(ActionEvent event)
    {
        NewContact newContactGUI=new NewContact();
        Contact c=newContactGUI.get(roster);
        if(c!=null)
        {
            roster.addContact(c);
        }
    }

    @FXML
    public void handleDeleteContact(ActionEvent event)
    {
        enableContactSpecific(false);
        messageArea.clear();
        typingArea.clear();
        roster.removeContact(lastSelectedContact);
        lastSelectedContact=(Contact)contactList.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void handleModifyContact(ActionEvent event)
    {
        ;
    }


    public synchronized void updateContactListView(List<Contact> contacts)
    {
        shout(("updating contact list..."));
        contactList.setItems(FXCollections.observableArrayList(contacts));
        for (Contact c :contacts )
        {
            shout("listed contact: "+c.toString());    
        }
        resolveTypingAvailability();
        //TODO: (40) what to do when something is already typed?
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
            //TODO: (10) distribute status change to all
        }
    }



    @FXML
    private void handleContactSelection( MouseEvent event)
    {
        Contact selectedContact=(Contact)contactList.getSelectionModel().getSelectedItem();
        shout("contact selected: "+selectedContact);
        if(selectedContact==null)
        {
            enableContactSpecific(false);
            return;
        }
        if(selectedContact!=lastSelectedContact)//proceed if chosen contact was changed only
        {
            lastSelectedContact=selectedContact;
            enableContactSpecific(false);

            String buffer="";
            for (Message msg : selectedContact.getMessages())
            {
                    buffer+=msg+"\n";
            }
            messageArea.setText(buffer);

            resolveTypingAvailability();

        }
    }

    private void enableContactSpecific(Boolean en)
    {
        messageArea.setDisable(en);
        //typingArea.setDisable(en);
        menuContact.setDisable(en);
    }

    private void resolveTypingAvailability()
    {
        Contact selectedContact=(Contact)contactList.getSelectionModel().getSelectedItem();

        if(selectedContact==null)
        {
            shout("no contact chosen, nothing to resolve");
            return;
        }

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
