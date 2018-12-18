package com.mikealbert.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.entity.PermissionSet;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.entity.User;
import com.mikealbert.exception.MalBusinessException;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.UserServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.entity.User}(s), {@link com.mikealbert.entity.UserContextPK}(s), and {@link com.mikealbert.entity.UserContext}(s).
 *
 * @see com.mikealbert.entity.User
 * @see com.mikealbert.entity.UserContext
 * @see com.mikealbert.entity.UserContextPK
 * @see com.mikealbert.vision.service.UserServiceImpl
 **/
// TODO: update javadoc or refactor the permission stuff into another service
public interface UserService extends Serializable {
	public User getUserByWillowToken(String sessionId);
	public User getWillowUserByADToken(String token);	
	public List<User> getUsersForRole(String roleName,CorporateEntity corp);
	public List<WorkClass> findAllWorkClasses(CorporateEntity corp);
	public WorkClass findWorkClass(String workClass, CorporateEntity corp);
	// TODO: this is a "current" concept; the better long term model is to use
	// a list of groups that a user belongs to and introduce a groupmanager that
	// resolves a username to a list of groups that they are part of
	// ideally we should plug into org.springframework.security.provisioning.GroupManager
	public WorkClass findWorkClassByUser(User user);
	List<PermissionSet> getPermissionSets();
	List<WorkClassPermission> getWorkClassPermissions(WorkClass workClass);
	public List<WorkClassPermission> addOrRemoveWorkClassPermissions(WorkClass workClass, PermissionSet[] permissionSets)throws MalBusinessException;
	public Map<String, List<String>> getWorkclassToRoleMap();
	public Map<String, List<String>> getOriginToAuthorityMap();

}
