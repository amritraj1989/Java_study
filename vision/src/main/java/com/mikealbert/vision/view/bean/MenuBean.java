package com.mikealbert.vision.view.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.event.ToggleEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.service.OraSessionService;
import com.mikealbert.view.MenuGroup;
import com.mikealbert.view.MenuItem;
import com.mikealbert.view.MenuService;
import com.mikealbert.vision.view.ViewConstants;

@Component("menuBean")
@Scope("session")
public class MenuBean extends BaseBean {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private static final long serialVersionUID = 4608139884685606139L;

	private boolean collapseMenu;
	private List<MenuGroup> menuGroupList = new ArrayList<MenuGroup>();
	public static final String MENU_ITEM = "MENU_ITEM";
	private MenuItem selectedItem;
	private int selectedGroupIndex = 0;
	private String databaseName;
	private Date databaseDate;
	private String dbRefreshInfo;
	
	//Retrieves build version from buildInfo.properties file
	@Value("${build.version}")  String buildVersion;

	@Resource	MenuService  menuService;
	@Resource	OraSessionService oraSessionService;

	@PostConstruct
	public void loadMenu() {
		
		for (MenuGroup systemMenuGroup : menuService.getMenuGroupList()) {
			
			MenuGroup userMenuGroup = new MenuGroup();
			userMenuGroup.setGroupName(systemMenuGroup.getGroupName());
			userMenuGroup.setMenuItemList(new ArrayList<MenuItem>());			
			
			for (MenuItem systemMenuItem : systemMenuGroup.getMenuItemList()) {
				MenuItem userMenuItem = new MenuItem(systemMenuItem.getDisplayName(), systemMenuItem.getURL(), 
														systemMenuItem.getPermissionResourceName(), systemMenuItem.isApplicableForReadOnly());
				userMenuGroup.getMenuItemList().add(userMenuItem);
			}
			
			menuGroupList.add(userMenuGroup);
		}
		
		logger.debug("Total Menu List:"+menuService.getMenuGroupList().size() +"Copied Menu List:"+menuGroupList.size() )	;
		
		for (Iterator<MenuGroup> itrGroup = menuGroupList.iterator(); itrGroup.hasNext();) {
			MenuGroup menuGroup = (MenuGroup) itrGroup.next();
			for (Iterator<MenuItem> itrItem = menuGroup.getMenuItemList().iterator(); itrItem.hasNext();) {
				MenuItem menuItem = (MenuItem) itrItem.next();
				String resourceName =   menuItem.getPermissionResourceName();
				
				boolean hasPermissionOnItem  = hasPermission( resourceName);
				boolean isApplicableForReadOnly  = menuItem.isApplicableForReadOnly();
				logger.debug("User "+ this.getLoggedInUser().getUsername() +" permission on menu item "+ menuItem.getDisplayName() 
						+" is : hasPermission "+hasPermissionOnItem +" and isApplicableForReadOnly :"+isApplicableForReadOnly )	;
				
				if(! hasPermissionOnItem && ! isApplicableForReadOnly ){
					itrItem.remove();	
				}
			}
			if(menuGroup.getMenuItemList().size() == 0){
				itrGroup.remove();
			}
		}
		
		setMenuSelection(ViewConstants.DISPLAY_NAME_DRIVER_MODULE_GROUP, ViewConstants.DISPLAY_NAME_DRIVER_SEARCH);
		//Sets databaseName for output on UI template
		databaseName = oraSessionService.getDatabaseNameForDevQATrain();
		databaseDate = oraSessionService.getDatabaseDate();
		dbRefreshInfo = oraSessionService.getDBRefreshdate();
		collapseMenu = true; /** set default to collapsed **/
	}
	
	public String selectMenuItemAction(String selectedGroupName, String linkDisplayName) {
		setMenuSelection(selectedGroupName ,linkDisplayName);	
		String url = this.selectedItem.getURL();
		return url + "?faces-redirect=true";
	}
	
	
	public void setMenuSelection(String groupName , String linkName){
		
		for (MenuGroup menuGroup : menuGroupList) {
				for (MenuItem menuItem : menuGroup.getMenuItemList()) {
					if(menuGroup.getGroupName().equalsIgnoreCase(groupName) 
							&& menuItem.getDisplayName().equalsIgnoreCase(linkName)){
						
						menuItem.setActive(true);
						this.selectedItem = menuItem;
						this.selectedGroupIndex = menuGroupList.indexOf(menuGroup);
					}else{
						menuItem.setActive(false);
					}
				}			
		}
	}
	
	public String getBuildVersion(){
		return this.buildVersion;
	}
	

	public MenuItem getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(MenuItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getSelectedGroupIndex() {
		return selectedGroupIndex;
	}

	public void setSelectedGroupIndex(int selectedGroupIndex) {
		this.selectedGroupIndex = selectedGroupIndex;
	}

	public void handleToggle(ToggleEvent event) {
		this.collapseMenu = !collapseMenu;
	}

	public boolean isCollapseMenu() {
		return collapseMenu;
	}

	public void setCollapseMenu(boolean collapseMenu) {
		this.collapseMenu = collapseMenu;
	}
	
	public List<MenuGroup> getMenuGroupList() {
		return menuGroupList;
	}

	public void setMenuGroupList(List<MenuGroup> menuGroupList) {
		this.menuGroupList = menuGroupList;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public Date getDatabaseDate() {
		return databaseDate;
	}

	public void setDatabaseDate(Date databaseDate) {
		this.databaseDate = databaseDate;
	}

	public String getDbRefreshInfo() {
		return dbRefreshInfo;
	}

	public void setDbRefreshInfo(String dbRefreshInfo) {
		this.dbRefreshInfo = dbRefreshInfo;
	}

}
