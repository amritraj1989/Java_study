package com.mikealbert.service;

import java.util.List;

import com.mikealbert.data.entity.WillowConfig;
import com.mikealbert.data.vo.AccountVO;

public interface WillowConfigService {

	public final static String AUTODATA_DEALER_ACCESSORIES_CATEGORIES = "AD_DLR_ACC_CATEGORY";
	public final static String SLA_DAYS = "SLA_DAYS";	
	public final static String REPORTS_DIRECTORY = "REPORTS_DIRECTORY";
	public final static String RISK_MGT_CONTACT_PHONE = "RISK_MGT_CONTACT_PHONE";
	
	public String getConfigValue(String configName);	
	public List<AccountVO> getLeasePlanPayeeDetail();	
	public List<WillowConfig> getMultipleWillowConfigWithValue(List<String>  configNames);
	
}
