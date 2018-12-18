package com.mikealbert.vision.service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;


/**
 * Implementation of {@link com.mikealbert.vision.service.HelpDeskEmailService}
 */
@Service("helpDeskEmailService")
public class HelpDeskEmailServiceImpl implements HelpDeskEmailService {
	@Resource @Qualifier("emailService") EmailService emailService;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@Value("$email{email.helpDesk.enabled}")
	private String enabled;	
	
	@Value("$email{email.helpDesk.to.address}")
	private String emailTo;	
	
		
    @PostConstruct
    public void init(){
  	
    }
    
    @Override
    public void sendEmailToHelpDesk(Email email) throws Exception{
    	try {				

    		if(this.enabled.equalsIgnoreCase("false")) return;

    		email.setTo(new EmailAddress(emailTo));
    		this.emailService.sendEmail(email);

    	} catch(MalException me) {
    		logger.error(me);
    		throw me;
    	} catch (Exception e) {
    		logger.error(e);
    		throw new MalException("generic.error", new String[]{"Failed to send email to Help Desk"});
    	}

    }	
}
