package com.mikealbert.vision.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.PurchaseOrderReportDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.UnitProgressDAO;
import com.mikealbert.data.dao.UserContextDAO;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.InServiceProgressQueueV;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.enumeration.POCClientSystemEnum;
import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.data.util.DisplayFormatHelper;
import com.mikealbert.data.vo.ClientContactVO;
import com.mikealbert.data.vo.ReportContactVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.ClientPOCService;
import com.mikealbert.service.DocumentService;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.EmailRequestService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.service.PurchaseOrderReportService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.VelocityService;
import com.mikealbert.service.util.email.ByteArrayEmailAttachment;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.ContactInfo;
import com.mikealbert.vision.vo.PurchasingVehicleReadyEmailVO;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * Implementation of {@link com.mikealbert.vision.service.PurchasingEmailService}
 */
@Service("purchasingEmailService")
public class PurchasingEmailServiceImpl implements PurchasingEmailService {
	@Resource @Qualifier("emailService") EmailService emailService;
	@Resource @Qualifier("clientOrderConfirmationEmail") Email clientOrderConfirmationEmail;
	@Resource @Qualifier("vehicleReadyNotificationEmail") Email vehicleReadyNotificationEmail;	
	@Resource PurchaseOrderReportDAO purchaseOrderReportDAO;  //TODO This should be a service...add service methods
	@Resource ExternalAccountDAO externalAccountDAO; //TODO This should be a service...add service methods
	@Resource QuotationModelDAO quotationModelDAO; //TODO This should be a service...add service methods
	@Resource DocumentService documentService;
	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource LogBookService logBookService;
	@Resource MalMessage malMessage;
	@Resource ClientPOCService clientPOCService;
	@Resource SupplierService supplierService;
	@Resource ProcessStageService processStageService;	
	@Resource DriverService driverService;
	@Resource UnitProgressService unitProgressService;
	@Resource UpfitterService upfitterService;
	@Resource UnitProgressDAO unitProgressDAO;
	@Resource DocDAO docDAO;
	@Resource PurchaseOrderReportService purchaseOrderReportService;
	@Resource EmailRequestService emailRequestService;
	@Resource VelocityService velocityService;
	@Resource HelpDeskEmailService helpDeskEmailService;
	@Resource UserContextDAO userContextDAO;	

	
	private static final String ORDER_CONFIRMATION_EMAIL_SUCCESS_MESSAGE = "order.confirmation.successful";
	private static final String ORDER_CONFIRMATION_EMAIL_FAILURE_MESSAGE = "order.confirmation.unsuccessful";
	private static final String VEHICLE_READY_NOTIFICATION_SUCCESS_MESSAGE = "vehicle.ready.notification.successful";
	private static final String VEHICLE_READY_NO_EMAIL_MESSAGE_DETAIL = "vehicle.ready.no.recipient.message.detail";
	private static final String VEHICLE_READY_EMAIL_EXECPTION_MESSAGE = "vehicle.ready.email.exception.message";	
	private static final String VEHICLE_READY_NO_EMAIL_MESSAGE_SUMMARY = "vehicle.ready.no.recipient.message.summary";	
	private static final String EMAIL_DISPLAY_NAME = "Mike Albert Fleet Solutions";
	private static final String VEHICLE_READY_NO_DEALER_CONTACT = "vehicle.ready.no.dealer.contact.message";
	private static final String EMAIL_SUBJECT = "Error in vehicle ready notification email. Please investigate.";
	private static final String EMAIL_BODY = "Error in vehicle ready notification for Unit#: {0} . <br />Please investigate. <br /> <br />";
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	
	@Value("$email{email.purchasing.username}")
	private String emailUserName;
	
	@Value("$email{email.purchasing.password}")
	private String emailPassword;
	
	@Value("$email{email.purchasing.archiveInSendItems}")
	private String saveInSentItems;	
	
	@Value("$email{email.purchasing.from.address}")
	private String emailFrom;		

	@Value("${vehicle-ready-notification.resource.subject.path}")
	private String vehicleReadySubjectPath;		

