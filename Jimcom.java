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

import java.net.*;
import java.io.*;
import javax.swing.*;

public class Jimcom
{
	// public static void main(String[] args)
	// {
	// 	try
	// 	{
	// 		ServerSocket server=new ServerSocket(42917);
	// 		System.out.println("server");
	// 		Socket client=server.accept();
	// 		System.out.println("client");

	// 		BufferedReader in = new BufferedReader(
	// 		new InputStreamReader(client.getInputStream()));
	// 		PrintWriter out = new PrintWriter(client.getOutputStream(),true);
	// 	}

	// 	catch (IOException e)
	// 	{
	// 		System.out.println("Accept failed: 4321");
	// 		System.exit(-1);
	// 	}
	// 	System.out.println("init done");
	

	public static void main(String args[])
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				Gui dlg = new Gui();
				dlg.setVisible(true);
			}
		});
	}

		

		



	
}