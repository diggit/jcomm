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
import java.net.ProtocolException;

public class Protocol
{
	public static final String SUCCESS="OK";
	public static final String FAIL="FAILED";
	public static final String TRANSMISSION_HEAD="<transmission>";
	public static final String TRANSMISSION_TAIL="</transmission>";
	public static final String RESPONSE_HEAD="<response>";
	public static final String RESPONSE_TAIL="</response>";
	public static final String AUTH="<authRequest/>";
	public static final String CONTENT_TAIL="</content>";
	public static final String CONTENT_HEAD_BEGIN="<content=\"";
	public static final String CONTENT_HEAD_END="\" />";
	public static final String MESSAGE_HEAD=CONTENT_HEAD_BEGIN+"text_message"+CONTENT_HEAD_END;
	public static final String MESSAGE_TAIL=CONTENT_TAIL;



	public static String formatIdentity(Identity identity)
	{
		return "<identity nickname=\""+identity.getNickname()+"\" fingerprint=\""+identity.getFingerprint()+"\" />\n";
	}

	public static String authRequest(Identity identity)
	{	return TRANSMISSION_HEAD+"\n"+formatIdentity(identity)+AUTH+"\n"+TRANSMISSION_TAIL;}

	public static String messageSend(Message msg)
	{
		return TRANSMISSION_HEAD+"\n"+MESSAGE_HEAD+"\n"+formatIdentity(msg.getIdentity())+msg.getRaw()+"\n"+MESSAGE_TAIL+"\n"+TRANSMISSION_TAIL;
	}

	public static String authResponseAccept(Identity identity)
	{	return RESPONSE_HEAD+"\n"+SUCCESS+"\n"+formatIdentity(identity)+RESPONSE_TAIL;}

	public static String authResponseReject()
	{	return RESPONSE_HEAD+"\n"+FAIL+"\n"+RESPONSE_TAIL;}

	public static Identity parseIdentity(String line) throws ProtocolException
	{
		// System.out.println("Parsing IDENTITY...");
		//<identity nickname="targetnick" fingerprint="targethash" />
		if(line.startsWith("<identity"))
		{
			// System.out.println("identity tag found");
			int lastIndex=0;

			lastIndex=line.indexOf("nickname")+8+2;
			String targetNickname=line.substring(lastIndex,lastIndex=line.indexOf("\"",lastIndex));
			// System.out.println("Parsed nickname: "+targetNickname);

			lastIndex=line.indexOf("fingerprint",lastIndex)+11+2;
			String targetFingerprint=line.substring(lastIndex,lastIndex=line.indexOf("\"",lastIndex));
			// System.out.println("Parsed fingerprint: "+targetFingerprint);

			// shout("remote reported nick is: "+targetNickname);
			// shout("remote reported nick is: "+targetFingerprint);
			return new SimpleIdentity(targetNickname,targetFingerprint);
		}
		else
			throw new ProtocolException("identity tag not found!");

	}

	
}

//Protocol layout

//	<transmission>
//	<identity nickname="somenick" fingerprint="somehash" />
//case 1: content(message)
//	<content type=text_message>
//	some message text bla bla bla...
//	</content>
//case 2: authRequest
//	<authRequest/>
//esac
//	</transmission>

//for EVERY transmission, there is response

//	<response>
//case message Protocol.SUCCESS:
//	Protocol.SUCCESS
//case message FAIL:
//	Protocol.FAILEd
//case authentification Protocol.SUCCESS:
//	Protocol.SUCCESS
//	<identity nickname="targetnick" fingerprint="targethash" />
//case authentification Protocol.FAILEd:
//	Protocol.FAILEd
//case default:
//	not supported
//	</response>