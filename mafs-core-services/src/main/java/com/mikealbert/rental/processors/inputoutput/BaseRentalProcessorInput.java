package com.mikealbert.rental.processors.inputoutput;

import com.mikealbert.data.entity.QuotationElement;

public abstract class BaseRentalProcessorInput implements ProcessorInputType{
	
	private Long qelId;
	private Long qmaId;
	private Long qdaId;
	private Long qmdId;
	
	private Long period;
	private Long distance;
	
	public Long getDistance() {
		return distance;
	}	
	public void setDistance(Long distance) {
		this.distance = distance;
	}
	private QuotationElement quotationElement;

	public Long getQelId() {
		return qelId;
	}
	public void setQelId(Long qelId) {
		this.qelId = qelId;
	}
	public Long getQmaId() {
		return qmaId;
	}
	public void setQmaId(Long qmaId) {
		this.qmaId = qmaId;
	}
	public Long getQdaId() {
		return qdaId;
	}
	public void setQdaId(Long qdaId) {
		this.qdaId = qdaId;
	}
	
	public Long getPeriod() {
		return period;
	}
	public void setPeriod(Long period) {
		this.period = period;
	}
	
	@Override
	public QuotationElement getQuotationElement() {
		return quotationElement;
	}
	@Override
	public void setQuotationElement(QuotationElement quotationElement) {
		this.quotationElement = quotationElement;
	}
	public Long getQmdId() {
	    return qmdId;
	}
	public void setQmdId(Long qmdId) {
	    this.qmdId = qmdId;
	}
	
	@Override
	public void loadQuoteData(){
	    
	}
	
}
