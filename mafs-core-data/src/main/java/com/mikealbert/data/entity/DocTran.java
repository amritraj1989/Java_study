package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the DOC_TRANS database table.
 * 
 */
@Entity
@Table(name="DOC_TRANS")
public class DocTran implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private DocTranPK id;

	@Column(name="AUTO_HOLD")
	private String autoHold;

	@Column(name="AUTO_ISS_NSP")
	private String autoIssNsp;

	@Column(name="BUILD_USAGE")
	private String buildUsage;

	@Column(name="CRT_C_ID")
	private java.math.BigDecimal crtCId;

	@Column(name="CRT_EXT_ACC_TYPE")
	private String crtExtAccType;

	private String debtor;

	@Column(name="DEF_TERMS_CODE")
	private String defTermsCode;

	@Column(name="DEL_DOC")
	private String delDoc;

	private String description;

	@Column(name="\"DISABLE\"")
	private String disable;

	@Column(name="DISCOUNT_LEVEL")
	private String discountLevel;

	@Column(name="DIV_ANAL_CODE")
	private String divAnalCode;

	@Column(name="DIV_CAT_ID")
	private java.math.BigDecimal divCatId;

	@Column(name="DN_C_ID")
	private java.math.BigDecimal dnCId;

	@Column(name="DOC_IND")
	private String docInd;

	@Column(name="\"DOMAIN\"")
	private String domain;

	@Column(name="GL_DIST")
	private String glDist;

	@Column(name="GROUP_CODE")
	private String groupCode;

	@Column(name="INVOICE_PRINT_REPORT")
	private String invoicePrintReport;

	@Column(name="MODULE_TYPE")
	private String moduleType;

	@Column(name="OVER_CHARGE")
	private String overCharge;

	private String sales;

	private String stock;

	@Temporal(TemporalType.DATE)
	private Date versionts;

	//bi-directional many-to-one association to ExtAccTranTerm
	@OneToMany(mappedBy="id.docTran")
	private List<ExtAccTranTerm> extAccTranTerms;

	public DocTran() {
	}

	public DocTranPK getId() {
		return this.id;
	}

	public void setId(DocTranPK id) {
		this.id = id;
	}

	public String getAutoHold() {
		return this.autoHold;
	}

	public void setAutoHold(String autoHold) {
		this.autoHold = autoHold;
	}

	public String getAutoIssNsp() {
		return this.autoIssNsp;
	}

	public void setAutoIssNsp(String autoIssNsp) {
		this.autoIssNsp = autoIssNsp;
	}

	public String getBuildUsage() {
		return this.buildUsage;
	}

	public void setBuildUsage(String buildUsage) {
		this.buildUsage = buildUsage;
	}

	public java.math.BigDecimal getCrtCId() {
		return this.crtCId;
	}

	public void setCrtCId(java.math.BigDecimal crtCId) {
		this.crtCId = crtCId;
	}

	public String getCrtExtAccType() {
		return this.crtExtAccType;
	}

	public void setCrtExtAccType(String crtExtAccType) {
		this.crtExtAccType = crtExtAccType;
	}

	public String getDebtor() {
		return this.debtor;
	}

	public void setDebtor(String debtor) {
		this.debtor = debtor;
	}

	public String getDefTermsCode() {
		return this.defTermsCode;
	}

	public void setDefTermsCode(String defTermsCode) {
		this.defTermsCode = defTermsCode;
	}

	public String getDelDoc() {
		return this.delDoc;
	}

	public void setDelDoc(String delDoc) {
		this.delDoc = delDoc;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisable() {
		return this.disable;
	}

	public void setDisable(String disable) {
		this.disable = disable;
	}

	public String getDiscountLevel() {
		return this.discountLevel;
	}

	public void setDiscountLevel(String discountLevel) {
		this.discountLevel = discountLevel;
	}

	public String getDivAnalCode() {
		return this.divAnalCode;
	}

	public void setDivAnalCode(String divAnalCode) {
		this.divAnalCode = divAnalCode;
	}

	public java.math.BigDecimal getDivCatId() {
		return this.divCatId;
	}

	public void setDivCatId(java.math.BigDecimal divCatId) {
		this.divCatId = divCatId;
	}

	public java.math.BigDecimal getDnCId() {
		return this.dnCId;
	}

	public void setDnCId(java.math.BigDecimal dnCId) {
		this.dnCId = dnCId;
	}

	public String getDocInd() {
		return this.docInd;
	}

	public void setDocInd(String docInd) {
		this.docInd = docInd;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getGlDist() {
		return this.glDist;
	}

	public void setGlDist(String glDist) {
		this.glDist = glDist;
	}

	public String getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public String getInvoicePrintReport() {
		return this.invoicePrintReport;
	}

	public void setInvoicePrintReport(String invoicePrintReport) {
		this.invoicePrintReport = invoicePrintReport;
	}

	public String getModuleType() {
		return this.moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getOverCharge() {
		return this.overCharge;
	}

	public void setOverCharge(String overCharge) {
		this.overCharge = overCharge;
	}

	public String getSales() {
		return this.sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public String getStock() {
		return this.stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Date getVersionts() {
		return this.versionts;
	}

	public void setVersionts(Date versionts) {
		this.versionts = versionts;
	}

	public List<ExtAccTranTerm> getExtAccTranTerms() {
		return this.extAccTranTerms;
	}

	public void setExtAccTranTerms(List<ExtAccTranTerm> extAccTranTerms) {
		this.extAccTranTerms = extAccTranTerms;
	}

	
	public ExtAccTranTerm addExtAccTranTerms(ExtAccTranTerm extAccTranTerms) {
		getExtAccTranTerms().add(extAccTranTerms);
		extAccTranTerms.getId().setDocTran(this);

		return extAccTranTerms;
	}

	public ExtAccTranTerm removeExtAccTranTerms(ExtAccTranTerm extAccTranTerms) {
		getExtAccTranTerms().remove(extAccTranTerms);
		extAccTranTerms.getId().setDocTran(null);

		return extAccTranTerms;
	}
}