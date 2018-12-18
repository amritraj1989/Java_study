package com.mikealbert.vision.view.bean;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

@Component("loginBean")
@Scope("request")
public class LoginBean extends BaseBean {
	private static final long serialVersionUID = -7111658508554839399L;

	@Resource AuthenticationManager authenticationManager;
	
	private static final String UI_ID_USERNAME = "j_username";
	private static final String UI_ID_PASSWORD = "j_password";

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private String userName;
	private String password;
	
	public String login(){
		//String page = "/login.xhtml?faces-redirect=true";	
		String page = "";		
		Authentication userNamePasswordToken;
		Authentication result;		
		
		try {
			if(!MALUtilities.isEmpty(userName) && !MALUtilities.isEmpty(password)) {
				//invalidateSession();				
				userNamePasswordToken = new UsernamePasswordAuthenticationToken(getUserName(), getPassword());
				result = authenticationManager.authenticate(userNamePasswordToken);
				SecurityContextHolder.getContext().setAuthentication(result);	
				page = ViewConstants.DASHBOARD_PAGE;
			} else {
				if(MALUtilities.isEmptyString(userName)){
					super.addErrorMessageSummary(UI_ID_USERNAME, "required.field", "User");
				}
				if(MALUtilities.isEmptyString(password)){
					super.addErrorMessageSummary(UI_ID_PASSWORD, "required.field", "Password");
				}			
			}
		}catch(AuthenticationException ae) {
			super.addErrorMessageSummary("login.fail.msg");			
		} catch(MalException me) { 
		    super.addErrorMessageSummary("custom.message", new String[]{me.getMessage()});
		} catch (Exception e) {		
			super.addErrorMessageSummary("error.contact.help.desk");
			logger.error(e);
		}
		
		return page;
	}

	/**
	 * Acquired this code from TAL. Kind of makes sense in that if you are re-authenticating the
	 * existing session should be killed.
	 */
	public void invalidateSession() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession)facesContext.getExternalContext().getSession(false);
		if(session != null){
			session.invalidate();		
		}		
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
