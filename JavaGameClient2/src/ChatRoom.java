import java.io.Serializable;

public class ChatRoom implements Serializable {
	private static final long serialVersionUID = 1L;
	public String roomid;
	public String userlist;
	public String chatlog;
	
	
	public ChatRoom(String roomid, String userlist, String chatlog) {
		this.roomid = roomid;
		this.userlist = userlist;
		this.chatlog = chatlog;
	}
	public void setuser(String userlist) {
		      this.userlist = userlist;
	}
}