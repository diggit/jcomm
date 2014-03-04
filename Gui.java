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

import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

public class Gui extends JFrame implements ActionListener
{
	private JComboBox<Contact>	status;
	private JList		roster_list;
	private JMenuBar 	menu_bar;
	private JMenu		menu;
	
	private JMenuItem	quit_item;


	public Gui()
	{
		super();
		setTitle("JIMcom");

        //setSize(200, 200);
        //setLocation(100, 100);
        //setResizable(false);
 		
 		Container pane=this.getContentPane();
        menu_bar=new JMenuBar();
        roster_list=new JList();
        menu=new JMenu("STH");

        //setup Menu
        menu_bar.add(menu);
        JMenuItem item= new JMenuItem ("nothing!");
        menu.add(item);
        menu.addSeparator();
        quit_item=new JMenuItem ("Quit");
        quit_item.addActionListener(this);
        menu.add(quit_item);


        //setup status combo
        //String[] status_names={"online","away","offline"};

        status=new JComboBox<Contact>();
        Contact me=new Contact("me","my_fp");
        status.addItem(me);
        //status.setSelectedIndex(2);

		
		//arrange elements
		this.setJMenuBar(menu_bar);
        //pane.add(but,BorderLayout.PAGE_END);
        pane.add(roster_list,BorderLayout.CENTER);
        pane.add(status,BorderLayout.PAGE_END);
        
        //but.addActionListener(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }
    

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==quit_item)
		{
			dispose();
		}
	}

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