package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the DOC_SUPPLIERS database table.
 * @author Singh
 */
@Entity
@Table(name="DOC_SUPPLIERS")
public class DocSupplier extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DSP_SEQ")    
    @SequenceGenerator(name="DSP_SEQ", sequenceName="DSP_SEQ", allocationSize=1)
	@Column(name="DSP_ID")
	private long dspId;

	@ManyToOne
	private Doc doc;

	@ManyToOne
	@JoinColumn(name="SUP_SUP_ID")
	private Supplier supplier;

	
	@Column(name="WORKSHOP_CAPABILITY")
	private String workshopCapabilityCode;

	public DocSupplier() {
	}

	public long getDspId() {
		return this.dspId;
	}

	public void setDspId(long dspId) {
		this.dspId = dspId;
	}

	public Date getVersionts() {
		return this.versionts;
	}

	public void setVersionts(Date versionts) {
		this.versionts = versionts;
	}

	public Doc getDoc() {
		return this.doc;
	}

	public void setDoc(Doc doc) {
		this.doc = doc;
	}

	public Supplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getWorkshopCapabilityCode() {
		return this.workshopCapabilityCode;
	}

	public void setWorkshopCapabilityCode(String workshopCapabilityCode) {
		this.workshopCapabilityCode = workshopCapabilityCode;
	}

	@Override
	public String toString() {
		return "DocSupplier [dspId=" + dspId + ", doc=" + doc.getDocId() + ", supplier="
				+ supplier.getSupId() + ", workshopCapabilityCode="
				+ workshopCapabilityCode + "]";
	}
	
	

}