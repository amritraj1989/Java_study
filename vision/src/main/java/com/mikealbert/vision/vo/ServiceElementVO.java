package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.data.vo.FinanceParameterVO;

public  class ServiceElementVO implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String description;
		private Date effectiveBilling;
		private BigDecimal monthlyCost;
		private BigDecimal totalCost;
		private List<FinanceParameterVO> financeParameters;
		private Boolean editable;
		private List<FinanceParameterVO> currentFinanceParameters;
		private List<FinanceParameterVO> editableFinanceParameters = new ArrayList<FinanceParameterVO>();
		private boolean inRateTreatment;
		
		
		public ServiceElementVO(){}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Date getEffectiveBilling() {
			return effectiveBilling;
		}

		public void setEffectiveBilling(Date effectiveBilling) {
			this.effectiveBilling = effectiveBilling;
		}

		public Boolean getEditable() {
			return editable &&  editableFinanceParameters.size() > 0;
		}

		public void setEditable(Boolean editable) {
			this.editable = editable;
		}

		public BigDecimal getMonthlyCost() {
			return monthlyCost;
		}

		public void setMonthlyCost(BigDecimal monthlyCost) {
			this.monthlyCost = monthlyCost;
		}

		public BigDecimal getTotalCost() {
			return totalCost;
		}

		public void setTotalCost(BigDecimal totalCost) {
			this.totalCost = totalCost;
		}

		public List<FinanceParameterVO> getFinanceParameters() {
			return financeParameters;
		}

		public void setFinanceParameters(List<FinanceParameterVO> financeParameters) {
			this.financeParameters = financeParameters;
		}

		public List<FinanceParameterVO> getCurrentFinanceParameters() {
			return currentFinanceParameters;
		}

		public void setCurrentFinanceParameters(
				List<FinanceParameterVO> currentFinanceParameters) {
			this.currentFinanceParameters = currentFinanceParameters;
		}

		public List<FinanceParameterVO> getEditableFinanceParameters() {
			return editableFinanceParameters;
		}

		public void setEditableFinanceParameters(
				List<FinanceParameterVO> editableFinanceParameters) {
			this.editableFinanceParameters = editableFinanceParameters;
		}

		public boolean isInRateTreatment() {
			return inRateTreatment;
		}

		public void setInRateTreatment(boolean inRateTreatment) {
			this.inRateTreatment = inRateTreatment;
		}


}