package com.mikealbert.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.QuoteModelPropertyDAO;
import com.mikealbert.data.dao.QuoteModelPropertyValueDAO;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuoteModelProperty;
import com.mikealbert.data.entity.QuoteModelPropertyValue;
import com.mikealbert.data.enumeration.QuoteModelPropertyEnum;
import org.springframework.transaction.annotation.Transactional;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;


@Service("quoteModelPropertyValueService")
public class QuoteModelPropertyValueServiceImpl implements QuoteModelPropertyValueService {

	@Resource QuoteModelPropertyValueDAO quoteModelPropertyValueDAO;
	@Resource QuoteModelPropertyDAO quoteModelPropertyDAO;
	
	@Override
	public QuoteModelProperty findQuoteModelProperty(String propertyName){
		return quoteModelPropertyDAO.findByName(propertyName);
	}
	
	@Override
	public QuoteModelPropertyValue findByNameAndQmdId(String propertyName, Long qmdId) {
		
		return quoteModelPropertyValueDAO.findByNameAndQmdId(propertyName, qmdId);
	}

	@Override
	public QuoteModelPropertyValue saveOrUpdateQuoteModelPropertyValue(QuotationModel quotationModel,
			QuoteModelPropertyEnum quoteModelPropertyEnum, String value) {
		QuoteModelProperty qmp = quoteModelPropertyDAO.findByName(quoteModelPropertyEnum.toString());
		
		QuoteModelPropertyValue qmpv = new QuoteModelPropertyValue();
		qmpv.setQuoteModelProperty(qmp);
		qmpv.setQuotationModel(quotationModel);
		qmpv.setPropertyValue(value);
		qmpv = quoteModelPropertyValueDAO.saveAndFlush(qmpv);
		
		return qmpv;
	}
	
	
	@Override
	public List<QuoteModelPropertyValue> findAllByQmdId(Long qmdId) {
		
		return quoteModelPropertyValueDAO.findAllByQmdId(qmdId);
	}
	
	@Override
	public Map<String, String> findPropertyNameValuePair(Long qmdId) {
		
		Map<String, String> map = new HashMap<String, String>();
		List<QuoteModelPropertyValue> list = quoteModelPropertyValueDAO.findAllByQmdId(qmdId);
		if (list != null){
			for (QuoteModelPropertyValue quoteModelPropertyValue : list) {
				map.put(quoteModelPropertyValue.getQuoteModelProperty().getName() , quoteModelPropertyValue.getPropertyValue());
			} 
		}
		
		return map;
	}	

	@Transactional
	public void populateQuoteModelPropertyValue(QuoteModelPropertyValue quoteModelPropertyValue) throws MalBusinessException{
		
		QuoteModelPropertyValue qmpv = quoteModelPropertyValueDAO.findByNameAndQmdId(quoteModelPropertyValue.getQuoteModelProperty().getName(), quoteModelPropertyValue.getQuotationModel().getQmdId());
		if (!MALUtilities.isEmpty(qmpv)) {
			qmpv.setPropertyValue(quoteModelPropertyValue.getPropertyValue());
		}else {
			qmpv = quoteModelPropertyValue;
		}
		
		quoteModelPropertyValueDAO.saveAndFlush(qmpv);
	}

	@Transactional
	public void removeQuoteModelPropertyValue(long qmdId, String propertyName) throws MalBusinessException {
		QuoteModelPropertyValue qmpv = quoteModelPropertyValueDAO.findByNameAndQmdId(propertyName, qmdId);
		if (!MALUtilities.isEmpty(qmpv)) {
			quoteModelPropertyValueDAO.delete(qmpv);
		}
	}

}
