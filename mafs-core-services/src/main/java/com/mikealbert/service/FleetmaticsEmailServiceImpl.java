package com.mikealbert.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;

@Service("fleetmaticsEmailService")
public class FleetmaticsEmailServiceImpl implements FleetmaticsEmailService {

	@Resource
	@Qualifier("emailService")
	EmailService emailService;

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private String emailUserName;
	private String emailPassword;
	private String archiveInSendItems;
	private String enabled;
	private String emailTo;
	private String emailFrom;

	private final String INVALID_RECORDS = "INVALID_RECORDS";
	private final String UNMATCHED_RECORDS = "UNMATCHED_RECORDS";
	private final String INVALID_ACCOUNTS = "INVALID_ACCOUNTS";
	private final String INVALID_DATABASE = "INVALID_DATABASE";
	private final String INVALID_SETUP = "INVALID_SETUP";
	public  final String AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
	/* Email Subjects */
	private final String GEOTAB_INVALID_RECORDS_SUBJECT = "Invalid records from Geotab";
	private final String GEOTAB_UNMATCHED_RECORDS_SUBJECT = "Mismatch in Geotab and Willow vins";
	private final String GEOTAB_INVALID_ACCOUNTS_SUBJECT = "Geotab and Willow accounts are not in sync";
	private final String GEOTAB_INVALID_DATABASE_SUBJECT = "Geotab Job authentication failed.";
	private final String GEOTAB_INVALID_SETUP_SUBJECT = "Geotab Job failed.";
	private final String FLEET_COMPLETE_INVALID_RECORDS_SUBJECT = "Invalid records from AT&T Fleet Complete";
	private final String FLEET_COMPLETE_UNMATCHED_RECORDS_SUBJECT = "Mismatch in AT&T Fleet Complete and Willow vins";
	private final String FLEET_COMPLETE_AUTHENTICATION_FAILED_SUBJECT = "AT&T Fleet Complete API authentication failed";
	/* Email Body Headings */
	private final String GEOTAB_INVALID_RECORDS_BODY_INIT = "Vin missing in following records from Geotab :  <br /><br />";
	private final String GEOTAB_UNMATCHED_RECORDS_BODY_INIT = "Following vins received from Geotab do not exist in willow database (with In-service or On-Contract status) : <br /><br />";
	private final String GEOTAB_INVALID_ACCOUNTS_BODY_INIT = "Following accounts are not mapped with Geotab database : <br /><br />";
	private final String GEOTAB_INVALID_DATABASE_BODY_INIT = "Following Geotab database(s) authentication failed : <br /><br />";
	private final String GEOTAB_INVALID_SETUP_BODY_INIT = "No record exists in Geotab database and willow account synchronizing table (GEOTAB_DATABASE_SYNC). <br /><br />";
	private final String GEOTAB_TABLE_FORMAT_INVALID_RECORDS = "<tr bgcolor=\"#D3D3D3\"><td>Device Id</td><td>Device Name</td><td>Database Name</td></tr>";
	private final String GEOTAB_TABLE_FORMAT_UNMATCHED_RECORDS = "<tr bgcolor=\"#D3D3D3\"><td>Vin</td><td>Device Id</td><td>Device Name</td><td>Database Name</td></tr>";
	private final String GEOTAB_TABLE_FORMAT_INVALID_ACCOUNTS = "<tr bgcolor=\"#D3D3D3\"><td>Account Code</td></tr>";
	private final String GEOTAB_TABLE_FORMAT_INVALID_DATABASE = "<tr bgcolor=\"#D3D3D3\"><td>Database</td></tr>";
	private final String TABLE_FORMATTED_BODY_START = "<table style='border:2px solid black'>";
	private final String TABLE_FORMATTED_BODY_END = "</table>";
	private final String FLEET_COMPLETE_INVALID_RECORDS_BODY_INIT = "Vin missing in following records from AT&T Fleet Complete :  <br /><br />";
	private final String FLEET_COMPLETE_UNMATCHED_RECORDS_BODY_INIT = "Following vins received from AT&T Fleet Complete do not exist in willow database (with In-service or On-Contract status) : <br /><br />";
	private final String FLEET_COMPLETE_AUTHENTICATION_FAILED_BODY_INIT = "Authentication failed for AT&T Fleet Complete API<br />Below is the url <br /><br />";
	private final String FLEET_COMPLETE_TABLE_FORMAT_INVALID_RECORDS = "<tr bgcolor=\"#D3D3D3\"><td>Asset Id</td><td>Device Id</td></tr>";
	private final String FLEET_COMPLETE_TABLE_FORMAT_UNMATCHED_RECORDS = "<tr bgcolor=\"#D3D3D3\"><td>Vin</td><td>Device Id</td><td>Asset Id</td></tr>";


