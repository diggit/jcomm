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

public class Listener extends Thread
{
	private final Roster roster;
	private final int port;
	private final int backlog=5;//how many connections can wait in queue
	private final InetAddress bindAddress;
	private volatile boolean running=true;

	private ServerSocket server=null;
	private Status serverState=Status.Online;

	public Listener(Roster roster,int port)
	{
		this.roster=roster;
		this.port=port;
		this.bindAddress=null;
	}

	public Listener(Roster roster,int port,InetAddress bindAddress)
	{
		this.roster=roster;
		this.port=port;
		this.bindAddress=bindAddress;
	}

	public void exit()
	{
		running=false;
		setState(Status.Offline);
		this.interrupt();
		
	}

	synchronized  public void run()	
	{
		Socket client=null;

		while(this.running)
		{
			if(this.serverState==Status.Offline)
			{
				shout("contact offline, waiting for wake");
				try
					{this.wait();}
				catch(InterruptedException ex)
					{shout("woken from offline sleep");}	
			}
			else if (this.serverState==Status.Online)
			{
				while(this.running && this.serverState==Status.Online && (server==null || server.isClosed()))
				{
					try
					{
						if(this.bindAddress!=null)
						{
							shout("setting listener at specific IP: "+bindAddress);
							this.server=new ServerSocket(this.port,backlog,bindAddress);

						}
						else
						{
							shout("setting listener");						
							this.server=new ServerSocket(this.port,backlog);
						}
						shout("socket opened, waiting for incomming cons...");
					}
					catch(IOException ex)
					{
						shout("cannot create server on this port, unable to serve incomming connections!");
						shout(ex.getMessage());

						try
							{this.sleep(5000);}//sleep before next attemp
						catch(InterruptedException wex)
							{shout("woken from sleep, retrying to start server...");}
					}
				}

				while(this.running && this.serverState==Status.Online )//listen for incomming connections
				{	
					//TODO: (20) do not listen when offline state set
					try
					{
						client=server.accept();
						shout("got incomming con! processing...");
						this.roster.serveIncommingConnection(client);//give new socket to Roster, which will identify other side and process request...
						shout("processing done, back to listening...");
					}
					catch (IOException ex)
					{
						shout("Accept failed on port: "+this.port);
						shout(ex.getMessage());
					}
				}//waiting for incomming
			}//if Onlie
		}//main cycle
		shout("terminated!");
	}

	private void shout(String text)
	{
		//opt TODO: (90) use logger
		System.out.println("LISTENER: "+text);
	}

	public void setState(Status newServerState)
	{
		if(newServerState==this.serverState)
		{
			shout("Server is already: "+this.serverState);
			return;
		}

		this.serverState=newServerState;
		if(newServerState==Status.Offline)
		{
			shout("switching to offline");
			this.interrupt();//if waiting between create server retries
			
			if(server!=null)//if server is created (accept() running), stop it
				try
					{server.close();}
				catch(IOException e)
					{shout("server close failed");}
		}
		else if (newServerState==Status.Online)
		{
			shout("waking from offline mode");
			this.interrupt();//wake from sleep
		}
		shout("new serverState set!");
	}
}