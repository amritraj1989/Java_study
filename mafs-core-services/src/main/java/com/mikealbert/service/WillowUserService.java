package com.mikealbert.service;

import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;

public interface WillowUserService {
		
	public PersonnelBase getWillowUserByEmployeeNo (String employeeNo) throws MalBusinessException;
	public WillowUserLovVO getWillowUserByAdUsername(String adUsername) throws MalBusinessException;
	public String getEmailAddress(String employeeNo, Long cId);
}
