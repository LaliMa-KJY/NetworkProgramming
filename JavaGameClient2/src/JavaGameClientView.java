
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
import javax.swing.WindowConstants;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class JavaGameClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	JPanel panel;
	private JLabel lblMouseEvent;
	private Graphics gc;
	private int pen_size = 2; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null; 
	private Graphics gc2 = null;
	public JavaGameClientView view;
	private LocalTime localtime = LocalTime.now();
	private String userlist;
	public String roomId;
	private ChatRoom myRoom;

	
	/**
	 * Create the frame.
	 * @throws BadLocationException 
	 */
	public JavaGameClientView(String username, String roomId, String ip_addr, String port_no)  {
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBounds(100, 100, 382, 622);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.roomId = roomId;
		
		
		// 이 view가 가리키는 ChatRoom 객체 찾기 -> myRoom에 저장
		for(int i =0; i < JavaGameClientProfileList.RoomVec.size(); i++) {
			if(JavaGameClientProfileList.RoomVec.elementAt(i).roomid.equals(roomId)) {
				myRoom = JavaGameClientProfileList.RoomVec.elementAt(i);
				break;
			}
		}
		

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 352, 471);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setBackground(new Color(155, 187, 212));
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBackground(Color.WHITE);
		txtInput.setBounds(74, 489, 209, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("전 송");
		btnSend.setBorderPainted(false);
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBackground(new Color(254,240,27));
		btnSend.setBounds(295, 489, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("굴림", Font.BOLD, 14));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(12, 539, 62, 40);
		contentPane.add(lblUserName);
		setVisible(false);

		AppendText("");
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(12, 489, 50, 40);
		contentPane.add(imgBtn);

		JButton btnExit = new JButton("종 료");
		btnExit.setBackground(new Color(254,240,27));
		btnExit.setBorderPainted(false);
		btnExit.setFont(new Font("굴림", Font.PLAIN, 14));
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye", null, roomId);
				SendObject(msg);
				//System.exit(0);
				setVisible(false);
			}
		});
		btnExit.setBounds(295, 539, 69, 40);
		contentPane.add(btnExit);

		view = this;
		

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello", null, roomId);
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);


		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
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
						msg = String.format("%s", cm.data);
					} else
						continue;
					if(cm.roomid != null&&cm.roomid.equals(myRoom.roomid)) {
						switch (cm.code) {
						case "200": // chat message
							if (cm.UserName.equals(UserName))
								AppendContentR(msg); // 내 메세지는 우측에
							else {
								AppendProfile(cm.UserName);
								AppendContent(msg);
							}
							break;
						case "205": // 공지
							AppendTextC(msg);
							break;
						case "300": // Image 첨부
							if (cm.UserName.equals(UserName)) {
								AppendImage(cm.img);
								AppendImageTimeR();
							}
							else{
								AppendProfile(cm.UserName);
								AppendImage(cm.img);
								AppendImageTime();
							}
							break;
						}
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
//						dos.close();
//						dis.close();
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
	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				fd.setVisible(true);
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG", null, roomId);
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					
					SendObject(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/icon1.jpg");
	private JButton btnDrawing;

	private void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendContent(String msg) {
		msg = msg.trim();
		int len = textArea.getDocument().getLength();
		int oldplace = 0;
		int haha = -1;
		int good = -1;
		
		if((msg.indexOf("(하하)")!=-1) || (msg.indexOf("(굿)")!=-1)) {
			if(msg.equals("(하하)"))
				AppendBigImoji("haha");
			else if(msg.equals("(굿)"))
				AppendBigImoji("good");
			else {
				while(true) {
					haha = msg.indexOf("(하하)", oldplace);
					good = msg.indexOf("(굿)", oldplace);
					if(haha < good && haha != -1 && good != -1) { //(하하)가 (굿)보다 먼저 있음
						NoTrimAppendText(msg.substring(oldplace, haha));
						AppendImoji("haha");
						oldplace = haha + ("(하하)".length());
					}
					else if( good < haha && haha != -1 && good != -1) { //(굿)이 (하하)보다 먼저 있음
						NoTrimAppendText(msg.substring(oldplace, good));
						AppendImoji("good");
						oldplace = good + ("(굿)".length());
					}
					else if(haha != -1 && good == -1) { // (하하)만 있음
						NoTrimAppendText(msg.substring(oldplace, haha));
						AppendImoji("haha");
						oldplace = haha + ("(하하)".length());
					}
					else if(good != -1 && haha == -1) { // (굿)만 있음
						NoTrimAppendText(msg.substring(oldplace, good));
						AppendImoji("good");
						oldplace = good + ("(굿)".length());
					}
					else
						NoTrimAppendText(msg.substring(oldplace));
					
					if(haha == -1 && good ==-1)
						break;
				}
			}
		}
		else
			AppendText(msg);
		AppendTextTime();
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), "\n", left);
		}catch (BadLocationException e) {
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	// 화면 우측에 출력
	public void AppendContentR(String msg) {
		msg = msg.trim();
		int len = textArea.getDocument().getLength();
		int oldplace = 0;
		int haha = -1;
		int good = -1;
		
		AppendTextTimeR();
		
		if((msg.indexOf("(하하)")!=-1)|| (msg.indexOf("(굿)")!=-1)) {
			// msg에 (하하)나 (굿)이 있음
			if (msg.equals("(하하)"))		// 문자열에 (하하) 만 있음
				AppendBigImoji("haha");
			else if (msg.equals("(굿)"))	// 문자열에 (굿) 만 있음
				AppendBigImoji("good");
			else {
				while(true) {
					haha = msg.indexOf("(하하)", oldplace);
					good = msg.indexOf("(굿)", oldplace);
					if ( haha < good && haha != -1 && good != -1 )	// (하하)가 (굿)보다 먼저 있음
					{
						NoTrimAppendTextR(msg.substring(oldplace, haha));
						AppendImoji("haha");
						oldplace = haha + ("(하하)".length());
					}
					else if ( good < haha && haha != -1 && good != -1 )	// (굿)이 (하하)보다 먼저 있음
					{
						NoTrimAppendTextR(msg.substring(oldplace, good));
						AppendImoji("good");
						oldplace = good + ("(굿)".length());
					}
					else if ( haha != -1 && good == -1 )	// (하하)만 있음
					{
						NoTrimAppendTextR(msg.substring(oldplace, haha));
						AppendImoji("haha");
						oldplace = haha + ("(하하)".length());
					}
					else if ( good != -1 && haha == -1 )	// (굿)만 있음
					{
						NoTrimAppendTextR(msg.substring(oldplace, good));
						AppendImoji("good");
						oldplace = good + ("(굿)".length());
					}
					else NoTrimAppendTextR(msg.substring(oldplace));
					
					if(haha == -1 && good == -1) break;
				}
			}
		}
		else
			AppendTextR(msg);
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(), "\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	public void AppendTextTimeR() {
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setFontSize(right, 10);
		StyleConstants.setForeground(right, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),time + " ", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	
	public void NoTrimAppendTextR(String msg) {
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setFontSize(right, 12);
		StyleConstants.setForeground(right, Color.BLACK);	
		StyleConstants.setBackground(right, new Color(254,240,27));
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg, right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	public void NoTrimAppendText(String msg) {
		int len = textArea.getDocument().getLength();
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(left, 12);
		StyleConstants.setForeground(left, Color.BLACK);
		StyleConstants.setBackground(left, Color.WHITE);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg, left);
		}catch(BadLocationException e) {
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	public void AppendImoji(String type) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		ImageIcon Imoji = new ImageIcon("images/basicProfileImage.PNG");
		
		if(type.equals("haha"))
			Imoji = new ImageIcon("images/smallhaha.PNG");
		else if(type.equals("good")) {
			Imoji = new ImageIcon("images/smallgood.PNG");
		} else // 오류처리
			AppendText("imoji error");
		textArea.insertIcon(Imoji);
		len = textArea.getDocument().getLength();
	}
	public void AppendBigImoji(String type) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		ImageIcon Imoji = new ImageIcon("images/basicProfieImage.PNG");
		
		if (type.equals("haha")) {
			Imoji = new ImageIcon("images/bighaha.PNG");
		} else if (type.equals("good")) {
			Imoji = new ImageIcon("images/biggood.PNG");
		} else // 오류처리
			AppendText("imoji error");
		textArea.insertIcon(Imoji);
		len = textArea.getDocument().getLength();
	}
	public void AppendTextTime() {
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(left, 10);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), " " + time + "\n", left);
		}catch(BadLocationException e) {
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	public void AppendImageTime() {
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(left, 10);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(),"\n"+time+"\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	public void AppendImageTimeR() {
		String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setFontSize(right, 10);
		StyleConstants.setForeground(right, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),"\n"+time+"\n", right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		//textArea.setCaretPosition(len);
		//textArea.replaceSelection(msg + "\n");
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(left, 12);
		StyleConstants.setForeground(left, Color.BLACK);
		StyleConstants.setBackground(left, Color.WHITE);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg, left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);


	}
	// 화면 우측에 출력
	public void AppendTextR(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.	
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet right = new SimpleAttributeSet();
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setFontSize(right, 12);
		StyleConstants.setForeground(right, Color.BLACK);	
		StyleConstants.setBackground(right, new Color(254,240,27));
	    doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		try {
			doc.insertString(doc.getLength(),msg, right );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	// 화면 중앙에 출력
	public void AppendTextC(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontSize(center, 12);
		StyleConstants.setForeground(center, Color.WHITE);
	    doc.setParagraphAttributes(doc.getLength(), 1, center, false);
		try {
			doc.insertString(doc.getLength(), msg+"\n", center );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}
	
	
	
	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		JButton btn= new JButton("");
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			//textArea.insertIcon(new_icon);
			ImageIcon btnIcon= new_icon;
			btn.setIcon(btnIcon);
			btn.setContentAreaFilled(false);
			btn.setBorderPainted(false);
			btn.setBorder(new EmptyBorder(0, 0, 0, 0));
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ImageViewer viewer = new ImageViewer(ori_img);
				}
			});
			textArea.insertComponent(btn);
		} else {
			//textArea.insertIcon(ori_icon);
			new_img = ori_img;
			ImageIcon btnIcon= ori_icon;
			btn.setIcon(btnIcon);
			btn.setContentAreaFilled(false);
			btn.setBorderPainted(false);
			btn.setBorder(new EmptyBorder(0, 0, 0, 0));
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ImageViewer viewer = new ImageViewer(ori_img);
				}
			});
			textArea.insertComponent(btn);
		}
		len = textArea.getDocument().getLength();
	}
	
	public void AppendProfile(String username) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		ImageIcon profilePic = new ImageIcon("images/basicProfileImage.PNG");
		ImageIcon editedPic;
		Image img;
		
		for(int i =0; i <10; i ++)
			if(JavaGameClientMain.UserList[i].UserName.equals(username))
				profilePic = JavaGameClientMain.UserList[i].ProfileImg;
		
		img = profilePic.getImage();
		int width, height;
		double ratio;
		
		width = profilePic.getIconWidth();
		height = profilePic.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 30 || height > 30) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 30;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 30;
				width = (int) (height * ratio);
			}
			
			img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			editedPic = new ImageIcon(img);
			textArea.insertIcon(editedPic);
		}
		else
			textArea.insertIcon(profilePic);
		len = textArea.getDocument().getLength();
		
		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setFontSize(left, 12);
		StyleConstants.setForeground(left, Color.BLACK);
	    doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), "[" + username + "]\n", left );
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len2 = textArea.getDocument().getLength();
		textArea.setCaretPosition(len2);
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			// dos.writeUTF(msg);
//			byte[] bb;
//			bb = MakePacket(msg);
//			dos.write(bb, 0, bb.length);
			//time = localtime.format(DateTimeFormatter.ofPattern("HH:mm"));
			ChatMsg obcm = new ChatMsg(UserName, "200", msg, myRoom.userlist, myRoom.roomid);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
//				dos.close();
//				dis.close();
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
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}
}
