import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.*;

public class SelectMemberDialog extends JDialog {
	JCheckBox[] checklist = new JCheckBox[10];
	String selectedUser="";
	Vector<String> selecting = new Vector<String>();
	String username;
	public SelectMemberDialog(JFrame parent, String username) {
		super(parent, true);
		setBounds(new Rectangle(0, 0, 320, 400));
		getContentPane().setLayout(null);
		setSize(320, 435);
		
		this.username = username;
		JLabel lblNewLabel = new JLabel("초대할 멤버를 선택하세요!");
		lblNewLabel.setBounds(0, 0, 305, 35);
		getContentPane().add(lblNewLabel);
		
		//JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
		//chckbxNewCheckBox.setBounds(0, 38, 305, 23);
		//getContentPane().add(chckbxNewCheckBox);
		
		for(int i =0; i <10; i++) {
			checklist[i] = new JCheckBox(JavaGameClientMain.UserList[i].UserName);
			checklist[i].setBounds(0,35*(i+1),200,35);
			checklist[i].addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						for(int j = 0; j<10; j++)
							if(e.getItem()==checklist[j])
								
								selecting.add(JavaGameClientMain.UserList[j].UserName);
					}
					else {
						for(int j = 0; j<10; j++)
							if(e.getItem()==checklist[j])
								selecting.remove(JavaGameClientMain.UserList[j].UserName);
					}
				}
			});
			if(username.equals(JavaGameClientMain.UserList[i].UserName)) {
				checklist[i].setSelected(true);
				checklist[i].setEnabled(false);
			}
			getContentPane().add(checklist[i]);
	}
		
		
		JButton btnNewButton = new JButton("확인");
		btnNewButton.setForeground(new Color(0, 0, 255));
		btnNewButton.setBackground(new Color(128, 255, 0));
		btnNewButton.setBounds(215, 350, 91, 46);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for(int i =0; i < selecting.size(); i++)
					selectedUser += selecting.elementAt(i) +",";
				//System.out.println(selectedUser);
				setVisible(false);
			}
		});
		
		getContentPane().add(btnNewButton);
	}
	
	public String getMember() {
		if(selectedUser.length() > 0)
			return this.selectedUser;
		else
			return null;
	}
	public void init() {
		selectedUser=""; // 초기화
		for(int i =0; i <10; i++) {
			if(username.equals(JavaGameClientMain.UserList[i].UserName)) {
				checklist[i].setSelected(true);
				checklist[i].setEnabled(false);
			}
			else
				checklist[i].setSelected(false);
		}
	}
}
