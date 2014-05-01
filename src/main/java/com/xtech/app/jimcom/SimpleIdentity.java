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





public class SimpleIdentity implements Identity
{
	//basic ID
	private String nickname;
	private String figerprint;


	public SimpleIdentity(String nick, String fp)
	{
		nickname=nick;
		figerprint=fp;
	}

	public String getNickname()
	{
		return nickname;
	}

	public String getFingerprint()
	{
		return figerprint;
	}

	@Override
	public String toString()
	{
		return nickname+"@"+figerprint;
	}
	@Override
	public boolean equals(Object o)
	{
		if(o==null)
			return false;
		Identity eq=(Identity)o;
		boolean out=nickname.equals(eq.getNickname())&&figerprint.equals(eq.getFingerprint());
		return out;
	}
}