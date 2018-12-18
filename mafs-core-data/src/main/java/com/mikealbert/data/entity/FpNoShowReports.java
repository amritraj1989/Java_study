package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the FP_NO_SHOW_REPORTS database table.
 * 
 */
@Entity
@Table(name="FP_NO_SHOW_REPORTS")
public class FpNoShowReports implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="FPNSR_ID")
	private long fpnsr_Id;

	@Column(name="PARAMETER_ID")
	private String parameterId;

	@Column(name="REPORT_NAME")
	private String reportName;


    public FpNoShowReports() {
    }


	public long getFpnsr_Id() {
		return fpnsr_Id;
	}


	public void setFpnsr_Id(long fpnsr_Id) {
		this.fpnsr_Id = fpnsr_Id;
	}


	public String getParameterId() {
		return parameterId;
	}


	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}


	public String getReportName() {
		return reportName;
	}


	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	
}