	@Override
	public void emailForGeotabInvalidRecords(List<String> invalidData,
			String invalidType, Map<String, String> emailConfig) {

		Email geotabEmail = new Email();
		geotabEmail.setHtmlFmt(true);
		this.emailUserName = emailConfig.get("USERNAME");
		this.emailPassword = emailConfig.get("PASSWORD");
		this.archiveInSendItems = emailConfig.get("ARCHIVE");
		this.enabled = emailConfig.get("ENABLED");
		this.emailTo = emailConfig.get("TOADD");
		this.emailFrom = emailConfig.get("FROMADD");

		this.emailService.setEmailUserName(this.emailUserName);
		this.emailService.setEmailPassword(this.emailPassword);
		this.emailService.setSaveInSentItems(archiveInSendItems);

		String subject = "";
		String body = "";

		try {

			if (this.enabled.equalsIgnoreCase("false"))
				return;
			if (invalidType.equals(INVALID_RECORDS)) {
				subject = GEOTAB_INVALID_RECORDS_SUBJECT;
			} else if (invalidType.equals(UNMATCHED_RECORDS)) {
				subject = GEOTAB_UNMATCHED_RECORDS_SUBJECT;
			} else if (invalidType.equals(INVALID_ACCOUNTS)) {
				subject = GEOTAB_INVALID_ACCOUNTS_SUBJECT;
			} else if (invalidType.equals(INVALID_DATABASE)) {
				subject = GEOTAB_INVALID_DATABASE_SUBJECT;
			} else if (invalidType.equals(INVALID_SETUP)) {
				subject = GEOTAB_INVALID_SETUP_SUBJECT;
			}

			if (invalidType.equals(INVALID_RECORDS)) {
				body = GEOTAB_INVALID_RECORDS_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ GEOTAB_TABLE_FORMAT_INVALID_RECORDS;
			} else if (invalidType.equals(UNMATCHED_RECORDS)) {
				body = GEOTAB_UNMATCHED_RECORDS_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ GEOTAB_TABLE_FORMAT_UNMATCHED_RECORDS;
			} else if (invalidType.equals(INVALID_ACCOUNTS)) {
				body = GEOTAB_INVALID_ACCOUNTS_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ GEOTAB_TABLE_FORMAT_INVALID_ACCOUNTS;
			} else if (invalidType.equals(INVALID_DATABASE)) {
				body = GEOTAB_INVALID_DATABASE_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ GEOTAB_TABLE_FORMAT_INVALID_DATABASE;
			} else if (invalidType.equals(INVALID_SETUP)) {
				body = GEOTAB_INVALID_SETUP_BODY_INIT;
			}
			if (invalidData != null && invalidData.size() > 0) {
				if (!invalidType.equals(INVALID_SETUP)) {
					for (String invalid : invalidData) {
						body += "<tr> " + invalid + " </tr>";
					}
				}
				body += TABLE_FORMATTED_BODY_END;
			}
			geotabEmail.getTo().clear();
			geotabEmail.setTo(new EmailAddress(emailTo));
			geotabEmail.setFrom(new EmailAddress(emailFrom));
			geotabEmail.setSubject(subject);
			geotabEmail.setMessage(body);
			geotabEmail.getAttachments().clear();

			this.emailService.sendEmail(geotabEmail);

		} catch (MalException me) {
			logger.error(me);
			throw me;
		} catch (Exception e) {
			logger.error(e);
			throw new MalException("generic.error",
					new String[] { "Failed to send Geotab email. "
							+ "Please notify the Helpdesk." });
		}

	}
	
	@Override
	public void emailForFleetCompleteInvalidRecords(List<String> invalidData,
			String invalidType, Map<String, String> emailConfig) {
		Email geotabEmail = new Email();
		geotabEmail.setHtmlFmt(true);
		this.emailUserName = emailConfig.get("USERNAME");
		this.emailPassword = emailConfig.get("PASSWORD");
		this.archiveInSendItems = emailConfig.get("ARCHIVE");
		this.enabled = emailConfig.get("ENABLED");
		this.emailTo = emailConfig.get("TOADD");
		this.emailFrom = emailConfig.get("FROMADD");

		this.emailService.setEmailUserName(this.emailUserName);
		this.emailService.setEmailPassword(this.emailPassword);
		this.emailService.setSaveInSentItems(archiveInSendItems);

		String subject = "";
		String body = "";

		try {

			if (this.enabled.equalsIgnoreCase("false"))
				return;
			if (invalidType.equals(INVALID_RECORDS)) {
				subject = FLEET_COMPLETE_INVALID_RECORDS_SUBJECT;
			} else if (invalidType.equals(UNMATCHED_RECORDS)) {
				subject = FLEET_COMPLETE_UNMATCHED_RECORDS_SUBJECT;
			} else if (invalidType.equals(AUTHENTICATION_FAILED)){
				subject = FLEET_COMPLETE_AUTHENTICATION_FAILED_SUBJECT;
			}

			if (invalidType.equals(INVALID_RECORDS)) {
				body = FLEET_COMPLETE_INVALID_RECORDS_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ FLEET_COMPLETE_TABLE_FORMAT_INVALID_RECORDS;
			} else if (invalidType.equals(UNMATCHED_RECORDS)) {
				body = FLEET_COMPLETE_UNMATCHED_RECORDS_BODY_INIT + TABLE_FORMATTED_BODY_START
						+ FLEET_COMPLETE_TABLE_FORMAT_UNMATCHED_RECORDS;
			} else if (invalidType.equals(AUTHENTICATION_FAILED)){
				body = FLEET_COMPLETE_AUTHENTICATION_FAILED_BODY_INIT;
			}
			
			if (invalidData != null && invalidData.size() > 0) {
				if(invalidType.equals(AUTHENTICATION_FAILED)){
					for (String invalid : invalidData) {
						body += invalid + "<br />";
					}
				}else{
					for (String invalid : invalidData) {
					body += "<tr> " + invalid + " </tr>";
					}
					body += TABLE_FORMATTED_BODY_END;
				}
			}
			geotabEmail.getTo().clear();
			geotabEmail.setTo(new EmailAddress(emailTo));
			geotabEmail.setFrom(new EmailAddress(emailFrom));
			geotabEmail.setSubject(subject);
			geotabEmail.setMessage(body);
			geotabEmail.getAttachments().clear();

			this.emailService.sendEmail(geotabEmail);

		} catch (MalException me) {
			logger.error(me);
			throw me;
		} catch (Exception e) {
			logger.error(e);
			throw new MalException("generic.error",
					new String[] { "Failed to send Geotab email. "
							+ "Please notify the Helpdesk." });
		}
	}
}
