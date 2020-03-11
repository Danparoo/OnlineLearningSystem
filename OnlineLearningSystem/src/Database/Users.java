package Database;

public class Users {
	private int userid;
	private String username,realname,password,usertype,sex,email,profilephoto,school,userstate;
	public Users(String chatname, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public Users(int userid, String chatname, String realname, String password, String usertype, String sex,
			String email, String profilephoto, String school, String userstate) {
		super();
		this.userid = userid;
		this.username = username;
		this.realname = realname;
		this.password = password;
		this.usertype = usertype;
		this.sex = sex;
		this.email = email;
		this.profilephoto = profilephoto;
		this.school = school;
		this.userstate = userstate;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getChatname() {
		return username;
	}
	public void setChatname(String username) {
		this.username = username;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getProfilephoto() {
		return profilephoto;
	}
	public void setProfilephoto(String profilephoto) {
		this.profilephoto = profilephoto;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getUserstate() {
		return userstate;
	}
	public void setUserstate(String userstate) {
		this.userstate = userstate;
	}
	@Override
	public String toString() {
		return "Users [userid=" + userid + ", username=" + username + ", realname=" + realname + ", password="
				+ password + ", usertype=" + usertype + ", sex=" + sex + ", email=" + email + ", profilephoto="
				+ profilephoto + ", school=" + school + ", userstate=" + userstate + "]";
	}
	
	
}
