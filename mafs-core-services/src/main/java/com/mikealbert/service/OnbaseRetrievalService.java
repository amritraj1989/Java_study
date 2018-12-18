package com.mikealbert.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mikealbert.exception.MalException;
import com.mikealbert.imaging.GetDocResponse;
import com.mikealbert.imaging.GetList;
import com.mikealbert.imaging.GetListResponse;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;

public interface OnbaseRetrievalService extends Serializable {

	
	public List<Map<String,String>> getList(OnbaseDocTypeEnum docTypeEnum, List<OnbaseKeywordVO> keyWordVOList ) throws MalException;
	public GetListResponse getList(GetList request) throws MalException;
	public byte[] getDoc(OnbaseDocTypeEnum docTypeEnum, List<OnbaseKeywordVO> keyWordVOList) throws MalException;
	public byte[] getDoc(String docID) throws MalException;	
	public String getDocAsBase64(String docID) throws MalException;	
	public GetDocResponse getDoc(GetList request) throws MalException;
	
	public String ONBASE_RES_KEYWORD_ID = "ID";
	public String ONBASE_RES_KEYWORD_VALUE = "VALUE";
	public String KEYWORD_CUSTOMER = "Customer #";	
	public String KEYWORD_UNIT = "Unit #";	

}
