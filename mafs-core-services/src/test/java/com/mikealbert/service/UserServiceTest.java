package com.mikealbert.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.PermissionSet;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.UserService;

public class UserServiceTest extends BaseTest {
	@Resource
	UserService userService;
	
	@Test
	public void retrieveAUserByValidWillowToken() {				
		// We are faking a willow token login using the dev work around
		String token = "LIZAK";
		
		User user = userService.getUserByWillowToken(token);
		assertNotNull(user);

	}
	
	@Test(expected=UsernameNotFoundException.class)
	public void retrieveNoUserForInvalidWillowToken() {
		// We are faking a willow token login using the dev work around
		String token = "ASDFASDF";
		User user = userService.getUserByWillowToken(token);
	}
	
	@Test
	public void retrieveUserAuthoritiesForValidUser() {
		// We are faking a willow token login using the dev work around
		String token = "LIZAK";
		
		User user = userService.getUserByWillowToken(token);
		user.getAuthorities().size();
		assertTrue(user.getAuthorities().size() > 0);	
	}
	
	@Test
	public void retrieveUserListForUserRole() {
		String ROLE = "ROLE_PURCHASE_SUPER";
		
		List<com.mikealbert.data.entity.User> users = userService.getUsersForRole(ROLE, CorporateEntity.MAL);

		assertTrue(users.size() > 0);	
	}
	
	// add the first permission set to the first work class
	@Test
	public void addAPermissionSetToAWorkClass() {
		WorkClass wc = userService.findAllWorkClasses(CorporateEntity.MAL).get(0);
		PermissionSet ps = userService.getPermissionSets().get(0);
		PermissionSet[] pss = new PermissionSet[1];
		pss[0] = ps;
		List<WorkClassPermission> wcp = null;
		try {
			wcp = userService.addOrRemoveWorkClassPermissions(wc, pss);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(wcp.size() > 0);	
	}
	
	// remove the first permission set from WILLOW
	@Test
	public void removeAPermissionSetFromAWorkClass() {
		int origPermLength = 0;
		int newPermLength = 0;
		List<WorkClassPermission> wcp = null;
		List<WorkClass> workclasses = userService.findAllWorkClasses(CorporateEntity.MAL);
		WorkClass willowWorkClass = null;
		for(WorkClass wc :workclasses){
			if(wc.getWorkClassName().equalsIgnoreCase("WILLOW")){
				willowWorkClass = wc;
			}
		}
		List<WorkClassPermission> workClassPerms = userService.getWorkClassPermissions(willowWorkClass);
		origPermLength = workClassPerms.size(); 
		List<PermissionSet> perms = new ArrayList<PermissionSet>();
		// this will skip the first; add the rest
		for(int i = 1; i < workClassPerms.size(); i++){
			perms.add(workClassPerms.get(i).getPermissionSet());
		}
		PermissionSet[] pss = perms.toArray(new PermissionSet[perms.size()]);
		try {
			wcp = userService.addOrRemoveWorkClassPermissions(willowWorkClass, pss);
		} catch (MalBusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newPermLength = wcp.size();
		
		assertTrue(newPermLength > 0);
		assertTrue(newPermLength < origPermLength);
	}
	
	@Test
	public void findWorkClassByUser() {
		String token = "LIZAK";
		com.mikealbert.data.entity.User user = userService.getUserByWillowToken(token);
		WorkClass wc = userService.findWorkClassByUser(user);
		
		assertTrue(wc.getWorkClassName().length() > 0);	
	}
	
	@Test
	public void testWorkclassRoleXRefLoad() {
		Map<String, List<String>> map = userService.getWorkclassToRoleMap();
		assertTrue(map.size() > 0);	
	}

	@Test
	public void testOriginToAuthorityMapLoad() {
		Map<String, List<String>> map = userService.getOriginToAuthorityMap();
		assertTrue(map.size() > 0);	
	}

}
