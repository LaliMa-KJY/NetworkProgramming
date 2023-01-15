import java.awt.Image;

import javax.swing.*;
import java.awt.Font;

public class FriendForm extends JLabel {

	public ImageIcon img = new ImageIcon("images/friends1.PNG");
	public String name;
	public String state;
	public FriendForm(String name, String state, ImageIcon img) {
		setFont(new Font("한컴 말랑말랑 Regular", Font.PLAIN, 12));
		
		this.setSize(370, 100);
		this.name = name;
		this.state = state;
		Image i;
		if(img != null)
			i = img.getImage();
		else
			i = this.img.getImage();
		i=i.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		this.img = new ImageIcon(i);
		setIcon(this.img);
		setText("<html><body><strong>&nbsp;&nbsp;"+this.name
				+ "</strong><br>&nbsp;&nbsp;&nbsp;&nbsp;"
				+ this.state + "</body></html>");
	}

}
