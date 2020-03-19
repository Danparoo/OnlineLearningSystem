package Database;

import java.io.Serializable;
import java.sql.Timestamp;

public class Messages implements Serializable, Comparable<Messages>{
	private int messageid;
	private String postcontent,fromuser,touser;
	private Timestamp sendtime;
	private boolean status;
	public Messages(String fromuser, String touser, String postcontent, Timestamp sendtime) {
		super();
//		this.messageid = messageid;
		this.fromuser = fromuser;
		this.touser = touser;
		this.postcontent = postcontent;
		this.sendtime = sendtime;
//		this.status = status;
	}
	
	public int getMessageid() {
		return messageid;
	}
	public void setMessageid(int messageid) {
		this.messageid = messageid;
	}
	public String getFromuser() {
		return fromuser;
	}
	public void setFromuser(String fromuser) {
		this.fromuser = fromuser;
	}
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getPostcontent() {
		return postcontent;
	}
	public void setPostcontent(String postcontent) {
		this.postcontent = postcontent;
	}
	public Timestamp getSendtime() {
		return sendtime;
	}
	public void setSendtime(Timestamp sendtime) {
		this.sendtime = sendtime;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean equals(Messages message) {
		if (this == message)
			return true;
		if (message == null)
			return false;
		if (getClass() != message.getClass())
			return false;
		Messages other = (Messages) message;
		if (fromuser != other.fromuser)
			return false;
		if (messageid != other.messageid)
			return false;
		if (postcontent == null) {
			if (other.postcontent != null)
				return false;
		} else if (!postcontent.equals(other.postcontent))
			return false;
		if (sendtime == null) {
			if (other.sendtime != null)
				return false;
		} else if (!sendtime.equals(other.sendtime))
			return false;
		if (status != other.status)
			return false;
		if (touser != other.touser)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "Messages [messageid=" + messageid + ", fromuser=" + fromuser + ", touser=" + touser
				+ ", postcontent=" + postcontent + ", sendtime=" + sendtime + "]\n";
	}

	@Override
	public int compareTo(Messages msg) {
		return this.getSendtime().compareTo(msg.getSendtime());
	}
	
	
	
}
