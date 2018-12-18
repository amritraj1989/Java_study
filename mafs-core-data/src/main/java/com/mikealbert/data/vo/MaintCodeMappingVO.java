package com.mikealbert.data.vo;

import java.io.Serializable;

public class MaintCodeMappingVO implements Serializable {
	private static final long serialVersionUID = 3110107987200544867L;
	
	private String mafsMaintCode;
	private String vendorMaintCode;  
	private String vendorDescription;
	
	public String getMafsMaintCode() {
		return mafsMaintCode;
	}
	public void setMafsMaintCode(String mafsMaintCode) {
		this.mafsMaintCode = mafsMaintCode;
	}
	public String getVendorDescription() {
		return vendorDescription;
	}
	public void setVendorDescription(String vendorDescription) {
		this.vendorDescription = vendorDescription;
	}
	public String getVendorMaintCode() {
		return vendorMaintCode;
	}
	public void setVendorMaintCode(String vendorMaintCode) {
		this.vendorMaintCode = vendorMaintCode;
	} 


	
}
