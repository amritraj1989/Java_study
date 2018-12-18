package com.mikealbert.service.util.email;

import org.springframework.beans.factory.annotation.Value;


public interface EmailService {

//	/**
//	 * Sends an email with html content.
//	 * 
//	 * @param mail	email message
//	 */
//	public abstract void sendHtmlEmail(Email mail);
//
//	/**
//	 * Sends an email with plain text content.
//	 * 
//	 * @param mail	email message
//	 */
//	public abstract void sendTextEmail(Email mail);
//	
//	/**
//	 * Sends an email with html content.
//	 * 
//	 * @param mail	email message
//	 */
//	public abstract void sendHtmlEmail();
//
//	/**
//	 * Sends an email with plain text content.
//	 * 
//	 * @param mail	email message
//	 */
//	public abstract void sendTextEmail();
//	
	/**
	 * Sends an email.
	 * 
	 * @param mail	email message
	 */
	public abstract void sendEmail();
	
	/**
	 * Sends an email.
	 * 
	 * @param mail	email message
	 */
	public abstract void sendEmail(Email mail);
	
	
	public abstract void sendAsyncEmail(Email mail);
	
	/**
	 * Exposed to provide capability to change host since 
	 * @param host
	 */
	public void setEmailHost(String host);
	
	/**
	 * Exposed to provide capability to change username 
	 * @param host
	 */	
	public void setEmailUserName(String userName);
	

	/**
	 * Exposed to provide capability to change password 
	 * @param host
	 */	
	public void setEmailPassword(String password);
	
	/**
	 * Exposed to provide capability to change password 
	 * @param host
	 */	
	public void setSaveInSentItems(String trueFalse);	
	
}