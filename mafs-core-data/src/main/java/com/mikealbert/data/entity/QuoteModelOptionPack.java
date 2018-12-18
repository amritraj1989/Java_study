package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the QUOTE_MODEL_OPTION_PACKS database table.
 * 
 */
@Entity
@Table(name="QUOTE_MODEL_OPTION_PACKS")
public class QuoteModelOptionPack extends com.mikealbert.data.entity.BaseEntity implements Serializable {
	private static final long serialVersionUID = 1163223081807475592L;
	
	@Id
	@SequenceGenerator(name="QMOP_SEQ_GENERATOR", sequenceName="QMOP_SEQ", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QMOP_SEQ_GENERATOR")
	@Column(name="QMOP_ID")
	private Long qmopId;
	
	@Column(precision=19, scale=2)
	private BigDecimal cost;
	
	@JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID")
    @ManyToOne(optional = false)
    private QuotationModel quotationModel;
	
	@Column(name="RESIDUAL_AMT", precision=11, scale=2)
	private BigDecimal residualAmt;
	
	//bi-directional many-to-one association to OptionPackHeader
	@ManyToOne
	@JoinColumn(name="OPH_OPH_ID", nullable=false)
	private OptionPackHeader optionPackHeader;

	public QuoteModelOptionPack() {
	}
	
	public Long getQmopId() {
		return this.qmopId;
	}

	public void setQmopId(Long qmopId) {
		this.qmopId = qmopId;
	}

	public BigDecimal getCost() {
		return this.cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public BigDecimal getResidualAmt() {
		return this.residualAmt;
	}

	public void setResidualAmt(BigDecimal residualAmt) {
		this.residualAmt = residualAmt;
	}

	public OptionPackHeader getOptionPackHeader() {
		return this.optionPackHeader;
	}

	public void setOptionPackHeader(OptionPackHeader optionPackHeader) {
		this.optionPackHeader = optionPackHeader;
	}

}