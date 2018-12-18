package com.mikealbert.service.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.VehicleOrderType;

public class FormalOpenEndQuoteInputVO extends FormalQuoteInput {	
	private static final long serialVersionUID = 1L;
	
	private BigDecimal interestIndex;
	private BigDecimal interestAdjustment;
	private BigDecimal adminFactor;
	private BigDecimal depreciationFactor;
	private BigDecimal finalNetBookValue;
	private List<FormalQuoteStepInputVO> steps;	
	
	public FormalOpenEndQuoteInputVO() {
		this.steps = new ArrayList<>();		
	}
	
	public FormalOpenEndQuoteInputVO(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, Long odoReading, 
			String employeeNo) {
		super(client, profile, gradeGroupCode, orderType, term, distance, cfgId, odoReading, employeeNo);
		this.steps = new ArrayList<>();		
	}
	
	public FormalOpenEndQuoteInputVO(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, String unitNo, Long odoReading,
			String employeeNo, Long desiredQuoId) {	
		super(client, profile, gradeGroupCode, orderType, term, distance, cfgId, unitNo, odoReading, employeeNo, desiredQuoId);
		this.steps = new ArrayList<>();			
	}

	public BigDecimal calucateInterestRate() {
		return getInterestIndex().add(getInterestAdjustment());
	}
	
	public BigDecimal getInterestIndex() {
		return interestIndex;
	}

	public void setInterestIndex(BigDecimal interestIndex) {
		this.interestIndex = interestIndex;
	}

	public BigDecimal getInterestAdjustment() {
		return interestAdjustment;
	}

	public void setInterestAdjustment(BigDecimal interestAdjustment) {
		this.interestAdjustment = interestAdjustment;
	}

	public BigDecimal getAdminFactor() {
		return adminFactor;
	}

	public void setAdminFactor(BigDecimal adminFactor) {
		this.adminFactor = adminFactor;
	}

	public BigDecimal getDepreciationFactor() {
		return depreciationFactor;
	}

	public void setDepreciationFactor(BigDecimal depreciationFactor) {
		this.depreciationFactor = depreciationFactor;
	}

	public BigDecimal getFinalNetBookValue() {
		return finalNetBookValue;
	}

	public void setFinalNetBookValue(BigDecimal finalNetBookValue) {
		this.finalNetBookValue = finalNetBookValue;
	}

	public List<FormalQuoteStepInputVO> getSteps() {
		return steps;
	}

	public void setSteps(List<FormalQuoteStepInputVO> steps) {
		this.steps = steps;
	}

}
