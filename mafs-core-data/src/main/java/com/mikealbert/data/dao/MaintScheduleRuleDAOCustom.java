package com.mikealbert.data.dao;

import java.util.List;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.MaintScheduleRule;

public interface MaintScheduleRuleDAOCustom {
	public List<MaintScheduleRule>	getRulesByActiveAndHighMileageAndClientSchedType(String activeFlag, String highMileage, Long clientScheduleId, ExternalAccount externalAccount);
	public String getEndYear(MaintScheduleRule rule);	
}
