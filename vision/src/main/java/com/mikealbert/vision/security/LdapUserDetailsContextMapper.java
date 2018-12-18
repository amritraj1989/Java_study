package com.mikealbert.vision.security;

import java.util.Collection;
import javax.annotation.Resource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.UserService;
import com.mikealbert.util.MALUtilities;

@Service("ldapUserDetailsContextMapper")
public class LdapUserDetailsContextMapper implements UserDetailsContextMapper {

	@Resource UserService userService;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {
		User user = null;
		
		try {
		user = userService.getWillowUserByADToken(username);
		if(MALUtilities.isEmpty(user)) {
			logger.debug("Unable to locate a Willow user account for user " + username);
			throw new MalException("generic.error", new String[]{"Unable to locate the corresponding Willow user account for user " + username});
		}
		} catch(NullPointerException npe) {
			logger.debug("Unable to locate a Willow user account for user " + username);
			throw new MalException("generic.error", new String[]{"Unable to locate the corresponding Willow user account for user " + username});			
		}
				
		return user;
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// TODO Auto-generated method stub

	}

}
