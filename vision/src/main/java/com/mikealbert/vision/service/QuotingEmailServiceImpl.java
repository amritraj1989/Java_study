package com.mikealbert.vision.service;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.UserContextDAO;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.OraSessionService;
import com.mikealbert.service.WillowUserService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.QuotingEmailService}
 */
@Service("quoteRequestEmailService")
public class QuotingEmailServiceImpl implements QuotingEmailService {
	@Resource @Qualifier("emailService") EmailService emailService;
	@Resource @Qualifier("quoteRequestEmail") Email quoteRequestEmail;
	@Resource WillowUserService willowUserService;
	@Resource OraSessionService oraSessionService;
	
	private static final String STATUS_CHANGE_SUBJECT = "{0} Quote Request is {1} for {2}";
	private static final String STATUS_CHANGE_BODY = "{0} Quote Request for {1} is {2} <br /> <br /> "
			+ "Request ID: {3} <br /> Assigned To: {4} <br /> Request Submitted Date: {5} <br /> Request Due Date: {6} <br /> Vehicle Description: {7}";		
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@Value("$email{email.quoting.username}")
	private String emailUserName;
	
	@Value("$email{email.quoting.password}")
	private String emailPassword;
	
	@Value("$email{email.quoting.archiveInSendItems}")
	private String saveInSentItems;	
	
	@Value("$email{email.quoting.archiveInSendItems}")
	private String archiveInSendItems;		

	@Value("$email{email.quoting.enabled}")
	private String enabled;	
	
	@Value("$email{email.quoting.to.address}")
	private String emailTo;	
	
		
    @PostConstruct
    public void init(){
		this.emailService.setEmailUserName(this.emailUserName);
		this.emailService.setEmailPassword(this.emailPassword);
		this.emailService.setSaveInSentItems(archiveInSendItems);    	
    }
    
	@Override
	public void emailQuoteRequestStatusChange(QuoteRequest quoteRequest) throws Exception {
		String recipientEmailAddresses;
		String subject;
		String body;
		String dueDate, submittedDate, vehicleDescription, dbEnv, assignedTo;
		SimpleDateFormat sdf = new SimpleDateFormat(MALUtilities.DATE_PATTERN); 
		
		try {				
			
			if(this.enabled.equalsIgnoreCase("false")) return;
			
			//Adding this check here so that we do not accidentally send out test emails to our business users.
			if(oraSessionService.isDevelopmentDatabase() || oraSessionService.isQADatabase() || oraSessionService.isTrainingDatabase()) {
				recipientEmailAddresses = this.emailTo;
			} else {
				recipientEmailAddresses = willowUserService.getEmailAddress(quoteRequest.getSubmittedBy().toUpperCase(), 1l);				
			}
 			
			if(MALUtilities.isEmpty(recipientEmailAddresses)) {
				throw new MalException("generic.error", new String[]{"Unable to send email to Quote Requestor, " + quoteRequest.getSubmittedBy() 
				        + ". The email address could not be retrieved from Active Directory. Please notify the Helpdesk."});
			}
			
			dbEnv = oraSessionService.getDatabaseNameForDevQATrain();
			assignedTo = MALUtilities.isEmpty(quoteRequest.getAssignedTo()) ? "N/A" : quoteRequest.getAssignedTo();
			submittedDate = MALUtilities.isEmpty(quoteRequest.getSubmittedDate()) ?  "N/A" : sdf.format(quoteRequest.getSubmittedDate());			
			dueDate = MALUtilities.isEmpty(quoteRequest.getDueDate()) ? "N/A" : sdf.format(quoteRequest.getDueDate());
			vehicleDescription = MALUtilities.isEmpty(quoteRequest.getQuoteRequestVehicles().get(0).getFleetmaster()) 
					? quoteRequest.getQuoteRequestVehicles().get(0).getVehicleDescription() : quoteRequest.getQuoteRequestVehicles().get(0).getFleetmaster().getModel().getModelDescription();  
			vehicleDescription = MALUtilities.isEmpty(vehicleDescription) ? "N/A" : vehicleDescription;
			
			subject = MessageFormat.format(STATUS_CHANGE_SUBJECT, quoteRequest.getQuoteRequestType().getName(), 
					quoteRequest.getQuoteRequestStatus().getName(), quoteRequest.getClientAccount().getAccountName()).concat(" " + dbEnv);
			body = MessageFormat.format(STATUS_CHANGE_BODY, quoteRequest.getQuoteRequestType().getName(), 
					quoteRequest.getClientAccount().getAccountName(), quoteRequest.getQuoteRequestStatus().getName(), 
					String.valueOf(quoteRequest.getQrqId()), assignedTo, submittedDate, dueDate, vehicleDescription);

			this.quoteRequestEmail.getTo().clear();		
			this.quoteRequestEmail.setTo(new EmailAddress(recipientEmailAddresses));
			this.quoteRequestEmail.setSubject(subject);			
			this.quoteRequestEmail.setMessage(body);
			this.quoteRequestEmail.getAttachments().clear();			
	
			this.emailService.sendEmail(this.quoteRequestEmail);
			
		} catch(MalException me) {
			logger.error(me, "qrqId = " + quoteRequest.getQrqId());
			throw me;
		} catch (Exception e) {
			logger.error(e, "qrqId = " + quoteRequest.getQrqId());
			throw new MalException("generic.error", new String[]{"Failed to send email to quote requestor, " + quoteRequest.getSubmittedBy() + ". " 
			        + "Please notify the Helpdesk. qrqId = " + quoteRequest.getQrqId()});
		}
	}
	
	
}
