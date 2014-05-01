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

public class Contact extends Thread implements Identity
{	

	private Identity localID;
	//basic ID
	private String nickname;
	private String figerprint;
	
	//message history
	private volatile ArrayList<Message> messageHistory=new ArrayList<Message>();
	
	//last seen, last ip,...

	//session only
	private volatile Socket sck; //socket to this contact
	private volatile boolean online=false;
	private volatile InetAddress ip;
	private volatile int port;

	private BufferedReader in;//our incomming stream
	private PrintWriter out;

	//handling connection
	public void run()
	{
		String raw;

		while(true)
		{
			if(!online)
			{	
				if(connect())
				{
					targetState state=handshake();
					shout("HANDSHAKE over");


					if(state==targetState.MATCH)
					{
						shout("target identity match!");
						online=true;
					}
					else
					{
						if (state==targetState.MISMATCH)
						{
							shout("ID mismatch, WHAT TO DO?!");

							if(nickname.isEmpty()&&figerprint.isEmpty())
							{
								//TODO: (20) when new contact is created, accept even when mismatching (we dont know ID yet), ID is not set known
								;
							}
							else
							{
								//TODO: (40) what to do in case of mismatch? some GUI?
								;
							}
						} 

						else if (state==targetState.REJECTED) 
							shout("our connection attempt was REJECTED :( ");
						else if (state==targetState.PROTOCOL_ERROR)
							shout("encountered error during handshake, opponents port is probably occupied by some different process...");

						else
						{
							shout("failed to create con OR verify oponent, unknown state!");
						}

						shout("closing socket NEWAY...");
						
						try
							{sck.close();}
						catch(IOException e)
							{shout("closing failed, probably closed");}

					}

					

				}

				if(!online)//when connection was not succ. opened, wait and retry...
				{
					try
						{this.sleep(10000);}
					catch(InterruptedException ex)
						{shout("forced to recheck connection!");}		
				}
			}
			else //we are online (in case of success of connection attempt above, loop will be looped one more time to get here, but who cares...)
			{
				try
				{
					this.sck.setSoTimeout(0);
					shout("socket timeout set back to infinity for patient waiting");
				}//reset timeout back to infinity
				catch(SocketException ex)
					{shout("unable to RESET socket SO timeout!");}
				
				try
				{
					shout("waiting for message...");
					
					while(true)//TODO: (30) when to stop listening for messages
					{
						//TODO: (20) handle incomming events/messages
						raw=in.readLine();
						if(raw==null)
						{
							shout("read NULL message");
							if(sck.isConnected())
							{
								shout("but still connected...");
							}
							else
								throw new IOException("connection lost!");
						}
						else
						{
							//shout("incomming message: "+raw);
							if(raw.equals(Protocol.TRANSMISSION_HEAD))
							{
								shout("parsed TRANSMISSION_HEAD");
								if(in.readLine().equals(Protocol.MESSAGE_HEAD))
								{
									Identity identity=Protocol.parseIdentity(in.readLine());
									//TODO: (10) parse message text
									//TODO: (20) process Message (print to messageView and respond) 
								}

							}
						}
						

					}
					
					
				}
				catch(IOException ex)
				{
					shout("got an IOException:\n"+ex.getMessage());
					online=false;
					try
						{sck.close();}
					catch(IOException e)
						{shout("closing failed, probably closed");}
				}

			}


		}
	}
	public boolean isOnline()
	{
		return online;
	}

