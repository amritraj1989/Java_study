package com.mikealbert.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.DiaryEntryDAO;
import com.mikealbert.data.entity.DiaryEntry;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.DiaryService}
 */
@Service("diaryService")
public class DiaryServiceImpl implements DiaryService {
	@Resource
	DiaryEntryDAO diaryEntryDAO;
	
	/**
	 * Saves the provided diary entry.<br>
	 * Used By:<br>
	 * 1) Purchasing Notification
	 * @param  diaryEntry Diary to be saved
	 * @return Diary Entry that has been saved
	 */
	public DiaryEntry addDiaryEntry(DiaryEntry diaryEntry) {
		try {
			return diaryEntryDAO.saveAndFlush(diaryEntry);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving or updating a diary entry" }, ex);
		}
		
	}

}
