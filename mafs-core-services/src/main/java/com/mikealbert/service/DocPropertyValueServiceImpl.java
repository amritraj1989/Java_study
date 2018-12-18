package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DocPropertyDAO;
import com.mikealbert.data.dao.DocPropertyValueDAO;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.util.MALUtilities;

@Service("docPropertyValueService")
public class DocPropertyValueServiceImpl implements DocPropertyValueService {

	@Resource DocPropertyValueDAO docPropertyValueDAO;
	@Resource DocPropertyDAO docPropertyDAO;
	@Resource DocumentService documentService;
	
	@Override
	public DocPropertyValue findByNameDocId(DocPropertyEnum property, Long docId) {
		return docPropertyValueDAO.findByNameDocId(property.getCode(), docId);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)	
	public void saveOrUpdateDocumentPropertyValue(Long docId, DocPropertyEnum property, String propertyValue){
		DocPropertyValue docPropertyValue;
		
		docPropertyValue = findByNameDocId(property, docId);
		
		if(MALUtilities.isEmpty(docPropertyValue)){
			docPropertyValue = new DocPropertyValue();
			docPropertyValue.setDoc(documentService.getDocumentByDocId(docId));
			docPropertyValue.setDocProperty(docPropertyDAO.findByName(property.getCode()));
		} 
		
		docPropertyValue.setPropertyValue(propertyValue);
		
		docPropertyValueDAO.save(docPropertyValue);
	}

	@Override
	public List<DocPropertyValue> findAllByDocId(Long docId) {
		return docPropertyValueDAO.findAllByDocId(docId);
	}
}
