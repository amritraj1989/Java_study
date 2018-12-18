package com.mikealbert.data.vo;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.QuotationProfile;

public class QuoteRequestQuoteVO {
	
	QuotationProfile quoteProfile;
	BigDecimal leaseTerm;
	BigDecimal leaseDistance;
	BigDecimal projectedReplacementMonths;
	String paymentTermType;
	List<QuoteRequestStepStructureVO> paymentTermSteps;
	String depreciatonMethod;
	BigDecimal depreciationValue;
	String driverGradeGroup;
	String driverGradeGroupDesc;
	
	public QuotationProfile getQuoteProfile() {
		return quoteProfile;
	}
	public void setQuoteProfile(QuotationProfile quoteProfile) {
		this.quoteProfile = quoteProfile;
	}
	public BigDecimal getLeaseTerm() {
		return leaseTerm;
	}
	public void setLeaseTerm(BigDecimal leaseTerm) {
		this.leaseTerm = leaseTerm;
	}
	public BigDecimal getLeaseDistance() {
		return leaseDistance;
	}
	public void setLeaseDistance(BigDecimal leaseDistance) {
		this.leaseDistance = leaseDistance;
	}
	public BigDecimal getProjectedReplacementMonths() {
		return projectedReplacementMonths;
	}
	public void setProjectedReplacementMonths(BigDecimal projectedReplacementMonths) {
		this.projectedReplacementMonths = projectedReplacementMonths;
	}
	public String getPaymentTermType() {
		return paymentTermType;
	}
	public void setPaymentTermType(String paymentTermType) {
		this.paymentTermType = paymentTermType;
	}
	public List<QuoteRequestStepStructureVO> getPaymentTermSteps() {
		return paymentTermSteps;
	}
	public void setPaymentTermSteps(List<QuoteRequestStepStructureVO> paymentTermSteps) {
		this.paymentTermSteps = paymentTermSteps;
	}
	public String getDepreciatonMethod() {
		return depreciatonMethod;
	}
	public void setDepreciatonMethod(String depreciatonMethod) {
		this.depreciatonMethod = depreciatonMethod;
	}
	public BigDecimal getDepreciationValue() {
		return depreciationValue;
	}
	public void setDepreciationValue(BigDecimal depreciationValue) {
		this.depreciationValue = depreciationValue;
	}
	public String getDriverGradeGroup() {
		return driverGradeGroup;
	}
	public void setDriverGradeGroup(String driverGradeGroup) {
		this.driverGradeGroup = driverGradeGroup;
	}
	public String getDriverGradeGroupDesc() {
		return driverGradeGroupDesc;
	}
	public void setDriverGradeGroupDesc(String driverGradeGroupDesc) {
		this.driverGradeGroupDesc = driverGradeGroupDesc;
	}
	
}
