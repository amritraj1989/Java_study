package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;
import com.mikealbert.data.beanvalidation.MASize;

/**
 * Mapped to CONTACTS table
 */

@Entity
@Table(name = "CONTACTS")
public class Contact extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 4424118551419715990L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DRV_SEQ")    
    @SequenceGenerator(name="DRV_SEQ", sequenceName="DRV_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "CNT_ID")
    private Long contactId;

    // TODO: should this change across all the entities to something like "enteredBy"?
    @Size(max = 25)
    @NotNull
    @Column(name = "ORIGINATOR_CODE")
    private String userName;
    
    @Size(max = 1)
    @Column(name = "DEFAULT_IND")
    private String defaultInd;
    
    @Size(max = 10)
    @Column(name = "CONTACT_TYPE")
    private String contactType;
    
    @Size(max = 80)
    @Column(name = "E_MAIL")
    private String email;
    
    @Basic(optional = false)
    @MANotNull(label = "Title")   
    @MASize(label = "Title", min = 1, max = 10)   
    @Column(name = "TITLE")    
    private String title;
    
    @Basic(optional = false)
    @MANotNull(label = "Contact First Name")
    @MASize(label = "Contact First Name", min = 1, max = 80)
    @Column(name = "FIRST_NAME")
    private String firstName;

    @Basic(optional = false)
    @MANotNull(label = "Contact Last Name")
    @MASize(label = "Contact Last Name", min = 1, max = 80)
    @Column(name = "LAST_NAME")
    private String lastName;
   
    @Basic(optional = false)
    @Size(max = 25)
    @MANotNull(label = "Job Title")
    @MASize(label = "Job Title", min = 1, max = 25)
    @Column(name = "JOB_TITLE")
    private String jobTitle;
    
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;
    
    @JoinColumn(name="SUP_SUP_ID", referencedColumnName = "SUP_ID")
    @ManyToOne
    private ServiceProvider serviceProvider;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contact", orphanRemoval=true)    
    private List<ClientContact> clientContacts;      
    
	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDefaultInd() {
		return defaultInd;
	}

	public void setDefaultInd(String defaultInd) {
		this.defaultInd = defaultInd;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}
