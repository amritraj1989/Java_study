package com.mikealbert.vision.view.bean.components;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.comparator.UpfitterProgressComparator;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.service.UpfitProgressService;
import com.mikealbert.service.UpfitProgressServiceImpl;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;
import com.mikealbert.vision.view.converter.UpfitterProgressConverter;
 
/**
 * A backing bean supporting the Upfit Progress component. This component will support the business in tracking and capturing the progress of upfits performed by vendors.
 * 
 *
 * <p>Interface Parameters:</p>
 * <p>
 *     <ul>
 *         <li><b>quotationModel</b> - The QuotationModel ({@link QuotationModel)} <b>(Required)</b></li>
 *         <li><b>upfitPOStatuses</b> - List of DocumentStatus ({@link DocumentStatus}) the upfit purchase orders must be in <b>(Required)</b></li>
 *         <li><b>enableBailment</b> - Boolean flag indicated whether to display the bailment control. If set, bailment vendor will be a required input</li>
 *         <li><b>readOnly</b> - Boolean flag indicated whether the dialog should be in readonly mode or not</li> *         
 *         <li><b>enableStartEndDates</b> -Boolean flag indicating whether to enable the Start and End Date controls</li>
 *         <li><b>enableFollowUpDate</b> - Enable or disable the follow up date fields</li>
 *         <li><b>windowTitle</b> - Window title to display in the dialog</li>
 *         <li><b>onClose</b> - Client callback method to invoke when the dialog closes</li>
 *     </ul>
 * </p>
 *
 * See also: <br />
 * <pre>
 * {@link QuotationModel} <br />
 * {@link DocumentStatus} <br />
 * {@link UpfitProgressService} <br />
 * {@link UpfitProgressServiceImpl} <br />
 * {@link UpfitterProgress} <br />
 * {@link UpfitterProgressConverter} <br />
 * {@link UpfitterProgressComparator} <br />
 * </pre>
 * 
**/
@Component
@Scope("view")
public class UpfitProgressBean extends BaseBean {
	private static final long serialVersionUID = -2054166518363122920L;
	
	@Resource UpfitProgressService upfitProgressService;
	
	private static final String WINDOW_TITLE = " Unit {0} ";
	private static final String UI_ID_BAILMENT_VENDOR_MENU = "ccUpfitProgress:ccBailmentVendorMnu";
	private int	UP = -1;
	private int	DOWN = 1;

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private String onCloseCallback;		
	private String clientId;
	private String windowTitle;
	private QuotationModel quotationModel;
	private List<UpfitterProgress> upfitterProgressList;
	private UpfitterProgress bailmentUpfitterProgress;
	private List<DocumentStatus> upfitPOStatuses;
	private Boolean enableBailment;
	private boolean readOnly;
	private Boolean enableStartEndDates;
	private Boolean saved;
	private Long mainPoDocId;
	private Long mdlId;
	private boolean refreshNeeded;
	
		
		
	/**
	 * Initializes the bean
	 */
    @SuppressWarnings("unchecked")
	public void init(){ 
		FacesContext fc;	


    	try {
    		clearEverything();
    		setRefreshNeeded(false);
    		fc = FacesContext.getCurrentInstance();     		
    		setClientId(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.clientId}", String.class));
    		setQuotationModel(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.quotationModel}", QuotationModel.class));  
    		setMainPoDocId(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.mainPODocId}", Long.class));
    		setUpfitPOStatuses(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.upfitPOStatuses}", List.class));
    		setEnableBailment(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.enableBailment}", Boolean.class));
    		setReadOnly(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.readOnly}", Boolean.class));    		
    		setEnableStartEndDates(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.enableStartEndDates}", Boolean.class));    		
    		setWindowTitle(fc.getApplication().evaluateExpressionGet(fc, "#{cc.attrs.windowTitle}", String.class));
    		setMdlId(getQuotationModel().getModel().getModelId());
    		setUpfitterProgressList(upfitProgressService.generateUpfitProgressList(getQuotationModel().getQmdId(), getQuotationModel().getUnitNo(), getUpfitPOStatuses(), getMainPoDocId()) );
    		setWindowTitle(getWindowTitle() + MessageFormat.format(WINDOW_TITLE, getQuotationModel().getUnitNo()));
    		
    		//Set the bailment upfitter
  		    for(UpfitterProgress ufp : getUpfitterProgressList()){
  		    	if(MALUtilities.convertYNToBoolean(ufp.getBailmentYN()))
  		    		setBailmentUpfitterProgress(ufp);
  		    }

		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
			logger.error(e, "In UpfitProgressBean.init()"); 
		}    	
    }

