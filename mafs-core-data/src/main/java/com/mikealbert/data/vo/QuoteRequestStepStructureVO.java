package com.mikealbert.data.vo;

public class QuoteRequestStepStructureVO {

    private int stepCount;
    private Long fromPeriod;
    private Long toPeriod;
    private Long netPeriod;
    
	public int getStepCount() {
		return stepCount;
	}
	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}
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
	public Long getNetPeriod() {
		return netPeriod;
	}
	public void setNetPeriod(Long netPeriod) {
		this.netPeriod = netPeriod;
	}
    
       
}