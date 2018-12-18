package com.mikealbert.service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.OraSessionDAO;
import com.mikealbert.data.dao.PermissionSetDAO;
import com.mikealbert.data.dao.UserContextDAO;
import com.mikealbert.data.dao.WorkClassDAO;
import com.mikealbert.data.dao.WorkClassPermissionDAO;
import com.mikealbert.data.entity.OraSession;
import com.mikealbert.data.entity.PermissionSet;
import com.mikealbert.data.entity.UserContext;
import com.mikealbert.data.entity.UserContextPK;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.entity.User;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.HashMapFromProperties;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.UserService}
 */
@Service("userService")
public class UserServiceImpl implements UserService {

	private static final long serialVersionUID = 1L;

	@Resource
	private OraSessionDAO oraSessionDao;
	@Resource
	private UserContextDAO userContextDao;
	@Resource 
	private LookupCacheService lookupCacheService;
	@Resource 
	private WorkClassPermissionDAO workClassPermissionDao;
	@Resource
	private WorkClassDAO workClassDao;
	@Resource
	private PermissionSetDAO permissionSetDao;
	@Resource
	private WorkClassPermissionDAO workClassPermissionsDao;	

	
	/**
	 * Retrieve the Willow user based on the Active Directory
	 * user name. Users that will be authenticated via the login 
	 * view must have their Willow mapped to the AD username.
	 * 
	 * Assumptions: 
	 *     - Corp Enity will always be MAL
	 *     - User will never been in inquiry mode
	 */
	public User getWillowUserByADToken(String token) {
		User user = null;
		UserContext userCxt =  null;
		
		if(token != null) {
			userCxt = findUserContextByActiveDirectoryToken(token, CorporateEntity.MAL);			
			user = createUserForContext(userCxt, token, CorporateEntity.MAL, null);			
		}
		
		return user;
	}
	
	/**
	 * Based on the provided token, the User is retrieved.
	 * @param token Database Audsid
	 * @return User is returned 
	 */
	
	public User getUserByWillowToken(String token) {
		User user = null;
		
		if(token != null)
		{	
			String tokenArray[] = token.split("_");
			UserContext userCxt =  null;
			CorporateEntity corpEntity = null;
			String originAndInquiryFlag = null;
			
			if (tokenArray.length == 3) {
				corpEntity = determineCorporateEntity(tokenArray[1]);
				userCxt = findUserContext(tokenArray[0], corpEntity);
				originAndInquiryFlag = tokenArray[2];
			} else if (tokenArray.length == 2) {
				corpEntity = determineCorporateEntity(tokenArray[1]);
				userCxt = findUserContext(tokenArray[0], corpEntity);
			} else if (tokenArray.length == 1) {
				corpEntity = determineCorporateEntity(String.valueOf(CorporateEntity.MAL.getCorpId()));
				userCxt = findUserContext(token);
			}
			if(userCxt != null){

				// make sure we capture their legal entity (corp entity) this means we need to create our own user subclass of the
				user = createUserForContext(userCxt,tokenArray[0],corpEntity,originAndInquiryFlag);
				
			}else{
				throw new UsernameNotFoundException("A User Session was not found in the DB for AUDSID : " + tokenArray[0]);
			}
		}

		return user;
	}
	
