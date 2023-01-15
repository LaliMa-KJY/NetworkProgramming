import javax.swing.ImageIcon;

public class User {
	public String UserName; // 이름
	public ImageIcon ProfileImg; //프로필사진
	public String State; // 상태메세지
	public Boolean OnLine; // on-off
	
	public User(){
		this.UserName = "";
		this.ProfileImg = new ImageIcon("images/basicProfileImage.PNG");
		this.State = "";
		this.OnLine = false;
	}
}
