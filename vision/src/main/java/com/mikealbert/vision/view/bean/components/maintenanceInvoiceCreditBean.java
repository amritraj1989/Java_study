package com.mikealbert.vision.view.bean.components;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Docl;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.enumeration.MaintenanceRequestStatusEnum;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class maintenanceInvoiceCreditBean extends BaseBean{
	private static final long serialVersionUID = -7243110837187947560L;
	
	@Resource MaintenanceRequestService maintRequestService;
	
	private List<Doc> creditList;
	private List<MaintenanceInvoiceCreditVO> creditTaskList;
	private Doc selectedCredit;
	private BigDecimal markupTotalWithoutTax;
	private BigDecimal markupTaxTotal;
	private BigDecimal allCreditsTotalNoTax;
	private BigDecimal allCreditsTotalWithTax;
	private BigDecimal creditRechargeLinesTotal;
	private BigDecimal creditTaxTotal;
	private BigDecimal rechargeTaxTotal;
	private BigDecimal creditRechargeTotal;
	
	public void initDialog(long mrqId){
		MaintenanceRequest mrq = maintRequestService.getMaintenanceRequestByMrqId(mrqId);
		
		try{
			setCreditTabInfo(mrq);
		} catch(Exception e) {
			super.addErrorMessage("generic.error", e.getMessage());
		}  
	}
	
	public void setCreditTabInfo(MaintenanceRequest mrq){
		if(mrq.getMaintReqStatus().equalsIgnoreCase(MaintenanceRequestStatusEnum.MAINT_REQUEST_STATUS_COMPLETE.getCode())){
			creditList = maintRequestService.getMaintenanceCreditAP(mrq);
			setCreditTaskList(maintRequestService.getMaintenanceCreditAPLines(mrq));
			setMarkupTotalWithoutTax(calculateMarkupWithoutTax(mrq));
			setMarkupTaxTotal(calculateMarkupTaxTotal(mrq));
			setAllCreditsTotalNoTax(calculateAllCreditsTotal(mrq, false));
			setAllCreditsTotalWithTax(calculateAllCreditsTotal(mrq, true));
			setCreditRechargeLinesTotal(calculateAllCreditsRechargeTotalWoTax(mrq));
			setCreditTaxTotal(calculateTaxTotal(mrq));
			setRechargeTaxTotal(calculateRechargeTax(mrq));
			setCreditRechargeTotal(calculateCreditRechargeTotal(mrq));
			if(!creditList.isEmpty()){
				this.setSelectedCredit(creditList.get(0));
			}
		}
	}
	
	public BigDecimal calculateMarkupWithoutTax(MaintenanceRequest mrq){
		BigDecimal markupTotal = new BigDecimal("0.00");
		List<Docl> markupList = maintRequestService.getMaintenanceCreditARMarkupList(mrq);
		
		if(markupList != null){
			for(Docl markupLine : markupList){
				markupTotal = markupTotal.add(markupLine.getTotalPrice());
			}
		}
		return markupTotal;
	}
	
	public BigDecimal calculateMarkupTaxTotal(MaintenanceRequest mrq){
		BigDecimal markupTaxTotal = new BigDecimal("0.00");
		List<Docl> markupList = maintRequestService.getMaintenanceCreditARMarkupList(mrq);
		
		if(markupList != null){
			for(Docl markupLine : markupList){
				markupTaxTotal = markupTaxTotal.add(markupLine.getUnitTax());
			}
		}
		return markupTaxTotal;
	}
	
	public BigDecimal calculateRechargeTax(MaintenanceRequest mrq){
		BigDecimal rechargeTaxTotal = new BigDecimal("0.00");
		List<Docl> taxList = maintRequestService.getMaintenanceCreditARTaxList(mrq);
		
		if(taxList != null){
			for(Docl taxLine : taxList){
				rechargeTaxTotal = rechargeTaxTotal.add(taxLine.getTotalPrice());
			}
		}
		return rechargeTaxTotal;
	}
	
	public BigDecimal calculateAllCreditsTotal(MaintenanceRequest mrq, boolean includeTax){
		BigDecimal creditTotal = new BigDecimal("0.00");
		if(creditTaskList != null){
			for(MaintenanceInvoiceCreditVO line : creditTaskList){
				if(line.getMrtId().equals(0L)){ //Null Long value mrtId is 0
					if(includeTax){
						creditTotal = creditTotal.add(line.getTotalPrice());
					}
				}else{
					creditTotal = creditTotal.add(line.getTotalPrice());
				}
			}
		}
		return creditTotal;
	}
	
	public BigDecimal calculateTaxTotal(MaintenanceRequest mrq){
		BigDecimal taxTotal = new BigDecimal("0.00");
		if(creditTaskList != null){
			for(MaintenanceInvoiceCreditVO line : creditTaskList){
				if(line.getMrtId().equals(0L)){ //Null Long value mrtId is 0
					taxTotal = taxTotal.add(line.getTotalPrice());
				}
			}
		}
		return taxTotal;
	}
	
	public BigDecimal calculateCreditRechargeTotal(MaintenanceRequest mrq){
		BigDecimal creditRechargeTotal = new BigDecimal("0.00");
		creditRechargeTotal = creditRechargeTotal.add(calculateAllCreditsRechargeTotalWoTax(mrq).add(rechargeTaxTotal).add(markupTotalWithoutTax).add(markupTaxTotal));
		return creditRechargeTotal;
	}
	
	public List<MaintenanceInvoiceCreditVO> creditListWithoutTaxLines(){
		List<MaintenanceInvoiceCreditVO> creditListWithoutTaxLines = new ArrayList<MaintenanceInvoiceCreditVO>();
		if(creditTaskList != null){
			for(MaintenanceInvoiceCreditVO line : creditTaskList){
				if(!line.getMrtId().equals(0L)){
					creditListWithoutTaxLines.add(line);
				}
			}
		}
		return creditListWithoutTaxLines;
	}
	
	public List<MaintenanceInvoiceCreditVO> creditListWithoutTaxByDocNo(){
		List<MaintenanceInvoiceCreditVO> creditListByDocNo = new ArrayList<MaintenanceInvoiceCreditVO>();
		List<MaintenanceInvoiceCreditVO> creditList = creditListWithoutTaxLines();
		
		if(creditList != null){
			for(MaintenanceInvoiceCreditVO line : creditList){
				if(line.getCreditNo().equals(getSelectedCredit().getDocNo())){
					creditListByDocNo.add(line);
				}
			}
		}
		return creditListByDocNo;
	}
	
	
	public BigDecimal calculateAllCreditsRechargeTotalWoTax(MaintenanceRequest mrq){
		BigDecimal creditsRechargeTotal = new BigDecimal("0.00");
		List<Docl> rechargeDoclListWithoutTaxAndMarkup = new ArrayList<Docl>();
		if(creditTaskList != null){
			rechargeDoclListWithoutTaxAndMarkup = maintRequestService.getMaintenanceCreditARLinesWithoutMarkupAndTaxList(mrq);
			for(Docl line : rechargeDoclListWithoutTaxAndMarkup){
				creditsRechargeTotal = creditsRechargeTotal.add(line.getTotalPrice());
			}
		}
		return creditsRechargeTotal;
	}	
	
	public BigDecimal calculateCreditListTotalWoTaxByDocNo(){
		BigDecimal creditListTotal = new BigDecimal("0.00");
		List<MaintenanceInvoiceCreditVO> creditList = creditListWithoutTaxByDocNo();

		if(creditList != null){
			for(MaintenanceInvoiceCreditVO line : creditList){ 
				creditListTotal = creditListTotal.add(line.getTotalPrice());
			}
		}
		return creditListTotal;
	}	

	public List<Doc> getCreditList() {
		return creditList;
	}

	public void setCreditList(List<Doc> creditList) {
		this.creditList = creditList;
	}

	public List<MaintenanceInvoiceCreditVO> getCreditTaskList() {
		return creditTaskList;
	}

	public void setCreditTaskList(List<MaintenanceInvoiceCreditVO> creditTaskList) {
		this.creditTaskList = creditTaskList;
	}

	public Doc getSelectedCredit() {
		return selectedCredit;
	}

	public void setSelectedCredit(Doc selectedCredit) {
		this.selectedCredit = selectedCredit;
	}

	public BigDecimal getMarkupTotalWithoutTax() {
		return markupTotalWithoutTax;
	}

	public void setMarkupTotalWithoutTax(BigDecimal markupTotalWithoutTax) {
		this.markupTotalWithoutTax = markupTotalWithoutTax;
	}

	public BigDecimal getMarkupTaxTotal() {
		return markupTaxTotal;
	}

	public void setMarkupTaxTotal(BigDecimal markupTaxTotal) {
		this.markupTaxTotal = markupTaxTotal;
	}

	public BigDecimal getAllCreditsTotalNoTax() {
		return allCreditsTotalNoTax;
	}

	public void setAllCreditsTotalNoTax(BigDecimal allCreditsTotalNoTax) {
		this.allCreditsTotalNoTax = allCreditsTotalNoTax;
	}

	public BigDecimal getAllCreditsTotalWithTax() {
		return allCreditsTotalWithTax;
	}

	public void setAllCreditsTotalWithTax(BigDecimal allCreditsTotalWithTax) {
		this.allCreditsTotalWithTax = allCreditsTotalWithTax;
	}

	public BigDecimal getCreditTaxTotal() {
		return creditTaxTotal;
	}

	public void setCreditTaxTotal(BigDecimal creditTaxTotal) {
		this.creditTaxTotal = creditTaxTotal;
	}

	public BigDecimal getCreditRechargeTotal() {
		return creditRechargeTotal;
	}

	public void setCreditRechargeTotal(BigDecimal creditRechargeTotal) {
		this.creditRechargeTotal = creditRechargeTotal;
	}

	public BigDecimal getRechargeTaxTotal() {
		return rechargeTaxTotal;
	}

	public void setRechargeTaxTotal(BigDecimal rechargeTaxTotal) {
		this.rechargeTaxTotal = rechargeTaxTotal;
	}

	public BigDecimal getCreditRechargeLinesTotal() {
		return creditRechargeLinesTotal;
	}

	public void setCreditRechargeLinesTotal(BigDecimal creditRechargeLinesTotal) {
		this.creditRechargeLinesTotal = creditRechargeLinesTotal;
	}
	
}
