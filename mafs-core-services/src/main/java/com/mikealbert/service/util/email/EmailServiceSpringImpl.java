package com.mikealbert.service.util.email;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
// TODO: I don't like this but working with IMAPS does not exist in spring JavaMailSender interface
//import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.util.MALUtilities;

/**
 * Email sender implementation based on Spring email APIs.
 * 
 *
 */
@Service("emailService")
public class EmailServiceSpringImpl implements EmailService {
	
	private JavaMailSenderImpl mailSender;
	private Email _mail;
	
	@Value("$email{email.archiveInSendItems}")
	private String saveInSentItems;
	
	@Value("$email{email.host}")
	private String emailHost;
	@Value("$email{email.username}")
	private String emailUserName;
	@Value("$email{email.password}")
	private String emailPassword;

	
	public void sendEmail(){
		this.sendEmail(_mail);
	}
	
	public void sendEmail(Email mail){
		if(mail.isHtmlFmt()){
			this.sendEmailInternal(mail,true);
		}else{	
			this.sendEmailInternal(mail,false);
		}
	}
	
	
	public void setMail(Email mail){
		_mail = mail;
	}
	
	@Async
	public void sendAsyncEmail(Email mail) {
		sendEmail(mail);
	}
	
	/**
	 * Helper method to send mime message as appropriate type
	 * (i.e. text or html).
	 * 
	 * @param mail	email message to send
	 * @param html	flag indicating if body is html or not
	 */
	private void sendEmailInternal(Email mail, boolean html){
		MimeMessage msg = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		
		// populate email
		try {
			// create helper
			if(mail.getAttachments().size() > 0 || mail.getInlines().size() > 0){
				helper = new MimeMessageHelper(msg,true);
			}
			else{
				helper = new MimeMessageHelper(msg,false);
			}
			helper.setTo(this.createAddress(mail.getTo()));
			helper.setBcc(this.createAddress(mail.getBcc()));
			helper.setCc(this.createAddress(mail.getCc()));
			helper.setFrom(this.createAddress(mail.getFrom()));
			if(this.createAddress(mail.getReplyTo()) != null){
				helper.setReplyTo(this.createAddress(mail.getReplyTo()));
			}
			helper.setSentDate(new Date());
			helper.setSubject(mail.getSubject());
			helper.setText(mail.getMessage(), html);
			addAttachements(helper, mail);
			addInlines(helper, mail);

			if(MALUtilities.isNotEmptyString(saveInSentItems) && Boolean.parseBoolean(saveInSentItems)){
				// TODO: archive the mail in the sent items
				Session session = this.mailSender.getSession();
				// send email
			    // Copy message to "Sent Items" folder as read
			    Store store = session.getStore("imaps");
			    store.connect(emailHost, emailUserName, emailPassword);
			    Folder folder = store.getFolder("Sent Items");
			    folder.open(Folder.READ_WRITE);
			    msg.setFlag(Flag.SEEN, true);
			    folder.appendMessages(new Message[] {msg});		    
			    
			    mailSender.send(msg);
			    store.close();
			    
			}else{
				mailSender.send(msg);
			}
			
		} catch (MessagingException e) {
			throw new EmailException("Error populating mime email message.",e);
		}
				
	}

	/**
	 * Helper method to add email attachments.
	 * 
	 * @param helper	mime message helper
	 * @param mail		email
	 * @throws MessagingException 
	 */
	private void addAttachements(MimeMessageHelper helper, Email mail) throws MessagingException{
		for(EmailAttachment a : mail.getAttachments()){
			helper.addAttachment(a.getFileName(), a.getContentResource());
		}
	}
	
	/**
	 * Helper method to add email inlines.
	 * 
	 * @param helper	mime message helper
	 * @param mail		email
	 * @throws MessagingException 
	 */
	private void addInlines(MimeMessageHelper helper, Email mail) throws MessagingException{
		for(EmailInline inline : mail.getInlines()){
			helper.addInline(inline.getContentId() , inline.getContentResource());
		}
	}
	
	
	/**
	 * Helper method to create an array of InternetAddress instances
	 * from the specified list of EmailAddresses.
	 * 
	 * @param addresses	email addresses
	 * @return			internet addresses
	 */
	private InternetAddress[] createAddress(List<EmailAddress> addresses){
		InternetAddress[] addrs = new InternetAddress[addresses.size()];
		if(addresses.size() > 0){
			int i = 0;
			for(EmailAddress a : addresses){
				addrs[i++] = this.createAddress(a);
			}
		}
		return addrs;
	}
	
	/**
	 * Helper method to create an InternetAddress instance
	 * from the specified EmailAddress intance.
	 * 
	 * @param a	email address 
	 * @return	internet address
	 */
	private InternetAddress createAddress(EmailAddress a){
		InternetAddress addr = null;
		// check if address is null
		if(a == null){
			return null;
		}
		addr = new InternetAddress();
		addr.setAddress(a.getAddress());
		try {
			addr.setPersonal(a.getDisplayName());
		} catch (UnsupportedEncodingException e) {
			// don't fail, personal will just be missing
		}
		return addr;
	}

	/**
	 * Dependency injection method for the mail sender used
	 * to send email.
	 * 
	 * @param mailSender	java mail sender
	 */
	public void setMailSender(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void setEmailHost(String host) {
		this.emailHost = host;
		
	}

	@Override
	public void setEmailUserName(String userName) {
		this.emailUserName = userName;
		
	}

	@Override
	public void setEmailPassword(String password) {
		this.emailPassword = password;
		
	}
	
	@Override
	public void setSaveInSentItems(String trueFalse){
		this.saveInSentItems = trueFalse;
	}
}
