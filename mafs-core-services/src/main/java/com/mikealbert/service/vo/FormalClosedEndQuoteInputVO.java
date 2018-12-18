package com.mikealbert.service.vo;

import java.math.BigDecimal;
import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.enumeration.VehicleOrderType;
import com.mikealbert.service.bean.validation.MAFormalQuote;

public class FormalClosedEndQuoteInputVO extends FormalQuoteInput {	
	private static final long serialVersionUID = 1L;
	
	private BigDecimal adminFee;
	private BigDecimal residual;
	private BigDecimal disposalFee;
	private BigDecimal monthlyPayment;
	
	public FormalClosedEndQuoteInputVO() {}
	
	public FormalClosedEndQuoteInputVO(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, Long odoReading, 
			String employeeNo) {
		super(client, profile, gradeGroupCode, orderType, term, distance, cfgId, odoReading, employeeNo);
	}
	
	public FormalClosedEndQuoteInputVO(ExternalAccount client, QuotationProfile profile, DriverGradeGroupCode gradeGroupCode, 
			VehicleOrderType orderType, Long term, Long distance, Long cfgId, String unitNo, Long odoReading,
			String employeeNo, Long desiredQuoId) {
		super(client, profile, gradeGroupCode, orderType, term, distance, cfgId, unitNo, odoReading, employeeNo, desiredQuoId);
	}
	
	public BigDecimal getAdminFee() {
		return adminFee;
	}

	public void setAdminFee(BigDecimal adminFee) {
		this.adminFee = adminFee;
	}

	public BigDecimal getResidual() {
		return residual;
	}

	public void setResidual(BigDecimal residual) {
		this.residual = residual;
	}

	public BigDecimal getDisposalFee() {
		return disposalFee;
	}

	public void setDisposalFee(BigDecimal disposalFee) {
		this.disposalFee = disposalFee;
	}

	public BigDecimal getMonthlyPayment() {
		return monthlyPayment;
	}

	public void setMonthlyPayment(BigDecimal monthlyPayment) {
		this.monthlyPayment = monthlyPayment;
	}


}