	/**
     * Clears the component's attributes to bring it back to an initial state.
     * This is necessary to update/clear the UI
     */
	public void clearEverything() {
		((UIInput)getComponent(UI_ID_BAILMENT_VENDOR_MENU)).setValid(true);
		setEnableBailment(false);
		setDirtyData(false);
		setUpfitterProgressList(null);
		setWindowTitle(null);
		setUpfitPOStatuses(null);
		setQuotationModel(null);
		setOnCloseCallback(null);
		setBailmentUpfitterProgress(null);
		setReadOnly(false);
		setSaved(false);
	}

		
    /**
     * Actions to perform when dialog is being closed
     */
	public void onHideListener(){
		
		if(getSaved()){	
			RequestContext.getCurrentInstance().addCallbackParam("success", "Y");
		}		
		
		clearEverything();			
	}
		
	/**
	 * 	Saves changes made
	 */
	public void save(){
		List<UpfitterProgress> savedUpfitterProgressesList;
		RequestContext context;
		
		context = RequestContext.getCurrentInstance();
		
		try {
			if(validInput()){
			    savedUpfitterProgressesList = upfitProgressService.saveOrUpdateUpfitterProgress(getUpfitterProgressList(), getLoggedInUser().getEmployeeNo(), getQuotationModel().getQmdId(), getQuotationModel().getUnitNo(), getMainPoDocId(), getMdlId());
				clearEverything();
			    setSaved(true);
			    setRefreshNeeded(true);
			} else {
				context.addCallbackParam("failure", true);
				setSaved(false);
			}
			
		} catch(Exception e) {	
			setSaved(false);
			logger.error(e,"qmdId = " + (MALUtilities.isEmpty(getQuotationModel()) ? "n/a" : getQuotationModel().getQmdId())); 			
			super.addErrorMessageSummary("generic.error", e.getMessage());
			context.addCallbackParam("failure", true);			
		} 
									
	}
	
	private boolean validInput(){
		boolean isValid = true;		
		
		if(getEnableBailment() && MALUtilities.isEmpty(getBailmentUpfitterProgress())){
			super.addErrorMessageSummary(UI_ID_BAILMENT_VENDOR_MENU, "custom.message", "Bailment Vendor is required");	
			isValid = false;				
		}
		
		for(UpfitterProgress ufp : getUpfitterProgressList()) {
			if(!MALUtilities.isEmpty(ufp.getEndDate()) && MALUtilities.isEmpty(ufp.getStartDate())) {
				super.addErrorMessageSummary("custom.message", MessageFormat.format("Vendor {0} must have a start date", ufp.getUpfitter().getAccountName()));
				isValid = false;					
			}
		}
		
		return isValid;
	}
	
	public void saveAndStay(){
		super.addErrorMessage("generic.error", "Save and Stay feature has not been implemeted. Please contact ITS Suuport");
	}
	
	/**
	 * Resquences the selected item and it's chidlren based on the UP/DOWN order
	 * the user selected.
	 * @param selectedRowIndex the selected item
	 * @param direction -1 or 1 to indicated the direction to move the item
	 */
	public void reSequenceRow(int selectedRowIndex, int direction) {
		Long selectedRowOldSeqNo = null;
		Long neighboringRowOldSeqNo = null;
		UpfitterProgress selectedUpfitterProgress = null;
		UpfitterProgress neighboringUpfitterProgress = null;
		
		selectedUpfitterProgress = getUpfitterProgressList().get(selectedRowIndex);	
		selectedRowOldSeqNo = selectedUpfitterProgress.getSequenceNo();
		
		//Finding neighbor item, which could be previous or next based reseq direction
    	for(UpfitterProgress ufp : getUpfitterProgressList()){
    		if(ufp.getSequenceNo().equals(selectedRowOldSeqNo + direction)) {
    			neighboringUpfitterProgress = ufp;
    			neighboringRowOldSeqNo = neighboringUpfitterProgress.getSequenceNo();
    			break;
    		}    		
    	}		
		
		//Flagging the selected item and is children 
    	for(UpfitterProgress ufp : getUpfitterProgressList()){
    		if(ufp.getSequenceNo().equals(selectedRowOldSeqNo))
    			ufp.setSequenceNo(-1L);
    	}
    	
    	//Updating the sequence of the neighboring item and it's children
    	for(UpfitterProgress ufp : getUpfitterProgressList()){
    		if(ufp.getSequenceNo().equals(neighboringRowOldSeqNo))
    			ufp.setSequenceNo(selectedRowOldSeqNo);
    	}    	
		
    	//Updating the sequence of the selected flagged item and it's children
    	for(UpfitterProgress ufp : getUpfitterProgressList()){
    		if(ufp.getSequenceNo().equals(-1L))
    			ufp.setSequenceNo(selectedRowOldSeqNo + direction);
    	}    	

		Collections.sort(getUpfitterProgressList(), new UpfitterProgressComparator());	
	}
	
	
	/**
	 * Creates a list of available parent UpfitterProgress items that a given UpfitterProgress can be linked to
	 * @param upfitterProgress The selected item
	 * @return List containing the upfitter progress that the selected item can be linked to
	 */
	public List<UpfitterProgress> linkedToList(UpfitterProgress upfitterProgress){
		List<UpfitterProgress> availableUpfitterProgressList = new ArrayList<UpfitterProgress>();
		
		if(MALUtilities.isEmpty(upfitterProgress.getChildrenUpfitterProgress()) || upfitterProgress.getChildrenUpfitterProgress().size() == 0) {
			if(MALUtilities.isEmpty(upfitterProgress.getStartDate())) {
				for(UpfitterProgress candidate : getUpfitterProgressList()) {
					if(!upfitterProgress.equals(candidate)) {			 
						if(MALUtilities.isEmpty(candidate.getParentUpfitterProgress())) {
							if( (!MALUtilities.isEmpty(candidate.getEndDate()) && upfitterProgress.getSequenceNo().equals(candidate.getSequenceNo())) 
									|| MALUtilities.isEmpty(candidate.getEndDate()) ) {
								availableUpfitterProgressList.add(candidate);
							}
						}
					}	
				}
			}
		}
		
		return availableUpfitterProgressList;
	}
	
