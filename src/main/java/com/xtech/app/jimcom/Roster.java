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

	private volatile boolean running=true;

	private Storage storage;

	GuiFXController controller;
	Roster(GuiFXController controller,List<String> args,Identity local)
	{
		if(controller==null)
			throw new NullPointerException("refference to controller was null!");
		this.controller=controller;
		contactList=new ArrayList();
		storage=new Storage(this,"storedData");
		if(args.contains("-Atest"))
		{
			this.local=new SimpleIdentity("Atest","somecoolhash");
		}
		else if (args.contains("-Btest"))
		{
			this.local=new SimpleIdentity("Btest","somehothash");
		}
		else
			this.local=local;
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

	public Identity getIdentity()
	{
		return local;
	}

	public void setLocalIdentity(Identity id)
	{
		if(id!=null)
			if(local.equals(id))
				shout("new identity is  same as now, ognoring");
			else
			{
				shout("updating local identity");
				this.local=id;

				shout("informing contacts about change...");
				//disconnect all at first

				//TODO: (20) inform contacts about change instead of reconnecting
				for(Contact c:contactList)
				{
					c.setConnectionState(Status.Offline);
				}

				//set new local ID and reconnect
				for(Contact c:contactList)
				{
					c.setNewLocalID(local);
					c.setConnectionState(Status.Online);
				}
			}
	}

	public void run()
	{
		shout("started!");
		shout("local identity: "+this.local);
		loadStoredContacts();

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

		listener.start();//start listener to receive incomming connections, forking

		serve();//blocking until exit() called
		shout("serving stopped");


		storeAll();
		shout("terminating all connections first!");
		listener.exit();
		if(!contactList.isEmpty())
		{
			for (Contact c : contactList)//quickly stop them
			{
				shout("stopping: "+c);
				c.exit();
			}	

			for (Contact c : contactList)//then wait for their death!
			{
				try
				{c.join();}
				catch(InterruptedException ex)
				{
					shout("unable to join roster thread!");
				}
			}
		}
		try
		{listener.join();}
		catch(InterruptedException ex)
		{shout("unable to join listener thread!");}

		shout("terminated!");
	}

	private void serve()
	{
		while(this.running)
		{
			shout("updating contact list...");
			//TODO: (40) do something useful during refresh
			for (Contact c : contactList)
			{
				shout("contact: "+c.getNickname());
			}
			shout("updated, sleeping...");
			updateContactList();
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

	public void exit()
	{
		this.running=false;
		this.interrupt();
	}

	public List<Contact> getContactList()
	{
		return contactList;
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
			addContact(new Contact(this,"Btest","somehothash",convertAddress(192, 168, 1, 139),5564));
		}
		else if (args.contains("-Btest"))
		{
			addContact(new Contact(this,"Atest","somecoolhash",convertAddress(192, 168, 1, 128),5564));
		}
		else
		{
			shout("reading from file...");
			contactList=storage.getContacts();
			if (contactList==null)
			{
				contactList=new ArrayList<Contact>();	
			}

			for (Contact c : contactList )
			{
				shout("starting contact: "+c);
				c.start();	
			}
			updateContactList();
		}


		shout("all contacts loaded!");
	}

	public void storeAll()
	{
		storage.store(contactList);
	}

	synchronized void addContact(Contact newContact)
	{
		shout("adding: "+newContact);
		Contact item;

		if(!contactList.contains(newContact))
		{
			shout("adding contact...");
			contactList.add(newContact);

			updateContactList();
			newContact.start();
			shout("done");
		}
		else
		{
			shout("this Contact is already listed");
		}
	}

	synchronized void removeContact(Contact toRemove)
	{
		toRemove.exit();
		shout("contact "+toRemove+"exited");
		try
		{
			toRemove.join();//if something screws up, this will block GUI!
			shout("contact "+toRemove+"thread terminated");
		}
		catch(InterruptedException ex)
		{
			shout("unable to join contact thread!");
		}
		contactList.remove(toRemove);
		shout("contact: "+toRemove.toString()+" removed");
		updateContactList();
	}

	public void updateContactList()
	{
		if(contactList!=null)
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
							// shout("local:\t"+local);
							// shout("incomming:\t"+incommingIdentity);
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
								if(contactList.get(index).isConnected())
									shout("contact already online!");
								else
								{
									shout("binding socket and bringing online!");
									contactList.get(index).bindSocket(incommingConnection);	
								}

								
							}
							else
							{
								shout("contact "+incommingIdentity+" not yet known");
								//TODO: (20) do some decisions, accept or not? user interraction needed - GUI
								this.addContact(new Contact(this,incommingIdentity,incommingConnection));
							}
							updateContactList();
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