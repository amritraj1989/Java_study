package com.mikealbert.data.enumeration;

/**
* Client Service Element Types.
* 
* <P>Enum of Client Service Element Types</p> 
*  
*/
public enum ClientServiceElementTypeCodes {
	
	ACCOUNT(1L, "AC", "Account"),
	EXTERNAL_ACCOUNT_GRADE_GROUP(2L, "GG", "Grade Group"),
	DRIVER_COST_CENTRE(3L, "CC",	"Cost Centre");
	
	private Long id;
	private String code;
	private String description;
	
	private ClientServiceElementTypeCodes(Long id, String code, String description){
		this.setId(id);
		this.setCode(code);
		this.setDescription(description);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
