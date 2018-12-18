package com.mikealbert.data.vo;

import java.io.Serializable;

public class ProgressChasingQueueVO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String maintRequestStatus;
	private String maintRequestStatusDescription;
	private int poStatusCount;
	
	public String getMaintRequestStatus() {
		return maintRequestStatus;
	}
	
	public void setMaintRequestStatus(String maintRequestStatus) {
		this.maintRequestStatus = maintRequestStatus;
	}
	
	public String getMaintRequestStatusDescription() {
		return maintRequestStatusDescription;
	}

	public void setMaintRequestStatusDescription(
			String maintRequestStatusDescription) {
		this.maintRequestStatusDescription = maintRequestStatusDescription;
	}

	public int getPoStatusCount() {
		return poStatusCount;
	}
	
	public void setPoStatusCount(int poStatusCount) {
		this.poStatusCount = poStatusCount;
	}

}


