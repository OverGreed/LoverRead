package ua.zp.center.book.data;

import java.io.Serializable;
import java.util.Date;

public class Book implements Serializable {

	private static final long serialVersionUID = 3932523987186698419L;
	
	private long id;
	private long remoteId;
	private long categoryId;
	private String title;
	private String autor;
	private String description;
	private String series;
	private String language;
	private boolean saved;
	private int pages;
	private int readPage;
	private Date added;
	
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
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String serias) {
		this.series = serias;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getPages() {
		return pages;
	}
	public void setPages(int pages) {
		this.pages = pages;
	}
	public boolean isSaved() {
		return saved;
	}
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
	public int getReadPage() {
		return readPage;
	}
	public void setReadPage(int readPage) {
		this.readPage = readPage;
	}
	public Date getAdded() {
		return added;
	}
	public void setAdded(Date added) {
		this.added = added;
	}
	
}
