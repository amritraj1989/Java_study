package com.mikealbert.vision.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.OraSessionService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

public class WillowPreAuthFilter extends
		AbstractPreAuthenticatedProcessingFilter {
	@Resource
	OraSessionService oraSessionService;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "password_not_applicable";
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principal = null;
		
		logger.debug("Autbentiation Call made for request url : " + request.getRequestURL());
		
		// grab the audsid value from the request as the principal
		String oraSessionId = request.getParameter("audsid");	
		String corpEntityId = request.getParameter("corp_entity");
		String origin = request.getParameter("origin");
		String isInquiry = ((!MALUtilities.isEmptyString(request.getParameter("query_mode"))) && request.getParameter("query_mode").equalsIgnoreCase("Y")) ? "Y" : "N";
		
		String userName = System.getProperty("user.name"); 
		
		if(oraSessionId != null && corpEntityId != null){
			principal = oraSessionId + "_" + corpEntityId;
			if(origin != null){
				principal = principal + "_" + origin + "-" + isInquiry;
			}
		}else {
			if (userName != null && oraSessionService.isDevelopmentDatabase()){
				principal = userName.toUpperCase();				
			}
		}
		
		//TODO: move this it has nothing to do with pre-authentication??
		//We are expecting this to pass by willow while launching vision UI
		
		String height = request.getParameter("height");
		String width = request.getParameter("width");
		
		if(height== null || width == null ){
			height = "800";
			width = "1200";
		}
		logger.debug("resolution height  is "+height +" and width is "+width);
		
		request.getSession().setAttribute(ViewConstants.SCREEN_RESOLUTION_HEIGHT, Integer.parseInt(height));
		request.getSession().setAttribute(ViewConstants.SCREEN_RESOLUTION_WIDTH, Integer.parseInt(width));
		
		return principal;
	}

}
