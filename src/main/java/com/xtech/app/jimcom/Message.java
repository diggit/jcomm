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
import java.util.Calendar;

public class Message
{
	private final String msg;
	private final Identity sender,target;

	Calendar cal;
	SimpleDateFormat sdf;

	public Message(String text, Identity sender, Identity target)
	{
		this.msg=text;
		this.sender=sender;
		this.target=target;
		this.cal = Calendar.getInstance();
		this.sdf = new SimpleDateFormat("[HH:mm:ss]");
	}

	@Override
	public String toString()
	{
		return sdf.format(cal.getTime())+" <"+sender.getNickname()+"> "+msg;
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