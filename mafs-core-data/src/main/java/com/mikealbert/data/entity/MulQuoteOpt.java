package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;


/**
 * The persistent class for the MUL_QUOTE_OPT database table.
 * 
 */
@Entity
@Table(name="MUL_QUOTE_OPT")
public class MulQuoteOpt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TMQO_SEQ")    
    @SequenceGenerator(name="TMQO_SEQ", sequenceName="TMQO_SEQ", allocationSize=1)
    @Basic(optional = false)
	@Column(name="TMQO_ID")
	private long tmqoId;

	@Column(name="FPR_FPR_ID")
	private BigDecimal fprFprId;

	@Column(name="POX_POX_ID")
	private BigDecimal poxPoxId;

	@Column(name="SHOW_FIELD")
	private String showField;

	//bi-directional many-to-one association to MulQuoteEle
    @ManyToOne
	@JoinColumn(name="TMQE_TMQE_ID")
	private MulQuoteEle mulQuoteEle;

    public MulQuoteOpt() {
    }

	public long getTmqoId() {
		return this.tmqoId;
	}

	public void setTmqoId(long tmqoId) {
		this.tmqoId = tmqoId;
	}

	public BigDecimal getFprFprId() {
		return this.fprFprId;
	}

	public void setFprFprId(BigDecimal fprFprId) {
		this.fprFprId = fprFprId;
	}

	public BigDecimal getPoxPoxId() {
		return this.poxPoxId;
	}

	public void setPoxPoxId(BigDecimal poxPoxId) {
		this.poxPoxId = poxPoxId;
	}

	public String getShowField() {
		return this.showField;
	}

	public void setShowField(String showField) {
		this.showField = showField;
	}

	public MulQuoteEle getMulQuoteEle() {
		return this.mulQuoteEle;
	}

	public void setMulQuoteEle(MulQuoteEle mulQuoteEle) {
		this.mulQuoteEle = mulQuoteEle;
	}
	
}