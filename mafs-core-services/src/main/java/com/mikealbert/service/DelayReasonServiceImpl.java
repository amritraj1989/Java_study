package com.mikealbert.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.DelayReasonDAO;
import com.mikealbert.data.entity.DelayReason;

/** 
* 
*  @see com.mikealbert.data.entity.DelayReason
* */
@Service("delayReasonService")
public class DelayReasonServiceImpl implements DelayReasonService{
	
	@Resource DelayReasonDAO delayReasonDAO;

	@Override
	public DelayReason getDelayReasonByCode(String code) {
		return delayReasonDAO.getDelayReasonByCode(code);
	}
	
}
