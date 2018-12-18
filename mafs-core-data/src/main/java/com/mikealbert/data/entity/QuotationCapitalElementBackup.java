package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the QUOTE_CAP_ELE_BACKUP database table.
 * @author sibley
 */
@Entity
@Table(name="QUOTE_CAP_ELE_BACKUP")
public class QuotationCapitalElementBackup extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @NotNull
    @Column(name = "QCEB_ID")
    private Long qcebId;
    
    @Column(name = "CER_CER_ID")
    private Long cerCerId;
    
    @NotNull
    @Column(name = "VALUE")
    private BigDecimal value;
    
    @Size(max = 1)
    @Column(name = "QUOTE_CAPITAL")
    private String quoteCapital;
    
    @Size(max = 1)
    @Column(name = "PURCHASE_ORDER")
    private String purchaseOrder;
    
    @Size(max = 1)
    @Column(name = "FIXED_ASSET")
    private String fixedAsset;
    
    @Size(max = 1)
    @Column(name = "RECLAIM_ACTIONED")
    private String reclaimActioned;
    
    @Size(max = 10)
    @Column(name = "SOURCE_CODE")
    private String sourceCode;
    
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)    
    private QuotationModel quotationModel;	
    
	//bi-directional many-to-one association to CapitalElement
    @ManyToOne
	@JoinColumn(name="CEL_CEL_ID", nullable=false)
	private CapitalElement capitalElement;
    

    public QuotationCapitalElementBackup() {
    }

    public Long getQcebId() {
        return qcebId;
    }

    public void setQcebId(Long qcebId) {
        this.qcebId = qcebId;
    }

    public Long getCerCerId() {
        return cerCerId;
    }

    public void setCerCerId(Long cerCerId) {
        this.cerCerId = cerCerId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getQuoteCapital() {
        return quoteCapital;
    }

    public void setQuoteCapital(String quoteCapital) {
        this.quoteCapital = quoteCapital;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getFixedAsset() {
        return fixedAsset;
    }

    public void setFixedAsset(String fixedAsset) {
        this.fixedAsset = fixedAsset;
    }

    public String getReclaimActioned() {
        return reclaimActioned;
    }

    public void setReclaimActioned(String reclaimActioned) {
        this.reclaimActioned = reclaimActioned;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public CapitalElement getCapitalElement() {
		return capitalElement;
	}

	public void setCapitalElement(CapitalElement capitalElement) {
		this.capitalElement = capitalElement;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (qcebId != null ? qcebId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof QuotationCapitalElementBackup)) {
            return false;
        }
        QuotationCapitalElementBackup other = (QuotationCapitalElementBackup) object;
        if ((this.qcebId == null && other.qcebId != null) || (this.qcebId != null && !this.qcebId.equals(other.qcebId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.vision.entity.QuoteCapEleBackup[ qcebId=" + qcebId + " ]";
    }
	
	
}