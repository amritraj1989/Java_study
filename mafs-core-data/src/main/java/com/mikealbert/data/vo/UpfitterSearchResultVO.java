package com.mikealbert.data.vo;

public class UpfitterSearchResultVO {

	private Long payeeCorporateId;
	private String payeeAccountType;
	private String payeeAccountCode;
	private String payeeAccountName;
	private String payeeAccountStatus;
	private String payeeAddress1;
	private String payeeAddress2;
	private String payeeAddress3;
	private String payeeCity;
	private String payeeState;
	private String payeeZip;
	private String payeePhoneNumber;
	
	public Long getPayeeCorporateId() {
		return payeeCorporateId;
	}

	
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) this.payeeCorporateId.longValue();
        hash += (this.payeeAccountType != null ? this.payeeAccountType.hashCode() : 0);
        hash += (this.payeeAccountCode != null ? this.payeeAccountCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the CorporateId, Type, and Code fields are not set
        if (!(object instanceof UpfitterSearchResultVO)) {
            return false;
        }
        
        UpfitterSearchResultVO other = (UpfitterSearchResultVO) object;
        if (this.getPayeeCorporateId() != other.getPayeeCorporateId()) {
            return false;
        }
        if ((this.getPayeeAccountType() == null && other.getPayeeAccountType() != null) || (this.getPayeeAccountType() != null && !this.getPayeeAccountType().equals(other.getPayeeAccountType()))) {
            return false;
        }
        if ((this.getPayeeAccountCode() == null && other.getPayeeAccountCode() != null) || (this.getPayeeAccountCode() != null && !this.getPayeeAccountCode().equals(other.getPayeeAccountCode()))) {
            return false;
        }
        return true;
    }
    
	public void setPayeeCorporateId(Long payeeCorporateId) {
		this.payeeCorporateId = payeeCorporateId;
	}

	public String getPayeeAccountType() {
		return payeeAccountType;
	}

	public void setPayeeAccountType(String payeeAccountType) {
		this.payeeAccountType = payeeAccountType;
	}

	public String getPayeeAccountCode() {
		return payeeAccountCode;
	}

	public void setPayeeAccountCode(String payeeAccountCode) {
		this.payeeAccountCode = payeeAccountCode;
	}

	public String getPayeeAccountName() {
		return payeeAccountName;
	}

	public void setPayeeAccountName(String payeeAccountName) {
		this.payeeAccountName = payeeAccountName;
	}

	public String getPayeeAccountStatus() {
		return payeeAccountStatus;
	}


	public void setPayeeAccountStatus(String payeeAccountStatus) {
		this.payeeAccountStatus = payeeAccountStatus;
	}


	public String getPayeeAddress1() {
		return payeeAddress1;
	}

	public void setPayeeAddress1(String payeeAddress1) {
		this.payeeAddress1 = payeeAddress1;
	}

	public String getPayeeAddress2() {
		return payeeAddress2;
	}

	public void setPayeeAddress2(String payeeAddress2) {
		this.payeeAddress2 = payeeAddress2;
	}

	public String getPayeeAddress3() {
		return payeeAddress3;
	}

	public void setPayeeAddress3(String payeeAddress3) {
		this.payeeAddress3 = payeeAddress3;
	}

	public String getPayeeCity() {
		return payeeCity;
	}

	public void setPayeeCity(String payeeCity) {
		this.payeeCity = payeeCity;
	}

	public String getPayeeState() {
		return payeeState;
	}

	public void setPayeeState(String payeeState) {
		this.payeeState = payeeState;
	}

	public String getPayeeZip() {
		return payeeZip;
	}

	public void setPayeeZip(String payeeZip) {
		this.payeeZip = payeeZip;
	}

	public String getPayeePhoneNumber() {
		return payeePhoneNumber;
	}

	public void setPayeePhoneNumber(String payeePhoneNumber) {
		this.payeePhoneNumber = payeePhoneNumber;
	}
	
}
