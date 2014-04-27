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

import org.xtech.app.jimcom.Message;

import java.util.ArrayList;
import java.net.*;
import java.io.*;
import java.lang.Thread;

public class Contact extends Thread
{	

	private int port;
	//basic ID
	private String nickname;
	private String figerprint;
	
	//message history
	ArrayList<Message> messageHistory=new ArrayList<Message>();
	
	//last seen, last ip,...

	//session only
	Socket sck; //socket to this contact
	boolean isOnline;
	InetAddress ip;

	private BufferedReader in=null;//our incomming stream
	private PrintWriter out = null;


	public void run()//handling connection
	{
		String rawIncomming;
		if(ip==null || port==0)
		{
			System.out.println("you must setup connection first!");
			return;
		}
		else
		{
			this.connect();
			try
			{
				rawIncomming=this.read();
				//TODO:parse incomming

				messageHistory.add(new Message(rawIncomming,this));
				//notify somethig?
			}
			catch (IOException ex)
			{
				System.out.println(nickname+" unable to read incomming message!");
			}
		}

	}

	
	Contact(String nick, String fp)
	{
		nickname=nick;
		figerprint=fp;
	}

	public void setConnection(InetAddress ip,int port)
	{
		this.ip=ip;
		this.port=port;
	}

	public String getNickname()
	{
		return nickname;
	}
	public String getFingerprint()
	{
		return figerprint;
	}

	public boolean connect()
	{

		try
		{
			sck = new Socket(ip,port);
		}
		catch (IOException e)
		{
			System.out.println(nickname+"connection failed, host is probably down...");
			//System.out.println(e);
			return false;
		}

		try
		{
			in = new BufferedReader(new InputStreamReader(sck.getInputStream()));
		}
		catch (IOException ex)
		{
			System.out.println(nickname+"can't establish input stream!");
			return false;
		}

		try
		{
			out = new PrintWriter(sck.getOutputStream(),true);
		}
		catch(IOException ex)
		{
			System.out.println(nickname+"can't establish output stream");
			return false;
		}

		System.out.println(nickname+"connection established");
		return true;
	}



	@Override
	public String toString()
	{
		if(ip==null)
			return nickname+"(off)";
		else
			return nickname+"("+ip.getHostAddress()+")";
	}

	@Override
	public boolean equals(Object o)
	{
		if(o==null)
			return false;
		Contact eq=(Contact)o;
		return nickname.equals(eq.getNickname())&&figerprint.equals(eq.getFingerprint());
		
	}

	@Override
	public int hashCode()
	{
		int hash=3;
		hash+=nickname.hashCode()*5;
		hash+=figerprint.hashCode()*13;
		return hash;
	}

	private String read() throws IOException
	{
		//if(!isOnline)
		// throw new IOException("contact is not online, can't read input");

		
		String message=in.readLine();
		System.out.println("got incomming message: "+message);
		return message;

	}

	
}

