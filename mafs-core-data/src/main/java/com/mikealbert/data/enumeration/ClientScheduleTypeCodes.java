package com.mikealbert.data.enumeration;

public enum ClientScheduleTypeCodes {
	MAFS(1L, 1001L, "MAFS"),
	MFG(2L, 1002L, "MFG"),
	CLIENT(3L, 1003L, "CLIENT");
	
	private Long id;
	private Long code;
	private String description;
	
	private ClientScheduleTypeCodes(Long id, Long code, String description){
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

	public Long getCode() {
		return code;
	}


	public void setCode(Long code) {
		this.code = code;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
