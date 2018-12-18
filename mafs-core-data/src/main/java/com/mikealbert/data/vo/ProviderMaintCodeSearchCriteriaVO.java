package com.mikealbert.data.vo;

import java.util.Arrays;
import java.util.List;

public class ProviderMaintCodeSearchCriteriaVO {
	private String serviceProvider;
	private String maintenanceCategory;
	private String approvalStatus;
	//TODO: yet to be determined whether we need a Re-Approval status
	//public static final List<String> APPROVAL_STATUSES = Arrays.asList("Needs Approval","Needs Re-Approval","Approved","All");
	public static final List<String> APPROVAL_STATUSES = Arrays.asList("All","Approved","Needs Approval");
	public static final String NEEDS_APPROVE_STATUS = "Needs Approval";
	public static final String NEEDS_RE_APPROVE_STATUS = "Needs Re-Approval";
	public static final String APPROVED_STATUS = "Approved";
	public static final String ALL_STATUS = "All";
	
	public String getServiceProvider() {
		return serviceProvider;
	}
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	public String getMaintenanceCategory() {
		return maintenanceCategory;
	}
	public void setMaintenanceCategory(String maintenanceCategory) {
		this.maintenanceCategory = maintenanceCategory;
	}
	public String getApprovedStatus() {
		return approvalStatus;
	}
	public void setApprovedStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}
}
