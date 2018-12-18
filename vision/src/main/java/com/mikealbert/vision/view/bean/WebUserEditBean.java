package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.WebsiteUser;
import com.mikealbert.data.entity.WebsiteUserAssociation;
import com.mikealbert.data.vo.WebsiteUserAssociationVO;
import com.mikealbert.service.WebsiteUserService;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class WebUserEditBean extends StatefulBaseBean {

	private static final long serialVersionUID = 2437933046906999010L;
	
	@Resource WebsiteUserService websiteUserService;

	
	private WebsiteUser selectedWebsiteUser;
	private long id;
	private String addName;
	private String accountCode;
	private Long newAssociationId;
	private List<WebsiteUserAssociationVO> websiteUserAssociationVOList = new ArrayList<WebsiteUserAssociationVO>();

	
	@PostConstruct
	public void init() {
		initializeDataTable(600, 850, new int[] {40, 24}).setHeight(0);
		openPage();
		selectedWebsiteUser = websiteUserService.getWebsiteUser(id);
		accountCode = selectedWebsiteUser.getExternalAccount().getExternalAccountPK().getAccountCode();
		if(!selectedWebsiteUser.getWebsiteUserAssociationList().isEmpty()) {
			websiteUserAssociationVOList.addAll(websiteUserService.getWebsiteUserAssociationVOList(selectedWebsiteUser));
		}
		setTableHeight();
	}
	
	public String cancel() {
		return super.cancelPage();
	}
	
	public void add() {
		if(newAssociationId != null) {
			WebsiteUserAssociation websiteUserAssociation = new WebsiteUserAssociation();
			websiteUserAssociation.setAssociationId(newAssociationId);
			websiteUserAssociation.setUserType("DRIVER");
			websiteUserAssociation.setWebsiteUser(selectedWebsiteUser);
			selectedWebsiteUser.getWebsiteUserAssociationList().add(websiteUserAssociation);
			websiteUserAssociationVOList.add(websiteUserService.getWebsiteUserAssociationVO(websiteUserAssociation));
			setTableHeight();			
		}
	}
	
	public void remove(WebsiteUserAssociationVO association) {
		selectedWebsiteUser.getWebsiteUserAssociationList().remove(association.getWebsiteUserAssociation());
		websiteUserAssociationVOList.remove(association);
		setTableHeight();
	}
	
	private void setTableHeight() {
		if(websiteUserAssociationVOList != null){
			if(websiteUserAssociationVOList.isEmpty()){
				super.getDataTable().setHeight(30);
			}else{
				if(websiteUserAssociationVOList.size() > 0) {
					super.getDataTable().setMaximumHeight();
				}
			}
		}else{
			super.getDataTable().setHeight(30);
		}

	}
	
	public String save() {
		try {
			if(validList()) {
				websiteUserService.saveWebsiteUser(selectedWebsiteUser);
				addSuccessMessage("saved.success", "Web User Associations");				
				return cancel();
			}
		} catch(Exception e) {
    		super.addErrorMessage("generic.error", e.getMessage());
		}		
		return null;
	}
	
	private boolean validList() {
		boolean isValid = true;
		
		Set<Long> set = new HashSet<Long>();
	    for (WebsiteUserAssociationVO wuaVO: websiteUserAssociationVOList) {
	    	if (!set.add(wuaVO.getWebsiteUserAssociation().getAssociationId())) {
	    		addSimplErrorMessage("Cannot have duplicate associations");
	    		isValid = false;
	    	}
			if(wuaVO.getWebsiteUserAssociation().getAssociationId() == selectedWebsiteUser.getDriver().getDrvId()) {
				addSimplErrorMessage("Cannot associate the same driver to themselves");
				isValid = false;
			}
			WebsiteUserAssociation wua = websiteUserService.getWebsiteUserAssociation(wuaVO.getWebsiteUserAssociation().getAssociationId(), "DRIVER");
			if(wua != null && !wua.getWebsiteUser().getId().equals(selectedWebsiteUser.getId())) {
					addSimplErrorMessage("Assocation with Id " + wua.getAssociationId() + " is already associated with username " + wua.getWebsiteUser().getUsername());
					isValid = false;				
			}					
	
	    }
		
		return isValid;
	}
	
	@Override
	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_EDIT_WEB_USER);
		thisPage.setPageUrl(ViewConstants.WEB_USER_EDIT);
		if (thisPage.getInputValues().get("WEB_USER_ID") != null) {
			id = ((Long) thisPage.getInputValues().get("WEB_USER_ID"));
		}

	}

	@Override
	protected void restoreOldPage() {
	}

	public WebsiteUser getSelectedWebsiteUser() {
		return selectedWebsiteUser;
	}

	public void setSelectedWebsiteUser(WebsiteUser selectedWebsiteUser) {
		this.selectedWebsiteUser = selectedWebsiteUser;
	}

	public String getAddName() {
		return addName;
	}

	public void setAddName(String addName) {
		this.addName = addName;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public Long getNewAssociationId() {
		return newAssociationId;
	}

	public void setNewAssociationId(Long newAssociationId) {
		this.newAssociationId = newAssociationId;
	}

	public List<WebsiteUserAssociationVO> getWebsiteUserAssociationVOList() {
		return websiteUserAssociationVOList;
	}

	public void setWebsiteUserAssociationVOList(List<WebsiteUserAssociationVO> websiteUserAssociationVOList) {
		this.websiteUserAssociationVOList = websiteUserAssociationVOList;
	}

}