	@Value("${vehicle-ready-notification.resource.html.body.path}")
	private String vehicleReadyHrmlBodyPath;		

			
	@Async
	@Override
	public void emailClientConfirmation(JasperPrint print, Long docId, String username) {
		ByteArrayEmailAttachment attachment;
		List<String> rawEmailAddresses;
		String sender;
		String subject;
		String body;
		EmailAddress senderEmailAddress;
		List<EmailAddress> recipientEmailAddresses;
		Doc doc = null;
		QuotationModel qmd;
		FleetMaster fms = null;
		ObjectLogBook olb = null;
		String fleetRefNo = "";
		String unitNo = "";
		
		try {				
			doc = documentService.getDocumentByDocId(docId);
			qmd = quotationService.getQuotationModel(doc.getGenericExtId());
			fms = fleetMasterService.findByUnitNo(qmd.getUnitNo());			
					
			if(!MALUtilities.isEmpty(fms)){
				fleetRefNo = MALUtilities.isEmpty(fms.getFleetReferenceNumber()) ? "" : fms.getFleetReferenceNumber();
				unitNo = MALUtilities.isEmpty(fms.getUnitNo()) ? "" : fms.getUnitNo();			
			}	
			
			//Continue w/ email only when there are recipients to send to.
			rawEmailAddresses = formatEmailAddressesLine(getClientOrderConfirmationRecipients(doc));			
			if(MALUtilities.isEmpty(rawEmailAddresses)){
				throw new MalBusinessException("service.validation", new String[]{"Email recipients were not found"});				
			}	
			
			sender = purchaseOrderReportDAO.getReportEmailSender(ReportNameEnum.CLIENT_ORDER_CONFIRMATION);
			subject = purchaseOrderReportDAO.getReportEmailSubject(ReportNameEnum.CLIENT_ORDER_CONFIRMATION);
			body = purchaseOrderReportDAO.getReportEmailBody(ReportNameEnum.CLIENT_ORDER_CONFIRMATION);

			senderEmailAddress = new EmailAddress();
			senderEmailAddress.setAddress(sender);
			senderEmailAddress.setDisplayName(EMAIL_DISPLAY_NAME);

			recipientEmailAddresses = new ArrayList<EmailAddress>();
			for(String recipientEmailAddress : rawEmailAddresses){
				recipientEmailAddresses.add(new EmailAddress());
				recipientEmailAddresses.get(recipientEmailAddresses.size() - 1).setAddress(recipientEmailAddress);
			}

			this.clientOrderConfirmationEmail.setFrom(senderEmailAddress);
			this.clientOrderConfirmationEmail.setTo(recipientEmailAddresses);
			this.clientOrderConfirmationEmail.setSubject(subject);			
			this.clientOrderConfirmationEmail.setMessage(MALUtilities.isEmpty(body) ? "" : body);  

			this.clientOrderConfirmationEmail.getAttachments().clear();			
			attachment = new ByteArrayEmailAttachment();
			attachment.setContent(JasperExportManager.exportReportToPdf(print));
			attachment.setFileName("ClientOrderConfirmation_" + fms.getUnitNo() + ".pdf");		
			this.clientOrderConfirmationEmail.setAttachment(attachment);

			this.emailService.setEmailUserName(this.emailUserName);
			this.emailService.setEmailPassword(this.emailPassword);
			this.emailService.setSaveInSentItems(this.saveInSentItems);
			this.emailService.sendEmail(this.clientOrderConfirmationEmail);	

			//Add successful note to the unit's log book		
			logEmailTransmission(LogBookTypeEnum.TYPE_BASE_VEH_ORDER_NOTES, fms,  
					MessageFormat.format(malMessage.getMessage(ORDER_CONFIRMATION_EMAIL_SUCCESS_MESSAGE), unitNo, fleetRefNo), 
					username);
			
			//OTD-3897 Log date email was sent in doc property
			purchaseOrderReportService.logClientOrderConfirmationEmailed(docId);

				
		} catch (Exception e) {
			logger.error(e, "docId = " + docId);			
			//Add unsuccessful note to the unit's log book
			if(! MALUtilities.isEmpty(olb)){
			    logBookService.addEntry(olb, username, 
			    		MessageFormat.format(malMessage.getMessage(ORDER_CONFIRMATION_EMAIL_FAILURE_MESSAGE), unitNo, fleetRefNo), null, false);	
			}
			emailClientOrderConfirmationEmailFailure(doc, fms);			
		}
	}
	
	
	
