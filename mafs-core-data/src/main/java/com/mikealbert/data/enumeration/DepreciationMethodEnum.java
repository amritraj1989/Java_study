package com.mikealbert.data.enumeration;

import java.util.ArrayList;
import java.util.List;

/**
*/
public enum DepreciationMethodEnum {	
	
	FULL("FULL_DEPRECIATION", "Full Depreciation"),
	MATCH("MATCH", "Match"),
	CAPITAL_COST("PCT_OF_CAPITAL_COST", "% of Capital Cost"),
	FIXED("FIXED_AMMOUNT", "Fixed Amount");
	
	private String code;
	private String description;
	
	private DepreciationMethodEnum(String code, String description){
		this.setCode(code);
		this.setDescription(description);
	}
	
	public static DepreciationMethodEnum getByCode(String code) {
	    for(DepreciationMethodEnum e : values()) {
	        if(e.getCode().equals(code)) return e;
	    }
	    return null;
	 }
	
	public static List<DepreciationMethodEnum> getAll() {
		List<DepreciationMethodEnum> depreciationMethodList = new ArrayList<DepreciationMethodEnum>();
	    for(DepreciationMethodEnum e : values()) {
	    	depreciationMethodList.add(e);
	    }
	    return depreciationMethodList;
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
