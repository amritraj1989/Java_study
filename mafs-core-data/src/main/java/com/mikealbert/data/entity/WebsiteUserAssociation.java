package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * 
 * Web site associations
 */
@Entity
@Table(name="WEB_USER_ASSOCIATIONS")
public class WebsiteUserAssociation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WEB_USER_ASSOCIATIONS_SEQ")    
    @SequenceGenerator(name="WEB_USER_ASSOCIATIONS_SEQ", sequenceName="WEB_USER_ASSOCIATIONS_SEQ", allocationSize=1)      
	@Column(name="ID")
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAssociationId() {
		return associationId;
	}

	public void setAssociationId(long associationId) {
		this.associationId = associationId;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
    @JoinColumn(name = "USER_ID")
	@ManyToOne(optional = false)
    private WebsiteUser websiteUser;    

	public WebsiteUser getWebsiteUser() {
		return websiteUser;
	}

	public void setWebsiteUser(WebsiteUser websiteUser) {
		this.websiteUser = websiteUser;
	}

	@Column(name="ASSOCIATION_ID")
	private long associationId;

	@Column(name="USER_TYPE")
	private String userType;

    public WebsiteUserAssociation() {
    }
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.WebsiteUserAssociation[ id=" + id +" , associationId "+ associationId+" ]";
    }

}