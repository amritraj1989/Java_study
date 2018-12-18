package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.QuotationService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.CapitalCostOverviewService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.CapitalCostVO;
import com.mikealbert.vision.vo.CapitalCostVO.CapitalCostGroup;
import com.mikealbert.vision.vo.CapitalCostVO.GroupCostElement;
import com.mikealbert.vision.vo.CostElementVO;

@Component
@Scope("view")
public class CapitalCostsInquiryBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781659L;
	
	@Resource  FleetMasterDAO fleetMasterDAO ;
	@Resource CapitalCostOverviewService capitalCostOverviewService;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource QuotationService quotationService;

	
	private CapitalCostVO capitalCostVO ;	
	
	private boolean toogleCostElement = true;
	private boolean showDataTable = false;

	private String standardQuoteInfo;
	private String acceptedQuoteInfo;
	List<CostElementVO> listForScreen = new ArrayList<CostElementVO>();
	List<CostElementVO> listForToggleSummary = new ArrayList<CostElementVO>();
	List<CostElementVO> listForToggleAll = new ArrayList<CostElementVO>();
	
	
	
	public boolean isShowDataTable() {
		return showDataTable;
	}

	public void setShowDataTable(boolean showDataTable) {
		this.showDataTable = showDataTable;
	}

	@PostConstruct
	public void init() {
		super.openPage();
		initializeDataTable(500,770, new int[]{20,18,18,12,18,9,9,9,9,12,9,9});	
		try {
			capitalCostVO = new CapitalCostVO();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}

	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName("Capital Cost Overview");
		thisPage.setPageUrl(ViewConstants.CAPITAL_COST_OVERVIEW);
	}

	@Override
	protected void restoreOldPage() {}
	
	public void onShowReport() {
		
		capitalCostVO.clearData();
		showDataTable = false;
		toogleCostElement = true;
		if(MALUtilities.isEmptyString(capitalCostVO.getUnitNo())){	
			super.addErrorMessage("required.field", "Unit No");
		}else{
			FleetMaster fleetMaster  = fleetMasterDAO.findByUnitNo(capitalCostVO.getUnitNo());
			if(fleetMaster == null){
				super.addErrorMessage("notfound", "Unit");
			}else{
				populateData();
			}
		}
				
		
	}
	private void populateData(){
		 
	}
	
	
	public CapitalCostVO getCapitalCostVO() {
		return capitalCostVO;
	}

	public void setCapitalCostVO(CapitalCostVO capitalCostVO) {
		this.capitalCostVO = capitalCostVO;
	}
	public boolean isToogleCostElement() {
		return toogleCostElement;
	}

	public void onToogleCostElement() {
		//this.toogleCostElement = !toogleCostElement;
		if(this.toogleCostElement){
			this.toogleCostElement = false;
			listForScreen = listForToggleSummary;
		}else{
			this.toogleCostElement = true;
			listForScreen = listForToggleAll;
		}
	}
	public void setToogleCostElement(boolean toogleCostElement) {
		this.toogleCostElement = toogleCostElement;
	}
	
	public String getStandardQuoteInfo() {
		return standardQuoteInfo;
	}

	public void setStandardQuoteInfo(String standardQuoteInfo) {
		this.standardQuoteInfo = standardQuoteInfo;
	}

	public String getAcceptedQuoteInfo() {
		return acceptedQuoteInfo;
	}

	public void setAcceptedQuoteInfo(String acceptedQuoteInfo) {
		this.acceptedQuoteInfo = acceptedQuoteInfo;
	}

	public List<CostElementVO> getListForScreen() {
		return listForScreen;
	}

	public void setListForScreen(List<CostElementVO> listForScreen) {
		this.listForScreen = listForScreen;
	}
	
}