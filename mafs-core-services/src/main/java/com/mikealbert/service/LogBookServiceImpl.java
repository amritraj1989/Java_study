package com.mikealbert.service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.comparator.LogBookEntryDateComparator;
import com.mikealbert.data.comparator.LogBookEntryFollowUpDateComparator;
import com.mikealbert.data.dao.FleetNotesDAO;
import com.mikealbert.data.dao.LogBookDAO;
import com.mikealbert.data.dao.LogBookEntryDAO;
import com.mikealbert.data.dao.ObjectLogBookDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.LogBook;
import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.TALFileNoteVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("logBookService")
public class LogBookServiceImpl implements LogBookService {
	@Resource LogBookDAO logBookDAO;
	@Resource LogBookEntryDAO logBookEntryDAO;	
	@Resource ObjectLogBookDAO objectLogBookDAO;	
	@Resource FleetMasterService fleetMasterService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Deprecated @Resource FleetNotesDAO fleetNoteDAO; //Backwards compatibility only
 
	@Override
	@Transactional(readOnly=true)
	public ObjectLogBook getObjectLogBook(Object entity, LogBookTypeEnum logBookType){
        ObjectLogBook objLogBook = null;
        try {
			objLogBook = objectLogBookDAO.findByObject(logBookType.getCode(), getObjectId(entity), "TABLE", getObjectName(entity));
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", new String[] { "retrieving " + getObjectName(entity) + " log book" }, e);
		}
        return objLogBook;		
	}
		
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public List<LogBookEntry> saveOrUpdateLogBookEntries(List<LogBookEntry> logBookEntries) throws MalException{
		//TODO Validate prior to saving
		return logBookEntryDAO.saveAll(logBookEntries);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public LogBookEntry saveOrUpdateLogBookEntry(LogBookEntry logBookEntry) throws MalException{
		//TODO Validate prior to saving
		LogBookEntry dbLogBookEntry = logBookEntryDAO.save(logBookEntry);
		Hibernate.initialize(dbLogBookEntry.getObjectLogBook());
		
		return dbLogBookEntry;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public LogBookEntry getLogBookEntry(Long lbeId) throws MalException {
		//TODO Validate prior to saving
		LogBookEntry dbLogBookEntry = logBookEntryDAO.findById(lbeId).orElse(null);
		Hibernate.initialize(dbLogBookEntry.getObjectLogBook());
		
		return dbLogBookEntry;
	}
	
	@Override
	@Transactional
	public ObjectLogBook createObjectLogBook(Object entity, LogBookTypeEnum logBookType){		
		LogBook logBook = null;
		ObjectLogBook objLogBook = null;
		List<ObjectLogBook> objLogBooks = new ArrayList<ObjectLogBook>();
		Long objectId = null;
		String objectName = null;

		try {
			//Check if the object's log book for the specified type exists. If so, do not create another
			//return the existing log book instead.
			objLogBook = getObjectLogBook(entity, logBookType);
			if(MALUtilities.isEmpty(objLogBook)){
				objLogBook = new ObjectLogBook();
						
				objectName = getObjectName(entity);			
				objectId = getObjectId(entity);

				if(!MALUtilities.isEmpty(objectId) && objectId > 0){
					logBook = logBookDAO.findByType(logBookType.getCode());
					objLogBook.setObjectId(objectId);
					objLogBook.setObjectName(objectName);
					objLogBook.setObjectType("TABLE");
					objLogBook.setLogBook(logBook);
					objLogBook.setLogBookEntries(new ArrayList<LogBookEntry>());
					objLogBooks.add(objLogBook);
					logBook.setObjectLogBooks(objLogBooks);

					objLogBook = saveOrUpdateObjectLogBook(objLogBook);	
					objLogBook = objectLogBookDAO.findById(objLogBook.getOlbId()).orElse(null);
				}			
			}			
			
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", new String[] { "creating a log book" }, e);
		}

		
		return objLogBook;		
	}
	
	@Override
	public List<ObjectLogBook> createObjectsLogBook(List<Object> entities, LogBookTypeEnum logBookType){
		List<ObjectLogBook> logBooks;
		
		logBooks = new ArrayList<ObjectLogBook>();
		for(Object entity : entities){
			logBooks.add(createObjectLogBook(entity, logBookType));
		}
		
		return logBooks;
	}
	
	@Override
	@Transactional
	public ObjectLogBook addEntry(ObjectLogBook oLogBook, String username, String entrydetail, Date followUpDate, boolean overrideFollowUpDate) throws MalException{
		LogBookEntry entry;
//		ObjectLogBook oLogBook = objectLogBookDAO.findById(objectLogBook.getOlbId()).orElse(null);

		// Validations
		if(MALUtilities.isEmpty(oLogBook))
			throw new MalException("generic.error.occured.while", new String[] { "adding a log book entry. A log book is required" });
		if(MALUtilities.isEmpty(username))
			throw new MalException("generic.error.occured.while", new String[] { "adding a log book entry. A username is required" });
		
		//OTD-472 Added to allow removal of existing follow up dates in lieu of a new one
		if(overrideFollowUpDate){			
			for(LogBookEntry lbe : oLogBook.getLogBookEntries()){
				if(!MALUtilities.isEmpty(followUpDate)){
					lbe.setFollowUpDate(null);
				}
			}
		}
		
		entry = new LogBookEntry();
		entry.setObjectLogBook(oLogBook);
		entry.setEntryUser(username);
		entry.setEntryDate(Calendar.getInstance().getTime());
		entry.setDetail(entrydetail.replaceAll("[\r\n]", ""));
		entry.setFollowUpDate(followUpDate);		
		oLogBook.getLogBookEntries().add(entry);

		oLogBook = saveOrUpdateObjectLogBook(oLogBook);
//		oLogBook = objectLogBookDAO.findById(oLogBook.getOlbId()).orElse(null);
		
		return oLogBook;
	}
		
	@Override
	@Transactional
	public void deleteLogBook(Object entity, LogBookTypeEnum logBookType) throws MalException{
		try {
			ObjectLogBook objectLogBook = getObjectLogBook(entity, logBookType);
			
			if(!MALUtilities.isEmpty(objectLogBook)){
				objectLogBookDAO.deleteById(objectLogBook.getOlbId());
			}
			
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "attempting to delete a " + getObjectName(entity) + " " + logBookType.getCode() + " Log Book" }, e);
		}
	}
	