	/**
	 * Sets up the user's context.  This includes setting the user's roles based on work class 
	 * and extra authorities based on origin.
	 * @param userContext
	 * @param credentials
	 * @param corpEntity Legal Entity 
	 * @param originAndInquiryFlag Origin of where the user obtained entry into the system.
	 * @return Set up User Entity
	 */
	private User createUserForContext(UserContext userContext, String credentials, CorporateEntity corpEntity, String originAndInquiryFlag){
		Collection<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();

		// cross reference them between their user admin assigned permission sets from the DB
		List<WorkClassPermission> workClassToPermissions = workClassPermissionDao.findByWorkClass(userContext.getWorkClass(), corpEntity.getCorpId());
		
		// if we have specific permissions in the DB
		if(workClassToPermissions.size() > 0){
			// assign all of the roles that apply to that work class
			for(WorkClassPermission permission: workClassToPermissions){
				userAuthorities.add(new SimpleGrantedAuthority(permission.getPermissionSet().getPermissionSetName()));
			}
			// I think I want to also add the USER role to the list so they are also granted general access
			userAuthorities.add(new SimpleGrantedAuthority("USER"));
			// add extra authorities based upon origin (currently only used to "mark" users coming from EQ001 as "read only" users) and inquiry flag
			if(!MALUtilities.isEmptyString(originAndInquiryFlag)){
				addOriginBasedExtraAuthorities(userAuthorities,originAndInquiryFlag);
			}
			
		}else{ // just add the role user (generic user as the story is written today)
			userAuthorities.add(new SimpleGrantedAuthority("USER"));
			
			// add extra authorities based upon origin (currently only used to "mark" users coming from EQ001 as "read only" users
			if(!MALUtilities.isEmptyString(originAndInquiryFlag)){
				addOriginBasedExtraAuthorities(userAuthorities,originAndInquiryFlag);
			}
		}

		boolean enabled = true;
		boolean accountNonExpired = true;
		boolean credentialsNonExpired = true;
		boolean accountNonLocked = true;

		// make sure we capture their legal entity (corp entity) this means we need to create our own user subclass of the
		return new User(userContext.getUsername(), credentials,  userContext.getEmployeeNo(),userContext.getWorkClass(), corpEntity, enabled, accountNonExpired,
				credentialsNonExpired, accountNonLocked, userAuthorities);
		
	}
	
	/**
	 * Add extra authorities to the user based on their origin of entry into the system.
	 * @param userAuthorities Authorities to grant to user
	 * @param origin Origin of where the user obtained entry into the system.
	 */
	private void addOriginBasedExtraAuthorities(Collection<GrantedAuthority> userAuthorities, String origin){
		// also get the origin and use it to determine whether to add in an extra user role based upon the place they came from (i.e. read only)
		Map<String,List<String>> originToExtraAuthorities = getOriginToAuthorityMap();
		
		// add extra authorities based upon origin (currently only used to "mark" users coming from EQ001 as "read only" users
		if(!MALUtilities.isEmptyString(origin)){
			if(originToExtraAuthorities.get(origin) != null){
				for(String role: originToExtraAuthorities.get(origin)){
					if(!MALUtilities.isEmptyString(role)){
						userAuthorities.add(new SimpleGrantedAuthority(role));
					}
				}
			}
		}
	}
	
	/**
	 * Determines Corporate Entity based on provided input corporate Id.
	 * @param corpIdRaw Id for the Corporate Entity
	 * @return Corporate Entity
	 */
	private CorporateEntity determineCorporateEntity(String corpIdRaw){
		CorporateEntity entity = null;

		Long corpId = Long.parseLong(corpIdRaw);
		
		// TODO: can we make this more dynamic; either way it is only in one place so not too 
		// bad of a practice
		if(CorporateEntity.MAL.getCorpId() == corpId){
			entity = CorporateEntity.MAL;
		}else if(CorporateEntity.LTD.getCorpId() == corpId){
			entity = CorporateEntity.LTD;
		}
		
		return entity;
	}
	
	/**
	 * Find user context based on the session Id and corporate entity.
	 * @param sessionId
	 * @param userCorpEntity
	 * @return User Context
	 */
	private UserContext findUserContext(String sessionId, CorporateEntity userCorpEntity){
		UserContext userCxt = null;
		
		OraSession session = oraSessionDao.findById(Long.parseLong(sessionId)).orElse(null);
		
		if(session != null){
			String oraUserName = session.getUsername();
			// find the username
			String userName = oraUserName.substring(userCorpEntity.getCorpPrefix().length());
			// go against user context to find the single user context record
			UserContextPK userId = new UserContextPK();
			userId.setcId(userCorpEntity.getCorpId());
			userId.setUsername(userName);
			userCxt = userContextDao.findById(userId).orElse(null);
		}
		
		return userCxt;
	}

