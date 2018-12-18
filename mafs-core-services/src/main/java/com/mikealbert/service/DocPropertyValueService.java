package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.enumeration.DocPropertyEnum;

public interface DocPropertyValueService {	
	
	public DocPropertyValue findByNameDocId(DocPropertyEnum property, Long docId);
	
	public void saveOrUpdateDocumentPropertyValue(Long docId, DocPropertyEnum property, String propertyValue);
	
	public List<DocPropertyValue> findAllByDocId(Long docId);
}