	/**
	 * Determines whether an entity has log book entries. If the entity is
	 * a FleetMaster, then the search of log book entries spans across all
	 * related FletMaster entities that have the same VIN. For backwards
	 * compatibility, FleetNotes and JobNotes entries are also checked.
	 * 
	 * @param entity The entity to search on for log book entries
	 * @param logBookTypes The type of log book entries to look for
	 * @return boolean indicating whether entries have been found or not
	 */
	public boolean hasLogs(Object entity, List<LogBookTypeEnum> logBookTypes){
		boolean hasLogs = false;
		ObjectLogBook objLogBook = null;
		List<FleetNotes> fleetNotes = new ArrayList<FleetNotes>();
		List<FleetMaster> fleetMasters = null;
		try{
			
			if(entity instanceof FleetMaster){
				fleetMasters = fleetMasterService.findRelatedFleetMasters(fleetMasterService.getFleetMasterByFmsId(getObjectId(entity)).getVin());
				for(FleetMaster fleetMaster : fleetMasters){
					for(LogBookTypeEnum logBookType : logBookTypes){
						objLogBook = getObjectLogBook(fleetMaster, logBookType);
						if(!MALUtilities.isEmpty(objLogBook)){
							if(!MALUtilities.isEmpty(objLogBook.getLogBookEntries()) 
									&& objLogBook.getLogBookEntries().size() > 0 ){
								hasLogs = true;
								break;
							}
						}
					}
				}				
			} else {
				for(LogBookTypeEnum logBookType : logBookTypes){
					objLogBook = getObjectLogBook(entity, logBookType);
					if(!MALUtilities.isEmpty(objLogBook)){
						if(!MALUtilities.isEmpty(objLogBook.getLogBookEntries()) 
								&& objLogBook.getLogBookEntries().size() > 0 ){
							hasLogs = true;
							break;
						}
					}
				}
			}				
						
			//Backwards compatibility w/ Fleet Notes
			if(!hasLogs && (entity instanceof FleetMaster || entity instanceof MaintenanceRequest)){
				if(entity instanceof FleetMaster){
					for(FleetMaster fleetMaster : fleetMasters){					
						fleetNotes.addAll(fleetNoteDAO.findFleetNotesByFmsId(getObjectId(fleetMaster)));
					}
				} else {
					fleetNotes = fleetNoteDAO.findByMaintenanceRequestId(getObjectId(entity));					
				}
				hasLogs = !MALUtilities.isEmpty(fleetNotes) && fleetNotes.size() > 0 ? true : false;				
			}
			
		} catch (Exception e) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "Checking for existing logs for " + getObjectName(entity) + " " + logBookTypes.toString() + " Log Book" }, e);			
		}
		
		return hasLogs;
	}
	
	public LogBookEntry getLatestLogBookEntryByDate(Object entity, LogBookTypeEnum logBookType) {
		LogBookEntry lastLogBookEntry = new LogBookEntry();
		
		ObjectLogBook olb = getObjectLogBook(entity, logBookType);

		if(olb != null){
			List<LogBookEntry> logBookEntriesList = olb.getLogBookEntries();
			if(logBookEntriesList != null && logBookEntriesList.size() > 0){
				lastLogBookEntry = Collections.max(logBookEntriesList, new Comparator<LogBookEntry>(){
					public int compare(LogBookEntry lbe1, LogBookEntry lbe2) { 
						return lbe1.getEntryDate().compareTo(lbe2.getEntryDate()); 
					}
				});
			}
		}
		
		return lastLogBookEntry;
	}
	
	/**
	 * Mash up log book entries from a list of object log books
	 * @param entities List of entity objects who's log book entries will be included in the mashed up list
	 * @param logBookType The type of log book to include
	 * @return List of mashed up log book entries sorted on entry date in DESC 
	 */
	@Override
	@Transactional(readOnly=true)
	public List<LogBookEntry> mashupObjectLogBookEntries(List<?> entities, LogBookTypeEnum logBookType){
		List<LogBookEntry> entries = new ArrayList<LogBookEntry>();
		ObjectLogBook objectLogBook = null;
		    for(Object entity : entities){
		    	objectLogBook = getObjectLogBook(entity, logBookType);
		    	if(!MALUtilities.isEmpty(objectLogBook)){
			    	for(LogBookEntry entry : objectLogBook.getLogBookEntries()){
			    		entries.add(entry);
			    	}		    		
		    	}
		    }
		    
		    Collections.sort(entries, new LogBookEntryDateComparator());
			
		return entries;
	}
	
	/**
	 * Combines the entries of each passed in log book type for a particular entity
	 * into one list. The entries will be sorted on the entry date in descending order
	 * @param Object The business entity to which the log book is attached
	 * @param List of log book types
	 */
	public List<LogBookEntry> combineObjectLogBookEntries(Object entity, List<LogBookTypeEnum> logBookTypes){
		List<LogBookEntry> combinedEntries = new ArrayList<LogBookEntry>();
		ObjectLogBook objectLogBook;
		
		for(LogBookTypeEnum logBookType : logBookTypes){
			objectLogBook = getObjectLogBook(entity, logBookType);
			
			if(!MALUtilities.isEmpty(objectLogBook)){
				combinedEntries.addAll(objectLogBook.getLogBookEntries());
			}
		}
		
	    Collections.sort(combinedEntries, new LogBookEntryDateComparator());
	    
		return combinedEntries;
	}
	
	public Date getFollowUpDate(Object entity, LogBookTypeEnum logBookType){
		ObjectLogBook olb;
		List<LogBookEntry> olbEntries;
		Date nextFollowUpDate = null;
		
		olb = getObjectLogBook(entity, logBookType);
		
		//Must have a list of note entries to continue
		if(MALUtilities.isEmpty(olb)) return null;
		if(MALUtilities.isEmpty(olb.getLogBookEntries())) return null;
		if(olb.getLogBookEntries().isEmpty()) return null;
		
		olbEntries = olb.getLogBookEntries();
		Collections.sort(olbEntries, new LogBookEntryFollowUpDateComparator());	
		
		// Help Desk ticket # is 8322 get the earliest follow up date
		for(LogBookEntry entry : olbEntries) {
			if(!MALUtilities.isEmpty(entry.getFollowUpDate())){
				nextFollowUpDate = entry.getFollowUpDate();
				break;
			}
		}
		
		return nextFollowUpDate;
	}
	
	@Override
	public boolean hasFollowUpDate(Object entity, LogBookTypeEnum logBookType){
		return MALUtilities.isEmpty(getFollowUpDate(entity, logBookType)) ? false : true;		
	}
	
	/**
	 * Retrieves notes that are not managed by the Log Book component. For example,
	 * TAL Diaries, etc. This method will be updated to integrate other external
	 * notes in the future. Updates should include a new LogBookTypeEnum for the
	 * external note,  detection of the log book, and implementation for retrieving 
	 * it's source.
	 */
	@Transactional(readOnly=true)
	public ObjectLogBook getExternalNotes(Object entity, LogBookTypeEnum logBookType) throws MalException {
		List<LogBookEntry> entries = new ArrayList<LogBookEntry>();
		List<FleetNotes> fleetNotes;
		LogBookEntry entry;
		LogBook transientLogBook = null;		
		ObjectLogBook transientObjectLogBook = null;
		Calendar entryDateCal = Calendar.getInstance();
		
	
		transientLogBook = new LogBook();
		transientObjectLogBook = new ObjectLogBook();
		transientObjectLogBook.setLogBook(transientLogBook);		
		
		if(logBookType.equals(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES)){
			transientLogBook.setType(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES.getCode());
			transientLogBook.setName(LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES.getDescription());
			for(TALFileNoteVO talNote : getTALFileNotes((FleetMaster)entity)){
				entry = new LogBookEntry();
				entry.setObjectLogBook(transientObjectLogBook);
				entry.setEntryUser(talNote.getEntryUser().toUpperCase());
				entry.setEntryDate(talNote.getEntryDate());
				entry.setDetail(talNote.getDetail());
				entry.setLbeId(talNote.getDryId());
				entries.add(entry);
			}
			transientObjectLogBook.setLogBookEntries(entries);
		} else if (logBookType.equals(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES)){
			transientLogBook.setType(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES.getCode());
			transientLogBook.setName(LogBookTypeEnum.TYPE_EXTERNAL_FLEET_NOTES.getDescription());
			
			//entity can be a fleet master or maintenance request at this point
			//fleet notes can be displayed at either level
			if(entity instanceof FleetMaster){
				fleetNotes = maintenanceRequestService.mashUpFleetNotes(
						fleetMasterService.findRelatedFleetMasters(((FleetMaster)entity).getVin()));				
			} else {
				fleetNotes = maintenanceRequestService.mashUpFleetNotes(
						fleetMasterService.findRelatedFleetMasters(((MaintenanceRequest)entity).getFleetMaster().getVin()));				
			}

			
			for(FleetNotes fleetNote : fleetNotes){
				entry = new LogBookEntry();
				entry.setObjectLogBook(transientObjectLogBook);
				entry.setDetail(fleetNote.getNote());
				entry.setLbeId(fleetNote.getFnoId());
				
				//HACK: To get fleet not to sort in decending order based on date.
				entryDateCal.add(Calendar.SECOND, -1);
				entry.setEntryDate(entryDateCal.getTime());
				
				entries.add(entry);
			}
			transientObjectLogBook.setLogBookEntries(entries);			
		} else if (logBookType.equals(LogBookTypeEnum.TYPE_EXTERNAL_JOB_NOTES)){
			transientLogBook.setType(LogBookTypeEnum.TYPE_EXTERNAL_JOB_NOTES.getCode());
			transientLogBook.setName(LogBookTypeEnum.TYPE_EXTERNAL_JOB_NOTES.getDescription());
			
			fleetNotes = maintenanceRequestService.getJobNotesByMrqId(((MaintenanceRequest)entity).getMrqId());
			
			for(FleetNotes fleetNote : fleetNotes){
				entry = new LogBookEntry();
				entry.setObjectLogBook(transientObjectLogBook);
				entry.setDetail(fleetNote.getNote());
				entry.setLbeId(fleetNote.getFnoId());
				entries.add(entry);
			}
			transientObjectLogBook.setLogBookEntries(entries);			
		} else {
			throw new MalException("Unhandled Log Book Type " + logBookType.getCode());
		}
		
		return transientObjectLogBook;
	}
	
	/*
	 * Retrieves the TAL File Notes, excluding letter in and out.
	 * @param FleetMaster The Fleet Master the notes are attached to.
	 * @return List TALFileNoteVO 
	 */
	private List<TALFileNoteVO> getTALFileNotes(FleetMaster unit){
		List<TALFileNoteVO> notes;
		notes = logBookDAO.findTALFileNotesExcludeLetterInOut(unit.getFmsId());
		return notes;
	}
	
	@Transactional
	private ObjectLogBook saveOrUpdateObjectLogBook(ObjectLogBook objectLogBook){
		return objectLogBookDAO.saveAndFlush(objectLogBook);
	}
	
	private Long getObjectId(Object entity) throws Exception{
		Field[] fields;
		Long objectId = null;	
		
		fields = entity.getClass().getDeclaredFields();				
		for(int i=0; i < fields.length; i++){
			if(!MALUtilities.isEmpty(fields[i].getAnnotation(Id.class))){
				objectId = (Long) new PropertyDescriptor(fields[i].getName(), entity.getClass()).getReadMethod().invoke(entity); 
				break;
			}
		}		
		return objectId;
	}
	
	private String getObjectName(Object entity){
		return entity.getClass().getAnnotation(Table.class).name();
	}
	
	public List<LogBookEntry> getLogBookEntries(List<Long> lbeIds){
		return logBookEntryDAO.getLogBookEntries(lbeIds);
	}
	
}

