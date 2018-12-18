package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CapitalCostVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long standardQuoteModelId; 
	private Long acceptedQuoteModelId;
	private Long finalizedQuoteId;
	private String unitNo;
	private String accountCode;
	private String accountName;
	
	
	public Long getStandardQuoteModelId() {
		return standardQuoteModelId;
	}
	public void setStandardQuoteModelId(Long standardQuoteModelId) {
		this.standardQuoteModelId = standardQuoteModelId;
	}
	public Long getAcceptedQuoteModelId() {
		return acceptedQuoteModelId;
	}
	public void setAcceptedQuoteModelId(Long acceptedQuoteModelId) {
		this.acceptedQuoteModelId = acceptedQuoteModelId;
	}
	public Long getFinalizedQuoteId() {
		return finalizedQuoteId;
	}
	public void setFinalizedQuoteId(Long finalizedQuoteId) {
		this.finalizedQuoteId = finalizedQuoteId;
	}
	
	private List<CapitalCostGroup> allGroup = new ArrayList<CapitalCostGroup>();;
	
	public void addGroup(CapitalCostGroup capitalCostGroup) {
		allGroup.add(capitalCostGroup);
	}
	public List<CapitalCostGroup> getAllGroup() {
		return allGroup;
	}
	public void setAllGroup(List<CapitalCostGroup> allGroup) {
		this.allGroup = allGroup;
	}
	public void clearData() {
		this.allGroup  = new ArrayList<CapitalCostGroup>();	
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public static class CapitalCostGroup implements Serializable {
		private static final long serialVersionUID = 1L;

		private String groupName;		
		private GroupCostElement groupCostElement ;
		private List<CostElementVO> costElementList ;

		
		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		
		public List<CostElementVO> getCostElementList() {
			return costElementList;
		}

		public void setCostElementList(List<CostElementVO> costElementList) {
			this.costElementList = costElementList;
		}
		public GroupCostElement getGroupCostElement() {		
			if(groupCostElement == null){				
				 groupCostElement = new GroupCostElement();
			}
			return groupCostElement;
		}

		public void calculatedGroupCostElement() {
			
			 groupCostElement = new GroupCostElement();
			 groupCostElement.setGroupCostElementData(true);
			
			if(costElementList != null){
				for (CostElementVO costElement : costElementList) {
					
					groupCostElement.setOriginalOrderDeal(groupCostElement.getOriginalOrderDeal()  + costElement.getOriginalOrderDeal());
					groupCostElement.setOriginalOrderCustomer(groupCostElement.getOriginalOrderCustomer() + costElement.getOriginalOrderCustomer());
					
					groupCostElement.setAcceptedQuoteDeal(groupCostElement.getAcceptedQuoteDeal() + costElement.getAcceptedQuoteDeal());
					groupCostElement.setAcceptedQuoteCustomer(groupCostElement.getAcceptedQuoteCustomer() + costElement.getAcceptedQuoteCustomer());
					
					groupCostElement.setInvoiceAmount(groupCostElement.getInvoiceAmount() + costElement.getInvoiceAmount());
					
					groupCostElement.setFinalizedQuoteDeal(groupCostElement.getFinalizedQuoteDeal() + costElement.getFinalizedQuoteDeal());
					groupCostElement.setFinalizedQuoteCustomer(groupCostElement.getFinalizedQuoteCustomer() + costElement.getFinalizedQuoteCustomer());
				}
				
			}
			
		}
		
	}

	
public static class GroupCostElement extends CostElementVO implements Serializable {	
	private static final long serialVersionUID = 1L;
	}
	
}
