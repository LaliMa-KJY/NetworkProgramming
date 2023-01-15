
// ChatMsg.java 채팅 메시지 ObjectStream 용.
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.ImageIcon;

class ChatMsg implements Serializable {
	private static final long serialVersionUID = 1L;
	// 100:로그인, 400:로그아웃, 200:채팅메시지, 300:Image
	// 900:프로필사진변경,  910:상태메세지 변경
	// 500:채팅방 초대, 510:채팅방 생성, 600: 퇴장
	public String code; 
	public String UserName;
	public String data;
	public ImageIcon img;
	public String time=null;
	public String roomid=null;

	public ChatMsg(String UserName, String code, String msg, String time, String roomid) {
		this.code = code;
		this.UserName = UserName;
		this.data = msg;
		this.time = time;
		this.roomid = roomid;
	}
}