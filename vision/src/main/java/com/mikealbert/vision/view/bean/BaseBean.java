package com.mikealbert.vision.view.bean;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.exception.NestableException;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.hibernate.StaleObjectStateException;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.orm.hibernate5.HibernateSystemException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.entity.User;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.util.FaceResolver;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;
import com.mikealbert.view.DataTable;
import com.mikealbert.vision.view.ViewConstants;

public abstract class BaseBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private DataTable	dataTable;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	private boolean dirtyData = false;
	
	
	@Resource protected LookupCacheService lookupCacheService;
	@Resource protected MalMessage talMessage;
	
	public final String	dbActionInsert = "INSERT";
	public final String	dbActionUpdate = "UPDATE";
	public final String	dbActionDelete = "DELETE";
	
	

	protected void handleException(String messageKey, String[] messageParam,
			Exception ex, String methodName) {
		
		
		if (ex instanceof HibernateSystemException) {
			if (ex.getCause() != null) {
				if (ex.getCause() instanceof PessimisticEntityLockException) {
					addErrorMessage("record.locked.by.another.user");
				}
			}
		
			
		} else if (ex instanceof HibernateOptimisticLockingFailureException) {
			addErrorMessage("record.updated.by.another.user");
		} else if (ex instanceof StaleObjectStateException) {
			addErrorMessage("record.updated.by.another.user");
		} else if(ex instanceof JpaObjectRetrievalFailureException){
			addErrorMessage("record.updated.by.another.user");
		} else if (ex instanceof NestableException) {
			if (ex.getCause() != null) {
				if (ex.getCause().getCause() instanceof StaleObjectStateException) {
					addErrorMessage("record.updated.by.another.user");
				} else if (ex.getCause().getCause() instanceof HibernateOptimisticLockingFailureException) {
					addErrorMessage("record.updated.by.another.user");
				}
			}
		} else if (ex instanceof NestableRuntimeException) {
			if (ex.getCause() != null) {
				if (ex.getCause().getCause() instanceof StaleObjectStateException) {
					addErrorMessage("record.updated.by.another.user");
				} else if (ex.getCause().getCause() instanceof HibernateOptimisticLockingFailureException) {
					addErrorMessage("record.updated.by.another.user");
				}
			}
		} else if (ex instanceof MalException) {
			MalException malException = (MalException) ex;
			// here we are showing msg in place of actual error msg
			// we can also show actual message by getMessage()
			addSimplErrorMessage(malException.getMessage());
		} else if(ex instanceof MalBusinessException){
			addSimplErrorMessage(ex.getMessage());
		}
		else {
			addErrorMessage(messageKey, messageParam);			
		}
		
		logger.debug(ex.getMessage());
		
	}
	

    public User getLoggedInUser(){
    	
    	User user = null;
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof User)		{
			user = ((User)authentication.getPrincipal());
		}
		return user;
    }
	
    
    public String getLoggedInUserName(){
    	
    	String userName = "";
    	User user = getLoggedInUser();    	
		if(user != null){
			userName = user.getUsername();
		}
		
		return userName;
    }
	
	protected boolean validateRequireField(String fieldName, Object fieldValue) {

		boolean success = true;

		if (fieldValue instanceof String) {

			String strValue = (String) fieldValue;
			if ((strValue == null || strValue.trim().length() == 0)) {
				getFacesContext().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", talMessage.getMessage("field.required", fieldName)));
				success = false;
			}
		} else if (fieldValue instanceof Long) {
			Long longValue = (Long) fieldValue;
			if ((longValue == null || longValue == 0)) {
				getFacesContext().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", talMessage.getMessage("field.required", fieldName)));
				success = false;
			}
		}

		return success;
	}

	protected boolean validateFieldLength(String fieldName, Object fieldValue, int maxLength) {
		boolean success = true;
		success = validateRequireField(fieldName, fieldValue);
		if (success && fieldValue instanceof String) {

			String strValue = (String) fieldValue;
			if ((strValue == null || strValue.length() > maxLength)) {
				getFacesContext().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", talMessage.getMessage("field.maxlength", fieldName,
								String.valueOf(maxLength))));
				success = false;
			}
		}

		return success;
	}
	
	protected boolean validateFieldLength(String fieldName, Object fieldValue, int maxLength,boolean required) {
		boolean success = true;
		if(required){
			success = validateRequireField(fieldName, fieldValue);
		}
		if (success && fieldValue instanceof String) {

			String strValue = (String) fieldValue;
			if ((strValue == null || strValue.length() > maxLength)) {
				getFacesContext().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error : ", talMessage.getMessage("field.maxlength", fieldName,
								String.valueOf(maxLength))));
				success = false;
			}
		}

		return success;
	}
	

	protected void addInfoMessage(String key) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, talMessage.getMessage(key)));
	}
	
	protected void addInfoMessage(String key, String arg1) {
		addSuccessMessage(key, new String[] { arg1 });
	}

	protected void addInfoMessage(String key, String arg1, String arg2) {
		addSuccessMessage(key, new String[] { arg1, arg2 });
	}

	protected void addInfoMessage(String key, String[] args) {
		addSuccessMessage(key, args);
	}

	protected void addSuccessMessage(String key, String arg1) {
		addSuccessMessage(key, new String[] { arg1 });
	}

	protected void addSuccessMessage(String key, String arg1, String arg2) {
		addSuccessMessage(key, new String[] { arg1, arg2 });
	}

	protected void addSuccessMessage(String key, String[] args) {
		String newMessage = talMessage.getMessage(key, args);
		boolean isMessageExits = false;
		Iterator<FacesMessage> msgList	=  getFacesContext().getMessages();
		if(msgList != null){
			while(msgList.hasNext()){
				FacesMessage	msg = msgList.next();
				if(msg.getDetail().equals(newMessage)){
					isMessageExits = true;
					break;
				}
			}
		}
		
		if(!isMessageExits){
			getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, talMessage.getMessage(key, args)));
		}
		
	}

	protected void addWarnMessage(String key, String arg1) {
		addWarnMessage(key, new String[] { arg1 });
	}

	protected void addWarnMessage(String key, String[] args) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, null, talMessage.getMessage(key, args)));

	}

	protected void addErrorMessage(String key) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, talMessage.getMessage(key)));
	}
	
	protected void addErrorMessageSummary(String key) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, talMessage.getMessage(key), ""));
	}
	
	protected void addErrorMessageSummary(String key, String arg1) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, talMessage.getMessage(key,arg1), ""));
	}
	
	protected void addErrorMessageSummary(String htmlId,String key, String arg1) {
		getFacesContext().addMessage(htmlId, new FacesMessage(FacesMessage.SEVERITY_ERROR, talMessage.getMessage(key,arg1), ""));		
		UIComponent comp = getComponent(htmlId);
   	 	if(comp!= null) ((UIInput) comp).setValid(false); 
	}

	protected void addErrorMessageSummary(String key, String[] arg1) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, talMessage.getMessage(key,arg1), ""));
	}
	
	protected void addSimplErrorSummaryMessage(String planeMessage) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, planeMessage, ""));
	}
	
	protected void addInfoMessageSummary(String key) {		
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, talMessage.getMessage(key), ""));
	}	
	protected void addInfoMessageSummary(String key, String arg1) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, talMessage.getMessage(key,arg1), ""));
	}	

	protected void addInfoMessageSummary(String key, String[] arg1) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, talMessage.getMessage(key,arg1), ""));
	}
	
	protected void addErrorMessage(String key, String arg1) {
		addErrorMessage(key, new String[] { arg1 });
	}
	protected void addErrorMessage(String htmlId, String key, String arg1) {
		addErrorMessage(htmlId, key, new String[] { arg1 });
	}

	protected void addErrorMessage(String key, String[] args) {
		addErrorMessage(null, key, args);
	}
	
	protected void addErrorMessage(String htmlId, String key, String[] args) {
		getFacesContext().addMessage(htmlId, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, talMessage.getMessage(key, args)));
		if(htmlId != null){
			UIComponent comp = getComponent(htmlId);
	   	 	if(comp!= null) ((UIInput) comp).setValid(false); 
		}
	}
	protected void addSimplErrorMessage(String planeMessage) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null, planeMessage));
	}

	protected void addSimpleMessage(String planeMessage) {
		getFacesContext().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null, planeMessage));
	}
	
	protected FacesContext getFacesContext() {
		FaceResolver faceResolver = (FaceResolver) SpringAppContext.getBean(FaceResolver.class);
		return faceResolver.getFaceContext();
	}

	protected ExternalContext getFaceExternalContext() {
		return getFacesContext().getExternalContext();
	}

	protected String getRequestParameter(String key) {
		return (String) getFaceExternalContext().getRequestParameterMap().get(key);
	}

	protected Map<String, Object> getRequestScopeMap() {
		return getFaceExternalContext().getRequestMap();
	}
	
	protected Map<String, Object>  getFlashScopeMap() {
		return getFaceExternalContext().getFlash();
	}
	
	protected Map<String, Object>  getSessionScopeMap() {
		return getFaceExternalContext().getSessionMap();
	}
	
	protected Map<String, Object>  getViewScopeMap() {
		return getFacesContext().getViewRoot().getViewMap();
	}

	protected void redirectToURL(String url) {
		ExternalContext externalContext = getFaceExternalContext();
		try {
			externalContext.redirect(externalContext.getRequestContextPath() + url + "?faces-redirect=true");
		} catch (Exception e) {
			logger.error(e);
			//TODO: check error handling / navigation
		}
	}
	protected void forwardToURL(String url) {
		FacesContext facesContext =  getFacesContext();
		
		NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
		navigationHandler.handleNavigation(facesContext, null, url);
	}
	
	protected UIComponent getComponent(String componentId) {
		FacesContext context = getFacesContext();
	    UIViewRoot root = context.getViewRoot();
	    UIComponent x = firstWithContainerClientId(componentId, root, context);

	    return x;
	  }
	
	/** Our previous implementation used our own findComponent implementation. 
	 *  This implementation new uses a utility method that was "borrowed" firstWithId from Primefaces GITHUB Repo
	 *  and slightly modified to match our odd needs
	 *  @see src/main/java/org/primefaces/util/ComponentTraversalUtils.java 
	 *  This implementation /vs our own findComponent runs within milliseconds /vs typically over 1 second
	 *  to do the same thing.
	 */
    private UIComponent firstWithContainerClientId(String id, UIComponent base, FacesContext context) {
        if (id.equals(base.getId())) {
            return base;
        }

        UIComponent result = null;

        Iterator<UIComponent> kids = base.getFacetsAndChildren();
        while (kids.hasNext() && (result == null)) {
            UIComponent kid = (UIComponent) kids.next();
            if (id.equals(kid.getContainerClientId(context))) {
                result = kid;
                break;
            }
            result = firstWithContainerClientId(id, kid, context);
            if (result != null) {
                break;
            }
        }
        return result;
    }
    
	/**
	 * @param heightDeduction  This is pixel  size which should be allocate to page except data table.   
	 * @param minWidth    Minimum width of data table 
	 * @param cols  % width of each column.
	 */
	
	protected DataTable initializeDataTable(int heightDeduction, int minWidth , int[] cols) {
		
		int resolutionHeight= ((Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_HEIGHT) != null)? (Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_HEIGHT) : 800;
		int resolutionWidth= ((Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_WIDTH) != null)? (Integer)getSessionScopeMap().get(ViewConstants.SCREEN_RESOLUTION_WIDTH) : 1200;		
		int leftMenuWidth =  (Integer)ViewConstants.LEFT_MENU_PANEL_WIDTH;
		this.dataTable = getDataTable();
		this.dataTable.initialize(resolutionHeight,minWidth ,resolutionWidth, heightDeduction, leftMenuWidth,cols);	
		
		return this.dataTable;
	}
	//make public to access from UI
	public DataTable getDataTable() {
		if(dataTable == null){
			dataTable = new DataTable();
		}
		return dataTable;
	}
	protected boolean isNotNull(String str){		
		return MALUtilities.isNotEmptyString(str);
	}
	protected boolean isNull(String str){		
		return MALUtilities.isEmptyString(str);
	}
	/**
	 * Core logic for determining whether the user has a role
	 * that can access a given resource. If the user has a role
	 * that has been assigned to a resource, then the user has
	 * permission to access it. Otherwise, access is denied.
	 * 
	 * Note: The readonly role trumps all roles. When detected,
	 * access to the resource is denied.
	 * 
	 * @param resourceId The page or element id
	 * @return boolean Indicating whether user can access the resource
	 */
	protected boolean determineResourceAccess(String resourceId){
		boolean permitted = false;
		boolean readonly = false;
		User user = this.getLoggedInUser();			
		Map<String, List<String>> resourceMap = null;
		List<String> pageRoles;
		
		//Readonly role trumps all other roles
		if(!MALUtilities.isEmpty(user)){
			for(GrantedAuthority ga : user.getAuthorities()){
				if(ga.getAuthority().equals(ViewConstants.USER_READ_ONLY_ROLE))
					readonly = true;			
			}
		}else{
			// if we are an unauthenticated user (no user object) then we don't really have permissions
			// so default back to "READONLY" access
			readonly = true;
		}
					
		//If not readonly, then check the user's roles.
		//Otherwise, the user does not have permission
		if(!readonly) {
			resourceMap = lookupCacheService.getResourceRoleMap();
			pageRoles = resourceMap.get(resourceId);	
			
			for(GrantedAuthority ga : user.getAuthorities()){
				if(pageRoles != null && pageRoles.contains(ga.getAuthority())){
					permitted = true;
					break;
				}
			}
			 
		} else {
			permitted = false;
		}
		 
		return permitted;		
	}
	
	/**
	 * variant 2: (second) simple argument permits the supply 
	 * of a target page to determine user's access to that page
	 * @param resourceId
	 * @return
	 */	
	public boolean hasPermission(String resourceId) {
		return determineResourceAccess(resourceId);	
	}

	public boolean isDirtyData() {
		return dirtyData;
	}

	public void setDirtyData(boolean dirtyData) {
		this.dirtyData = dirtyData;
	}	

	/**
	 * Method to check for ReadOnly access for current user irrespective of any resource
	 * 
	 */
	public boolean isReadOnlyAccess() {
		boolean readonly = false;
		User user = this.getLoggedInUser();
		if(!MALUtilities.isEmpty(user)){
			for(GrantedAuthority ga : user.getAuthorities()){
				if(ga.getAuthority().equals(ViewConstants.USER_READ_ONLY_ROLE))
					readonly = true;			
			}
		}
		
		return readonly;
	}
	
}
