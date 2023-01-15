import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.text.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import net.miginfocom.swing.MigLayout;
import javax.swing.border.MatteBorder;
public class JavaGameClientProfileList extends JFrame{
	private String UserName;
	public static Socket socket;
	public static ObjectInputStream ois;
	public static ObjectOutputStream oos;
	private String ip_addr;
	private String port_no;
	private JLabel myName;
	private JLabel myState;
	private JTextField textField;
	private JTextPane FriendListPane;
	private JTextPane ChatRoomListPane;
	private JButton ChatRoomCreateBtn;
	
	public JavaGameClientView view;
	private SelectMemberDialog inviteDialog;
	private User UserList[] = JavaGameClientMain.UserList;
	public static Vector<ChatRoom> RoomVec = new Vector<>();
	private FriendForm[] fList = new FriendForm[10];
	private ChatRoomLabel[] roomList = new ChatRoomLabel[20];
	private Vector<JavaGameClientView> roomView = new Vector<>();
	private int myIndex =0;
	public String state="";
	private LocalTime localtime = LocalTime.now();
	String time;
	
	public JavaGameClientProfileList(String username, String ip_addr, String port_no) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100,100,462,637);
		getContentPane().setLayout(null);
		this.ip_addr = ip_addr;
		this.port_no = port_no;
		
		// UserList에서 나 찾기
		for(int i = 0; i <10; i++) {
			if((JavaGameClientMain.UserList[i].UserName).equals(username))
				myIndex = i;
		}
		
		JPanel ProfileListPanel = new JPanel();
		ProfileListPanel.setBackground(new Color(255, 255, 255));
		ProfileListPanel.setBounds(75, 0, 373, 592);
		getContentPane().add(ProfileListPanel);
		ProfileListPanel.setLayout(null);
		
		JPanel MyProfilePanel = new JPanel();
		MyProfilePanel.setBorder(null);
		MyProfilePanel.setBounds(0, 63, 373, 120);
		MyProfilePanel.setBackground(new Color(255, 255, 255));
		ProfileListPanel.add(MyProfilePanel);
		MyProfilePanel.setLayout(null);
		
		
		UserName = username;
		myName = new JLabel();
		myName.setBounds(100, 33, 68, 32);
		MyProfilePanel.add(myName);
		myName.setBorder(null);
		myName.setFont(new Font("맑은 고딕", Font.BOLD, 17));
		myName.setBackground(new Color(255, 255, 255));
		myName.setText(username);
		
		
		myState = new JLabel();
		myState.setBounds(100, 75, 197, 21);
		MyProfilePanel.add(myState);
		myState.setBorder(null);
		myState.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
		myState.setText(state);
		
		myState.setBackground(new Color(255, 255, 255));

		JButton btnNewButton = new JButton("Message");
		btnNewButton.setFont(new Font("한컴 말랑말랑 Regular", Font.PLAIN, 13));
		btnNewButton.setBackground(new Color(176, 225, 247));
		btnNewButton.setBorder(null);
		btnNewButton.setBounds(251, 75, 91, 23);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String message = JOptionPane.showInputDialog(null,"상태메세지 변경","",JOptionPane.OK_CANCEL_OPTION);
				myState.setText(message);
				JavaGameClientMain.UserList[myIndex].State = message;
				time = localtime.format(DateTimeFormatter.ofPattern("HH:mm"));
				ChatMsg msg = new ChatMsg(username, "910", message, time, null);
				SendObject(msg);
			}
		});
		MyProfilePanel.add(btnNewButton);
		

		JLabel MyProfileIcon = new JLabel("");
		MyProfileIcon.setBorder(null);
		MyProfileIcon.setBackground(Color.ORANGE);
		MyProfileIcon.setBounds(13, 33, 75, 75);
		ImageIcon myIcon = JavaGameClientMain.UserList[myIndex].ProfileImg;
		MyProfileIcon.setIcon(myIcon);
		MyProfilePanel.add(MyProfileIcon);
		
		JTextArea MyProfileTitle = new JTextArea();
		MyProfileTitle.setBounds(13, 0, 29, 31);
		MyProfilePanel.add(MyProfileTitle);
		MyProfileTitle.setFont(new Font("한컴 말랑말랑 Bold", Font.BOLD, 20));
		MyProfileTitle.setText("나");
		
		JButton editProfileImagebtn = new JButton("Profile");
		editProfileImagebtn.setFont(new Font("한컴 말랑말랑 Regular", Font.PLAIN, 13));
		editProfileImagebtn.setBackground(new Color(176, 225, 247));
		editProfileImagebtn.setBorder(null);
		editProfileImagebtn.setBounds(251, 41, 91, 23);
		editProfileImagebtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Frame frame = new Frame("이미지첨부");
				FileDialog fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "900", "IMG", null, null);
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					Image i=img.getImage();
					i=i.getScaledInstance(75, 75, Image.SCALE_SMOOTH);
					img.setImage(i);
					obcm.img = img;	
					MyProfileIcon.setIcon(img);
					JavaGameClientMain.UserList[myIndex].ProfileImg = img;
					SendObject(obcm);
				}
			}
		});
		MyProfilePanel.add(editProfileImagebtn);
		
		
		
		JTextArea ProfileListTitle = new JTextArea();
		ProfileListTitle.setFont(new Font("한컴 말랑말랑 Bold", Font.BOLD, 35));
		ProfileListTitle.setText("친구");
		ProfileListTitle.setBounds(12, 10, 73, 65);
		ProfileListPanel.add(ProfileListTitle);
		
		
		textField = new JTextField();
		textField.setBorder(null);
		textField.setBounds(0, 190, 373, 32);
		ProfileListPanel.add(textField);
		textField.setText("  친구 목록");
		textField.setFont(new Font("한컴 말랑말랑 Bold", Font.BOLD, 20));
		textField.setColumns(10);
		

		FriendListPane = new JTextPane();
		FriendListPane.setBorder(null);
		FriendListPane.setSize(370, 800);
		JScrollPane FriendListScrollPane = new JScrollPane(FriendListPane);
		FriendListScrollPane.setBorder(null);
		FriendListScrollPane.setBounds(0, 230, 370, 370);
		ProfileListPanel.add(FriendListScrollPane);
		
		StyledDocument doc = (StyledDocument)FriendListPane.getDocument();
		
		for(int i =0; i <10; i++) {
			if(i!=myIndex) {
				try {
					FriendListPane.setSelectionStart(FriendListPane.getText().length());
					FriendListPane.setSelectionEnd(FriendListPane.getText().length());
					doc.insertString(doc.getLength(), "    ", FriendListPane.getLogicalStyle());
				}catch(BadLocationException x) {
					x.printStackTrace();
				}
				fList[i] = new FriendForm(JavaGameClientMain.UserList[i].UserName, JavaGameClientMain.UserList[i].State, JavaGameClientMain.UserList[i].ProfileImg);
				fList[i].setSize(370,80);
				FriendListPane.insertComponent(fList[i]);
				fList[i].addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {

					}
				});
				try {
					FriendListPane.setSelectionStart(FriendListPane.getText().length());
					FriendListPane.setSelectionEnd(FriendListPane.getText().length());
					doc.insertString(doc.getLength(), "\n\n", FriendListPane.getLogicalStyle());
				}catch(BadLocationException x) {
					x.printStackTrace();
				}
			}
		}
		FriendListPane.setSelectionStart(0);
		FriendListPane.setSelectionEnd(0);
		

		
		// 채팅방 목록 보여주는 Panel
		JPanel ChatRoomListPanel = new JPanel();
		ChatRoomListPanel.setBackground(new Color(255, 255, 255));
		ChatRoomListPanel.setBounds(75, 0, 373, 592);
		getContentPane().add(ChatRoomListPanel);
		ChatRoomListPanel.setLayout(null);
		ChatRoomListPanel.setVisible(false);
		

		ChatRoomListPane = new JTextPane();
		ChatRoomListPane.setSize(370, 800);
		JScrollPane ChatRoomListScrollPane = new JScrollPane(ChatRoomListPane);
		ChatRoomListScrollPane.setBounds(0, 0, 370, 590);
		ChatRoomListPanel.add(ChatRoomListScrollPane);
		StyledDocument doc2 = (StyledDocument)ChatRoomListPane.getDocument();

		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(192, 192, 192));
		panel_1.setBounds(0, 0, 71, 592);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JButton ProfileListBtn = new JButton("");
		ProfileListBtn.setBorder(null);
		ProfileListBtn.setForeground(new Color(255, 255, 255));
		ProfileListBtn.setBackground(new Color(255, 255, 255));
		ProfileListBtn.setIcon(new ImageIcon("images/profileIcon3.png"));
		ProfileListBtn.setBounds(15, 50, 37, 40);
		ProfileListBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProfileListPanel.setVisible(true);
			}
		});
		panel_1.add(ProfileListBtn);
		
		JButton ChatRoomBtn = new JButton("");
		ChatRoomBtn.setBorder(null);
		ChatRoomBtn.setIcon(new ImageIcon("images/chattingIcon.png"));
		ChatRoomBtn.setBounds(12, 107, 47, 40);
		ChatRoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProfileListPanel.setVisible(false);
				ChatRoomListPanel.setVisible(true);
			}
		});
		panel_1.add(ChatRoomBtn);
		
		ChatRoomCreateBtn = new JButton("");
		ImageIcon createBtnIcon = new ImageIcon("images/createRoomIcon.PNG");
		Image i = createBtnIcon.getImage();
		i = i.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
		createBtnIcon.setImage(i);
		ChatRoomCreateBtn.setIcon(createBtnIcon);
		ChatRoomCreateBtn.setBorderPainted(false);
		ChatRoomCreateBtn.setBounds(12, 500, 50, 50);
		panel_1.add(ChatRoomCreateBtn);
		inviteDialog = new SelectMemberDialog(this, username);
		ChatRoomCreateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inviteDialog.setVisible(true);	
				String members = inviteDialog.getMember();
				if(members == null) return;
				time = localtime.format(DateTimeFormatter.ofPattern("HH:mm"));
				ChatMsg msg = new ChatMsg(username, "500", members, time, "");
				SendObject(msg);
				ProfileListPanel.setVisible(false);
				ChatRoomListPanel.setVisible(true);
				inviteDialog.init();
			}
		});
		
		
		setVisible(true);
		

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());
			
			ListenNetwork net = new ListenNetwork(); // server에게 object수신
			net.start();
			
			time = localtime.format(DateTimeFormatter.ofPattern("HH:mm"));
			ChatMsg obcm = new ChatMsg(UserName, "100", "Online",time, "");
			JavaGameClientMain.UserList[myIndex].OnLine = true;
			SendObject(obcm);
		}catch(NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {

					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;
					switch (cm.code) {
					case "510": // 채팅방 초대
						RoomVec.add(new ChatRoom(cm.roomid, cm.data,""));
						roomView.add(new JavaGameClientView(UserName, cm.roomid, ip_addr, port_no));
						// Label 만들기
						ChatRoomListPane.setText("");
						StyledDocument doc2 = (StyledDocument)ChatRoomListPane.getDocument();
						for(int i =0; i <RoomVec.size(); i++) {
							try {
								ChatRoomListPane.setSelectionStart(ChatRoomListPane.getText().length());
								ChatRoomListPane.setSelectionEnd(ChatRoomListPane.getText().length());
								doc2.insertString(doc2.getLength(), "", ChatRoomListPane.getLogicalStyle());
							}catch(BadLocationException x) {
								x.printStackTrace();
							}
							roomList[i] = new ChatRoomLabel(RoomVec.elementAt(i).roomid, RoomVec.elementAt(i).userlist);
							roomList[i].setSize(370,120);
							ChatRoomListPane.insertComponent(roomList[i]);
							roomList[i].addMouseListener(new MouseAdapter() {
								public void mouseClicked(MouseEvent e) {
									ChatRoomLabel tmp = (ChatRoomLabel)e.getSource();
									for(int j =0; j<RoomVec.size();j++) {
										if(roomView.get(j).roomId.equals(tmp.roomId))
											roomView.get(j).setVisible(true);
									}
								}
							});
							try {
								ChatRoomListPane.setSelectionStart(ChatRoomListPane.getText().length());
								ChatRoomListPane.setSelectionEnd(ChatRoomListPane.getText().length());
								doc2.insertString(doc2.getLength(), "\n\n\n", ChatRoomListPane.getLogicalStyle());
							}catch(BadLocationException x) {
								x.printStackTrace();
							}
						}
						break;
					case "400": // 채팅방 나가기
						if(cm.UserName.equals(UserName)) { 
							// 구현 실패
						}
						break;
					case "900": // 프사 변경
						if(cm.UserName.equals(UserName)==false) {
							FriendListPane.setText("");
							StyledDocument doc = (StyledDocument)FriendListPane.getDocument();
							for(int i =0; i <10; i++) {
								if(i!=myIndex) {
									try {
										FriendListPane.setSelectionStart(FriendListPane.getText().length()*i);
										FriendListPane.setSelectionEnd(FriendListPane.getText().length()*i);
										doc.insertString(doc.getLength(), "    ", FriendListPane.getLogicalStyle());
									}catch(BadLocationException x) {
										x.printStackTrace();
									}
									if(fList[i].name.equals(cm.UserName)) {
										JavaGameClientMain.UserList[i].ProfileImg = cm.img;
										fList[i] = new FriendForm(JavaGameClientMain.UserList[i].UserName, JavaGameClientMain.UserList[i].State, cm.img);										
									}
									else {
										fList[i] = new FriendForm(JavaGameClientMain.UserList[i].UserName, JavaGameClientMain.UserList[i].State, JavaGameClientMain.UserList[i].ProfileImg);
									}
									fList[i].setSize(370,80);
									FriendListPane.insertComponent(fList[i]);
									try {
										FriendListPane.setSelectionStart(FriendListPane.getText().length()*i);
										FriendListPane.setSelectionEnd(FriendListPane.getText().length()*i);
										doc.insertString(doc.getLength(), "\n\n", FriendListPane.getLogicalStyle());
									}catch(BadLocationException x) {
										x.printStackTrace();
									}
								}
							}
							FriendListPane.setSelectionStart(0);
							FriendListPane.setSelectionEnd(0);
						}
						break;
					case "910": // 상메 변경
						if(cm.UserName.equals(UserName)==false) {
							FriendListPane.setText("");
							StyledDocument doc = (StyledDocument)FriendListPane.getDocument();
							for(int i =0; i <10; i++) {
								if(i!=myIndex) {
									try {
										FriendListPane.setSelectionStart(FriendListPane.getText().length()*i);
										FriendListPane.setSelectionEnd(FriendListPane.getText().length()*i);
										doc.insertString(doc.getLength(), "    ", FriendListPane.getLogicalStyle());
									}catch(BadLocationException x) {
										x.printStackTrace();
									}
									if(fList[i].name.equals(cm.UserName)) {
										JavaGameClientMain.UserList[i].State = cm.data;
										fList[i] = new FriendForm(JavaGameClientMain.UserList[i].UserName, cm.data, JavaGameClientMain.UserList[i].ProfileImg);										
									}
									else {
										fList[i] = new FriendForm(JavaGameClientMain.UserList[i].UserName, JavaGameClientMain.UserList[i].State, JavaGameClientMain.UserList[i].ProfileImg);
									}
									fList[i].setSize(370,80);
									FriendListPane.insertComponent(fList[i]);
									try {
										FriendListPane.setSelectionStart(FriendListPane.getText().length()*i);
										FriendListPane.setSelectionEnd(FriendListPane.getText().length()*i);
										doc.insertString(doc.getLength(), "\n\n", FriendListPane.getLogicalStyle());
									}catch(BadLocationException x) {
										x.printStackTrace();
									}
								}
							}
							FriendListPane.setSelectionStart(0);
							FriendListPane.setSelectionEnd(0);
						}
						break;
					}
				} catch (IOException e) {
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}
	
	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			time = localtime.format(DateTimeFormatter.ofPattern("HH:mm"));
			ChatMsg obcm = new ChatMsg(UserName, "200", msg, time, null);
			oos.writeObject(obcm);
		} catch (IOException e) {
			try {
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
		}
	}
}
