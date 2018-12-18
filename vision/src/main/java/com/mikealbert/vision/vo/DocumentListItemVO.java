package com.mikealbert.vision.vo;

import com.mikealbert.data.enumeration.ReportNameEnum;

public class DocumentListItemVO {
	private Long id;
	private String label;
	private boolean emailable;
	private boolean emailed;
	private ReportNameEnum reportName;
	private boolean viewed;
	private boolean render;
	private boolean required;

	public DocumentListItemVO(){}
	
	public DocumentListItemVO(Long id, ReportNameEnum reportName){
		setId(id);
		setLabel(reportName.getDescription());
		setReportName(reportName);
		setEmailable(false);
		setEmailed(false);
		setRender(true);
		setRequired(true);
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean isEmailed() {
		return emailed;
	}
	
	public void setEmailed(boolean emailed) {
		this.emailed = emailed;
	}

	public boolean isEmailable() {
		return emailable;
	}

	public void setEmailable(boolean emailable) {
		this.emailable = emailable;
	}

	public ReportNameEnum getReportName() {
		return reportName;
	}

	public void setReportName(ReportNameEnum reportName) {
		this.reportName = reportName;
	}

	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
}