	/**
	 * Handles the event where the user selects an item from the linked to list.
	 * The upfitter progress will be resequenced just right under the 
	 * linked to item. However, if no linked to item was selected, the upfitter progress
	 * will be resequenced to the end of the list.
	 * @param selectedRowIndex Selected item
	 */
	public void linkedToListener(int selectedRowIndex) {
		UpfitterProgress upfitterProgress;
		int startResequenceIndex;
		int parentRowIndex;
		long rootSequenceNo;
		
		upfitterProgress = getUpfitterProgressList().get(selectedRowIndex);
		
		if(MALUtilities.isEmpty(upfitterProgress.getParentUpfitterProgress())) {			
			getUpfitterProgressList().remove(selectedRowIndex);
			getUpfitterProgressList().add(getUpfitterProgressList().size(), upfitterProgress);
			//Removes the selected upfitter progress from the parent's children list			
			for(UpfitterProgress ufp : getUpfitterProgressList()) {
				if(!MALUtilities.isEmpty(ufp.getChildrenUpfitterProgress())){
					ufp.getChildrenUpfitterProgress().remove(upfitterProgress);
				}
			}
		} else {
			//Adds the selected upfitter progress to the parent's children list
			UpfitterProgress parentUfp = getUpfitterProgressList().get(getUpfitterProgressList().indexOf(upfitterProgress.getParentUpfitterProgress())); 
			if(MALUtilities.isEmpty(parentUfp.getChildrenUpfitterProgress())) {
				parentUfp.setChildrenUpfitterProgress(new ArrayList<UpfitterProgress>());
			}
			parentUfp.getChildrenUpfitterProgress().add(upfitterProgress);
			getUpfitterProgressList().remove(selectedRowIndex);				
			parentRowIndex = getUpfitterProgressList().indexOf(upfitterProgress.getParentUpfitterProgress());		
			startResequenceIndex = parentRowIndex + 1;						
			getUpfitterProgressList().add(startResequenceIndex, upfitterProgress);		
			
		} 	

		rootSequenceNo = 0;
		for(int i = 0; i < getUpfitterProgressList().size(); i++) {
			if(MALUtilities.isEmpty(getUpfitterProgressList().get(i).getParentUpfitterProgress())){
				getUpfitterProgressList().get(i).setSequenceNo(++rootSequenceNo);
			} else {
				getUpfitterProgressList().get(i).setSequenceNo(rootSequenceNo);				
			}
		}		

		Collections.sort(getUpfitterProgressList(), new UpfitterProgressComparator());

	}
	
	/**
	 * Handles the event where the user selects a bailment vendor.
	 * The selected item will be stored as a property.
	 * @param rowIndex
	 */
	public void bailmentUpfitterListener(){
		for(UpfitterProgress ufp : getUpfitterProgressList()){
			ufp.setBailmentYN("N");
		}
		
		if(!MALUtilities.isEmpty(getBailmentUpfitterProgress())) {
			getBailmentUpfitterProgress().setBailmentYN("Y");
		}
	}
	
