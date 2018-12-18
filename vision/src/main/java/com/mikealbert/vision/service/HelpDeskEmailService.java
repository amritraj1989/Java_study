package com.mikealbert.vision.service;

import com.mikealbert.service.util.email.Email;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.HelpDeskEmailServiceImpl}.
 * 
 * @see com.mikealbert.vision.service.HelpDeskEmailServiceImpl
 * */
public interface HelpDeskEmailService {		
	public void sendEmailToHelpDesk(Email email) throws Exception;	
	
}
