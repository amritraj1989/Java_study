package com.mikealbert.data.vo;

import java.io.Serializable;


public class MaintenanceProgramVO implements Serializable{
	private static final long serialVersionUID = 1L;
	private String elementType;
	private String elementName;
	private String elementDescription;
	private String specialInstructions;
	
	public String getElementType() {
		return elementType;
	}
	
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public String getElementName() {
		return elementName;
	}
	
	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	public String getElementDescription() {
		return elementDescription;
	}
	
	public void setElementDescription(String elementDescription) {
		this.elementDescription = elementDescription;
	}
	
	public String getSpecialInstructions() {
		return specialInstructions;
	}
	
	public void setSpecialInstructions(String specialInstructions) {
		this.specialInstructions = specialInstructions;
	}
}