	/**
	 * User context is retrieved Based on its AD username property 
	 * @param token
	 * @return
	 */
	private UserContext findUserContextByActiveDirectoryToken(String token, CorporateEntity userCorpEntity) {
		UserContext userCxt = null;
		userCxt = userContextDao.findByADUsername(token, userCorpEntity.getCorpId());
		return userCxt;
	}
	
	/**
	 * This private method is only used for a development by-pass of security while
	 * running locally; it hard codes the MAL corporate entity Id
	 * 
	 * @param userName
	 * @return UserContext object used to create a User object for authentication
	 */
	private UserContext findUserContext(String userName) {
		UserContext userCxt = null;
		// go against user context to find the single user context record
		UserContextPK userId = new UserContextPK();
		userId.setcId(1);
		userId.setUsername(userName.toUpperCase());
		userCxt = userContextDao.findById(userId).orElse(null);

		return userCxt;
	}

	// TODO: though this is a very good start for an admin function; there are 2 missing bits.. the user is looked up directly 
	// and thru their credentials are not set to really anything (no willow token, for example)
	// and it is yet to be determined whether the inquiry mode information should be "inheritied" is set on the logged in user.. my guess would be no!
	/**
	 * This method is user to get a list of Users constrained by the name of a Role and a Corporate Entity (MAL or LTD)
	 * 
	 * @param roleName  the name of the role to get users contained within
	 * @param corp 		the corporate entity of the caller (logged in user likely) used to restrict the users just for the Corp.
	 * 
	 * @return List<User> all of the users for that role name and corporate entity (MAL or LTD)
	 */
	
	public List<User> getUsersForRole(String roleName,CorporateEntity corp) {
		List<User> users = new ArrayList<User>();
		// cross reference them between their work_class and their properties file that hold all of the roles
		Map<String,List<String>> workClassToRoles = getWorkclassToRoleMap();
		
		// loop thru each work class entry
		// find each work class containing that role
		for(String workClass : workClassToRoles.keySet()){
			for(String role: workClassToRoles.get(workClass)){
				if(role.equalsIgnoreCase(roleName)){
					List<UserContext> userCtxs = userContextDao.findByWorkClass(workClass);
					for(UserContext userCtx: userCtxs){
						if(userCtx.getId().getcId() == corp.getCorpId()){
							users.add(createUserForContext(userCtx,"",corp,null));
						}
					}
				}
			}
		}

		return users;
	}

	/**
	 * This method is used to find the list of work classes (user groups in Willow terms)
	 * for a specific corporate entity; it is used in administering permissions to groups of users
	 * @param CorporateEntity object used to find/filter the work classes returned
	 * @return a list of work classes
	 */
	
	public List<WorkClass> findAllWorkClasses(CorporateEntity corp) {
		return workClassDao.findAllForContextId(corp.getCorpId());
	}
	
	/**
	 * This method will return the WorkClass value for the provided workClass string.
	 * @param workClass String used to search
	 * @param corp Corporate Entity value used to search
	 * @return WorkClass 
	 */
	
	public WorkClass findWorkClass(String workClass, CorporateEntity corp) {
		return workClassDao.findWorkClass(workClass, corp.getCorpId());
	}
	
	/**
	 * Returns a list of all permission sets. This is used only in administering permissions to groups of users
	 * @return List of permission sets
	 */
	
	public List<PermissionSet> getPermissionSets() {
		return permissionSetDao.findAll(new Sort(Sort.Direction.ASC, "permissionSet"));
	}
	
	/**
	 * Returns a list of work class permissions assigned to a specific work class. 
	 * This is used only in administering permissions to groups of users. A cached method that returns similar data in actually
	 * used in security 
	 * @see LookupCacheServiceImpl
	 * 
	 * @param the work class
	 * @return List of permission sets
	 */
	
