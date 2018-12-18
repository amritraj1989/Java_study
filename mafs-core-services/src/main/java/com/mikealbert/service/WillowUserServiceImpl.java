package com.mikealbert.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.UserContextDAO;
import com.mikealbert.data.dao.WillowUserDAO;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.exception.MalBusinessException;

@Service
@Transactional
public class WillowUserServiceImpl implements WillowUserService {
	@Resource
	private WillowUserDAO willowUserDAO;
	@Resource UserContextDAO userContextDAO; 

	@Override
	public PersonnelBase getWillowUserByEmployeeNo(String employeeNo) throws MalBusinessException {
		return willowUserDAO.findByEmployeeNo(employeeNo);
	}

	@Override
	public WillowUserLovVO getWillowUserByAdUsername(String adUsername) throws MalBusinessException {
		return willowUserDAO.getWillowUserByAdUserName(adUsername);
	}

	@Override
	public String getEmailAddress(String employeeNo, Long cId) {
		return userContextDAO.getEmailAddress(employeeNo, cId);
	}
}
