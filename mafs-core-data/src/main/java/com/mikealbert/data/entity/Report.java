package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the REPORTS database table.
 * @author Amritraj Singh
 */
@Entity
@Table(name="REPORTS")
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "REPORT_NAME")
    private String reportName;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "REPORT_EXEC")
    private String reportExec;
    
    @Column(name = "REPORT_TYPE")
    private String reportType;

    public Report() {
    }

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReportExec() {
		return reportExec;
	}

	public void setReportExec(String reportExec) {
		this.reportExec = reportExec;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	
}