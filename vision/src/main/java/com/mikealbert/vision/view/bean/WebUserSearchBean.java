package com.mikealbert.vision.view.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.component.datatable.DataTable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.service.WebsiteUserService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class WebUserSearchBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	static final String DATA_TABLE_UI_ID = "DT_UI_ID";
	
	@Resource WebsiteUserService websiteUserService;

	
	private String accountCode;
	private List<WebsiteUser> websiteUserList;
	private WebsiteUser selectedWebsiteUser;
	private boolean searchRequired;
	
	@PostConstruct
	public void init() {
		initializeDataTable(500, 850, new int[] {40, 24}).setHeight(0);
		openPage();
		if(searchRequired) {
			performSearch();
			selectedWebsiteUser = (WebsiteUser) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		}
	}
	
	public void performSearch() {
		websiteUserList = websiteUserService.getEnabledWebsiteUsersByAccountAndType(new ExternalAccountPK(1l, "C", accountCode), "DRIVER");
		if(!websiteUserList.isEmpty()) {
			selectedWebsiteUser = websiteUserList.get(0);			
		}
		if(websiteUserList != null){
			if(websiteUserList.isEmpty()){
				super.getDataTable().setHeight(30);
			}else{
				if(websiteUserList.size() > 0) {
					super.getDataTable().setMaximumHeight();
				}
			}
		}else{
			super.getDataTable().setHeight(30);
		}

	}
		
	public String cancel() {
		return super.cancelPage();
	}
	
	public void navigateToWebUserEdit(){
		Map<String, Object> nextPageValues = new HashMap<String, Object>();	
		saveRestoreStateValues(getCurrentPageRestoreStateValuesMap());	 	
		nextPageValues.put("WEB_USER_ID", selectedWebsiteUser.getId());		
	    saveNextPageInitStateValues(nextPageValues);   
		forwardToURL(ViewConstants.WEB_USER_EDIT);
	}

	private Map<String, Object> getCurrentPageRestoreStateValuesMap() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);		
		
		restoreStateValues.put(ViewConstants.DT_SELECTED_ITEM, selectedWebsiteUser);			
		restoreStateValues.put(ViewConstants.DT_SELECTD_PAGE_START_INDEX, dt.getFirst());
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ACCOUNT_CODE, accountCode);			

		
		return restoreStateValues;		
	}	

	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants. DISPLAY_NAME_SEARCH_WEB_USERS);
		thisPage.setPageUrl(ViewConstants.WEB_USER_SEARCH);
	}


	@Override
	protected void restoreOldPage() {
		searchRequired = true;
		accountCode = (String) thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_ACCOUNT_CODE);
		selectedWebsiteUser = (WebsiteUser) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTED_ITEM);
		int firstRow = (int) thisPage.getRestoreStateValues().get(ViewConstants.DT_SELECTD_PAGE_START_INDEX);
		DataTable dt = (DataTable) getComponent(DATA_TABLE_UI_ID);
		dt.setFirst(firstRow);

	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public List<WebsiteUser> getWebsiteUserList() {
		return websiteUserList;
	}

	public void setWebsiteUserList(List<WebsiteUser> websiteUserList) {
		this.websiteUserList = websiteUserList;
	}

	public WebsiteUser getSelectedWebsiteUser() {
		return selectedWebsiteUser;
	}

	public void setSelectedWebsiteUser(WebsiteUser selectedWebsiteUser) {
		this.selectedWebsiteUser = selectedWebsiteUser;
	}
}