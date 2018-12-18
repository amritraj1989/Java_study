package com.mikealbert.data.vo;

public class ClientQuoteRequestServiceElementVO {

	Long elementId;
	String elementName;
	String elementStatus;
	String productCode;
	Long clientGradeGroupId;
	String gradeGroupCode;
	String gradeGroupDesc;
	String productExclusions;
	String gradeGroupExclusions;
	
	public Long getElementId() {
		return elementId;
	}
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	public String getElementName() {
		return elementName;
	}
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	public String getElementStatus() {
		return elementStatus;
	}
	public void setElementStatus(String elementStatus) {
		this.elementStatus = elementStatus;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public Long getClientGradeGroupId() {
		return clientGradeGroupId;
	}
	public void setClientGradeGroupId(Long clientGradeGroupId) {
		this.clientGradeGroupId = clientGradeGroupId;
	}
	public String getGradeGroupCode() {
		return gradeGroupCode;
	}
	public void setGradeGroupCode(String gradeGroupCode) {
		this.gradeGroupCode = gradeGroupCode;
	}
	public String getGradeGroupDesc() {
		return gradeGroupDesc;
	}
	public void setGradeGroupDesc(String gradeGroupDesc) {
		this.gradeGroupDesc = gradeGroupDesc;
	}
	public String getProductExclusions() {
		return productExclusions;
	}
	public void setProductExclusions(String productExclusions) {
		this.productExclusions = productExclusions;
	}
	public String getGradeGroupExclusions() {
		return gradeGroupExclusions;
	}
	public void setGradeGroupExclusions(String gradeGroupExclusions) {
		this.gradeGroupExclusions = gradeGroupExclusions;
	}

}
