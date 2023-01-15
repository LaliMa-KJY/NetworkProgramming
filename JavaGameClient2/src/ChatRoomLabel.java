import java.awt.Image;
import java.util.Vector;

import javax.swing.*;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import java.awt.Font;

public class ChatRoomLabel extends JLabel {

	public String roomId;
	public String userlist;
	public ChatRoomLabel(String roomId, String userlist) {
		setFont(new Font("한컴 말랑말랑 Regular", Font.PLAIN, 12));
		setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setBackground(new Color(192, 192, 192));
		
		this.setSize(370, 120);
		this.roomId = roomId;
		this.userlist = userlist;

		setText("<html><body><big>&nbsp;&nbsp;&nbsp;"+ userlist
				+ "</big><br>&nbsp;&nbsp;&nbsp;&nbsp;"
				+ this.roomId + "</body></html>");
		
		
	}

}
