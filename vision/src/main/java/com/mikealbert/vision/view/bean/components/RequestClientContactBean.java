package com.mikealbert.vision.view.bean.components;

import java.util.List;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ProcessStageObject;
import com.mikealbert.data.enumeration.OrderToDeliveryProcessStageEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ProcessStageService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;
 
@Component
@Scope("view")
public class RequestClientContactBean extends BaseBean {
	private static final long serialVersionUID = -6315810075917429044L;
	
	private static final String REQUEST_CLIENT_CONTACT_REASON_UI_ID = "ccRequestClientContactReasonTxt";	
	private static final String VIEW_NAME = "addDealerAccessory";
	
	@Resource ProcessStageService processStageService;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private List<ProcessStageObject> processStageObjects;
	private OrderToDeliveryProcessStageEnum processStageEnum;
	private String reason;
	private boolean copyToTarget;
	private String onCloseCallback;		
	private String clientId;
	private String windowTitle;
	
		
	/**
	 * Initializes the bean
	 */
    public void init(){ 
		FacesContext fc;	

    	try {
    		clearEverything();
    		
    		fc = FacesContext.getCurrentInstance(); 
    		
    		setProcessStageObjects(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.processStageObjects}", List.class));    		
    		setProcessStageEnum(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.targetStage}", OrderToDeliveryProcessStageEnum.class));
    		setCopyToTarget(Boolean.parseBoolean(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.copyToTarget}", String.class)));    		
    		setWindowTitle(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.windowTitle}", String.class));
    		setOnCloseCallback(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.onClose}", String.class));
    		setClientId(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.clientId}", String.class));    		
    		  		
    		    		
			//super.setDirtyData(false);

		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
			logger.error(e);
		}    	
      	    	     	
    }

	/**
     * Clears the component's attributes to bring it back to an initial state.
     */
	private void clearEverything() {
		setReason(null);
		//((UIInput)getComponent(REQUEST_CLIENT_CONTACT_REASON_UI_ID)).setValid(true);
	}

		
    /**
     * Actions to perform when dialog is being closed
     */
	public void onHideListener(){			
		
	}
		
	/**
	 * 	Saves the dealer accessory code and assigns it to the trim
	 *  with a no vendor pricing in the process.
	 */
	public void save(){
		RequestContext context;
		
		context = RequestContext.getCurrentInstance();
				
		if(MALUtilities.isEmpty(getReason())){
			super.addErrorMessageSummary(getClientId() + ":" + REQUEST_CLIENT_CONTACT_REASON_UI_ID, "required.field", "Client Contact Reason");			
			RequestContext.getCurrentInstance().addCallbackParam("failure", true);
		}else{
			try {
				for(ProcessStageObject processStageObject : getProcessStageObjects()){
					processStageService.copyToClientFacing(processStageObject, getReason(), this.getLoggedInUser().getEmployeeNo());					
				}
				//addSuccessMessage("custom.message", "Unit is moved to client facing queue");
				context.execute("ccPostSaveBtnHandler()");					
			} catch (Exception e) {
				if(e instanceof MalBusinessException){
					addErrorMessageSummary("custom.message", e.getMessage());
				}else{
					logger.error(e);
					addErrorMessageSummary("custom.message", "Error occured while saving queue info.. ");
				}
				RequestContext.getCurrentInstance().addCallbackParam("failure", true);
			}
		}								
	}
	
	public void saveAndStay(){
		super.addErrorMessage("generic.error", "Save and Stay feature has not been implemeted. Please contact ITS Suuport");
	}
	
	/**
	 * Validates the user input
	 * @return boolean indicating whether the validation succeeded or failed.
	 */
	private boolean isValidInput(){
		boolean isValid = true;				
		return isValid;
	}
	

	public List<ProcessStageObject> getProcessStageObjects() {
		return processStageObjects;
	}

	public void setProcessStageObjects(List<ProcessStageObject> processStageObjects) {
		this.processStageObjects = processStageObjects;
	}

	public OrderToDeliveryProcessStageEnum getProcessStageEnum() {
		return processStageEnum;
	}



	public void setProcessStageEnum(OrderToDeliveryProcessStageEnum processStageEnum) {
		this.processStageEnum = processStageEnum;
	}



	public String getReason() {
		return reason;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}



	public boolean isCopyToTarget() {
		return copyToTarget;
	}



	public void setCopyToTarget(boolean copyToTarget) {
		this.copyToTarget = copyToTarget;
	}	
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public String getOnCloseCallback() {
		return onCloseCallback;
	}

	public void setOnCloseCallback(String onCloseCallback) {
		this.onCloseCallback = onCloseCallback;
	}
		
}
