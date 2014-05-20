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
import java.util.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.time.Instant;

public class Storage
{
	private static String fileName;
	private static Roster roster;
	private boolean loaded=false;

	private static String IDSTART="ID_START";
	private static String IDEND="ID_END";

	private static String CONTACTLISTSTART="CONTACT_LIST_START";
	private static String CONTACTLISTEND="CONTACT_LIST_END";

	private static String CONTACTSTART="CONTACT_START";
	private static String CONTACTEND="CONTACT_END";

	private static String MESSAGE_LIST_START="MESSAGE_LIST_START";
	private static String MESSAGE_LIST_END="MESSAGE_LIST_END";

	private static String MESSAGE_START="MESSAGE_START";
	private static String MESSAGE_END="MESSAGE_END";

	private Identity loadedIdentity=null;
	private List <Contact> loadedContacts=null;



	public Storage(String fileName)
	{
		this.roster=null;
		this.fileName=fileName;
		shout("assigned file "+fileName);
	}
	public Storage(Roster roster,String fileName)
	{
		this.roster=roster;
		this.fileName=fileName;
		shout("assigned file "+fileName);
	}

	public Identity getIdentity()
	{
		this.load();
		
		return loadedIdentity;
	}

	public List<Contact> getContacts()
	{
		this.load();

		if(loadedContacts!=null)
			return loadedContacts;
		else
			return new ArrayList<Contact>();
	}

	public void load()
	{
		//TODO: (30) skip empty lines
		if(loaded)
			return;

		loaded=true;

		Scanner sc;
		try
		{
			sc = new Scanner(new FileInputStream(this.fileName), "UTF8");
		}
		catch(FileNotFoundException ex)
		{
			shout("cannot find file");
			shout(ex.getMessage());
			return;
		}
		shout("file opened");


		try
		{
			this.loadedIdentity=loadIdentity(sc);

			if(roster!=null)
			{
				try
				{
					this.loadedContacts=loadContacts(sc);
				}
				catch(ParseException exn)
				{
					shout("cannot parse Contacts");
					shout(exn.getMessage());
					this.loadedContacts=new ArrayList<Contact>(); //if parse error, return empty contactList
					return;
				}
			}
		}
		catch(ParseException ex)
		{
			shout("cannot parse Identity");
			shout(ex.getMessage());
			this.loadedIdentity=new SimpleIdentity("unknown","error");//if parse error, return default Identity
			return ;
		}
		catch(NoSuchElementException ex){;}

		sc.close();

		
	}

	private List<Contact> loadContacts(Scanner sc) throws ParseException
	{
		shout("running contacts parser");
		String raw;
		String bound;
		String msg_buff;
		ArrayList<Message> messages;
		Contact lastOne;
		Direction messageDirection;
		long messageTimeStamp;

		//buffers for parsing contact info
		String fp,nickname;
		int port;
		InetAddress ip;

		List <Contact> contacts=new ArrayList<Contact>();


		parseTag(sc,CONTACTLISTSTART);
		while(!(raw=sc.nextLine()).equals(CONTACTLISTEND))
		{
			shout("parsing contact");
			parseTag(raw,CONTACTSTART);
			nickname=sc.nextLine();
			shout("nick: "+nickname);
			fp=sc.nextLine();
			shout("fingerprint: "+fp);
			port=Integer.parseInt(sc.nextLine());
			shout("port: "+port);

			raw=sc.nextLine();
			shout("raw string IP for parsing: "+raw);
			try
			{
				ip=InetAddress.getByName(raw);
				shout("IP parsed");
			}
			catch(UnknownHostException ex)
			{
				shout("cannot parse IP");
				throw new ParseException("cannot parse IP from string: "+raw,0);
			}
			lastOne=new Contact(roster,nickname,fp,ip,port);
			contacts.add(lastOne);



			//parse messages
			messages=new ArrayList<Message>();
			parseTag(sc,MESSAGE_LIST_START);
			raw=sc.nextLine();
			if (!raw.equals(MESSAGE_LIST_END))//if not empty message list
			{	
				bound=raw;
				shout("found unique bound: "+bound);

				while(!(raw=sc.nextLine()).equals(MESSAGE_LIST_END))
				{

					try
					{
						parseTag(raw,MESSAGE_START);
						messageDirection=Direction.fromString(sc.nextLine());//direction
						try
						{
							raw=sc.nextLine();
							messageTimeStamp=Long.parseLong(raw);
						}
						catch(Exception e)
						{
							shout("cannot parse message time");
							throw new ParseException("cannot parse time from: "+raw,0);
						}
						
						msg_buff=new String();
						msg_buff="";
						while(!(raw=sc.nextLine()).equals(bound))
						{
							shout("concating to message: "+raw);
							if(!msg_buff.isEmpty())
								msg_buff+="\n";
							msg_buff+=raw;
						}
						shout("parsed complete message:"+msg_buff);

						if (messageDirection==Direction.IN)
						{
							messages.add(new Message(msg_buff,lastOne,this.loadedIdentity,messageTimeStamp));	
						}
						else if (messageDirection==Direction.OUT)
						{
							messages.add(new Message(msg_buff,this.loadedIdentity,lastOne,messageTimeStamp));
						}
						else
						{
							shout("ERROR, unknown message direction!");
						}
						raw=sc.nextLine();
					}
					catch(ParseException pex)
					{
						shout("message parsing failed: "+pex.getMessage());
						shout("skipping to next");
						while(!(raw=sc.nextLine()).equals(MESSAGE_END));//find nearest messageend and try to continue
					}
					parseTag(raw,MESSAGE_END);
				}
				parseTag(raw,MESSAGE_LIST_END);
				if(!messages.isEmpty())
				{
					lastOne.setMessageHistory(messages);
					for (Message m : messages)
					{
						shout(m.toString());	
					}
					shout("messages assigned to contact!");
				}
				
			}
			parseTag(sc,CONTACTEND);
			
		}
		parseTag(raw,CONTACTLISTEND);
		shout("contacts loaded!");

		return contacts;
	}

