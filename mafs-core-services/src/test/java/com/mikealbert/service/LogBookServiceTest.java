package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.junit.Ignore;
import org.junit.Test;

import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.ObjectLogBookDAO;
import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

import edu.emory.mathcs.backport.java.util.Arrays;


public class LogBookServiceTest extends BaseTest{
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource LogBookService logBookService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ObjectLogBookDAO objectLogBookDAO;
	
	//static final Long MRQ_ID = 642828L; 
	static final Long MRQ_ID = 764000L;	
	static final String USERNAME ="JUnit";
	static final String NOTE = "JUnit Test...";
	
	
	@Test
	public void testGetObjectLogBook(){
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByMrqId(MRQ_ID);
		logBookService.createObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		ObjectLogBook objLogBook = logBookService.getObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		assertNotNull("The Object's Log Book was not found", objLogBook);
	}
	
	@Test
	public void testCreateObjectLogBook(){
		String jobNo;
		ObjectLogBook objLogBook;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);		
		query.setParameter(1, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());	
		jobNo = (String)query.getSingleResult();
		
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);
		
		objLogBook = logBookService.createObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		
		assertNotNull("The Object's Log Book was not created", objLogBook.getOlbId());		
	}
	
	@Test
	public void testAddLogBookEntry(){
		String jobNo;
		ObjectLogBook objLogBook;
		
		Query query = em.createNativeQuery(TestQueryConstants.READ_MAINTENANCE_REQUEST_JOB_NO);		
		query.setParameter(1, MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_BOOKED_IN.getCode());	
		jobNo = (String)query.getSingleResult();
		
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByJobNo(jobNo);	
		
		objLogBook = logBookService.createObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		objLogBook = logBookService.addEntry(objLogBook, "SETUP", "JUNIT TEST", null, false);
		
		assertTrue("The Object's Log book entry was not added", objLogBook.getLogBookEntries().get(0).getLbeId() > 0);
	}
	
	@Test
	public void testGetLatestActivityByDate(){
		Date latestEntryDate = null;
		ObjectLogBook objLogBook;
		
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByMrqId(MRQ_ID);
		
		objLogBook = logBookService.createObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		objLogBook = logBookService.addEntry(objLogBook, "JUNIT 1", "JUNIT TEST ADD 1", null, false);	
		objLogBook = logBookService.addEntry(objLogBook, "JUNIT 2", "JUNIT TEST ADD 2", null, false);
		
		LogBookEntry lbe = logBookService.getLatestLogBookEntryByDate(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		latestEntryDate = lbe.getEntryDate();
	
		ObjectLogBook olb = logBookService.getObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		if(olb != null){
			List<LogBookEntry> logBookEntriesList = olb.getLogBookEntries();
			
			Collections.sort(logBookEntriesList, Collections.reverseOrder(new Comparator<LogBookEntry>(){
			public int compare(LogBookEntry lbe1, LogBookEntry lbe2) {
				Date date1 = lbe1.getEntryDate();
		    	Date date2 = lbe2.getEntryDate();
		    	
		        if (date1.after(date2)){
		            return 1;
		        }else if (date1.before(date2)){
		            return -1;
		        }else{
		            return 0;
		        }
			}
			}));

			assertEquals("latest entry date does not match most recent in list", latestEntryDate,logBookEntriesList.get(0).getEntryDate());
		}
	}
	
	@Test
	public void testCombineObjectLogBookEntries(){
		int i = 0;
		Long olbId;
		ObjectLogBook objLogBook1 = null;
		ObjectLogBook objLogBook2 = null;
		List<LogBookEntry> combinedLogBookEntries;
		LogBookTypeEnum[] logBookTypes;
		Query query;
		Object entity;			
		
		query = em.createNativeQuery(TestQueryConstants.READ_OBJECT_LOG_BOOK_FROM_OBJECT_WITH_MANY_LOG_BOOKS);		
		olbId = ((BigDecimal)query.getSingleResult()).longValue();	
		objLogBook1 = objectLogBookDAO.findById(olbId).orElse(null);
		
		while(MALUtilities.isEmpty(objLogBook1) && (i+=1) < 5) {
			query = em.createNativeQuery(TestQueryConstants.READ_OBJECT_LOG_BOOK_FROM_OBJECT_WITH_MANY_LOG_BOOKS);		
			olbId = ((BigDecimal)query.getSingleResult()).longValue();	
			objLogBook1 = objectLogBookDAO.findById(olbId).orElse(null);
		}
		
		i=0;
		while(!MALUtilities.isEmpty(objLogBook1) 
				&& MALUtilities.isEmpty(objLogBook2)  
				&& (i+=1) < 10){
			query = em.createNativeQuery(TestQueryConstants.READ_OBJECT_LOG_BOOK_FROM_OBJECT_WITH_MANY_LOG_BOOKS);		
			olbId = ((BigDecimal)query.getSingleResult()).longValue();		 
			
			if(!MALUtilities.isEmpty(olbId) && !olbId.equals(objLogBook1.getOlbId())) {
				objLogBook2 = objectLogBookDAO.findById(olbId).orElse(null);				
			}
		}
		
		if(!(MALUtilities.isEmpty(objLogBook1) || MALUtilities.isEmpty(objLogBook2))){
			logBookTypes = LogBookTypeEnum.values();
			
			if(objLogBook1.getObjectName().equals("FLEET_MASTERS")){
				entity = fleetMasterService.getFleetMasterByFmsId(objLogBook1.getObjectId());
			} else if (objLogBook1.getObjectName().equals("MAINTENANCE_REQUESTS")){
				entity = maintenanceRequestService.getMaintenanceRequestByMrqId(objLogBook1.getObjectId());
			} else {
				System.err.println("Unable to testCombineObjectLogBookEntries due to unexpected object log book object name ");
				return;
			}
			
			combinedLogBookEntries = logBookService.combineObjectLogBookEntries(entity, Arrays.asList(logBookTypes));
					
			assertTrue("Two log book entries where not combined correctly", 
					combinedLogBookEntries.size() >= objLogBook1.getLogBookEntries().size());
		}
		
		
	}
	
	@Test
	public void testGetFollowUpDate(){
		String note = "JUnit Test...";
		Calendar cal = Calendar.getInstance();
		MaintenanceRequest mrq = maintenanceRequestService.getMaintenanceRequestByMrqId(764000L);
		//FleetMaster fms = fleetMasterService.getFleetMasterByFmsId(981349L);
		ObjectLogBook objLogBook = logBookService.getObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		cal.add(Calendar.DAY_OF_MONTH, 5);		
		logBookService.addEntry(objLogBook, USERNAME, note, cal.getTime(), false);
		cal.add(Calendar.DAY_OF_MONTH, 5);		
		logBookService.addEntry(objLogBook, USERNAME, note, cal.getTime(), false);
		objLogBook = logBookService.getObjectLogBook(mrq, LogBookTypeEnum.TYPE_ACTIVITY);		
		Date date = logBookService.getFollowUpDate(mrq, LogBookTypeEnum.TYPE_ACTIVITY);
		
		assertNotNull("Follow up date could not be determined", date);
		
	}
	
	@Test
	public void testGetTALFileNotes(){
		ObjectLogBook objLogBook;
		objLogBook = logBookService.getExternalNotes(fleetMasterService.findByUnitNo("00189304"), LogBookTypeEnum.TYPE_EXTERNAL_TAL_FILE_NOTES);
		assertTrue("TAL File Notes Object Log Book was not found", !MALUtilities.isEmpty(objLogBook));
		assertTrue("TAL File Note entries were not found", !objLogBook.getLogBookEntries().isEmpty());
	}
	
	@Test
	public void testAddEntryOverrideFollowUpdate(){
		int i = 0;		
		Long olbId;
		Query query;
		Object entity;
		ObjectLogBook olb;
		Date date;
		int followUpDateCount = 0;
		
		query = em.createNativeQuery(TestQueryConstants.READ_UPFIT_LOG_BOOK_WITH_FOLLOW_UP_DATE);
		olbId =  ((BigDecimal)query.getSingleResult()).longValue();
		
		while(MALUtilities.isEmpty(olbId) && (i+=1) < 5) {
			query = em.createNativeQuery(TestQueryConstants.READ_UPFIT_LOG_BOOK_WITH_FOLLOW_UP_DATE);		
			olbId = ((BigDecimal)query.getSingleResult()).longValue();	
		}
		
		entity = fleetMasterService.getFleetMasterByFmsId(olbId);		
		olb = logBookService.getObjectLogBook(entity, LogBookTypeEnum.TYPE_UPFIT_PRG_NOTES);
		
		date = Calendar.getInstance().getTime();
		olb = logBookService.addEntry(olb, USERNAME, NOTE, date, true);
		
		for(LogBookEntry lbe : olb.getLogBookEntries()){
			if(!MALUtilities.isEmpty(lbe.getFollowUpDate())){
				if(lbe.getFollowUpDate().equals(date)){
					followUpDateCount += 1;
				}
			}
		}
		
		assertTrue("Follow Up Date was not saved correctly", followUpDateCount == 1);
		
		
		
	}
		
}