	@Override
	public boolean isClientConfirmationEmailable(Long docId){
		boolean isEmailable = false;
		Doc mainPO;		
		List<ReportContactVO> reportContacts;		
		
		try {
			mainPO = documentService.getDocumentByDocId(docId);
			
			reportContacts = getClientOrderConfirmationRecipients(mainPO);	
			
			if(!(MALUtilities.isEmpty(reportContacts) || reportContacts.size() == 0 || MALUtilities.isEmpty(mainPO.getSubAccCode()))){				
				for(ReportContactVO reportContactVO : reportContacts) {
					if(reportContactVO.getDeliveryMethod().equals("EMAIL") && !MALUtilities.isEmpty(reportContactVO.getEmailAddres())) {
						isEmailable = true;						
						break;
					}
				}				
			}			
			
		} catch(Exception e) {
			logger.error(e, "doc " + docId);
		}
		
		return isEmailable;
	}	
	
	
	
	/**
	 * Sends via email vehicle ready notification to client based on the following business rules:
	 *     - Client's Point of Contact has an email address
	 *     - Vehicle ready notification has not been set previously to the same client for the same unit
	 *     @param SupplierProgressHistory The supplier progress status that initiated this email, i.e. the save VEHRDY
	 *     @param String The logged in user that initiated the transaction
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void emailVehicleReadyNotification(SupplierProgressHistory sph,  Doc doc, FleetMaster fms, QuotationModel qmd ,Driver driver, String username, CorporateEntity coproateEntity)  throws MalBusinessException, MalException {
		final int EMAIL_CHAR_LIMIT = 125;
		String sender;
		String subject;
		String body;
		Map<String,Object> data;
		EmailAddress senderEmailAddress;
		List<EmailAddress> recipientEmailAddresses;
		List<String> rawEmailAddresses = null;
		String truncatedEmails;
		
		try{
			
			
			//The business wants to know when no email is detected by adding to logbook and adding unit to client facing
			rawEmailAddresses = getVehicleReadyNotificationRecipients(fms, qmd.getQuotation().getExternalAccount(), null);
			if(MALUtilities.isEmpty(rawEmailAddresses) || rawEmailAddresses.size() == 0){
				throw new MalBusinessException("custom.message", new String[] {"Point of Contact did not returned an email recipient"});	
			}
			
			recipientEmailAddresses = new ArrayList<EmailAddress>();
			for(String recipientEmailAddress : rawEmailAddresses){
				recipientEmailAddresses.add(new EmailAddress());
				recipientEmailAddresses.get(recipientEmailAddresses.size() - 1).setAddress(recipientEmailAddress);
			}			
			
			sender = this.emailFrom;
			senderEmailAddress = new EmailAddress();
			senderEmailAddress.setAddress(sender);
			senderEmailAddress.setDisplayName(EMAIL_DISPLAY_NAME);
			
			data = generateVehicleReadyEmailContent(coproateEntity, qmd, fms, driver);	
			if(MALUtilities.isEmpty(data) || data.size() < 1) {
				throw new MalException(malMessage.getMessage(VEHICLE_READY_NO_DEALER_CONTACT));
			}
			
			subject = velocityService.getMergedTemplate(data, vehicleReadySubjectPath);
			body = velocityService.getMergedTemplate(data, vehicleReadyHrmlBodyPath);

			this.vehicleReadyNotificationEmail.setFrom(senderEmailAddress);
			this.vehicleReadyNotificationEmail.setTo(recipientEmailAddresses);
			this.vehicleReadyNotificationEmail.setSubject(subject);			
			this.vehicleReadyNotificationEmail.setMessage(body);  	

			this.emailService.setEmailUserName(this.emailUserName);
			this.emailService.setEmailPassword(this.emailPassword);
			this.emailService.setSaveInSentItems(this.saveInSentItems);
			sendAsyncEmail(this.vehicleReadyNotificationEmail , MessageFormat.format(EMAIL_BODY , fms.getUnitNo()) , username);	

			//Add successful note to the unit's log book. Truncate the email list do to char length restriction	
			truncatedEmails = MALUtilities.convertListToCommaSeparatedString(rawEmailAddresses);
			truncatedEmails = truncatedEmails.length() > EMAIL_CHAR_LIMIT ? truncatedEmails.substring(0, EMAIL_CHAR_LIMIT).concat("...") : truncatedEmails;
			logEmailTransmission(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, fms,  
					MessageFormat.format(malMessage.getMessage(VEHICLE_READY_NOTIFICATION_SUCCESS_MESSAGE), 
							truncatedEmails, 
							MALUtilities.getNullSafeDatetoString(Calendar.getInstance().getTime())), 
							username);				
		
			
		} catch (Exception e) {
			if(e instanceof MailSendException || e instanceof MalBusinessException){
				logEmailTransmission(LogBookTypeEnum.TYPE_IN_SERV_PRG_NOTES, fms,  
						MessageFormat.format(malMessage.getMessage(VEHICLE_READY_NO_EMAIL_MESSAGE_DETAIL), 
								MALUtilities.convertListToCommaSeparatedString(rawEmailAddresses), 
								MALUtilities.getNullSafeDatetoString(Calendar.getInstance().getTime())), 
								username);	
				processStageService.addToClientFacing(qmd.getQmdId(), malMessage.getMessage(VEHICLE_READY_NO_EMAIL_MESSAGE_SUMMARY), username);	
				throw new MalBusinessException("custom.message", new String[]{malMessage.getMessage(VEHICLE_READY_EMAIL_EXECPTION_MESSAGE)});
			} else {
				throw new MalException("generic.error", new String[]{e.getMessage()});				
			
			}
		}
	}
	
	
	private void sendAsyncEmail(Email mail , String altMsgBody , String userName) {
		
		try {
			
			this.emailService.sendAsyncEmail(mail);
			
		} catch (Exception e) {
			
			String fromEmail = userContextDAO.getEmailAddress(userName, 1l);				
			Email email = new Email();
			email.setHtmlFmt(true);
			email.setFrom(new EmailAddress(fromEmail));
			email.setSubject(EMAIL_SUBJECT);
			email.setMessage(altMsgBody +"/n Error :"+e.getMessage());
			
			try {
				helpDeskEmailService.sendEmailToHelpDesk(email);
			} catch (Exception ex) {			
				logger.error(ex , "Failed TO send Help Desk Email.." );
			}
		}
		
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void requestEmail(Doc purchaseOrder, EmailRequestEventEnum emailType, boolean scheduled, String username) {
		emailRequestService.stageRequest(purchaseOrder, emailType, scheduled, username);
	}

	@Transactional(rollbackFor=Exception.class)
	public void requestEmail(Long docId, EmailRequestEventEnum emailType, boolean scheduled, String username) {
		Doc po = documentService.getDocumentByDocId(docId);
		emailRequestService.stageRequest(po, emailType, scheduled, username);		
	}
	
	/**
	 * Creates the email content that will be bind to the velocity template(s).
	 * 
	 * Note: The delivering dealer contact information follows the same pattern that the In Service
	 *       queue uses. At the time of writing, both the In Service Queue and Vehicle Ready Email 
	 *       should render the same contact info. 
	 * @param coproateEntity
	 * @param qmd
	 * @param fms
	 * @return Map<String, Object> Containing the data that the Velocity template(s) needs.
	 */
	private Map<String, Object> generateVehicleReadyEmailContent(CorporateEntity coproateEntity, QuotationModel qmd, FleetMaster fms , Driver driver) {
		Map<String, Object> data = new HashMap<String,Object>();;
		ContactInfo contactInfo = null;						
		InServiceProgressQueueV inServiceQueueV;
		
		inServiceQueueV = unitProgressDAO.getInserviceQueueItem(fms.getUnitNo());
		if(MALUtilities.isEmpty(inServiceQueueV)) {
			return null;
		}
		
		if(qmd.getOrderType().equals("L")) {
			contactInfo = unitProgressService.getDealerContactInfo(inServiceQueueV.getDeliveryDealerCode());
		}
		
		if(MALUtilities.isEmpty(contactInfo) ) {
			contactInfo = unitProgressService.getVendorSupplierContactInfo(inServiceQueueV.getDeliveryDealerCode());
		}
		
		if(!MALUtilities.isEmpty(contactInfo)) {
			String singleLineDealerAddress = null;
			String multiLineDealerAddress = null;
			Object[] record = null;
			List<Object[]> dealerAddressList = upfitterService.getUpfitterDealerPostAddress(coproateEntity.getCorpId(), inServiceQueueV.getDeliveryDealerCode());
			record = null;
			if (dealerAddressList != null && dealerAddressList.size() > 0) {
				record = dealerAddressList.get(0);			
				String addressLine1 = (String) record[2];			
				String addressLine2 = (String) record[3];
				String city = (String) record[4];
				String state = (String) record[5];
				String zip =(String) record[6];			
			
				singleLineDealerAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2, null, null, city, state,zip, "\n");
				multiLineDealerAddress = DisplayFormatHelper.formatAddressForTable(null, addressLine1, addressLine2, null, null, city, state, zip, "<br/>");
			}
			

			//Velocity template binding			
			PurchasingVehicleReadyEmailVO emailVO = new PurchasingVehicleReadyEmailVO();
			emailVO.setUnitNo(fms.getUnitNo());
			emailVO.setFleetReferenceNo(fms.getFleetReferenceNumber());
			emailVO.setDriverFirstName(MALUtilities.isEmpty(driver.getDriverForename()) ? "" : driver.getDriverForename());
			emailVO.setDriverLastName(MALUtilities.isEmpty(driver.getDriverSurname()) ? "" : driver.getDriverSurname());
			emailVO.setDeliveringDealerName(inServiceQueueV.getDeliveryDealerName());
			emailVO.setDeliveringDealerContactName(contactInfo.getName());
			emailVO.setDeliveringDealerSingleLineAddress(singleLineDealerAddress);
			emailVO.setDeliveringDealerMultiLineAddress(multiLineDealerAddress);
			emailVO.setDeliveringDealerContactPhoneNumber(contactInfo.getPhone());

			data.put("emailVO", emailVO);
		}
		
