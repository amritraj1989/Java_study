package com.mikealbert.vision.view.bean;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.vision.view.ViewConstants;

@Component("userBean")
@Scope("session")
public class UserBean extends BaseBean
{
	private static final long serialVersionUID = 3126960999949433073L;
	
	private String username;
	private String password;
	
    public String getUsername()
    {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if( authentication != null && authentication.getName() != null){
    		username = authentication.getName();
    	}else{
    		username = "";
    	}
    	return username;
    }
        
	public void invalidateSession()  {		
		SecurityContextHolder.getContext().setAuthentication(null);
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession)facesContext.getExternalContext().getSession(false);
		if(session != null){
			session.invalidate();		
		}
		
		SecurityContextHolder.clearContext();
		
	}
    
	public void logout() {
		invalidateSession();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}
