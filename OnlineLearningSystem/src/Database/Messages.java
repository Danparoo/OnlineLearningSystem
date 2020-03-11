package Database;

import java.io.Serializable;
import java.sql.Timestamp;

public class Messages implements Serializable {
	private int messageid,fromuserid,touserid;
	private String postcontent;
	private Timestamp sendtime;
	private boolean status;
	public Messages(int fromuserid, int touserid, String postcontent, Timestamp sendtime) {
		super();
//		this.messageid = messageid;
		this.fromuserid = fromuserid;
		this.touserid = touserid;
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
	public int getFromuserid() {
		return fromuserid;
	}
	public void setFromuserid(int fromuserid) {
		this.fromuserid = fromuserid;
	}
	public int getTouserid() {
		return touserid;
	}
	public void setTouserid(int touserid) {
		this.touserid = touserid;
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
		if (fromuserid != other.fromuserid)
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
		if (touserid != other.touserid)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return "Messages [messageid=" + messageid + ", fromuserid=" + fromuserid + ", touserid=" + touserid
				+ ", postcontent=" + postcontent + ", sendtime=" + sendtime + "]";
	}
	
	
	
}
