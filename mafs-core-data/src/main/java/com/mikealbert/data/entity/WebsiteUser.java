package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to WEB_USER table
 * This is used to get information about a user that is also a CA "Customer Access" web site user.
 */
@Entity
@Table(name = "WEB_USER")
public class WebsiteUser implements Serializable {
	private static final long serialVersionUID = -219016958925110551L;
	
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Long id;
	
	// TODO: add an enumeration around this
	@Column(name="USER_TYPE", nullable=false, insertable=false, updatable=false, length=31)
	private String websiteUserType;
	
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
    
    @JoinColumn(name = "CNT_ID", referencedColumnName = "CNT_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private Contact contact;

    @JoinColumn(name = "DRV_ID", referencedColumnName = "DRV_ID")
    @OneToOne(fetch = FetchType.EAGER)
    private Driver driver;

    @OneToMany(mappedBy = "websiteUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WebsiteUserAssociation> websiteUserAssociationList;    
    
	@Column(name="USER_NAME")
	private String username;

	@Column(name="LOGIN_ENABLED")
	private String loginEnabled;

	
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<WebsiteUserAssociation> getWebsiteUserAssociationList() {
		return websiteUserAssociationList;
	}

	public void setWebsiteUserAssociationList(List<WebsiteUserAssociation> websiteUserAssociationList) {
		this.websiteUserAssociationList = websiteUserAssociationList;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public String getWebsiteUserType() {
		return websiteUserType;
	}

	public void setWebsiteUserType(String websiteUserType) {
		this.websiteUserType = websiteUserType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public String getLoginEnabled() {
		return loginEnabled;
	}

	public void setLoginEnabled(String loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

	
}
