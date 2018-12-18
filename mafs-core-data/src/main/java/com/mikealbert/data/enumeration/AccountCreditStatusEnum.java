package com.mikealbert.data.enumeration;

/**
*/
public enum AccountCreditStatusEnum {	
	
	APPROVED("A", "Approved"),
	UNAPPROVED("U", "Unapproved"),
	REVIEW_REQUIRED("R", "Review Required");
	
	private String code;
	private String description;
	
	private AccountCreditStatusEnum(String code, String description){
		this.setCode(code);
		this.setDescription(description);
	}
	
	public static AccountCreditStatusEnum getByStatus(String code) {
	    for(AccountCreditStatusEnum e : values()) {
	        if(e.getCode().equals(code)) return e;
	    }
	    return null;
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
