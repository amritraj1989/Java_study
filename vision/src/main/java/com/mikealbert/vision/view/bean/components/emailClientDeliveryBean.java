package com.mikealbert.vision.view.bean.components;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.service.util.email.Email;
import com.mikealbert.service.util.email.EmailAddress;
import com.mikealbert.service.util.email.EmailService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class emailClientDeliveryBean extends BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3927646164812731083L;

	private Email email;
	private String titleText;
	
	@Resource 
	@Qualifier("emailService")
	EmailService emailService;
	
	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){ 
    	try {
    		FacesContext fc = FacesContext.getCurrentInstance();
    	    email = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.email}", Email.class);
    	    titleText = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.titleText}", String.class);
		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}    	
      	    	    	
    }
	
	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}
	
	/* Helper to get/set formatted From email addresses from display */
	public String getFromAddrSimple(){
		return MALUtilities.isEmpty(email)? null : email.getFrom().getAddress();
	}
	public void setFromAddrSimple(String fromAddress){
		EmailAddress from = new EmailAddress();
		from.setAddress(fromAddress);
		if(!MALUtilities.isEmpty(email)) email.setFrom(from);
	}
	
	/* Helper to get/set formatted To email addresses from display */
	public String getToAddrSimple(){
		StringBuffer toAddr = new StringBuffer();
		toAddr.append("");
		int i = 0;
		if(!MALUtilities.isEmpty(email)){
			for(EmailAddress a : email.getTo()){
				if(MALUtilities.isNotEmptyString(a.getAddress())){
					toAddr.append(a.getAddress());
					if(i < email.getTo().size()){
						toAddr.append(";");
					}
					i++;
				}
			}
		}
		
		return toAddr.toString();
	}
	public void setToAddrSimple(String toAddress){
		List<EmailAddress> to = new ArrayList<EmailAddress>();
		if(MALUtilities.isNotEmptyString(toAddress)){
			String[] toAddrs = toAddress.split(";");
			if(toAddrs.length > 0){
				for(String a : toAddrs){
					to.add(new EmailAddress(a));
				}
			}
		}
		if(!MALUtilities.isEmpty(email)) email.setTo(to);
	}
	
	
	/* Helper to get/set formatted To email addresses from display */
	public String getCcAddrSimple(){
		StringBuffer ccAddr = new StringBuffer();
		ccAddr.append("");
		int i = 0;
		if(!MALUtilities.isEmpty(email)){
			for(EmailAddress a : email.getCc()){
				if(MALUtilities.isNotEmptyString(a.getAddress())){
					ccAddr.append(a.getAddress());
					if(i < email.getTo().size()){
						ccAddr.append(";");
					}
					i++;
				}
			}
		}
		
		return ccAddr.toString();
	}
	public void setCcAddrSimple(String ccAddress){
		List<EmailAddress> cc = new ArrayList<EmailAddress>();
		if(MALUtilities.isNotEmptyString(ccAddress)){
			String[] ccAddrs = ccAddress.split(";");
			if(ccAddrs.length > 0){
				for(String a : ccAddrs){
					cc.add(new EmailAddress(a));
				}		
			}
		}
		if(!MALUtilities.isEmpty(email)) email.setCc(cc);
	}
	
	
	
	public void send(){
		try{
			emailService.sendEmail(email);
			super.addSuccessMessage("delivery.success", "Email");
		}catch(Exception ex){
			ex.printStackTrace();
			super.addErrorMessage("generic.error.occured.while", "sending email");
		}
		
	}
	
	public void cancel(){
		
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}
	
	
}
