package com.mikealbert.vision.view.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.primefaces.model.menu.DefaultMenuItem;


import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.view.ClientState;
import com.mikealbert.vision.view.ViewConstants;


public abstract class StatefulBaseBean extends BaseBean  implements Serializable{

	private static final long serialVersionUID = 1L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	protected List<ClientState> pageList;
	protected ClientState thisPage;
	protected String returnURL;	


	public MenuModel getBreadCrumbTrail() { 
		MenuModel menuModel;
		menuModel = new DefaultMenuModel();
		
		DefaultMenuItem item = new DefaultMenuItem("dashboard");
		item.setId("0");
		item.setAjax(false);
		item.setCommand("#{" + this.getClass().getSimpleName().substring(0, 1).toLowerCase() + this.getClass().getSimpleName().substring(1) + ".processAction(" + 0 + ",'/view/dashboard')}");
		menuModel.addElement(item);
		
		if (pageList != null) {
			for(int i=1; i <= pageList.size(); i++){
				item = new DefaultMenuItem(pageList.get(i-1).getPageDisplayName());
				item.setId(String.valueOf(i));
				item.setAjax(false);
				//TODO: put this in MalUtilities?!
				item.setCommand("#{" + this.getClass().getSimpleName().substring(0, 1).toLowerCase() + this.getClass().getSimpleName().substring(1) + ".processAction(" + i + ",'"+ pageList.get(i-1).getPageUrl() +"')}");
				menuModel.addElement(item);
				logger.info("added menu option: " + pageList.get(i-1).getPageDisplayName());
			}
		}

		return menuModel; 
	}

	public ClientState getPageClientState(String pageDisplayName) { 
		
		ClientState clientState = null;
		
		for (int i = pageList.size() -1 ; i >= 0 ; i--) {			
			if(pageList.get(i).getPageDisplayName().equals(pageDisplayName)){
				 clientState = pageList.get(i) ;
				 break;
			}
		}
		return clientState;
	}
	
	/**
	 * TODO: Determine whether we need to append the file extension to the page id
	 * 
	 * Retrieves the page Id from the page's URL. The page Id at the time
	 * of writing is the name of the view.
	 * @return String The page Id, which is the name of the view
	 */
	public String getPageId(){
		String[] splitURL = this.thisPage.getPageUrl().split("/");
		String pageId = "";
		
		if(splitURL.length > 0) {
			pageId = splitURL[splitURL.length - 1];
		}

        return pageId;
	}
	
	@SuppressWarnings("unchecked")
	protected void openPage() {
		
		pageList = (List<ClientState>) getRequestScopeMap().get(ViewConstants.PAGE_LIST);
		if(pageList == null) {
			pageList = new LinkedList<ClientState>();
		}		
		if(pageList.isEmpty()) {
			thisPage = new ClientState();
			pageList.add(thisPage);
		}
		else {
			thisPage = pageList.get(pageList.size()-1);
		}

		if(thisPage.getRestoreStateValues().isEmpty()){
			loadNewPage();
		}
		else {
			restoreOldPage();
		}

		if (pageList.size() > 1) {
			returnURL = (String) pageList.get(pageList.size()-2).getPageUrl();			
		}
		else {
			returnURL = ViewConstants.DASHBOARD_PAGE;
		}

	}	
	
	protected String cancelPage() {
		pageList.remove(pageList.size()-1);
		getRequestScopeMap().put(ViewConstants.PAGE_LIST, pageList);
		return returnURL;
	}

	protected void saveRestoreStateValues(Map<String, Object> map) {
		pageList.get(pageList.size()-1).getRestoreStateValues().putAll(map);		
	}
	
	protected void savePreviousPageRestoreStateValues(Map<String, Object> map) {
		if(pageList.size() > 1) {
			pageList.get(pageList.size()-2).getRestoreStateValues().putAll(map);
		}
	}

	protected void saveNextPageInitStateValues(Map<String, Object> map) {
		ClientState nextPage = new ClientState();
		nextPage.getInputValues().putAll(map);
		pageList.add(nextPage);
		getRequestScopeMap().put(ViewConstants.PAGE_LIST, pageList);
	}
	protected abstract void  loadNewPage();
	
	protected abstract void restoreOldPage();
	
	public void processAction(int id, String url) throws AbortProcessingException {
		int index = id;
		
		if ((index >= 0) && (index <= (pageList.size() - 1))) {
			int start = index;
			int end = pageList.size();
			pageList.subList(start, end).clear();
		}
		getRequestScopeMap().put(ViewConstants.PAGE_LIST, pageList);	
		forwardToURL(url);

	}
	
	
    /**
     * variant 1: (first/most critical) no argument looks at the 
     * this.pageIdentifier and logged in user's authorities to 
     * determine same page access	
     * @return
     */	
	public boolean hasPermission() {
		return determineResourceAccess(getPageId());
	}

}
