package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.ValueChangeEvent;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.PermissionSet;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.entity.WorkClassPermission;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.UserService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.view.ViewConstants;

@Component
@Scope("view")
public class AdminWorkClassPermissionsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952043784558L;

	@Resource UserService userService;
	@Resource LookupCacheService lookupCacheService;
	
	private CorporateEntity[] corpEntities;
	private CorporateEntity selectedCorpEntity;
	private List<WorkClass> availableWorkClasses;
	private WorkClass selectedWorkClass;
	private List<PermissionSet> permissionSets;
	private List<WorkClassPermission> workClassPermissions;
	private PermissionSet[] selectedPermissionSets;
	private PermissionSet activePermissionSet;
	private int DEFAULT_DATATABLE_HEIGHT = 175;
	private String workClassParam = null;

	/**
	 * Initializes the bean
	 */
    @PostConstruct
    public void init(){
    	// set the height and width of the datatables based upon the screen resolution
    	initializeDataTable(600, 280, new int[] {50}).setHeight(DEFAULT_DATATABLE_HEIGHT); 
    	super.openPage();
    	
    	// load the permission sets from lookup cache
    	this.permissionSets = userService.getPermissionSets();
    	// load the corpEntites from the enumeration
    	this.corpEntities = CorporateEntity.values();
    	
    	// set the default (selected) corpEntity to the MAL entry
    	this.setSelectedCorpEntity(CorporateEntity.MAL);
    	
    	// load the work class for MAL from the userService (the default) corp entity
    	this.availableWorkClasses = userService.findAllWorkClasses(CorporateEntity.MAL);
    	if(isNotNull(workClassParam)){
    		selectedWorkClass = userService.findWorkClass(workClassParam, CorporateEntity.MAL);
    	}
    	else{
	    	// set the default (selected) work class to the first in the list
	    	this.selectedWorkClass = this.availableWorkClasses.get(0);
    	}
    	// get the work class permissions
    	this.workClassPermissions = userService.getWorkClassPermissions(this.selectedWorkClass);
    	// loop thru the workclass permissions and add selectedPermissionSets entries for each one that has been granted
    	List<PermissionSet> perms = new ArrayList<PermissionSet>();
    	for(WorkClassPermission wcp : this.workClassPermissions){
    		perms.add(wcp.getPermissionSet());
    	}
    	setSelectedPermissionSets(perms.toArray(new PermissionSet[perms.size()]));
    }
    
    // handle the ajax event of changing a corp entity (re-load the work classes)    
    public void changeCorpEntity(ValueChangeEvent event){
    	
    	CorporateEntity newCorpEntity = (CorporateEntity)event.getNewValue();
    	
    	// load the work class for MAL from the userService (the default) corp entity
    	this.availableWorkClasses = userService.findAllWorkClasses(newCorpEntity);
    	// set the default (selected) work class to the first in the list
    	this.selectedWorkClass = this.availableWorkClasses.get(0);
    	// get the work class permissions
    	this.workClassPermissions = userService.getWorkClassPermissions(this.selectedWorkClass);
    	// loop thru the workclass permissions and add selectedPermissionSets entries for each one that has been granted
    	List<PermissionSet> perms = new ArrayList<PermissionSet>();
    	for(WorkClassPermission wcp : this.workClassPermissions){
    		perms.add(wcp.getPermissionSet());
    	}
    	setSelectedPermissionSets(perms.toArray(new PermissionSet[perms.size()]));
    }
    
    // handle the ajax event of selecting a work class row (re-select the permission sets from the DB)
    public void onRowSelect(SelectEvent event) {
    	// get the work class permissions
    	this.workClassPermissions = userService.getWorkClassPermissions(this.selectedWorkClass);
    	// loop thru the workclass permissions and add selectedPermissionSets entries for each one that has been granted
    	List<PermissionSet> perms = new ArrayList<PermissionSet>();
    	for(WorkClassPermission wcp : this.workClassPermissions){
    		perms.add(wcp.getPermissionSet());
    	}
    	setSelectedPermissionSets(perms.toArray(new PermissionSet[perms.size()]));
	}
    
    // handle the ajax event of selecting permission set; this is used only for the information dialog
    public void selectActivePermissionSet(PermissionSet activePermissionSet) {
    	this.activePermissionSet = activePermissionSet;
    	
	}

    /**
     * Handles page cancel button click event
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    	
    /**
     * Handles page save button click event
     * @return The current view
     */
    public String save(){ 
		try {
			// add/remove the work class permission sets
			this.workClassPermissions = userService.addOrRemoveWorkClassPermissions(this.selectedWorkClass, this.selectedPermissionSets);	
		} catch (Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
		}
		
		super.addSuccessMessage("process.success", "saving work class permissions ");
		
		return null;
    }
    
	/**
	 * Navigation code
	 */
	protected void loadNewPage() {      
		thisPage.setPageDisplayName("Work Class Permissions");
		thisPage.setPageUrl("adminWorkClassPermissions");	
		
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_WORK_CLASS) != null){
			workClassParam = (String)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_WORK_CLASS);
		}
	} 
	
	// new navigation code
	protected void restoreOldPage() {}

	public List<WorkClass> getAvailableWorkClasses() {
		return availableWorkClasses;
	}

	public void setAvailableWorkClasses(List<WorkClass> availableWorkClasses) {
		this.availableWorkClasses = availableWorkClasses;
	}

	public CorporateEntity[] getCorpEntities() {
		return corpEntities;
	}

	public void setCorpEntities(CorporateEntity[] corpEntities) {
		this.corpEntities = corpEntities;
	}

	public CorporateEntity getSelectedCorpEntity() {
		return selectedCorpEntity;
	}

	public void setSelectedCorpEntity(CorporateEntity selectedCorpEntity) {
		this.selectedCorpEntity = selectedCorpEntity;
	}

	public WorkClass getSelectedWorkClass() {
		return selectedWorkClass;
	}

	public void setSelectedWorkClass(WorkClass selectedWorkClass) {
		this.selectedWorkClass = selectedWorkClass;
	}

	public List<PermissionSet> getPermissionSets() {
		return permissionSets;
	}

	public void setPermissionSets(List<PermissionSet> permissionSets) {
		this.permissionSets = permissionSets;
	}

	public List<WorkClassPermission> getWorkClassPermissions() {
		return workClassPermissions;
	}

	public void setWorkClassPermissions(List<WorkClassPermission> workClassPermissions) {
		this.workClassPermissions = workClassPermissions;
	}
	
	public PermissionSet[] getSelectedPermissionSets() {
		return selectedPermissionSets;
	}

	public void setSelectedPermissionSets(PermissionSet[] selectedPermissionSets) {
		this.selectedPermissionSets = selectedPermissionSets;
	}
	
	public PermissionSet getActivePermissionSet() {
		return activePermissionSet;
	}

	public void setActivePermissionSet(PermissionSet activePermissionSet) {
		this.activePermissionSet = activePermissionSet;
	}
	
}
