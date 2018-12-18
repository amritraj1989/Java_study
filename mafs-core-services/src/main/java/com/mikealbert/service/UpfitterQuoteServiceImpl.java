package com.mikealbert.service;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mikealbert.data.dao.UpfitterQuoteDAO;
import com.mikealbert.data.entity.UpfitterQuote;

/** 
* 
*  @see com.mikealbert.data.entity.UpfitterQuote
* */
@Service("upfitterQuoteService")
public class UpfitterQuoteServiceImpl implements UpfitterQuoteService{
	@Resource UpfitterQuoteDAO upfitterQuoteDAO;
	
	@Transactional
	public UpfitterQuote getUpfitterQuote(Long ufqId) {
		UpfitterQuote upfitterQuote;
		upfitterQuote = upfitterQuoteDAO.findById(ufqId).orElse(null);
		upfitterQuote.getDealerAccessoryPrices().size();
		return upfitterQuote;
	}
}
