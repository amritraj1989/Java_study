package com.mikealbert.service.task;

import java.io.Serializable;

import com.mikealbert.data.enumeration.DocumentNameEnum;

public class POArchieveTask extends AbstractTask implements Serializable{

	private static final long serialVersionUID = 7486264177431645396L;

	private Long docId;
	private String stockYn;
	private DocumentNameEnum docNameEnumType;	

	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	
	public DocumentNameEnum getDocNameEnumType() {
		return docNameEnumType;
	}

	public void setDocNameEnumType(DocumentNameEnum docNameEnumType) {
		this.docNameEnumType = docNameEnumType;
	}
	
	public String getStockYn() {
		return stockYn;
	}

	public void setStockYn(String stockYn) {
		this.stockYn = stockYn;
	}
	
	@Override
	public String toString() {
		return "POArchieveTaskRequest [taskId=" + getTaskId() + ", docId=" + docId  + ", stockYn=" + stockYn
				+ ", docNameEnumType=" + docNameEnumType.getName() + "]";
	}
	
	
}
