package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.annotation.Resource;
import org.junit.Test;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.DiaryEntry;

public class DiaryServiceTest extends BaseTest{
	@Resource DiaryService diaryService;

	@Test
	public void testAddDiaryEntry(){
		DiaryEntry diaryEntry = new DiaryEntry();
		diaryEntry.setActionComplete("N");
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(GregorianCalendar.MINUTE, 5);
		diaryEntry.setActionDate(cal.getTime());
		
		diaryEntry.setActionFor("DUNCAN_J");
		diaryEntry.setDescription("Testing");
		diaryEntry.setEnteredBy("DUNCAN_J");
		diaryEntry.setEntryDate(new Date());
		diaryEntry.setEntryType("TA");
		diaryEntry.setNote("This is a test entry");
		
		DiaryEntry savedEntry = diaryService.addDiaryEntry(diaryEntry);
		
		assertNotNull(savedEntry.getDryId());
        
    }



}
