package com.mikealbert.service;

import com.mikealbert.data.entity.DiaryEntry;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.DiaryServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.DiaryEntry}(s).
 * 
 * @see com.mikealbert.data.entity.DiaryEntry
 * @see com.mikealbert.vision.service.DiaryServiceImpl
 * */
public interface DiaryService {
	
	public DiaryEntry addDiaryEntry(DiaryEntry diaryEntry);
	
}
