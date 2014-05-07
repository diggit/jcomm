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

	private Identity loadedIdentity=null;
	private List <Contact> loadedContacts=null;


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

		return loadedContacts;
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
			sc = new Scanner(new FileInputStream(fileName), "UTF8");
		}
		catch(FileNotFoundException ex)
		{
			shout("cannot find file");
			shout(ex.getMessage());
			return;
		}


		try
		{
			this.loadedIdentity=loadIdentity(sc);
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
		catch(ParseException ex)
		{
			shout("cannot parse Identity");
			shout(ex.getMessage());
			this.loadedIdentity=new SimpleIdentity("unknown","error");//if parse error, return default Identity
			return ;
		}

		
	}

	private List<Contact> loadContacts(Scanner sc) throws ParseException
	{
		shout("running contacts parser");
		String raw;

		//buffers for parsing contact info
		String fp,nickname;
		int port;
		InetAddress ip;

		List <Contact> contacts=new ArrayList<Contact>();


		parseTag(sc,CONTACTLISTSTART);
		while(!(raw=sc.nextLine()).equals(CONTACTLISTEND))
		{
			//TODO: (10) do real loading of messages from file
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
			parseTag(sc,CONTACTEND);
			contacts.add(new Contact(roster,nickname,fp,ip,port,this.loadedIdentity));
		}

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
		//opt TODO: (90) use logger
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

	public void store()
	{

		try
		{
			Writer out = new BufferedWriter(new OutputStreamWriter(
			new FileOutputStream("storedData"), "UTF8"));
			out.write("test%n");
			out.close();
		}
		catch (UnsupportedEncodingException e)
		{
			shout("wrong encoding of file!");
		}
		catch (IOException e)
		{
			shout("encoutered IOException while writing file!");
		}
	}



	
}