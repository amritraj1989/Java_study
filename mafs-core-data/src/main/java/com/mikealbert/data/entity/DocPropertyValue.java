package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the DOC_PROPERTY_VALUES database table.
 * 
 */
@Entity
@Table(name="DOC_PROPERTY_VALUES")
public class DocPropertyValue extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DPVL_SEQ")    
    @SequenceGenerator(name="DPVL_SEQ", sequenceName="DPVL_SEQ", allocationSize=1)
    @Column(name = "DPVL_ID")
    private Long dpvlId;
    
	@Column(name="PROPERTY_VALUE")
	private String propertyValue;

	@JoinColumn(name = "DOC_DOC_ID", referencedColumnName = "DOC_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Doc doc;

	//bi-directional many-to-one association to DocProperty
	@ManyToOne
	@JoinColumn(name="DPT_DPT_ID")
	private DocProperty docProperty;
	
	@JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private ExternalAccount externalAccount;

	public Long getDpvlId() {
		return dpvlId;
	}

	public void setDpvlId(Long dpvlId) {
		this.dpvlId = dpvlId;
	}

	public DocPropertyValue() {
	}

	public String getPropertyValue() {
		return this.propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	public DocProperty getDocProperty() {
		return this.docProperty;
	}

	public void setDocProperty(DocProperty docProperty) {
		this.docProperty = docProperty;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Doc getDoc() {
		return doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

}