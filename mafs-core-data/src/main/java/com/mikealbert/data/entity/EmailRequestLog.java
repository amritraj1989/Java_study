package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="EmailRequestLog")
@Entity
@Table(name="EMAIL_REQUEST_LOG")
public class EmailRequestLog extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8629514261507412372L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ERL_SEQ")    
    @SequenceGenerator(name="ERL_SEQ", sequenceName="ERL_SEQ", allocationSize=1)	
    @Column(name = "ERL_ID")
    private Long erlId;

	@NotNull
    @Column(name = "OBJECT_ID")
    private String objectId;
    
	@NotNull
    @Column(name = "OBJECT_TYPE")
    private String objectType;

	@NotNull
    @Column(name = "EVENT")
    private String event;
	
	@NotNull
	@Size(min=1, max=1)
	@Column(name="SCHEDULED_YN")
	private String scheduledYN;	
	
	@NotNull
    @Column(name = "USERNAME")
    private String username;	

	@NotNull
    @Column(name = "REQUEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;
    
    @Column(name = "PROCESSED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processedDate;    

    @Column(name = "STATUS")
    private String status;
	
    @Column(name = "STATUS_MESSAGE")
    private String statusMessage;	
    
    @Column(name = "SENDER")
    private String sender;    
    
    @Column(name = "RECIPIENTS")
    private String recipients;  
    
    @Column(name = "PROPERTY_1")
    private String property1;  
    
    @Column(name = "PROPERTY_2")
    private String property2;      
    
    @Column(name = "PROPERTY_3")
    private String property3;      
    
    @Column(name = "PROPERTY_4")
    private String property4;      
    
    @Column(name = "PROPERTY_5")
    private String property5;          
    
    public EmailRequestLog(){}
    
    public EmailRequestLog(String objectId, String objectType, String event, String scheduledYN, String username){
    	setObjectId(objectId);
    	setObjectType(objectType);
    	setEvent(event);
    	setScheduledYN(scheduledYN);
    	setUsername(username);
    }    

	public Long getErlId() {
		return erlId;
	}

	public void setErlId(Long erlId) {
		this.erlId = erlId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getScheduledYN() {
		return scheduledYN;
	}

	public void setScheduledYN(String scheduledYN) {
		this.scheduledYN = scheduledYN;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(Date processedDate) {
		this.processedDate = processedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String message) {
		this.statusMessage = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getProperty3() {
		return property3;
	}

	public void setProperty3(String property3) {
		this.property3 = property3;
	}

	public String getProperty4() {
		return property4;
	}

	public void setProperty4(String property4) {
		this.property4 = property4;
	}

	public String getProperty5() {
		return property5;
	}

	public void setProperty5(String property5) {
		this.property5 = property5;
	}
}