	/**
	 * Determines wheather the selected upfitter progress is a child or not.
	 * @param rowIndex index of item in list
	 * @return boolean
	 */
	public boolean isChild(int rowIndex) {
		boolean isChild;
		
		if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getParentUpfitterProgress())){
			isChild = false;
		} else {
			isChild = true;			
		}
		
		return isChild;
	}
	
	/**
	 * Determines whether or not the upfitter progress can be moved UP the list
	 * @param rowIndex index of item in list
	 * @return boolean
	 */
	public boolean canGoUp(int rowIndex) {
		boolean canGoUp = false;
		
		if(rowIndex != 0 && !isChild(rowIndex) && !isReadOnly()){
			if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getParentUpfitterProgress())) {
				if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getStartDate())) {
					canGoUp = true;
				} 
			} else {
				if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getParentUpfitterProgress().getStartDate())) {
					canGoUp = true;
				}
			}
		}
		
		return canGoUp;
	}
	
	/**
	 * Determines whether or not the upfitter progress can be moved DOWN the list
	 * @param rowIndex index of item in list
	 * @return boolean
	 */
	public boolean canGoDown(int rowIndex) {
		boolean canGoDown = false;
		Long maxSequenceNo;
		
		if(rowIndex != getUpfitterProgressList().size() - 1 
				&& !isChild(rowIndex) 
				&& MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getStartDate())
				&& !isReadOnly()){
			maxSequenceNo = getUpfitterProgressList().get(getUpfitterProgressList().size() - 1).getSequenceNo();
			canGoDown = getUpfitterProgressList().get(rowIndex).getSequenceNo().compareTo(maxSequenceNo) < 0 ? true : false; 
		}
		
		return canGoDown;
	}	
	
	/**
	 * Determines whether the Start Date can be enable or not
	 * @param rowIndex index of item in list
	 * @return boolean
	 */
	public boolean enableStartDate(int rowIndex) {
		boolean enable = false;
		
		if(getEnableStartEndDates() && !isReadOnly()) {
			if(rowIndex == 0)  {
				if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getPersistedStartDate())){
					enable = true;
				}
			}

			if(rowIndex != 0 
					&& !isChild(rowIndex)
					&& MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getPersistedStartDate()) ) {

				if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getParentUpfitterProgress())) {
					if(!MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getPersistedEndDate())) {
						enable = true;
					} 
				} else {
					if(!MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex - 1).getParentUpfitterProgress().getPersistedEndDate())) {
						enable = true;
					}
				}
			}
		}
		
		return enable;
	}
	
	/**
	 * Determines whether the End Date can be enable or not
	 * @param rowIndex index of item in list
	 * @return boolean
	 */
	public boolean enableEndDate(int rowIndex) {
		boolean enable = false;
		//not empty upfitProgress.persistedEndDate or not upfitProgressBean.enableStartEndDates
		if(getEnableStartEndDates() && !isReadOnly()) {
			if(MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getPersistedEndDate())
					&& !MALUtilities.isEmpty(getUpfitterProgressList().get(rowIndex).getPersistedStartDate())) {
				enable = true;
			}				
		}
		
		return enable;		
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

	public List<UpfitterProgress> getUpfitterProgressList() {
		return upfitterProgressList;
	}

	public void setUpfitterProgressList(List<UpfitterProgress> upfitterProgressList) {
		this.upfitterProgressList = upfitterProgressList;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public int getUP() {
		return UP;
	}

	public void setUP(int uP) {
		UP = uP;
	}

	public int getDOWN() {
		return DOWN;
	}

	public void setDOWN(int dOWN) {
		DOWN = dOWN;
	}

	public UpfitterProgress getBailmentUpfitterProgress() {
		return bailmentUpfitterProgress;
	}

	public void setBailmentUpfitterProgress(UpfitterProgress bailmentUpfitterProgress) {
		this.bailmentUpfitterProgress = bailmentUpfitterProgress;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public List<DocumentStatus> getUpfitPOStatuses() {
		return upfitPOStatuses;
	}

	public void setUpfitPOStatuses(List<DocumentStatus> upfitPOStatuses) {
		this.upfitPOStatuses = upfitPOStatuses;
	}

	public Boolean getEnableBailment() {
		return enableBailment;
	}

	public void setEnableBailment(Boolean enableBailment) {
		this.enableBailment = enableBailment;
	}

	public Boolean getEnableStartEndDates() {
		return enableStartEndDates;
	}

	public void setEnableStartEndDates(Boolean enableStartEndDates) {
		this.enableStartEndDates = enableStartEndDates;
	}

	public Boolean getSaved() {
		return saved;
	}

	public void setSaved(Boolean saved) {
		this.saved = saved;
	}

	public Long getMainPoDocId() {
		return mainPoDocId;
	}

	public void setMainPoDocId(Long mainPoDocId) {
		this.mainPoDocId = mainPoDocId;
	}

	public Long getMdlId() {
		return mdlId;
	}

	public void setMdlId(Long mdlId) {
		this.mdlId = mdlId;
	}

	public boolean isRefreshNeeded() {
		return refreshNeeded;
	}

	public void setRefreshNeeded(boolean refreshNeeded) {
		this.refreshNeeded = refreshNeeded;
	}

}
