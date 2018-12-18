package com.mikealbert.service.task;

public class TaskResponse {

	private Long requestId;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "GenericTaskResponse [requestId=" + requestId + "]";
	}
	

}