	private targetState handshake()
	{
		String rawIncomming;
		targetState output;

		try
			{this.sck.setSoTimeout(1000);}//for case of missing response
		catch(SocketException ex)
		{
			shout("unable to set socket SO timeout!");
			return targetState.PROTOCOL_ERROR;
		}
		

	//tell the other side who am I
		String buf=Protocol.authRequest(localID);
		out.println(buf);
		//out.flush();
		shout("AUTH request sent...");

	//read response
		try
		{
			rawIncomming=in.readLine();
			if(rawIncomming.equals(Protocol.RESPONSE_HEAD))
			{
				shout("receiving&parsing response...");
			}
			else
				return targetState.PROTOCOL_ERROR;

			rawIncomming=in.readLine();
			if(rawIncomming.equals(Protocol.SUCCESS))
			{
				shout("AUTH phase successful!");
			
				//read target identity
				rawIncomming=in.readLine();
				shout("read: "+rawIncomming);

					
				//verify matching ID
				try
				{
					Identity identity=Protocol.parseIdentity(rawIncomming);
					shout("parsed identity: "+identity);
					if(identity.equals(this))//if remote identity matches expected one
					{
						shout("target ID matching!");
						output=targetState.MATCH;
					}
					else
					{
						shout("target ID NOT matching!");
						output=targetState.MISMATCH;
					}	
				}
				catch(ProtocolException ex)
				{
					shout("protocol ERROR!");
					shout(ex.getMessage());
					return targetState.PROTOCOL_ERROR;
				}
				shout("verification over");


			}
			else if (rawIncomming.equals(Protocol.FAIL))
			{
				shout("authentification rejected!");
				output=targetState.REJECTED;
			}
			else
			{
				shout("unknown response value:"+rawIncomming);
				output=targetState.PROTOCOL_ERROR;
			}

		//parse esponse endtag
			shout("looking for endtag...");
			if (in.readLine().equals(Protocol.RESPONSE_TAIL))
			{
				shout("endtag found");
				return output;
			}
			else
			{
				shout("protocol error, missing response endtag");
				return targetState.PROTOCOL_ERROR;
			}
		}
		catch(SocketTimeoutException ex)
		{
			shout("target not responded in time!");
			return targetState.PROTOCOL_ERROR;
		}
		catch(IOException ex)
		{
			shout("IOException");
			shout(ex.getMessage());
			return targetState.PROTOCOL_ERROR;
		}
	}

	
	Contact(String nick, String fp,InetAddress ip,int port, Identity localID)
	{
		this.nickname=nick;
		this.figerprint=fp;
		this.ip=ip;
		this.port=port;
		this.localID=localID;
	}

	Contact(Identity id,Socket connection, Identity localID)
	{
		this.nickname=id.getNickname();
		this.figerprint=id.getFingerprint();
		this.sck=connection;//already valid connection
		this.ip=connection.getInetAddress();
		this.port=connection.getPort();
		this.localID=localID;
		this.online=true;
	}


	public void setConnection()
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

	public ArrayList<Message> getMessages()
	{
		return messageHistory;
	}

	public boolean connect()
	{
		shout("trying to connect");
		if(sck==null || sck.isClosed())
		{
			try
			{
				sck = new Socket(ip,port);
			}
			catch (IOException e)
			{
				shout("connection failed, host is probably down...");
				shout(e.getMessage());
				return false;
			}
		}

		try
		{
			in = new BufferedReader(new InputStreamReader(sck.getInputStream()));
		}
		catch (IOException ex)
		{
			shout("can't establish input stream!\n"+ex.getMessage());
			return false;
		}

		try
		{
			out = new PrintWriter(sck.getOutputStream(),true);
		}
		catch(IOException ex)
		{
			shout("can't establish output stream");
			return false;
		}

		shout("connection established");
		return true;
	}

	public void bindSocket(Socket incommmingSocket)
	{
		shout("binding incomming socket to contact");
		this.sck=incommmingSocket;
		this.ip=sck.getInetAddress();
		this.port=sck.getPort();
		this.connect();//attach reader and writer
		this.online=true;
		this.interrupt();//proceed immediate connection
	}



	@Override
	public String toString()
	{
		if(online)
			return nickname+"("+ip.getHostAddress()+")";
		else
			return nickname+"(off)";
	}

	@Override
	public boolean equals(Object o)
	{
		// shout("testing equality...");
		if(o==null)
			return false;
		Identity eq=(Identity)o;
		return this.nickname.equals(eq.getNickname())&&this.figerprint.equals(eq.getFingerprint());
		
	}

	@Override
	public int hashCode()
	{
		int hash=3;
		hash+=nickname.hashCode()*5;
		hash+=figerprint.hashCode()*13;
		return hash;
	}

	// private String read() throws IOException
	// {
	// 	//if(!online)
	// 	// throw new IOException("contact is not online, can't read input");

		
	// 	String message=in.readLine().trim();
	// 	shout("got incomming message: "+message);
	// 	return message;

	// }
	public void sendMessage(String messageText)
	{
		Message msg=new Message(messageText,this,Direction.OUTGOING);

		String toSend=Protocol.messageSend(msg);
		shout("dataframe to send:\n"+toSend);
		out.println(toSend);//format and send message
		out.flush();
		shout("message sent");
	}

	private void shout(String text)
	{
		//opt TODO@34: use logger
		System.out.println("CONTACT ("+nickname+"): "+text);
	}

	enum targetState
	{MATCH,MISMATCH,NOT_FOUND,PROTOCOL_ERROR,REJECTED}
}