		return data;
	}
	
	/**
	 * Determines whether a vehicle ready notification can been emailed to the client. 
	 *     If the main PO does not have an existing vehicle ready status then the email can be sent.
	 *     If the main PO already has a vehicle ready status, and it's entered date is before the 
	 *     acceptance date on the current quote (3, 16, 17 state), email can be sent.
	 *     Otherwise, the email cannot/should not be sent to the client.
	 * @param SupplierProgressHistory Supplier Progress History to check against, should be VEHRDY
	 * @return boolean True: send, False: Do not send
	 */
	public boolean isVehicleReadyNotificationEmailable(SupplierProgressHistory supplierProgressHistory, QuotationModel qmd){
		boolean isEmailable = false;
		SupplierProgressHistory sph;
				
		if(!MALUtilities.isEmpty(qmd) && !MALUtilities.isEmpty(qmd.getAcceptanceDate())){
			sph = supplierService.getSupplierProgressHistoryByDocAndTypeExcludeSphId(supplierProgressHistory, UnitProgressCodeEnum.VEHRDY.getCode());
			
			if(MALUtilities.isEmpty(sph)){
				isEmailable = true;
			} else {
				if(sph.getEnteredDate().compareTo(qmd.getAcceptanceDate()) < 0){
					isEmailable = true;
				} else {
					isEmailable = false;					
				}
			}
		}
		
		return isEmailable;		
	}
	
	private List<ReportContactVO> getClientOrderConfirmationRecipients(Doc doc){
		QuotationModel qmd;
		FleetMaster fms = null;
		ExternalAccountPK clientAccountPK;		
		List<ReportContactVO> reportContacts = null;
		
		
		try {
			qmd = quotationService.getQuotationModel(doc.getGenericExtId());
			fms = fleetMasterService.findByUnitNo(qmd.getUnitNo());
			clientAccountPK = qmd.getQuotation().getExternalAccount().getExternalAccountPK();
			
			reportContacts = purchaseOrderReportDAO.getReportEmailContacts(
					ReportNameEnum.CLIENT_ORDER_CONFIRMATION, clientAccountPK.getCId(), 
					clientAccountPK.getAccountType(), 
					clientAccountPK.getAccountCode(), 
					fms.getFmsId(), qmd.getQmdId());
			
			
		} catch(Exception e) {
			logger.error(e, "doc " + doc.getDocId());
		}	
		
		return reportContacts;		
	}
	
	/**
	 * Retrieves a list of email recipients from the Point of Contact (POC) feature.
	 * 
	 * Note: When the POC exists, but now contact (including default) was not selected,
	 * the POC component raises an error. Although, this should never happen, such error
	 * will be logged and suppressed. Null will be returned in this case.
	 * @param fms FleetMaster
	 * @param clientAccount POC ExternalAccount
	 * @param costCenter POC Cost Center 
	 * @return List email address returned by the POC component
	 */
	private List<String> getVehicleReadyNotificationRecipients(FleetMaster fms, ExternalAccount clientAccount, CostCentreCode costCenter){
		List<String> emailAddresses;
		List<ClientContactVO> clientContactVOs;
		String emailAddress;
		
		try {
			emailAddresses = new ArrayList<String>();
			clientContactVOs = clientPOCService.getClientContactVOs(fms, clientAccount, null, ClientPOCService.POC_NAME_TAL_MULTI, POCClientSystemEnum.TAL.getCode(), false);
			for(ClientContactVO clientContactVO : clientContactVOs){
				emailAddress = clientContactVO.getEmail();
				if(!MALUtilities.isEmptyString(emailAddress)){
					emailAddresses.add(emailAddress);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			emailAddresses = null;
		}
		
		return emailAddresses;
	}
	
	/**
	 * Sends an failure email to the sender of the Client Order Confirmation.
	 * This email should only be sent when there is an exception thrown during
	 * the process of emailing the client their order confirmation. 
	 * @param doc Main purchase order
	 */
	private void emailClientOrderConfirmationEmailFailure(Doc doc, FleetMaster fms){
		String sender;
		String subject;
		String body;
		String fleetRefNo;
		String unitNo;
		EmailAddress senderEmailAddress;		
		
		sender = purchaseOrderReportDAO.getReportEmailSender(ReportNameEnum.CLIENT_ORDER_CONFIRMATION);
		subject = "FAILURE " + purchaseOrderReportDAO.getReportEmailSubject(ReportNameEnum.CLIENT_ORDER_CONFIRMATION);
		
		if(MALUtilities.isEmpty(fms)){
			fleetRefNo = "Error";
			unitNo = "Error";
		} else {
			fleetRefNo = MALUtilities.isEmpty(fms.getFleetReferenceNumber()) ? "" : fms.getFleetReferenceNumber();
			unitNo = MALUtilities.isEmpty(fms.getUnitNo()) ? "" : fms.getUnitNo();			
		}
		
		body = MessageFormat.format(malMessage.getMessage(ORDER_CONFIRMATION_EMAIL_FAILURE_MESSAGE), unitNo, fleetRefNo);
		
		senderEmailAddress = new EmailAddress();
		senderEmailAddress.setAddress(sender);
		senderEmailAddress.setDisplayName("Mike Albert Fleet Solutions");
		
		this.clientOrderConfirmationEmail.setFrom(senderEmailAddress);
		this.clientOrderConfirmationEmail.setTo(senderEmailAddress);
		this.clientOrderConfirmationEmail.setSubject(subject);			
		this.clientOrderConfirmationEmail.setMessage(MALUtilities.isEmpty(body) ? "" : body);  		
		
		this.emailService.setEmailUserName(this.emailUserName);
		this.emailService.setEmailPassword(this.emailPassword);
		this.emailService.setSaveInSentItems(this.saveInSentItems);
		this.emailService.sendEmail(this.clientOrderConfirmationEmail);			
		
	}	
	
	private void logEmailTransmission(LogBookTypeEnum logBookType, FleetMaster fms, String detail, String username) {
		ObjectLogBook olb;
		
		olb = logBookService.createObjectLogBook(fms, logBookType);
		logBookService.addEntry(olb, username, detail, null, false);
	}	
	
	private List<String> formatEmailAddressesLine(List<ReportContactVO> reportContacts) {
		List<String> emailAddresses = new ArrayList<String>();
				
		for(ReportContactVO reportContact : reportContacts){
			if(!MALUtilities.isEmpty(reportContact.getEmailAddres()) && reportContact.getDeliveryMethod().equals("EMAIL")) {
				emailAddresses.add(reportContact.getEmailAddres());
			}
		}
		
		return emailAddresses;
	}
}
