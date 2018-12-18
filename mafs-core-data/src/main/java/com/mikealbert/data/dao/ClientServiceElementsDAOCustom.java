package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.ServiceElementsVO;


public interface ClientServiceElementsDAOCustom  {	
	public List<ServiceElementsVO> getAvailableServiceElementsByAccountGradeGroupProfile(ExternalAccount externalAccount, String gradeGroup, Long qprId);
	
	public int getElementOverrideCountOnGradeGroups(Long cId, String accountType, String accountCode);

}
