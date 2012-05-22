package ua.zp.center.book.data;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable{

	private static final long serialVersionUID = -5069567903371117378L;

	private long id;
	private long remoteId;
	private String title;
	private int pagesLoaded;
	private Date updateDate;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getRemoteId() {
		return remoteId;
	}
	public void setRemoteId(long remoteId) {
		this.remoteId = remoteId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public int getPagesLoaded() {
		return pagesLoaded;
	}
	public void setPagesLoaded(int pagesLoaded) {
		this.pagesLoaded = pagesLoaded;
	}

}
