//	This program is free software: you can redistribute it and/or modify
//	it under the terms of the GNU General Public License as published by
//	the Free Software Foundation, either version 3 of the License, or
//	(at your option) any later version.

//	This program is distributed in the hope that it will be useful,
//	but WITHOUT ANY WARRANTY; without even the implied warranty of
//	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//	GNU General Public License for more details.

//	You should have received a copy of the GNU General Public License
//	along with this program.  If not, see <http://www.gnu.org/licenses/>.

//	author bachapat aka diggit

package org.xtech.app.jimcom;

import org.xtech.app.jimcom.*;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.NullPointerException;
import java.lang.InterruptedException;
import java.lang.Thread;

import javafx.application.Platform;


//all the work is done here
public class Roster extends Thread
{
	private List<Contact> contactList;

	GuiFXController controller;
	Roster(GuiFXController controller)
	{
		if(controller==null)
			throw new NullPointerException("refference to controller was null!");
		this.controller=controller;
		contactList=new ArrayList();
	}

	public void run()
	{
		// loadStoredContacts()

		System.out.println("testing...");

		Contact c2=new Contact("FLOLED","none");
        addContact(c2);
        

        Listener listener=new Listener(this,5564);
        listener.start();//start listener to receive incomming connections

        serve();
	}

	private void serve()
	{
		while(true)
		{
			System.out.println("updatting contact list...");
			// for (Contact c : contactList)
			// {

			// }
			Platform.runLater(new Runnable(){public void run() {controller.updateContactListView(contactList);}});
			try
			{
				this.sleep(10000);
			}
			catch(InterruptedException ex)
			{
				System.out.println("roster woken from sleep");
			}
		}
	}

	

	public void loadStoredContacts()
	{
		;
	}

    synchronized void addContact(Contact newContact)
    {
    	System.out.println("adding: "+newContact);
    	Contact item;
    	boolean listed=false;

    	if(!contactList.contains(newContact))
    	{
    		System.out.println("adding contact...");
    		contactList.add(newContact);

    		Platform.runLater(new Runnable(){public void run() {controller.updateContactListView(contactList);}});
			//OR
			//JRE8 only
			//Platform.runLater(() -> controller.updateContactListView(contactList));
    		newContact.start();
    		System.out.println("done");
    	}
    	else
    	{
    		System.out.println("this Contact is already listed");
    	}
    }

	public void updateAvailability()
	{
		;
	}
	public void transmitStatusChange()
	{
		;
	}
	public void	serveIncommingConnection(Socket incomming)
	{
		BufferedReader in=null;//our incomming stream
		Roster roster;//instance of roster, which to report incomming events
		boolean running;
		final int retries=3;
		PrintWriter out;

		try
			{incomming.setSoTimeout(1000);}//timeout for reading???
		catch(SocketException ex)
			{System.out.println("unable to set timeout");}

		try
		{
			in = new BufferedReader(new InputStreamReader(incomming.getInputStream()));
		}
		catch (IOException ex)
		{
			System.out.println("connection lost...");
		}

		try
			{out = new PrintWriter(incomming.getOutputStream(),true);}
		catch(IOException ex)
		{
			System.out.println("no outputStream");
			return;
		}
		

		out.print("identify\n");
		
		String response="";
		try
			{response=in.readLine();}//blocking
		catch(IOException ex)
			{System.out.println("response not received in time");}
		if(!response.equals("jimcom"))
		{
			System.out.println("incomming connection does not belong to jimcom app...");
			return;
		}
		out.print("ID");
		try
			{response=in.readLine();}//blocking
		catch(IOException ex)
			{System.out.println("response not received in time");}

		
	}
	
}

//  class Reader extends Thread
// {
// 	private Socket sck;
// 	private BufferedReader in=null;//our incomming stream
// 	private Roster roster;//instance of roster, which to report incomming events
// 	private boolean running;
// 	private final int retries=3;

// 	public void run()
// 	{
// 		for(int retry=0;retry<retries && in==null;retry++)
// 		{
// 			try
// 			{
// 				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
// 			}
// 			catch (IOException ex)
// 			{
// 				System.out.println("unable to open stream for reading, retriing...");
// 			}
// 		}
// 		if(in==null)
// 		{	
// 			System.out.println(retries+" of reconnection failed, giving up!");
// 			return;
// 		}
// 		System.out.println("got input stream");
// 	}

// 	public Reader(Socket socket, Roster roster)
// 	{
// 		this.sck=socket;
// 		this.roster=roster;
// 	}
// 	//PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
