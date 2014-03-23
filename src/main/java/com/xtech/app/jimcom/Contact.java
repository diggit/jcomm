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

import java.net.InetAddress;
import java.net.Socket;

public class Contact
{
	//basic ID
	private String nickname;
	private String figerprint;
	//extended ID
	
	//last seen, last ip,...

	//session only
	Socket sck; //socket to this contact
	boolean isOnline;
	InetAddress ip;

	public String getNickname()
	{
		return nickname;
	}
	public String getFingerprint()
	{
		return figerprint;
	}

	Contact(String nick, String fp)
	{
		nickname=nick;
		figerprint=fp;
	}

	@Override
	public String toString()
	{
		if(ip==null)
			return nickname+"(off)";
		else
			return nickname+"("+ip.getHostAddress()+")";
	}

	
}