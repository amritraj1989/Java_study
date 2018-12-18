package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "QUOTATION_MODEL_FINANCES")
public class QuotationModelFinances extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMF_SEQ")    
	@SequenceGenerator(name="QMF_SEQ", sequenceName="QMF_SEQ", allocationSize=1)   
	@NotNull
	@Column(name = "QMF_ID")
	private Long qmfId;

    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false)
    private QuotationModel quotationModel;

	@Size(max = 10)
	@Column(name = "PARAMETER_ID")
	private String parameterId;

	@Size(max = 1)
	@Column(name = "STATUS")
	private String status;

    @Temporal(TemporalType.DATE)
	@Column(name = "EFFECTIVE_FROM")
	private Date effectiveFromDate;

	@Size(max = 40)
	@Column(name = "PARAMETER_KEY")
	private String parameterKey;
    
	@Size(max = 80)
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "NVALUE", precision = 11, scale = 5)
	private BigDecimal nValue;

	@Size(max = 40)
	@Column(name = "CVALUE")
	private String cValue;
	

	public QuotationModelFinances() {}


	public Long getQmfId() {
		return qmfId;
	}

	public void setQmfId(Long qmfId) {
		this.qmfId = qmfId;
	}

	public String getParameterId() {
		return parameterId;
	}

	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEffectiveFromDate() {
		return effectiveFromDate;
	}

	public void setEffectiveFromDate(Date effectiveFromDate) {
		this.effectiveFromDate = effectiveFromDate;
	}

	public String getParameterKey() {
		return parameterKey;
	}

	public void setParameterKey(String parameterKey) {
		this.parameterKey = parameterKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getnValue() {
		return nValue;
	}

	public void setnValue(BigDecimal nValue) {
		this.nValue = nValue;
	}

	public String getcValue() {
		return cValue;
	}

	public void setcValue(String cValue) {
		this.cValue = cValue;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.qmfId != null ? this.qmfId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof QuotationModelFinances)) {
			return false;
		}
		QuotationModelFinances other = (QuotationModelFinances) object;
		if ((this.qmfId == null && other.qmfId != null) || (this.qmfId != null && !this.qmfId.equals(other.qmfId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.mikealbert.entity.QuotationModelFinances[ qmfId=" + qmfId + " ]";
	}







}
