package com.mikealbert.service;

import com.mikealbert.data.entity.DelayReason;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.DelayReasonServiceImpl} 
* for interacting with business service methods concerning: 
* 
*  @see com.mikealbert.service.DelayReasonServiceImpl
* */
public interface DelayReasonService {

	public DelayReason getDelayReasonByCode(String code);
	
}
