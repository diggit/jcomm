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

import java.text.SimpleDateFormat;
import java.util.*;

public class Message
{
	private final String msg;
	private final Identity sender,target;
	private final String timeStamp;
	private final String dateStamp;

	private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

	private final long stampInMillis;

	//basic contrusctor

	public Message(String text, Identity sender, Identity target)
	{
		this.msg=text;
		this.sender=sender;
		this.target=target;

		Calendar cal = Calendar.getInstance();
		this.stampInMillis=cal.getTimeInMillis();
		Date date=new Date(this.stampInMillis);
		this.timeStamp=timeFormat.format(date);
		this.dateStamp=dateFormat.format(date);
	}

	//constructor for definition of message time (used when loaded from file)
	public Message(String text, Identity sender, Identity target, long millis )
	{
		this.msg=text;
		this.sender=sender;
		this.target=target;
		this.stampInMillis=millis;
		Date date=new Date(this.stampInMillis);
		this.timeStamp=timeFormat.format(date);
		this.dateStamp=dateFormat.format(date);
	}

	public long getStampInMillis()
	{
		return stampInMillis;
	}

	@Override
	public String toString()
	{
		return "["+
		this.timeStamp+"]"+
		" <"+sender.getNickname()+
		"> "+msg;
	}

	public String getRaw()
	{
		return msg;
	}

	public Identity getSender()
	{
		return sender;
	}

	public Identity getTarget()
	{
		return target;
	}

	public boolean isSender(Identity id)
	{
		return sender.equals(id);
	}

	public boolean isTarget(Identity id)
	{
		return target.equals(id);
	}

	public boolean isInvolved(Identity id)
	{
		return sender.equals(id) || target.equals(id);
	}
}