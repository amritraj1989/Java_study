package com.mikealbert.vision.view.bean;

import static com.mikealbert.vision.comparator.InServiceProgressDateComparator.REQ_BY_DATE_SORT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.UnitProgressSearchVO;

@Component
@Scope("view")
public class UnitProgressSearchBean extends StatefulBaseBean {
	
	private static final long serialVersionUID = 8159335851834370766L;
	

	private List<UnitProgressSearchVO> masterList;
	private String emptyDataTableMessage;
	
	private static final int UPFIT_SEARCH = 1;
	private static final int IN_SERV_DATE_SEARCH = 2;	
	private int searchType ;
	
	private static final String PERMISISON_MANAGE_UPFIT = "maintainUpfit";
	private static final String PERMISISON_MANAGE_INSERV = "maintainInService";
	
	private boolean hasManagePermission = false;
	private boolean enquiryMode = false;
	private String unitNo;
	
	@PostConstruct
	public void init() {
		openPage();
	}

	public int reqdByCustomSort(Object o1, Object o2){
		 return REQ_BY_DATE_SORT.compare((UnitProgressSearchVO)o1, (UnitProgressSearchVO)o2);		
	}
	
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_UNIT_PROGRESS_CHASING);
		thisPage.setPageUrl(ViewConstants.UNIT_PROGRESS_SEARCH);
		
		Map<String, Object> map = super.thisPage.getInputValues();		
		String readMode = (String)map.get(ViewConstants.VIEW_MODE_READ);
		setEnquiryMode(Boolean.valueOf(readMode));
		if(enquiryMode) {
			setUnitNo((String)map.get(ViewConstants.VIEW_PARAM_UNIT_NO));
		}
		
		this.setEmptyDataTableMessage(talMessage.getMessage("no.records.found"));
	}

	protected void restoreOldPage() {
		
	}
	
	public String cancel(){    	
    	return super.cancelPage();
    }
	
	public List<UnitProgressSearchVO> getMasterList() {
		return masterList;
	}
	
	public void navigateToUpfitterQueue(){
//		FacesContext context = FacesContext.getCurrentInstance();
//	    context.getExternalContext().getRequestMap().put("searchType", 1);
	    Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(1));
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
	}
	
	public void navigateToInServiceQueue(){
//		FacesContext context = FacesContext.getCurrentInstance();
//	    context.getExternalContext().getRequestMap().put("searchType", 2);
	    Map<String, Object> nextPageValues = new HashMap<String, Object>();
    	nextPageValues.put("searchType", String.valueOf(2));
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.UPFITTER_IN_SERVICE_QUEUE);
		
	}
	
	public void navigateToManufacturerQueue(){
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.MANUFACTURER_PROGRESS_QUEUE);
	}
	
	public void navigateToPOReleaseQueue(){
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.PURCHASE_ORDER_RELEASE_QUEUE);
	}
	
	public void navigateToThirdPartyQueue(){
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());
		Map<String, Object> nextPageValues = new HashMap<String, Object>();
	    saveNextPageInitStateValues(nextPageValues);
		forwardToURL(ViewConstants.THIRD_PARTY_PROGRESS_QUEUE);
	}
	
	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {		
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		//Current page has just link and had not property for maintaining it['ss state 
		return restoreStateValues;
	}

	public String getEmptyDataTableMessage() {
		return emptyDataTableMessage;
	}

	public void setEmptyDataTableMessage(String emptyDataTableMessage) {
		this.emptyDataTableMessage = emptyDataTableMessage;
	}

	public int getSearchType() {
		return searchType;
	}


	public void setSearchType(int searchType) {
		this.searchType = searchType;
	}

	public boolean hasManagePermission(){
		return hasManagePermission;
	}

	public String getPermissionResourceName() {
		
		if(searchType == UPFIT_SEARCH )
			return PERMISISON_MANAGE_UPFIT;
		else if(searchType ==  IN_SERV_DATE_SEARCH )
			return	PERMISISON_MANAGE_INSERV;
		else 
			return "";
		
	}

	public boolean isEnquiryMode() {
		return enquiryMode;
	}

	public void setEnquiryMode(boolean enquiryMode) {
		this.enquiryMode = enquiryMode;
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

}
