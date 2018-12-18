package com.mikealbert.data.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.ObjectLogBook;

public class LogBookVO {

	private String detail;
	private Date followUpDate;
	private List<ObjectLogBook> objectLogBooks;
	private List<LogBookEntry> entries;
	
	public LogBookVO(ObjectLogBook objectLogBook){
		setObjectLogBooks(new ArrayList<ObjectLogBook>());
		getObjectLogBooks().add(objectLogBook);
	}
	
	public LogBookVO(ObjectLogBook objectLogBook, List<LogBookEntry> logBookEntries){
		setObjectLogBooks(new ArrayList<ObjectLogBook>());
		getObjectLogBooks().add(objectLogBook);		
		setEntries(logBookEntries);
	}
		
	public LogBookVO(List<ObjectLogBook> objectLogBooks){
		setObjectLogBooks(objectLogBooks);
	}		
	
	public String getDetail() {
		return detail;
	}
	
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Date getFollowUpDate() {
		return followUpDate;
	}

	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}

	public void setObjectLogBooks(List<ObjectLogBook> objectLogBooks) {
		this.objectLogBooks = objectLogBooks;
	}

	public List<ObjectLogBook> getObjectLogBooks() {
		return objectLogBooks;
	}

	public List<LogBookEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<LogBookEntry> entries) {
		this.entries = entries;
	}
		
}
