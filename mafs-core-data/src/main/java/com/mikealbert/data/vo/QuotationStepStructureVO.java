package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.List;

public class QuotationStepStructureVO {

    private int stepCount;
    private Long fromPeriod;
    private Long toPeriod;
    private Long netPeriod;
    private Long origNetPeriod;
    private BigDecimal leaseRate; 
    private BigDecimal finalLeaseRate;

    private BigDecimal netBookValue;
    private BigDecimal leaseFactor;
    boolean editable = true;  
    private Long associatedQmdId = null; //we may step structure for  multiple qmd in  if revision present
	private List<QuoteElementStepVO> quoteElementStepVOs;   

	public Long getFromPeriod() {
	return fromPeriod;
    }

    public void setFromPeriod(Long fromPeriod) {
	this.fromPeriod = fromPeriod;
    }

    public Long getToPeriod() {
	return toPeriod;
    }

    public void setToPeriod(Long toPeriod) {
	this.toPeriod = toPeriod;
    }

    public BigDecimal getLeaseRate() {
	return leaseRate;
    }

    public void setLeaseRate(BigDecimal leaseRate) {
	this.leaseRate = leaseRate;
    }

    public BigDecimal getNetBookValue() {
	return netBookValue;
    }

    public void setNetBookValue(BigDecimal netBookValue) {
	this.netBookValue = netBookValue;
    }

    public BigDecimal getLeaseFactor() {
	return leaseFactor;
    }

    public void setLeaseFactor(BigDecimal leaseFactor) {
	this.leaseFactor = leaseFactor;
    }

    public Long getNetPeriod() {
	return netPeriod;
    }
    
   
    public void setNetPeriod(Long netPeriod) {
	this.netPeriod = netPeriod;
    } 
  
    public Long getOrigNetPeriod() {
	return toPeriod - fromPeriod + 1;
    }

    public void setOrigNetPeriod(Long origNetPeriod) {
	this.origNetPeriod = origNetPeriod;
    }   
    
    public int getStepCount() {
	return stepCount;
    }

    public void setStepCount(int stepCount) {
	this.stepCount = stepCount;
    }
   
   
	public List<QuoteElementStepVO> getQuoteElementStepVOs() {
		return quoteElementStepVOs;
	}

	public void setQuoteElementStepVOs(List<QuoteElementStepVO> quoteElementStepVOs) {
		this.quoteElementStepVOs = quoteElementStepVOs;
	}

    public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public Long getAssociatedQmdId() {
		return associatedQmdId;
	}

	public void setAssociatedQmdId(Long associatedQmdId) {
		this.associatedQmdId = associatedQmdId;
	}

	@Override
    public String toString() {
	return "QuotationStepStructureVO [QmdId=" + associatedQmdId + ", fromPeriod=" + fromPeriod + ", toPeriod=" + toPeriod + ", leaseRate=" + leaseRate
		+ ", netBookValue=" + netBookValue + "]";
    }

	public BigDecimal getFinalLeaseRate() {
		return finalLeaseRate;
	}

	public void setFinalLeaseRate(BigDecimal finalLeaseRate) {
		this.finalLeaseRate = finalLeaseRate;
	}


}