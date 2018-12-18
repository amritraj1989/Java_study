package com.mikealbert.data.entity;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;


/**
 * The persistent class for the QUOTE_PROFILE_ADJ database table.
 * 
 */
@Entity
@Table(name="QUOTE_PROFILE_ADJ")
public class QuoteProfileAdj implements Serializable {
	private static final long serialVersionUID = 1L;

    public QuoteProfileAdj() {
    }
    @Id
    @Column(name = "QPA_ID")
    private Long qpaId;
    
    @Column(name = "QPR_QPR_ID")
    private Long qprId;
    
    @Column(name = "ADJ_TYPE")
    private String adjType;
    
    @Column(name = "GRID_TYPE")
    private String gridType;
    
    @Column(name = "MANDATORY_IND")
    private String mandatoryInd;
    
    @Column(name = "ADJ_VALUE" ,precision= 5)
    private BigDecimal adjValue;
    
    @Column(name = "ADJ_PERCENT" ,precision= 5)
    private BigDecimal adjPercent;

	public Long getQpaId() {
		return qpaId;
	}

	public void setQpaId(Long qpaId) {
		this.qpaId = qpaId;
	}

	public Long getQprId() {
		return qprId;
	}

	public void setQprId(Long qprId) {
		this.qprId = qprId;
	}

	public String getAdjType() {
		return adjType;
	}

	public void setAdjType(String adjType) {
		this.adjType = adjType;
	}

	public String getGridType() {
		return gridType;
	}

	public void setGridType(String gridType) {
		this.gridType = gridType;
	}

	public String getMandatoryInd() {
		return mandatoryInd;
	}

	public void setMandatoryInd(String mandatoryInd) {
		this.mandatoryInd = mandatoryInd;
	}

	public BigDecimal getAdjValue() {
		return adjValue;
	}

	public void setAdjValue(BigDecimal adjValue) {
		this.adjValue = adjValue;
	}

	public BigDecimal getAdjPercent() {
		return adjPercent;
	}

	public void setAdjPercent(BigDecimal adjPercent) {
		this.adjPercent = adjPercent;
	}
    
    
    
}