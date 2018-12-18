package com.mikealbert.vision.specs.fleet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.annotation.Resource;

import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.VehicleMaintenanceService;

public class DisplayPODetailsTest extends BaseSpec{
	
	@Resource VehicleMaintenanceService vehicleMaintenanceService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	
	String poNumber;
	int poLineCount;
	boolean existsMaintCategory = false;
	String serviceCode;
	String maintCode;
	String maintCodeDescription;
	String rechargeIndicator;
	String rechargeReason;
	String discountIndicator;
	int quantity = 0;
	BigDecimal unitPrice;
	BigDecimal totalAmount;
	BigDecimal poTotal;
	BigDecimal markUp;
	BigDecimal rechargeTotal;
	MaintenanceRequestTask maintRequestTask = new MaintenanceRequestTask();
	
	DecimalFormat decFormat = new DecimalFormat("################.00");
		
	public void testViewPurchaseOrderDetail(String purchaseOrderNumber, String maintCategory){
		
		setPoNumber(purchaseOrderNumber);
		setUnitPrice(new BigDecimal(0.00));
		setTotalAmount(new BigDecimal(0.00));
		setPoTotal(new BigDecimal(0.00));
		setMarkUp(new BigDecimal(0.00));
		setRechargeTotal(new BigDecimal(0.00));
		MaintenanceRequest maintenanceRequest = new MaintenanceRequest();
		maintenanceRequest = maintenanceRequestService.getMaintenanceRequestByJobNo(getPoNumber());
		setPoLineCount(maintenanceRequest.getMaintenanceRequestTasks().size());
		for (MaintenanceRequestTask task : maintenanceRequest.getMaintenanceRequestTasks()){
			if (task.getTotalCost() != null){
				setPoTotal(getPoTotal().add(task.getTotalCost()));
			}
			
			if (task.getMarkUpAmount()!=null){
				setMarkUp(getMarkUp().add(task.getMarkUpAmount()));
			}
			
			if (task.getRechargeFlag().equals("Y")){
				setRechargeTotal(getRechargeTotal().add(task.getRechargeTotalCost()));
			}
			if (task.getMaintCatCode().equals(maintCategory)){
				setMaintRequestTask(task);
			}
		}
		
		if (getMaintRequestTask() != null){
			setExistsMaintCategory(true);
			if (maintRequestTask.getServiceProviderMaintenanceCode() != null){
				setServiceCode(maintRequestTask.getServiceProviderMaintenanceCode().getCode());
			}else
			{
				setServiceCode("null");
			}
			
			setMaintCode(getMaintRequestTask().getMaintenanceCode().getCode());
			setMaintCodeDescription(getMaintRequestTask().getMaintenanceCode().getDescription());
			setRechargeIndicator(getMaintRequestTask().getRechargeFlag());
			if (vehicleMaintenanceService.convertMaintenanceRechargeCode(getMaintRequestTask().getRechargeCode()) != null){
				setRechargeReason((vehicleMaintenanceService.convertMaintenanceRechargeCode(getMaintRequestTask().getRechargeCode())).getDescription());
			}else
			{
				setRechargeReason("null");
				
			}
			
			setDiscountIndicator(getMaintRequestTask().getDiscountFlag());
			setQuantity(getMaintRequestTask().getTaskQty().intValue());
			setUnitPrice(new BigDecimal(decFormat.format((getMaintRequestTask().getUnitCost()))));
			setTotalAmount(new BigDecimal(decFormat.format(getMaintRequestTask().getTotalCost())));
		}
	}
	
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
	
	public MaintenanceRequestTask getMaintRequestTask() {
		return maintRequestTask;
	}

	public void setMaintRequestTask(MaintenanceRequestTask maintRequestTask) {
		this.maintRequestTask = maintRequestTask;
	}

	public int getPoLineCount() {
		return poLineCount;
	}

	public void setPoLineCount(int poLineCount) {
		this.poLineCount = poLineCount;
	}

	public boolean isExistsMaintCategory() {
		return existsMaintCategory;
	}

	public void setExistsMaintCategory(boolean existsMaintCategory) {
		this.existsMaintCategory = existsMaintCategory;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getMaintCode() {
		return maintCode;
	}

	public void setMaintCode(String maintenanceCode) {
		this.maintCode = maintenanceCode;
	}

	public String getMaintCodeDescription() {
		return maintCodeDescription;
	}

	public void setMaintCodeDescription(String maintCodeDescription) {
		this.maintCodeDescription = maintCodeDescription;
	}

	public String getRechargeIndicator() {
		return rechargeIndicator;
	}

	public void setRechargeIndicator(String rechargeIndicator) {
		this.rechargeIndicator = rechargeIndicator;
	}

	public String getRechargeReason() {
		return rechargeReason;
	}

	public void setRechargeReason(String rechargeReason) {
		this.rechargeReason = rechargeReason;
	}

	public String getDiscountIndicator() {
		return discountIndicator;
	}

	public void setDiscountIndicator(String discountIndicator) {
		this.discountIndicator = discountIndicator;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public BigDecimal getPoTotal() {
		return poTotal;
	}

	public void setPoTotal(BigDecimal poTotal) {
		this.poTotal = poTotal;
	}

	public BigDecimal getMarkUp() {
		return markUp;
	}

	public void setMarkUp(BigDecimal markUp) {
		this.markUp = markUp;
	}

	public BigDecimal getRechargeTotal() {
		return rechargeTotal;
	}

	public void setRechargeTotal(BigDecimal rechargeTotal) {
		this.rechargeTotal = rechargeTotal;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
}
