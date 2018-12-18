package com.mikealbert.data.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
*/
public enum PaymentTermTypeEnum {	
	
	LEVEL("L", "Level Pay"),
	STEP("S", "Step Pay");
	
	private String code;
	private String description;
	
	private PaymentTermTypeEnum(String code, String description){
		this.setCode(code);
		this.setDescription(description);
	}
	
	public static PaymentTermTypeEnum getByCode(String code) {
	    for(PaymentTermTypeEnum e : values()) {
	        if(e.getCode().equals(code)) return e;
	    }
	    return null;
	 }
	
	public static List<PaymentTermTypeEnum> getAll() {
		List<PaymentTermTypeEnum> paymentTermList = new ArrayList<PaymentTermTypeEnum>();
	    for(PaymentTermTypeEnum e : values()) {
	        paymentTermList.add(e);
	    }
	    return paymentTermList;
	 }

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
