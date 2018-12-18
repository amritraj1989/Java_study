package com.mikealbert.data.vo;

import java.util.List;

//Courtesy Delivery Instruction Document VO 
public class CourtesyDeliveryInstructionVO {

	private String logo;
	private String documentTitle;
	private String documentContactUsText;
	private String unitNo;
	private VehicleInfoVO vehicleInfoVO;
	private String purchaserName;
	private String purchaserAddress;
	private String purchaserFedralId;
	private List<AccountTaxExemptVO> accTaxExemptVOList;
	private List<AccountTaxExemptVO> accTaxExemptVOList1;
	private List<AccountTaxExemptVO> accTaxExemptVOList2;
	private String orderingDealer;
	private String orderingDealerAddress;
	private String orderingDealerPhone;
	private String driverName;
	private String driverPhone;
	private String driverEmail;
	private String driverAddress;
	private String returningVehicle;
	private boolean returningVehicleIndicator;
	private String returningVehicleUnitNo;
	private String returningVehicleVIN;
	private String specialInstr;
	private List<String> cdResponsibilityList;
/*
 *
 *
 *
 *
 *public List<EquipmentVO> getAccessoriesList1() {
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
 */
	private String documentFooterText;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	public String getDocumentContactUsText() {
		return documentContactUsText;
	}

	public void setDocumentContactUsText(String documentContactUsText) {
		this.documentContactUsText = documentContactUsText;
	}

	public VehicleInfoVO getVehicleInfoVO() {
		return vehicleInfoVO;
	}

	public void setVehicleInfoVO(VehicleInfoVO vehicleInfoVO) {
		this.vehicleInfoVO = vehicleInfoVO;
	}

	public String getPurchaserName() {
		return purchaserName;
	}

	public void setPurchaserName(String purchaserName) {
		this.purchaserName = purchaserName;
	}

	public String getPurchaserAddress() {
		return purchaserAddress;
	}

	public void setPurchaserAddress(String purchaserAddress) {
		this.purchaserAddress = purchaserAddress;
	}

	public String getPurchaserFedralId() {
		return purchaserFedralId;
	}

	public void setPurchaserFedralId(String purchaserFedralId) {
		this.purchaserFedralId = purchaserFedralId;
	}

	public List<AccountTaxExemptVO> getAccTaxExemptVOList() {
		return accTaxExemptVOList;
	}

	public void setAccTaxExemptVOList(
			List<AccountTaxExemptVO> accTaxExemptVOList) {
		this.accTaxExemptVOList = accTaxExemptVOList;
	}

	public String getOrderingDealer() {
		return orderingDealer;
	}

	public void setOrderingDealer(String orderingDealer) {
		this.orderingDealer = orderingDealer;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
	}

	public String getDriverEmail() {
		return driverEmail;
	}

	public void setDriverEmail(String driverEmail) {
		this.driverEmail = driverEmail;
	}

	public String getDriverAddress() {
		return driverAddress;
	}

	public void setDriverAddress(String driverAddress) {
		this.driverAddress = driverAddress;
	}

	public String getReturningVehicle() {
		return returningVehicle;
	}

	public void setReturningVehicle(String returningVehicle) {
		this.returningVehicle = returningVehicle;
	}

	public boolean isReturningVehicleIndicator() {
		return returningVehicleIndicator;
	}

	public void setReturningVehicleIndicator(boolean returningVehicleIndicator) {
		this.returningVehicleIndicator = returningVehicleIndicator;
	}

	public String getReturningVehicleUnitNo() {
		return returningVehicleUnitNo;
	}

	public void setReturningVehicleUnitNo(String returningVehicleUnitNo) {
		this.returningVehicleUnitNo = returningVehicleUnitNo;
	}

	public String getReturningVehicleVIN() {
		return returningVehicleVIN;
	}

	public void setReturningVehicleVIN(String returningVehicleVIN) {
		this.returningVehicleVIN = returningVehicleVIN;
	}

	public String getSpecialInstr() {
		return specialInstr;
	}

	public void setSpecialInstr(String specialInstr) {
		this.specialInstr = specialInstr;
	}

	public List<String> getCdResponsibilityList() {
		System.out.println("get cdResponsibilityList--"+cdResponsibilityList);
		return cdResponsibilityList;
	}

	public void setCdResponsibilityList(List<String> cdResponsibilityList) {		
		this.cdResponsibilityList = cdResponsibilityList;
	}

	public String getDocumentFooterText() {
	
		return documentFooterText;
	}

	public void setDocumentFooterText(String documentFooterText) {
		this.documentFooterText = documentFooterText;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public String getOrderingDealerAddress() {
		return orderingDealerAddress;
	}

	public void setOrderingDealerAddress(String orderingDealerAddress) {
		this.orderingDealerAddress = orderingDealerAddress;
	}

	public String getOrderingDealerPhone() {
		return orderingDealerPhone;
	}

	public void setOrderingDealerPhone(String orderingDealerPhone) {
		this.orderingDealerPhone = orderingDealerPhone;
	}

	public List<AccountTaxExemptVO> getAccTaxExemptVOList1() {
		if (accTaxExemptVOList != null) {
			int mod = accTaxExemptVOList.size() % 2;
			int half = accTaxExemptVOList.size() / 2;
			accTaxExemptVOList1 = accTaxExemptVOList.subList(0, half + mod);
		}
		return accTaxExemptVOList1;
	}

	public void setAccTaxExemptVOList1(List<AccountTaxExemptVO> accTaxExemptVOList1) {
		this.accTaxExemptVOList1 = accTaxExemptVOList1;
	}

	public List<AccountTaxExemptVO> getAccTaxExemptVOList2() {
		if (accTaxExemptVOList != null) {
			int mod = accTaxExemptVOList.size() % 2;
			int half = accTaxExemptVOList.size() / 2;
			accTaxExemptVOList2 = accTaxExemptVOList.subList(half + mod,accTaxExemptVOList.size());
		}
		return accTaxExemptVOList2;
	}

	
	public void setAccTaxExemptVOList2(List<AccountTaxExemptVO> accTaxExemptVOList2) {
		this.accTaxExemptVOList2 = accTaxExemptVOList2;
	}

}