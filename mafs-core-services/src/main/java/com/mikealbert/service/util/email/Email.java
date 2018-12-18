package com.mikealbert.service.util.email;

import java.util.ArrayList;
import java.util.List;
import com.mikealbert.service.util.email.EmailAddress;

/**
 * Abstraction for an email.
 * 
 */
public class Email {

	private List<EmailAddress> to = new ArrayList<EmailAddress>();
	private EmailAddress from;
	private EmailAddress replyTo ;
	private List<EmailAddress> cc = new ArrayList<EmailAddress>();
	private List<EmailAddress> bcc = new ArrayList<EmailAddress>();
	private String subject;
	private String message;
	private boolean htmlFmt;
	private List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
	private List<EmailInline> inlines = new ArrayList<EmailInline>();
	
	
	public List<EmailAddress> getBcc() {
		return bcc;
	}
	public void setBcc(List<EmailAddress> bcc) {
		if(bcc != null ){
			this.bcc = bcc;			
		}
	}
	public void setBcc(EmailAddress bcc){
		if(bcc != null && (!bcc.getAddress().equals(""))){
			this.bcc.add(bcc);	
		}
	}
	public List<EmailAddress> getCc() {
		return cc;
	}
	public void setCc(List<EmailAddress> cc) {
		if(cc != null){
			this.cc = cc;			
		}
	}
	public void setCc(EmailAddress cc){
		if(cc != null && (!cc.getAddress().equals(""))){
			this.cc.add(cc);			
		}
	}
	public EmailAddress getFrom() {
		return from;
	}
	public void setFrom(EmailAddress from) {
		if(from != null && (!from.getAddress().equals(""))){
			this.from = from;			
		}

	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public EmailAddress getReplyTo() {
		return replyTo;
	}
	public void setReplyTo(EmailAddress replyTo) {
		if(replyTo != null && (!replyTo.getAddress().equals(""))){
			this.replyTo = replyTo;
		}
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public List<EmailAddress> getTo() {
		return to;
	}
	public void setTo(List<EmailAddress> to) {
		if(to != null){
			this.to = to;			
		}
	}
	public void setTo(EmailAddress to){
		if(to != null && (!to.getAddress().equals(""))){
			this.to.add(to);	
		}
	}
	public void setSimpleTo(String toStringList) {
		if(toStringList != null && (!toStringList.equals(""))){
			for (String toStr : toStringList.split(",")){
				EmailAddress toAddr = new EmailAddress();
				toAddr.setAddress(toStr);
				this.to.add(toAddr);
			}		
		}
	}
	public String getDelimitedEmailAddresses(List<EmailAddress> emailAddresses, String delimiter) {
		StringBuilder recipientListBuilder = new StringBuilder();
		
		for(EmailAddress emailAddress : emailAddresses) {
			if(recipientListBuilder.length() == 0) 
				recipientListBuilder.append(emailAddress.getAddress());
			else
				recipientListBuilder.append(delimiter).append(emailAddress.getAddress());
		}
		
		return recipientListBuilder.toString();
	}
	
	public List<EmailAttachment> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<EmailAttachment> attachments) {
		this.attachments = attachments;
	}
	public void setAttachment(EmailAttachment attachment){
		this.attachments.add(attachment);
	}
	public List<EmailInline> getInlines() {
		return inlines;
	}
	public void setInlines(List<EmailInline> inlines) {
		this.inlines = inlines;
	}	
	public void setInline(EmailInline inline) {
		this.inlines.add(inline);
	}
	public boolean isHtmlFmt() {
		return htmlFmt;
	}
	public void setHtmlFmt(boolean htmlFmt) {
		this.htmlFmt = htmlFmt;
	}
}
