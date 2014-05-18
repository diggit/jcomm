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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

public class Utils
{
		public static String stringHash(String input)
		{
			String alg="SHA-256";
			try
			{
				MessageDigest md = MessageDigest.getInstance(alg);
				byte[] hash = md.digest(input.getBytes("UTF-8"));

				 //converting byte array to Hexadecimal String
				StringBuilder sb = new StringBuilder(2*hash.length);
				for(byte b : hash)
					{sb.append(String.format("%02x", b&0xff));}
				return sb.toString();
			}
			catch(NoSuchAlgorithmException ex)
			{
				System.out.println("unable to hash with: "+alg);
				return "cantHash";
			}
			catch(UnsupportedEncodingException ex)
			{
				System.out.println("encoding problem occured!");
				return "badEncoding";
			}
		}

}