package com.mikealbert.vision.security;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.mikealbert.service.UserService;

@Service("userDetailsService")
public class UserDetailServiceImpl implements AuthenticationUserDetailsService<Authentication> {

	@Resource
	private UserService userService;
	
	@Override
	public UserDetails loadUserDetails(Authentication auth)
			throws UsernameNotFoundException {
		
		User user = userService.getUserByWillowToken(auth.getPrincipal().toString());
		
		if(user != null){

			return user;	
		}else{
			throw new UsernameNotFoundException("A User Session was not found in the DB for Willow Token : " + auth.getPrincipal().toString());
		}
	}

}
