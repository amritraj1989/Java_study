package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the REPORTS_SPOOL database table.
 * @author Amritraj Singh
 */
@Entity
@Table(name="REPORTS_SPOOL")
public class ReportsSpool implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    protected ReportsSpoolPK reportsSpoolPK;
    
    @Column(name = "DELIVERY_METHOD")
    private String deliveryMethod;
    
     
    @JoinColumns({
        @JoinColumn(name = "C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private ExternalAccount externalAccount;
    
    
    @Column(name="QMD_QMD_ID")
	private Long qmdId;
    
    @Column(name="DOC_ID")
	private Long docId;
    
    @Column(name="FMS_FMS_ID")
	private Long fmsId;
    
    @Column(name = "MODULE_NAME")
    private String moduleName;

	public ReportsSpoolPK getReportsSpoolPK() {
		return reportsSpoolPK;
	}

	public void setReportsSpoolPK(ReportsSpoolPK reportsSpoolPK) {
		this.reportsSpoolPK = reportsSpoolPK;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public ExternalAccount getExternalAccount() {
		return externalAccount;
	}

	public void setExternalAccount(ExternalAccount externalAccount) {
		this.externalAccount = externalAccount;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Long getDocId() {
		return docId;
	}

	public void setDocId(Long docId) {
		this.docId = docId;
	}

	public Long getFmsId() {
		return fmsId;
	}

	public void setFmsId(Long fmsId) {
		this.fmsId = fmsId;
	}

	
}