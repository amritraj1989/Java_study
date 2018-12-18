package com.mikealbert.service;

import java.io.Serializable;
import java.util.List;

import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;

public interface OnbaseArchivalService extends Serializable {

	public void archiveDocumentInOnBase(List<OnbaseUploadedDocs> onbaseUploadedDocList) throws MalException;
	
	public void archiveDocumentInOnBase(OnbaseUploadedDocs onbaseUploadedDoc) throws MalException;
	
	public List<OnbaseKeywordVO> getOnbaseKeywords(OnbaseDocTypeEnum onbaseDocTypeEnum) throws MalException;
	
	public  String generateOnbaseKeywordsIndex(List<OnbaseKeywordVO> onbaseKeywordList)throws MalException;
	
	public Long getNextPK() throws MalException;

}
