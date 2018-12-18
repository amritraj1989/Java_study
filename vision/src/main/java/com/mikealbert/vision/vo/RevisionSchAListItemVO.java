package com.mikealbert.vision.vo;

import com.mikealbert.data.enumeration.ReportNameEnum;

public class RevisionSchAListItemVO extends DocumentListItemVO{
	private Long currentQmdId;
	private Long revisionQmdId;

	public RevisionSchAListItemVO(){}
	
	public RevisionSchAListItemVO(Long id, ReportNameEnum reportName, Long currentQmdId, Long revisionQmdId){
		super(id, reportName);
		setCurrentQmdId(currentQmdId);
		setRevisionQmdId(revisionQmdId);
	}

	public Long getCurrentQmdId() {
		return currentQmdId;
	}

	public void setCurrentQmdId(Long currentQmdId) {
		this.currentQmdId = currentQmdId;
	}

	public Long getRevisionQmdId() {
		return revisionQmdId;
	}

	public void setRevisionQmdId(Long revisionQmdId) {
		this.revisionQmdId = revisionQmdId;
	}

}
