package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * The persistent class for the BUYER_LIMITS database table.
 * 
 */
@Embeddable
public class BuyerLimitPK implements Serializable {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 12)
	@Column(name="C_ID")
	private Long cId;
	
	@NotNull
	@Size(min = 1, max = 10)
	@Column(name="DOC_TYPE")
	private String docType;
	
	@NotNull
	@Size(min = 1, max = 25)
	@Column(name="EMPLOYEE_NO")
	private String employeeNo;
	
	@NotNull
	@Size(min = 1, max = 10)
	@Column(name="TRAN_TYPE")
	private String tranType;

    public BuyerLimitPK() {
    }
    
    public BuyerLimitPK(Long cId, String docType, String employeeNo, String tranType) {
        this.cId = cId;
        this.docType = docType;
        this.employeeNo = employeeNo;
        this.tranType = tranType;
    }
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.BuyerLimit[ cId=" + getcId() +" , docType "+ getDocType() + " , employeeNo "+ employeeNo + " , tranType "+ tranType + " ]";
    }

	public String getEmployeeNo() {
		return employeeNo;
	}

	public void setEmployeeNo(String employeeNo) {
		this.employeeNo = employeeNo;
	}

	public String getTranType() {
		return tranType;
	}

	public void setTranType(String tranType) {
		this.tranType = tranType;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

}