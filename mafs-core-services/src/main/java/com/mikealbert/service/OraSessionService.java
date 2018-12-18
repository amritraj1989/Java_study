package com.mikealbert.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.PermissionSet;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.entity.User;
import com.mikealbert.exception.MalBusinessException;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.OraSessionServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.entity.OraSession}(s).
 *
 * @see com.mikealbert.entity.OraSession
 * @see com.mikealbert.dao.OraSessionDAO
 * @see com.mikealbert.vision.service.OraSessionImpl
 **/
// TODO: update javadoc or refactor the permission stuff into another service
public interface OraSessionService extends Serializable {
	public String getDatabaseInfo();
	public String getDatabaseNameForDevQATrain();
	public Date getDatabaseDate();
	public boolean isDevelopmentDatabase();
	public boolean isQADatabase();
	public boolean isTrainingDatabase();	
	public String getDBRefreshdate();
}
