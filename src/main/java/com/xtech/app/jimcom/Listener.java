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

	public void run()	
	{
		Socket client=null;
		ServerSocket server=null;

		while(server==null)
		{
			try
			{
				if(bindAddress!=null)
				{
					shout("setting listener at specific IP: "+bindAddress);
					server=new ServerSocket(this.port,backlog,bindAddress);

				}
				else
				{
					shout("setting listener");						
					server=new ServerSocket(this.port,backlog);
				}
				
			}
			catch(IOException ex)
			{
				shout("cannot create server on this port!");
				shout(ex.getMessage());
			}
			shout("socket opened, waiting for incomming cons...");
		}

		while(true)//listen for incomming connections
		{
			try
			{
				client=server.accept();
				shout("got incomming con! processing...");
				roster.serveIncommingConnection(client);//give new socket to Roster, which will identify other side and process request...
				shout("processing done, back to listening...");
			}

			catch (IOException ex)
			{
				shout("Accept failed on port: "+this.port);
				shout(ex.getMessage());
			}

				
		}
	}

	private void shout(String text)
	{
		//opt TODO: (90) use logger
		System.out.println("LISTENER: "+text);
	}
}