	public List<WorkClassPermission> getWorkClassPermissions(WorkClass workClass) {
		return workClassPermissionsDao.findByWorkClass(workClass.getId().getWorkClass(),workClass.getId().getcId());
	}

	@Transactional
	public List<WorkClassPermission> addOrRemoveWorkClassPermissions(
			WorkClass workClass, PermissionSet[] permissionSets)
			throws MalBusinessException {
		
		// keep the list of each permission that has been saved
		List<WorkClassPermission> removedPermissions = new ArrayList<WorkClassPermission>();
		List<WorkClassPermission> retainedPermissions = new ArrayList<WorkClassPermission>();
		List<WorkClassPermission> addedPermissions = new ArrayList<WorkClassPermission>();

		try {
			
			// get a list of saved permissions (from the DB) -- we'll remove them soon enough			
			removedPermissions = this.getWorkClassPermissions(workClass);
			// keep track of all permissions we are keeping
			for(WorkClassPermission wps : removedPermissions){
				for(PermissionSet ps: permissionSets){
					if(wps.getPermissionSet().getPermissionSetName().equalsIgnoreCase(ps.getPermissionSetName())){
						retainedPermissions.add(wps);
						break;
					}
				}
			}
			// take all of the retained permissions out of the list from the DB; this will give us the list to delete
			// then loop thru them one by one and actually delete them
			removedPermissions.removeAll(retainedPermissions);	
			for(WorkClassPermission removedPermission : removedPermissions){
				workClassPermissionDao.delete(removedPermission);
			}

			// add new ones
			// build up a list of workclass permission objects (excluded the retained and removed list - these should truly be new)
			for(PermissionSet permissionSet : permissionSets){
				if(!doesPermissionExistInWorkClassPermissionList(permissionSet,retainedPermissions) && !doesPermissionExistInWorkClassPermissionList(permissionSet,removedPermissions)){
					WorkClassPermission permissionFromUI = new WorkClassPermission();
					permissionFromUI.setPermissionSet(permissionSet);
					permissionFromUI.setWorkClass(workClass);
					addedPermissions.add(permissionFromUI);
				}
			}
			for(WorkClassPermission permission : addedPermissions)				
				workClassPermissionsDao.saveAndFlush(permission);
			
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new MalException("generic.error.occured.while", 
					new String[] { "saving workclass permission(s)" }, ex);			
		}
		
		// finally return the saved list to the caller
		return this.getWorkClassPermissions(workClass);
	}

	
	private boolean doesPermissionExistInWorkClassPermissionList(PermissionSet permission, List<WorkClassPermission> permissionList){
		boolean doesExist = false;
		for(WorkClassPermission wcp: permissionList){
			if(permission.getPermissionSetName().equalsIgnoreCase(wcp.getPermissionSet().getPermissionSetName())){
				doesExist = true;
				break;
			}
		}
		return doesExist;
	}

	@Override
	public WorkClass findWorkClassByUser(User user) {
		UserContextPK ctxKey = new UserContextPK(user.getCorporateEntity().getCorpId(),user.getUsername());
		UserContext cxt = userContextDao.findById(ctxKey).orElse(null);
		return workClassDao.findWorkClass(cxt.getWorkClass(), user.getCorporateEntity().getCorpId());
	}

	
	public Map<String, List<String>> getWorkclassToRoleMap() {
		return getPropertyMap("WorkclassRoleXRef.properties");
	}
	
	public Map<String, List<String>> getOriginToAuthorityMap() {
		return getPropertyMap("OriginAuthorityXRef.properties");		
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getPropertyMap(String propertyFileName) {
		HashMapFromProperties hashMapFromProperties = new HashMapFromProperties();
		hashMapFromProperties.setParsedAslistOfValues(true);
		hashMapFromProperties.setLocation(new ClassPathResource(propertyFileName));

		return hashMapFromProperties.getMap();
	}


}
