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

	private Identity local;
	private volatile List<Contact> contactList;
	private final List<String> args;

	private Storage storage;

	GuiFXController controller;
	Roster(GuiFXController controller,List<String> args)
	{
		if(controller==null)
			throw new NullPointerException("refference to controller was null!");
		this.controller=controller;
		contactList=new ArrayList();
		storage=new Storage(this,"storedData");
		if(args.contains("-Atest"))
		{
			local=new SimpleIdentity("Atest","somecoolhash");
		}
		else if (args.contains("-Btest"))
		{
			local=new SimpleIdentity("Btest","somehothash");
		}
		else
			local=storage.getIdentity();
		this.args=args;

		shout("args...");
		for (String arg : args) {
			System.out.println(arg);
		}
	}

	public List<String> getArgs()
	{
		return args;
	}


	public void run()
	{
		shout("started!");
		loadStoredContacts();

		shout("testing...");

		Listener listener;
		if(args.contains("-Atest"))
		{
			shout("setting up as node A");
			listener=new Listener(this,5564,convertAddress(192, 168, 1, 128));


		}
		else if (args.contains("-Btest"))
		{
			shout("setting up as node B");
			listener=new Listener(this,5564,convertAddress(192, 168, 1, 139));
		}
        
		else
        	listener=new Listener(this,5564);
        listener.start();//start listener to receive incomming connections

        serve();
	}

	private void serve()
	{
		while(true)
		{
			shout("updating contact list...");
			//TODO: (40) do something useful during refresh
			for (Contact c : contactList)
			{
				shout("contact: "+c.getNickname());
			}
			shout("updated, sleeping...");
			updateAvailability();
			try
			{
				this.sleep(30000);
			}
			catch(InterruptedException ex)
			{
				shout("roster woken from sleep");
			}
		}
	}

	
	public static InetAddress convertAddress(int a,int b,int c,int d)
	{
		if(a<0||a>255||b<0||b>255||c<0||c>255||d<0||d>255)
		{
			shout("IP number out of range 0~255 !");
			return null;
		}
		else
		{
			if(a>127)
				a-=256;
			if(b>127)
				b-=256;
			if(c>127)
				c-=256;
			if(d>127)
				d-=256;
			//omfg why this shit has bytes from -128 to 127 ?!
			try
			{
				return InetAddress.getByAddress(new byte[]{(byte)a,(byte)b,(byte)c,(byte)d});
			}
			catch(UnknownHostException ex)
			{
				shout("UnknownHostException");
				return null;
			}
		}
	}
	public void loadStoredContacts()
	{
		shout("loaoding contacts");
		Contact c3;
		if(args.contains("-Atest"))
		{
			addContact(new Contact(this,"Btest","somehothash",convertAddress(192, 168, 1, 139),5564,local));
		}
		else if (args.contains("-Btest"))
		{
			addContact(new Contact(this,"Atest","somecoolhash",convertAddress(192, 168, 1, 128),5564,local));
		}
		else
		{
			shout("reading from file...");
			contactList=storage.getContacts();
			for (Contact c : contactList )
			{
				shout("starting contact: "+c);
				c.start();	
			}
			updateAvailability();
		}


		shout("all contacts loaded!");
	}

    synchronized void addContact(Contact newContact)
    {
    	shout("adding: "+newContact);
    	Contact item;

    	if(!contactList.contains(newContact))
    	{
    		shout("adding contact...");
    		contactList.add(newContact);

    		updateAvailability();
    		newContact.start();
    		shout("done");
    	}
    	else
    	{
    		shout("this Contact is already listed");
    	}
    }

	public void updateAvailability()
	{
		Platform.runLater(new Runnable(){public void run() {controller.updateContactListView(contactList);}});
	}
	public void transmitStatusChange()
	{
		;
	}

	public void messageEvent(Contact id)
	{
		final Contact idCopy=id;

		Platform.runLater(new Runnable(){public void run() {controller.handleMessageEvent(local, idCopy);}});
		//just pipe this change to controller
	}

	public boolean	serveIncommingConnection(Socket incommingConnection)
	{
		shout("serving incomming connection...");
		BufferedReader in=null;//our incommingConnection stream
		Roster roster;//instance of roster, which to report incommingConnection events
		boolean running;
		final int retries=3;
		PrintWriter out;

		try
			{incommingConnection.setSoTimeout(1000);}//timeout for reading
		catch(SocketException ex)
			{shout("unable to set timeout"); return false;}

		try
		{
			in = new BufferedReader(new InputStreamReader(incommingConnection.getInputStream()));
		}
		catch (IOException ex)
		{
			shout("no InputStream");
			return false;
		}

		try
			{out = new PrintWriter(incommingConnection.getOutputStream(),true);}
		catch(IOException ex)
		{
			shout("no outputStream");
			return false;
		}

		shout("opened I/O streams to new contact...");
		

		try
		{
			shout("receiving&parsing tansmission...");
			if(in.readLine().trim().equals(Protocol.TRANSMISSION_HEAD))
			{
				shout("got TRANSMISSION_HEAD!");

				Identity incommingIdentity=Protocol.parseIdentity(in.readLine().trim());


				if(in.readLine().trim().equals(Protocol.AUTH))
				{
					shout("AUTH request received!");

					if(in.readLine().trim().equals(Protocol.TRANSMISSION_TAIL))//correctly closed tags
					{
						shout("dataframe properly closed, YEAH!");
						if (local.equals(incommingIdentity))
						{
							shout("this is WEIRD, LOOPBACK?");
							out.println(Protocol.authResponseReject());
							return false;
						}
						else
						{
							int index=contactList.indexOf(incommingIdentity);
							if(index>=0)//if identity is already in list
							{
								shout("contact "+incommingIdentity+" exists in database...");
								out.println(Protocol.authResponseAccept(local));
								if(contactList.get(index).isOnline())
									shout("contact already online!");
								else
								{
									shout("binding socket and bringing online!");
									//shout("WARNING: binding disabled!");
									contactList.get(index).bindSocket(incommingConnection);	
								}

								
							}
							else
							{
								shout("contact "+incommingIdentity+" not yet known");
								//TODO: (20) do some decisions, accept or not? user interraction needed - GUI
								this.addContact(new Contact(this,incommingIdentity,incommingConnection,local));
							}
							updateAvailability();
							return true;
						}
						
					}
				}

			}
			
			shout("wrong protocol format!");
		}
		catch(IOException ex)
		{
			shout("no client request, timeout!");
		}
		
		return false;
	}
	private static void shout(String text)
	{
		//opt TODO: (90) use logger
		System.out.println("ROSTER: "+text);
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
