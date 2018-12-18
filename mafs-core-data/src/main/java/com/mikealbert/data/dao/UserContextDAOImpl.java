package com.mikealbert.data.dao;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.UserContext;
import com.mikealbert.data.entity.UserContextPK;

/**
* DAO implementation for UpfitProgressChasing 
*/

public class UserContextDAOImpl extends GenericDAOImpl<UserContext, UserContextPK> implements UserContextDAOCustom {
	
	@Resource
	private UserContextDAO userContextDAO;
	private static final long serialVersionUID = -5554399771530709407L;	
		
	public String getEmailAddress(String employeeNo, Long cId) {
		String emailAddress;
		String stmt;
		
		stmt = "SELECT utility_wrapper.get_email_address(?, ?) FROM DUAL";
		
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, employeeNo);
		query.setParameter(2, cId);
		
		emailAddress = (String) query.getSingleResult();
				
		return emailAddress;
	}

}
