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
	@FXML protected ListView contactListView;
	@FXML protected TextArea typingArea;
	@FXML protected TextArea messageArea;
    @FXML protected Menu menuContact;
    @FXML protected Label leftStatus,rightStatus;

	Contact lastSelectedContact;

    private Status status;

    private Roster roster;

    private ObservableList contactList;

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
        status=Status.Online;
        statusBox.getSelectionModel().select(status);
		//statusBox.setSelectedIndex(0);
        lastSelectedContact=null;
        this.contactList=FXCollections.observableArrayList();
        this.contactListView.setItems(contactList);
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
        //contactList.removeAll(lastSelectedContact);
        enableContactSpecific(false);
        messageArea.clear();
        typingArea.clear();
        roster.removeContact(lastSelectedContact);
        lastSelectedContact=(Contact)contactListView.getSelectionModel().getSelectedItem();

    }

    @FXML
    public void handleModifyContact(ActionEvent event)
    {
        Contact modifying=lastSelectedContact;//latch contact
        NewContact newContactGUI=new NewContact(modifying);
        Contact c=newContactGUI.get(roster);
        if(c!=null)
        {
            if(!modifying.getIp().equals(c.getIp()) || modifying.getPort()!=c.getPort())//ip or port changed
            {
                shout("contact modified, applying...");
                Status original=modifying.getCurrentState();
                modifying.setState(Status.Offline);
                modifying.setConnection(c.getIp(),c.getPort());
                modifying.setState(original);
            }
        }
    }


    public synchronized void updateContactListView(List<Contact> contacts)
    {
        shout("updating contact list...");
        //contactList=FXCollections.observableArrayList(contacts);
        for (Contact c :contacts )
        {
            shout("listed contact: "+c.toString());    
        }
        contactList.setAll(contacts);
        //TODO: (40) what to do when something is already typed?
        if(!contacts.contains(lastSelectedContact))//only when selected disappear
        {
            resolveTypingAvailability();
            lastSelectedContact=null;
            leftStatus.setText("");
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
            roster.setState(status);
        }
    }



    @FXML
    private void handleContactSelection( MouseEvent event)
    {
        Contact selectedContact=(Contact)contactListView.getSelectionModel().getSelectedItem();
        shout("contact selected: "+selectedContact);
        if(selectedContact==null)
        {
            enableContactSpecific(false);
            return;
        }
        if(selectedContact!=lastSelectedContact)//proceed if chosen contact was changed only
        {
            selectedContact.setDisplayed(true);

            if(lastSelectedContact!=null)
                lastSelectedContact.setDisplayed(false);
            lastSelectedContact=selectedContact;

            enableContactSpecific(false);
            printMessages(selectedContact);
            leftStatus.setText(lastSelectedContact.getNickname()+"@"+lastSelectedContact.getIpString()+":"+lastSelectedContact.getPort());
            
            //roster.updateContactList();
        }
        resolveTypingAvailability();
    }

    private void printMessages(Contact owner)
    {
        String buffer="";
        for (Message msg : owner.getMessages())
        {
                buffer+=msg+"\n";
        }
        messageArea.setText(buffer);
    }

    private void enableContactSpecific(Boolean en)
    {
        messageArea.setDisable(en);
        //typingArea.setDisable(en);
        menuContact.setDisable(en);
    }

    private void resolveTypingAvailability()
    {
        Contact selectedContact=(Contact)contactListView.getSelectionModel().getSelectedItem();

        if(selectedContact==null || !selectedContact.isConnected())
            typingArea.setDisable(true);
        else
            typingArea.setDisable(false);
    }

    public void handleMessageEvent(Identity local,Contact msgId)
    {
        if(msgId.equals(lastSelectedContact))
        {
            //event came to selected contact
            //messageArea.appendText("\n"+msgId.getLastMessage().toString());
            printMessages(lastSelectedContact);

        }
    }

    private static void shout(String text)
    {
        System.out.println("FXCONTROLLER: "+text);
    }
}
