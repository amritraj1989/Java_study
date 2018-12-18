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

@Entity
@Table(name = "CONTRACT_REPORT_LANGUAGE")
public class ContractReportLanguage  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CRLD_SEQ")
	@SequenceGenerator(name = "CRLD_SEQ", sequenceName = "CRLD_SEQ", allocationSize = 1)
	@Column(name = "CRLD_ID")
	private Long	crldId;
	@Column(name = "REPORT_NAME")
	private	String	reportName;
	@Column(name = "PRODUCT_CODE")
	private	String	productCode;
	@Column(name = "EFFECTIVE_FROM_DATE")
	private	Date	effectiveFromDate;
	@Column(name = "EFFECTIVE_TO_DATE")
	private	Date	effectiveToDate;
	@Column(name = "LOCATION")
	private	String	location;
	@Column(name = "CALCULATION_TEXT")
	private	String	calculationText;
	@Column(name = "MODULE_FILENAME")
	private	String	moduleFileName;
	public Long getCrldId() {
		return crldId;
	}
	public void setCrldId(Long crldId) {
		this.crldId = crldId;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Date getEffectiveFromDate() {
		return effectiveFromDate;
	}
	public void setEffectiveFromDate(Date effectiveFromDate) {
		this.effectiveFromDate = effectiveFromDate;
	}
	public Date getEffectiveToDate() {
		return effectiveToDate;
	}
	public void setEffectiveToDate(Date effectiveToDate) {
		this.effectiveToDate = effectiveToDate;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCalculationText() {
		return calculationText;
	}
	public void setCalculationText(String calculationText) {
		this.calculationText = calculationText;
	}
	public String getModuleFileName() {
		return moduleFileName;
	}
	public void setModuleFileName(String moduleFileName) {
		this.moduleFileName = moduleFileName;
	}
	
	
}
