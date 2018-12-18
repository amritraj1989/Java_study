package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import com.mikealbert.data.beanvalidation.MANotNull;

/**
 * Composite logical Primary Key for VEHICLE_REPLACEMENT_V view
 * @author sibley
 */
@Embeddable
public class VehicleReplacementVPK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @MANotNull(label = "Current Unit")
    @Column(name = "CURRENT_UNIT_NO")
    private String currentUnitNo;
    
    @MANotNull(label = "CID")
    @Column(name = "C_ID")
    private long cId;
    
    @MANotNull(label = "Account Type")
    @Column(name = "ACCOUNT_TYPE")
    private String accountType;
    
    @MANotNull(label = "Account")
    @Column(name = "ACCOUNT_CODE")
    private String accountCode;   

    public VehicleReplacementVPK() {}
    
    public VehicleReplacementVPK(String currentUnitNo, Long cId, String accountType, String accountCode) {
    	setCurrentUnitNo(currentUnitNo);
    	setcId(cId);
    	setAccountType(accountType);
    	setAccountCode(accountCode);
    }    

    public String getCurrentUnitNo() {
		return currentUnitNo;
	}

	public void setCurrentUnitNo(String currentUnitNo) {
		this.currentUnitNo = currentUnitNo;
	}

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	@Override
    public String toString() {
		return "com.mikealbert.entity.VehicleReplacementVPK[ currentUnitNo=" + getCurrentUnitNo() + ", accountCode=" + accountCode + " ]";
    }
    
}
