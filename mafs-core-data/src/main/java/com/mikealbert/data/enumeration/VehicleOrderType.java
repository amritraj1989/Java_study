package com.mikealbert.data.enumeration;

public enum VehicleOrderType {

	FACTORY("F", "Factory"),
	LOCATE("L", "Locate");

	private String code;
	private String description;

	private VehicleOrderType(String code, String description) {
		this.setCode(code);
		this.setDescription(description);
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
	
	public static VehicleOrderType fromCode(String code) {
	    if (code != null) {
		      for (VehicleOrderType a : VehicleOrderType.values()) {
		        if (a.code.equals(code)) {
		          return a;
		        }
		      }
		    }
		    return null;
		}	

}