	private Identity loadIdentity(Scanner sc) throws ParseException
	{
		shout("parsing local Identity");
		String raw,nickname,hash;
		parseTag(sc,IDSTART);
		nickname=sc.nextLine();
		hash=sc.nextLine();
		parseTag(sc,IDEND);

		shout("parsed nick: "+nickname+"@"+hash);

		return new SimpleIdentity(nickname,hash);
	}

	private static void shout(String text)
	{
		System.out.println("STORAGE: "+text);
	}

	private void parseTag(String raw, String tag) throws ParseException
	{
		if(!raw.equals(tag))
			throw new ParseException("expected: "+tag+", but read: "+raw,0);
		shout("matching tag: "+tag);
	}

	private void parseTag(Scanner sc,String tag) throws ParseException
	{
		parseTag(sc.nextLine(),tag);
	}


	public void store(List<Contact> contactList)
	{
		if (contactList==null) {
			shout("contactList is NULL!");
		}
		shout("storing data");

		BufferedWriter out;
		try
		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.fileName), "UTF8"));
			// out.close();
		}
		catch (UnsupportedEncodingException e)
		{
			shout("wrong encoding of file!");
			shout(e.getMessage());
			return;
		}
		catch (IOException e)
		{
			shout("encoutered IOException while writing file!");
			shout(e.getMessage());
			return;
		}

		Identity local=roster.getIdentity();
	
		try
		{
			List<Message> messageList;
			String bound;

			//identity
			storeLn(out,IDSTART);
			storeLn(out,local.getNickname());
			storeLn(out,local.getFingerprint());
			storeLn(out,IDEND);
			//contacts
			storeLn(out,CONTACTLISTSTART);
			bound=""+Instant.now().toEpochMilli();
			for(Contact c:contactList)
			{
				storeLn(out,CONTACTSTART);
				storeLn(out,c.getNickname());
				storeLn(out,c.getFingerprint());
				storeLn(out,Integer.toString(c.getPort()));
				storeLn(out,c.getIpString());

				storeLn(out,MESSAGE_LIST_START);
				messageList=c.getMessages();
				if(!messageList.isEmpty())
				{
					storeLn(out,bound);
					for (Message m:messageList ){
						storeLn(out,MESSAGE_START);
						if(local.equals(m.getTarget()))
							storeLn(out,Direction.IN.toString());
						else
							storeLn(out,Direction.OUT.toString());	
						storeLn(out,""+m.getStampInMillis());
						storeLn(out,m.getRaw());
						storeLn(out,bound);
						storeLn(out,MESSAGE_END);

					}
				}
				
				storeLn(out,MESSAGE_LIST_END);

				storeLn(out,CONTACTEND);
			}
			storeLn(out,CONTACTLISTEND);
			out.close();

		}
		catch(IOException e)
		{
			shout("write of file failed!");
			shout(e.getMessage());
		}
		shout("written and closed");
	}

	private void storeLn(BufferedWriter out,String line) throws IOException
	{
		out.write(line);
		out.newLine();
	}

	enum Direction
	{
		IN("IN"),OUT("OUT"),UNKNOWN("UNKNOWN");

		private Direction(final String text) {
	        this.text = text;
	    }

	    private final String text;

	    @Override
	    public String toString() {
	        return text;
	    }

	    public static Direction fromString(String text)
	    {
			if (text != null)
			{
				for (Direction b : Direction.values())
				{
					if (text.equalsIgnoreCase(b.text))
					{return b;}
				}
			}
			return Direction.UNKNOWN;
		}
	}



	
}