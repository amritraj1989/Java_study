package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the MUL_QUOTE_ELE database table.
 * 
 */
@Entity
@Table(name="MUL_QUOTE_ELE")
public class MulQuoteEle implements Serializable {
	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TMQE_SEQ")    
    @SequenceGenerator(name="TMQE_SEQ", sequenceName="TMQE_SEQ", allocationSize=1)
    @Basic(optional = false)
	@Column(name="TMQE_SEQ")
	private long tmqeSeq;

	@Column(name="ADDIT_ELEM")
	private String additElem;

	@Column(name="AEL_ID")
	private BigDecimal aelId;

	@Column(name="ALLOW_MOD")
	private String allowMod;

	private String dependants;

	@Column(name="ELEMENT_TYPE")
	private String elementType;

	@Column(name="GROUP_CODE")
	private BigDecimal groupCode;	

	@Column(name="MANDATORY_IND")
	private String mandatoryInd;

	@Column(name="PEL_ID")
	private BigDecimal pelId;

	@Column(name="PRD_PRODUCT_CODE")
	private String prdProductCode;

	@Column(name="SELECTED_IND")
	private String selectedInd;

	@Column(name="TAX_CODE")
	private String taxCode;

	@Column(name="TAX_RATE")
	private BigDecimal taxRate;

    @Column(name = "RECALC_NEEDED")
    private String reCalcNeeded;
	
	//bi-directional many-to-one association to Quotation
    @ManyToOne
	@JoinColumn(name="QUO_ID")
	private Quotation quotation;
    
    @ManyToOne
	@JoinColumn(name="LEL_ID", nullable=false)
	private LeaseElement leaseElement;

	//bi-directional many-to-one association to MulQuoteOpt
	@OneToMany(mappedBy="mulQuoteEle")
	private List<MulQuoteOpt> mulQuoteOpts;

    public MulQuoteEle() {
    }

	public long getTmqeSeq() {
		return this.tmqeSeq;
	}

	public void setTmqeSeq(long tmqeSeq) {
		this.tmqeSeq = tmqeSeq;
	}

	public String getAdditElem() {
		return this.additElem;
	}

	public void setAdditElem(String additElem) {
		this.additElem = additElem;
	}

	public BigDecimal getAelId() {
		return this.aelId;
	}

	public void setAelId(BigDecimal aelId) {
		this.aelId = aelId;
	}

	public String getAllowMod() {
		return this.allowMod;
	}

	public void setAllowMod(String allowMod) {
		this.allowMod = allowMod;
	}

	public String getDependants() {
		return this.dependants;
	}

	public void setDependants(String dependants) {
		this.dependants = dependants;
	}

	public String getElementType() {
		return this.elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public BigDecimal getGroupCode() {
		return this.groupCode;
	}

	public void setGroupCode(BigDecimal groupCode) {
		this.groupCode = groupCode;
	}

	public String getMandatoryInd() {
		return this.mandatoryInd;
	}

	public void setMandatoryInd(String mandatoryInd) {
		this.mandatoryInd = mandatoryInd;
	}

	public BigDecimal getPelId() {
		return this.pelId;
	}

	public void setPelId(BigDecimal pelId) {
		this.pelId = pelId;
	}

	public String getPrdProductCode() {
		return this.prdProductCode;
	}

	public void setPrdProductCode(String prdProductCode) {
		this.prdProductCode = prdProductCode;
	}

	public String getSelectedInd() {
		return this.selectedInd;
	}

	public void setSelectedInd(String selectedInd) {
		this.selectedInd = selectedInd;
	}

	public String getTaxCode() {
		return this.taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	public BigDecimal getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public Quotation getQuotation() {
		return this.quotation;
	}

	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}
	
	public LeaseElement getLeaseElement() {
		return leaseElement;
	}

	public void setLeaseElement(LeaseElement leaseElement) {
		this.leaseElement = leaseElement;
	}
	
	public List<MulQuoteOpt> getMulQuoteOpts() {
		return this.mulQuoteOpts;
	}

	public void setMulQuoteOpts(List<MulQuoteOpt> mulQuoteOpts) {
		this.mulQuoteOpts = mulQuoteOpts;
	}
	
	public String getReCalcNeeded() {
		return reCalcNeeded;
	}

	public void setReCalcNeeded(String reCalcNeeded) {
		this.reCalcNeeded = reCalcNeeded;
	}

	@Override
    public String toString() {
        return "com.mikealbert.entity.MulQuoteEle[ tmqeSeq=" + tmqeSeq + " ]";
    }
	
}