package com.mikealbert.data.vo;

import java.util.List;

public class VendorInfoVO {
	
	private Long tpDocId;
	private String name;
	private String accountCode;
	private String address;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String zip;
	private String  cityStateZip;
	private String contactName;
	private String phoneNo;
	private String email;
	private String phoneAndEmail;
	private String leadTime;
	private String ETA;	
	private String vendorQuoteNo;
	private String quoteDate;
	
	private long vendorContext;
	private Long mainPoDocId;

	private Boolean vendorTaskCompleted;
	
	private Boolean linked;
    private Long sequenceNo;
	
	private List<EquipmentVO> accessoriesList;
	private List<EquipmentVO> accessoriesList1;//view Only
	private List<EquipmentVO> accessoriesList2;//view Only
	
	public VendorInfoVO(){
		
	}
	
	public VendorInfoVO(String vendorName ,String vendorCode, long vendorContext, Long mainPoDocId, Long thpyDocId , String leadTime){
		this.setName(vendorName);
		this.setAccountCode(vendorCode);
		this.setVendorContext(vendorContext);
		this.setMainPoDocId(mainPoDocId);
		this.setTpDocId(thpyDocId);
		this.setLeadTime(leadTime);
	}
	
	public VendorInfoVO(String vendorName ,String vendorCode, long vendorContext, Long mainPoDocId, Long thpyDocId , Long leadTime){
		this.setName(vendorName);
		this.setAccountCode(vendorCode);
		this.setVendorContext(vendorContext);
		this.setMainPoDocId(mainPoDocId);
		this.setTpDocId(thpyDocId);
		this.setLeadTime(leadTime.toString());
	}	
	
	public Long getTpDocId() {
		return tpDocId;
	}
	public void setTpDocId(Long tpDocId) {
		this.tpDocId = tpDocId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLeadTime() {
		return leadTime;
	}
	public void setLeadTime(String leadTime) {
		this.leadTime = leadTime;
	}
	public String getETA() {
		return ETA;
	}
	public void setETA(String eTA) {
		ETA = eTA;
	}
	public String getQuoteDate() {
		return quoteDate;
	}
	public void setQuoteDate(String quoteDate) {
		this.quoteDate = quoteDate;
	}

	public List<EquipmentVO> getAccessoriesList() {
		return accessoriesList;
	}

	public void setAccessoriesList(List<EquipmentVO> accessoriesList) {
		this.accessoriesList = accessoriesList;
	}

	public List<EquipmentVO> getAccessoriesList1() {
		if (accessoriesList != null) {
			int mod = accessoriesList.size() % 2;
			int half = accessoriesList.size() / 2;
			accessoriesList1 = accessoriesList.subList(0, half + mod);
		}
		return accessoriesList1;
	}

	public void setAccessoriesList1(List<EquipmentVO> accessoriesList) {
		this.accessoriesList = accessoriesList;
	}

	public List<EquipmentVO> getAccessoriesList2() {
		if (accessoriesList != null) {
			int mod = accessoriesList.size() % 2;
			int half = accessoriesList.size() / 2;
			accessoriesList2 = accessoriesList.subList(half + mod,accessoriesList.size());
		}
		return accessoriesList2;
	}

	public void setAccessoriesList2(List<EquipmentVO> accessoriesList) {
		this.accessoriesList = accessoriesList;
	}

	public String getVendorQuoteNo() {
		return vendorQuoteNo;
	}

	public void setVendorQuoteNo(String vendorQuoteNo) {
		this.vendorQuoteNo = vendorQuoteNo;
	}

	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getAddressLine1() {
		return addressLine1;
	}
	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}
	public String getAddressLine2() {
		return addressLine2;
	}
	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}
	public String getCityStateZip() {
		return cityStateZip;
	}
	public void setCityStateZip(String cityStateZip) {
		this.cityStateZip = cityStateZip;
	}
	public String getPhoneAndEmail() {
		return phoneAndEmail;
	}
	public void setPhoneAndEmail(String phoneAndEmail) {
		this.phoneAndEmail = phoneAndEmail;
	}
	public long getVendorContext() {
		return vendorContext;
	}
	public void setVendorContext(long vendorContext) {
		this.vendorContext = vendorContext;
	}
	public Long getMainPoDocId() {
		return mainPoDocId;
	}
	public void setMainPoDocId(Long mainPoDocId) {
		this.mainPoDocId = mainPoDocId;
	}

	public Boolean getVendorTaskCompleted() {
		return vendorTaskCompleted;
	}

	public void setVendorTaskCompleted(Boolean vendorTaskCompleted) {
		this.vendorTaskCompleted = vendorTaskCompleted;
	}

	public Long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Boolean getLinked() {
		return linked;
	}

	public void setLinked(Boolean linked) {
		this.linked = linked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountCode == null) ? 0 : accountCode.hashCode());
		result = prime * result
				+ (int) (vendorContext ^ (vendorContext >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VendorInfoVO))
			return false;
		VendorInfoVO other = (VendorInfoVO) obj;
		if (accountCode == null) {
			if (other.accountCode != null)
				return false;
		} else if (!accountCode.equals(other.accountCode))
			return false;
		if (vendorContext != other.vendorContext)
			return false;
		return true;
	}

	
}