package com.mikealbert.service.task;



public abstract class AbstractTask {

	private Long taskId;
	private boolean submittedReqest;

	public AbstractTask() {	
	}

	public Long getTaskId() {		
		return taskId;
	}
	
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public boolean isSubmittedReqest() {
		return submittedReqest;
	}

	public void setSubmittedReqest(boolean submittedReqest) {
		this.submittedReqest = submittedReqest;
	}

}
