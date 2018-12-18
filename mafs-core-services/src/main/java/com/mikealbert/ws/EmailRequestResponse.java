package com.mikealbert.ws;

public class EmailRequestResponse {
	private Long erlId;
	private boolean success;
	private String message;
	
	public EmailRequestResponse(){}

	public Long getErlId() {
		return erlId;
	}

	public void setErlId(Long erlId) {
		this.erlId = erlId;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	};

}
