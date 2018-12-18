package com.mikealbert.data.vo;

import java.io.Serializable;

import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
public class VendorMaintCodeVO implements Serializable {

	private static final long serialVersionUID = -8715923994927048069L;
	
	public VendorMaintCodeVO(ServiceProviderMaintenanceCode providerMaintCode){
		this.parentProvider = providerMaintCode.getServiceProvider().getServiceProviderName()  + " No: " + providerMaintCode.getServiceProvider().getServiceProviderName();
		this.partServiceCode = providerMaintCode.getCode();
		this.partServiceDesc = providerMaintCode.getDescription();
		//TODO: do we need to set this?
		this.operationCode = "";
	}
	
	public VendorMaintCodeVO(){
		
	}
	
	private String messageId;
	private String parentProvider;
	
	private String partServiceCode;  
	private String partServiceDesc; 
	private String operationCode;
	
	public String getPartServiceCode() {
		return partServiceCode;
	}
	public void setPartServiceCode(String partServiceCode) {
		this.partServiceCode = partServiceCode;
	}
	public String getPartServiceDesc() {
		return partServiceDesc;
	}
	public void setPartServiceDesc(String partServiceDesc) {
		this.partServiceDesc = partServiceDesc;
	}
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	public String getParentProvider() {
		return parentProvider;
	}
	public void setParentProvider(String parentProvider) {
		this.parentProvider = parentProvider;
	}
	
//	@Override
//	public String toString() {
//		StringBuffer str = new StringBuffer();
//		str.append("partServiceCode : ");
//		str.append(partServiceCode);
//		str.append(", ");
//		str.append("partServiceDesc : ");
//		str.append(partServiceDesc);
//		str.append(", ");
//		str.append("operationCode : ");
//		str.append(operationCode);
//		return str.toString();
//	}
	
}
