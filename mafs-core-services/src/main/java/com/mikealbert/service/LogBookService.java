package com.mikealbert.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.exception.MalException;

public interface LogBookService {
	public ObjectLogBook getObjectLogBook(Object entity, LogBookTypeEnum logBookType);
	public ObjectLogBook createObjectLogBook(Object entity, LogBookTypeEnum logBookType);
	public List<ObjectLogBook> createObjectsLogBook(List<Object> entities, LogBookTypeEnum logBookType);	
	public ObjectLogBook addEntry(ObjectLogBook oLogBook, String username, String entrydetail, Date followUpDate, boolean overrideFollowUpDate) throws MalException;
	public List<LogBookEntry> saveOrUpdateLogBookEntries(List<LogBookEntry> logBookEntries) throws MalException;
	public LogBookEntry saveOrUpdateLogBookEntry(LogBookEntry logBookEntry) throws MalException;
	public LogBookEntry getLogBookEntry(Long lbeId) throws MalException ;
	public void deleteLogBook(Object entity, LogBookTypeEnum logBookType) throws MalException;
	public boolean hasLogs(Object entity, List<LogBookTypeEnum> logBookTypes);
	public LogBookEntry getLatestLogBookEntryByDate(Object entity, LogBookTypeEnum logBookType);
	public List<LogBookEntry> mashupObjectLogBookEntries(List<?> entities, LogBookTypeEnum logBookType);
	public List<LogBookEntry> combineObjectLogBookEntries(Object entity, List<LogBookTypeEnum> logBookTypes);
	public Date getFollowUpDate(Object entity, LogBookTypeEnum logBookType);
	public boolean hasFollowUpDate(Object entity, LogBookTypeEnum logBookType);
	public ObjectLogBook getExternalNotes(Object entity, LogBookTypeEnum logBookType) throws MalException;
	public List<LogBookEntry> getLogBookEntries(List<Long> lbeIds);
}
