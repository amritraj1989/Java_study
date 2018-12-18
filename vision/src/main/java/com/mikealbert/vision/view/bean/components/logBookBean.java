package com.mikealbert.vision.view.bean.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.comparator.LogBookEntryDateComparator;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LogBook;
import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.ObjectLogBook;
import com.mikealbert.data.enumeration.LogBookTypeEnum;
import com.mikealbert.data.vo.LogBookVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.LogBookServiceImpl;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;
import com.mikealbert.vision.vo.LogBookTypeVO;

/**
 * A backing bean supporting the Log Book component.
 *
 * <p>Interface Parameters:</p>
 * <p>
 *     <ul>
 *         <li><b>Entity</b> - The object in which the Log Book is attached <b>(Required)</b></li>
 *         <li><b>logBookTypes</b> - List of Log Book Types ({@link LogBookTypeVO}) to display for the entity <b>(Required)</b></li>
 *         <li><b>update</b> - Id of the element on the calling page to update when the log book dialog closes</li>
 *         <li><b>addNoteOnComplete</b> - Callback method to invoke when a note has been added</li>
 *         <li><b>enableFollowUpDate</b> - Enable or disable the follow up date fields</li>
 *         <li><b>enableAddAndClose</b> - When set to true, the dialog will close each time a note has been added to a log book. Otherwise, the dialog remains open.</li>
 *         <li><b>onClose</b> - Callback method to invoke when the dialog closes</li>
 *         <li><b>combineLogBooks</b> - When set to true, the notes of related Log Books are combined with the Log Book Type. This feature does not work unless a log book has related log books </li>
 *     </ul>
 * </p>
 *
 * See also: <br />
 * <pre>
 * {@link LogBook} <br />
 * {@link ObjectLogBook} <br />
 * {@link LogBookEntry} <br />
 * {@link LogBookService} <br />
 * {@link LogBookServiceImpl} <br />
 * {@link LogBookTypeEnum} <br />
 * {@link LogBookTypeVO} <br />
 * </pre>
 * 
**/
@Component
@Scope("view")
public class logBookBean extends BaseBean {
	private static final long serialVersionUID = 3681086558632910718L;

	@Resource LogBookService logBookService;
	@Resource MaintenanceRequestService maintenanceRequestService;
	@Resource FleetMasterService fleetMasterService;
	
	static final String TAB_NAME_FLEET_NOTES = "Fleet Notes";
	static final String TAB_NAME_JOB_NOTES = "Job Notes";
	static final String VIEW_NAME = "logBook";
	static final String DEFAULT_SINGLE_ENTITY_WINDOW_TITLE = "Notes";
	static final String DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE = "Multiple Selection";
	static final String LABEL_DONE_BUTTON = "Done";
	static final String LABEL_CANCEL_BUTTON = "Cancel";	

	private Object entity;
	private List<Object> entities;
	
	private boolean logBookEntryChnaged;
	private List<LogBookTypeVO> logBookTypes;		
	private boolean enableFollowUpDate;
	private boolean showRelatedUnitLogEntry;
	private boolean addAndCloseFeature;
	private String onCloseCallback;	
	private int tabIndex;
	private String obsoleteListSortOrder = MalConstants.SORT_DESC;
	private List<LogBookVO> logBookVOs = new ArrayList<LogBookVO>();
	private List<FleetMaster> relatedFleetMasters = null;
	private String windowTitle;
	private boolean multiEntity = false;
	
	/**
	 * Initializes the bean
	 */
    @SuppressWarnings("unchecked")
	public void init(){ 

    	try {
    		FacesContext fc = FacesContext.getCurrentInstance();
    		setEntity(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.entity}", Object.class));
    		setEntities(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.entities}", List.class));     		
    		setLogBookTypes(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.logBookTypes}", List.class));    		
    		setEnableFollowUpDate(Boolean.parseBoolean(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.enableFollowUpDate}", String.class)));
    		setAddAndCloseFeature(Boolean.parseBoolean(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.enableAddAndClose}", String.class)));   		
    		setOnCloseCallback(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.onClose}", String.class));    		
    		setShowRelatedUnitLogEntry(Boolean.parseBoolean(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.showRelatedUnitLogEntry}", String.class)));
    		
    		setLogBookVOs(new ArrayList<LogBookVO>());
        	setTabIndex(0);
        	setObsoleteListSortOrder(MalConstants.SORT_DESC);      	        	
            setRelatedFleetMasters(null);    	
        	//OTD-472 Branch to support multi-entity selections
        	if(!MALUtilities.isEmpty(this.entity)){
        		initSingleEntityMode();
        	} else if(!MALUtilities.isEmpty(getEntities()) && getEntities().size() == 1) {
        		initSingleEntityMode();        	
        	} else if(!MALUtilities.isEmpty(getEntities()) && getEntities().size() > 1) {        	
        		initMultipleEntityMode();
        	} else {
        		throw new MalBusinessException("An entity must be selected in order to access the Log Book component");
        	}        	
        	
    		//OTD-470 Display Unit info in title bar/or like
    		setWindowTitle(determineWindowTitle());      	
        	
		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}    	
      	    	     	
    }

	private void initSingleEntityMode() {
		setMultiEntity(false);	
		
		if(MALUtilities.isEmpty(getEntities())){
			setEntities(new ArrayList<Object>());
	    	getEntities().add(this.entity);	 			
		}

		
		// Each Log Book may have zero to many related Log Books. 
		// When the combine entries flag is set to true, a tab
		// will be created for each Log Book Type. The entries
		// from the related log book types will be added to the primary 
		// Log Book's entry list. Otherwise, when the flag is set to false
		// a tab will be created for the Log Book Type and one for each
		// of it's related log books.
		for(LogBookTypeVO logBookTypeVO : getLogBookTypes()){
				
			//When an object log book does not exist in the DB for the entity and is not an external log book, create one.
			if(!logBookTypeVO.getLogBookType().getCode().contains("EXTERNAL")){
				if(MALUtilities.isEmpty(logBookService.getObjectLogBook(getEntity(logBookTypeVO), logBookTypeVO.getLogBookType()))){
					logBookService.createObjectLogBook(getEntity(logBookTypeVO), logBookTypeVO.getLogBookType());
				}
			}
			initializeLogBookVO(getEntity(logBookTypeVO), logBookTypeVO.getLogBookType(), logBookTypeVO.isRenderZeroEntries());       			  			    			
			
			//OTD-501 Enhancement to combine related logbook entries with specified log book
			if(logBookTypeVO.isCombineLogBookEntries()){
				getLogBookVOs().get(getLogBookVOs().size() - 1).getEntries().addAll(combineRelatedEntries(logBookTypeVO));
				Collections.sort(getLogBookVOs().get(getLogBookVOs().size() - 1).getEntries(), new LogBookEntryDateComparator());				        		
			}
		}      	
		
		if(getComponent("logBook:logsTabView") != null) {
			RequestContext.getCurrentInstance().update("logBook:logsTabView");
		}
	}
	
	private void initMultipleEntityMode() {
		LogBookTypeVO logBookTypeVO;
		List<ObjectLogBook> objectLogBooks;
		
		setMultiEntity(true);
		
		logBookTypeVO = getLogBookTypes().get(0);
		
		//TODO Create a log book for each of the entities when one does not exist


		//When the log book does not exist in the DB for any of the entities, create one.
		//Then set up the Log Book tab that will contain an object log book for each entity, but treated as one log book
		objectLogBooks = new ArrayList<ObjectLogBook>();
		for(Object entity : getEntities()){

			if(MALUtilities.isEmpty(logBookService.getObjectLogBook(entity, logBookTypeVO.getLogBookType()))){
				logBookService.createObjectLogBook(entity, logBookTypeVO.getLogBookType());
			}			
			
			objectLogBooks.add(logBookService.getObjectLogBook(entity, logBookTypeVO.getLogBookType()));
		}	
		getLogBookVOs().add(new LogBookVO(objectLogBooks));

	}

    /**
     * Adds user input as an entry into the object's log book.
     * @return null
     */
	public String addEntry(){
		ObjectLogBook olb;
		LogBookVO olbVO;
		LogBookTypeVO logBookTypeVO;
		
		try{			
			logBookTypeVO =  getLogBookTypes().get(getTabIndex());
			
			olbVO = getLogBookVOs().get(getTabIndex());
			
			if(MALUtilities.isEmpty(olbVO.getDetail())) return null;	
			
			
			//Adding note entry to each object log book
			for(ObjectLogBook objectLogBook : olbVO.getObjectLogBooks()){
				olb = logBookService.addEntry(objectLogBook,
						getLoggedInUser().getEmployeeNo(), olbVO.getDetail(), olbVO.getFollowUpDate(), isMultiEntity());				
			}
			setLogBookEntryChnaged(true);//tracking for new entry
			//Entries are not displayed when adding note to multiple objects.
			//As a result, there is no need to retrieve the log book VO from ORM.
			if(!isMultiEntity()) {			
				olb = logBookService.getObjectLogBook(getEntity(logBookTypeVO), getLogBookTypes().get(getTabIndex()).getLogBookType());

				//Combine the entity's secondary log book entries with the primary log book
				if(logBookTypeVO.isCombineLogBookEntries() && !logBookTypeVO.getRelatedLogBookTypes().isEmpty()) {
					olb.getLogBookEntries().addAll(combineRelatedEntries(logBookTypeVO));
				}  		

				olbVO.getObjectLogBooks().clear();
				olbVO.getObjectLogBooks().add(olb);
				olbVO.setEntries(olb.getLogBookEntries());
				olbVO.setDetail(null);
				olbVO.setFollowUpDate(null);

				Collections.sort(olbVO.getEntries(), new LogBookEntryDateComparator());
			}
			
		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}  
		
		if(isAddAndCloseFeature() || isMultiEntity()){
			//super.addSuccessMessage("process.success","Added note ");			
			RequestContext.getCurrentInstance().execute("setLogBookDirty(false); PF('logBookDialogWidget').hide();");				
		} else {
			RequestContext.getCurrentInstance().execute("setLogBookDirty(false)");			
		}
		
    	return null;
    }
	
	public void updateFollowUpDate(LogBookEntry entry){
	
		LogBookVO olbVO = getLogBookVOs().get(getTabIndex());
		int pos = olbVO.getEntries().indexOf(entry);
		logBookService.saveOrUpdateLogBookEntry(entry);
		LogBookEntry dbEntry = logBookService.getLogBookEntry(entry.getLbeId());
		olbVO.getEntries().remove(pos);
		olbVO.getEntries().add(pos, dbEntry);
		setLogBookEntryChnaged(true);
		// below  hack code to reset DB follow up since UI submit all entries  although we want one
		List<Long> lbeIds = new ArrayList<>();
		for (LogBookEntry lbe : olbVO.getEntries()) {
			lbeIds.add(lbe.getLbeId());
		}
		
		for (LogBookEntry dbLBE: logBookService.getLogBookEntries(lbeIds)) {
			for (LogBookEntry lbe: olbVO.getEntries()) {
				if(dbLBE.getLbeId().equals(lbe.getLbeId())) {
					lbe.setFollowUpDate(dbLBE.getFollowUpDate());
				}
			} 
		} 
		
	}
		
	public void onHideListener(){		
		if(logBookEntryChnaged) {//olbVO.getDetail() will have value in case multi object is selected.				
			RequestContext.getCurrentInstance().execute(getOnCloseCallback());// callback only if anything got changed(refresh parent page)
		}		
	}	
	
		
	/**
	 * When displaying the log book(s) at the Maintenance Request level, only the activity log is available for entry.
	 * @return
	 */
	public boolean isEntryDisabled(){
		boolean isDisabled = false;
		isDisabled = hasPermission();
		return isDisabled;
	}	
	
	/**
	 * Used to determine what elements to display or hide on the page when the tab is for
	 * a Fleet or Job Note
	 * @return True when active tab is Fleet or Job note
	 */
	public boolean isObsoleteNote(){
		boolean isObsolete = false;
		ObjectLogBook olb;
		
		if(!isMultiEntity()){
			olb =  getLogBookVOs().get(getTabIndex()).getObjectLogBooks().get(0);
			if(olb.getLogBook().getName().equals(TAB_NAME_JOB_NOTES) 
					|| olb.getLogBook().getName().equals(TAB_NAME_FLEET_NOTES)){
				isObsolete = true;
			}
		}
		
		return isObsolete;
	}
	
	/**
	 * Determines whether the active log book is managed by the log book component
	 * or not.
	 * @return True when active tab is for a managed log book
	 */	
	public boolean isUnManagedLogBook(){
		boolean isExternalNote = false;
		if(activeObjectLogBook().getLogBook().getType().contains("EXTERNAL")){
			isExternalNote = true;
		}
		return isExternalNote;
	}
	
	public String dialogWidth(){
		int width = 970;
		int multiEntityAdjustment = -15;
		int sourceEnabledAdjustment = 125;
		String totalWidth = "";
		
		width += isMultiEntity() ? multiEntityAdjustment : 0;
		width += isDisplayEntrySource() && !isMultiEntity() ? sourceEnabledAdjustment : 0;	
				
		totalWidth = String.valueOf(width);
		
		return totalWidth;
	}
	
	public String dialogHeight(){
		String height = "525";
		int amount = 275;
		
		if(isMultiEntity()){
			height = String.valueOf(Integer.valueOf(height) - amount);
		}		
		
		return height;		
	}
	
	/**
	 * I know, we keep adding functionality to the obsolete notes. Bare with me.
	 * This method will be called by the toggle control to sort the list based on
	 * the fleet note id in ASC or DESC order.
	 */
	@Deprecated
	public void sortObsoleteList(){	
		if(isMultiEntity()){
			Collections.sort(getLogBookVOs().get(getTabIndex()).getObjectLogBooks().get(0).getLogBookEntries(), new Comparator<LogBookEntry>() {
				public int compare(LogBookEntry lbe1, LogBookEntry lbe2) {
					if(getObsoleteListSortOrder().equals(MalConstants.SORT_DESC))
						return lbe2.getLbeId().compareTo(lbe1.getLbeId());	
					else
						return lbe1.getLbeId().compareTo(lbe2.getLbeId());						
				}						
			});	    
		}
	}
	
	private String determineWindowTitle(){
		StringBuilder titleBuilder = new StringBuilder();
		
		if(isMultiEntity()) {
			titleBuilder.append(DEFAULT_MULTIPLE_ENTITY_WINDOW_TITLE);
		} else if(getEntity() instanceof FleetMaster){
			FleetMaster fleetMaster = ((FleetMaster)getEntity());		
			titleBuilder.append("Unit No.: " + fleetMaster.getUnitNo());
			if(! MALUtilities.isEmpty(fleetMaster.getVin())){
				titleBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				titleBuilder.append("VIN: " + fleetMaster.getVin());	
			}
		} else {
			titleBuilder.append(DEFAULT_SINGLE_ENTITY_WINDOW_TITLE);
		}
		
		return titleBuilder.toString();
	}
	
	/**
	 * Initializes the LogBookVO for the passed in entity and log book type. 
	 * Once the VO is initialized, it is then added to the list that supports the
	 * tab control.
	 * NOTE: Only the log book for a primary entity or secondary entity with at 
	 *       least one log book entry is added to the list.
	 * @param entity
	 * @param logBookType 
	 */
	private void initializeLogBookVO(Object entity, LogBookTypeEnum logBookType, boolean isRenderZeroEntries){
		ObjectLogBook olb = null;
		List<LogBookEntry> entries = new ArrayList<LogBookEntry>();
		List<LogBookEntry> mashedUpEntries = new ArrayList<LogBookEntry>();
		
		//OTD-835 Branching to retrieve entries from different sources, i.e external or internal log book notes.
		//External entries are those that are not maintained by the log book component, but they are integrated for
		//view purposes only.
		if(logBookType.getCode().contains("EXTERNAL")){
			olb = logBookService.getExternalNotes(entity, logBookType);
			entries = olb.getLogBookEntries();			
		} else {			
			olb = logBookService.getObjectLogBook(entity, logBookType);
			if(!MALUtilities.isEmpty(olb)){
				entries = olb.getLogBookEntries();

				if(entity instanceof FleetMaster && isShowRelatedUnitLogEntry()){			
					if(relatedFleetMasters == null ) {
						relatedFleetMasters = fleetMasterService.findRelatedFleetMasters(((FleetMaster) entity).getVin());    
					}
					mashedUpEntries = logBookService.mashupObjectLogBookEntries(relatedFleetMasters, logBookType);    			
					entries = mashedUpEntries.size() > 0 ? mashedUpEntries : entries;				
				}	 
			}    
		}
		
		if(!MALUtilities.isEmpty(olb)) {
			if (isRenderZeroEntries || entries.size() > 0){
				Collections.sort(entries, new LogBookEntryDateComparator());				
				getLogBookVOs().add(new LogBookVO(olb, entries));    			
			} 			
		}   			
		
	}
	
	/**
	 * Combines the log entries of the passed in log book types for the entity.
	 * @param LogBookTypeVO The VO specified by calling page to configure the log book display
	 * @return A combined entry list of the related log books
	 */
	private List<LogBookEntry> combineRelatedEntries(LogBookTypeVO logBookTypeVO){
		List<LogBookEntry> logBookEntries;
		logBookEntries = logBookService.combineObjectLogBookEntries(getEntity(logBookTypeVO), logBookTypeVO.getRelatedLogBookTypes());
		return logBookEntries;
	}
			

	
	/**
	 * Determines whether a user has permission to maintain the active Log Book
	 * @return boolean
	 */
	public boolean hasPermission(){
		boolean isPermitted = false;
		
		//TODO Need a better way to differentiate log book types passed via the interface from those created by the component. 
		// Log books created by the component will not have an entry in the passed in log book type list.		
		if(MALUtilities.isEmpty(activeLogBookTypeVO()) || !activeLogBookTypeVO().isReadOnly()){
			// HACK: Log books for parent entity will be readonly, that is why this check is here.
			if(!((getEntity() instanceof MaintenanceRequest) 
					&& activeObjectLogBook().getLogBook().getName().equals(LogBookTypeEnum.TYPE_FM_UNIT_NOTES.getDescription()))){
				isPermitted = super.hasPermission(getLogBookVOs().get(getTabIndex()).getObjectLogBooks().get(0).getLogBook().getType());				
			}
		}
		
		return isPermitted;
	}
	
	/**
	 * Determines whether a user has permission to maintain a 
	 * particular entry
	 * @param entry Log Book entry
	 * @return boolean
	 */
	public boolean hasPermission(LogBookEntry entry){
		boolean isPermitted = false;
		
		//TODO Need a better way to differentiate log book types passed via the interface from those created by the component. 
		// Log books created by the component will not have an entry in the passed in log book type list.
		if(MALUtilities.isEmpty(activeLogBookTypeVO()) || !activeLogBookTypeVO().isReadOnly()){
			isPermitted = super.hasPermission(entry.getObjectLogBook().getLogBook().getType());			
		}
		
		return isPermitted;
	}
	
	/**
	 * Determines what label to use based on the whether the label was overridden or not. 
	 * If label was overridden, then the overridden label is return. Otherwise, the log
	 *  book name is returned.
	 * @return String Log Book Label
	 */
	public String getLogBookLabel(LogBookVO logBookVO) {
		String label = "";
		
		//Locating the object log's corresponding log book type in order
		//to get the configured label.
		for(LogBookTypeVO logBookTypeVO : getLogBookTypes()){
			if(logBookTypeVO.getLogBookType().getCode().equals(logBookVO.getObjectLogBooks().get(0).getLogBook().getType())){
				label = logBookTypeVO.getLabel();
				break;
			}
		}
		
		//If label was not configured, then using the log book name for the tab
		if(MALUtilities.isEmpty(label)){
		    label = logBookVO.getObjectLogBooks().get(0).getLogBook().getName(); 	
		}
				
		return label;
	}
	
	public String doneButtonLabel(){
		String label = LABEL_DONE_BUTTON;
		if(isMultiEntity()){
			label = LABEL_CANCEL_BUTTON;
		}
		return label;
	}
	
	private LogBookTypeVO activeLogBookTypeVO(){
		LogBookTypeVO logBookTypeVO;
		
		//TODO Need a better way to differentiate log book types passed via the interface from those created by the component. 
		// Log books created by the component will not have an entry in the passed in log book type list.		
		try{
			logBookTypeVO = getLogBookTypes().get(getTabIndex());
		} catch(Exception e) {
			logBookTypeVO = null;
		}
		
		return logBookTypeVO;
	}
	
	private ObjectLogBook activeObjectLogBook(){
		return getLogBookVOs().get(getTabIndex()).getObjectLogBooks().get(0);
	}
	    
    public Object getEntity(LogBookTypeVO logBookTypeVO){
    	Object entity;
    	
    	if(MALUtilities.isEmpty(logBookTypeVO.getEntity())){
    		entity = getEntity();
    	} else {
    		entity = logBookTypeVO.getEntity();
    	}
    	
    	return entity;
    }

    public Object getEntity() {
		return entities.get(0);
	}
    
	public void setEntity(Object entity) {
		this.entity = entity;
	}   

	public List<Object> getEntities() {
		return entities;
	}

	public void setEntities(List<Object> entities) {
		this.entities = entities;
	}

	public List<LogBookTypeVO> getLogBookTypes() {
		return logBookTypes;
	}

	public void setLogBookTypes(List<LogBookTypeVO> logBookTypes) {
		this.logBookTypes = logBookTypes;
	}

	public boolean isEnableFollowUpDate() {
		boolean isEnabled = false;
				
		if(this.enableFollowUpDate 
				&& !isUnManagedLogBook()){
			isEnabled = true;
		}		
		
		return isEnabled;
	}

	public void setEnableFollowUpDate(boolean enableFollowUpDate) {
		this.enableFollowUpDate = enableFollowUpDate;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getObsoleteListSortOrder() {
		return MALUtilities.isEmpty(obsoleteListSortOrder) ? MalConstants.SORT_DESC : obsoleteListSortOrder;
	}

	public void setObsoleteListSortOrder(String obsoleteListSortOrder) {
		this.obsoleteListSortOrder = obsoleteListSortOrder;
	}

	public List<LogBookVO> getLogBookVOs() {
		return logBookVOs;
	}

	public void setLogBookVOs(List<LogBookVO> logBookVOs) {
		this.logBookVOs = logBookVOs;
	}

	public List<FleetMaster> getRelatedFleetMasters() {
		return relatedFleetMasters;
	}

	public void setRelatedFleetMasters(List<FleetMaster> relatedFleetMasters) {
		this.relatedFleetMasters = relatedFleetMasters;
	}

	public boolean isAddAndCloseFeature() {
		return addAndCloseFeature;
	}

	public void setAddAndCloseFeature(boolean addAndCloseFeature) {
		this.addAndCloseFeature = addAndCloseFeature;
	}

	public String getOnCloseCallback() {
		return onCloseCallback;
	}

	public void setOnCloseCallback(String onCloseCallback) {
		this.onCloseCallback = onCloseCallback;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public boolean isMultiEntity() {
		return multiEntity;
	}

	public void setMultiEntity(boolean multiEntity) {
		this.multiEntity = multiEntity;
	}

	public boolean isDisplayEntrySource() {
		LogBookTypeVO logBookTypeVO;
		
		if(MALUtilities.isEmpty(getLogBookTypes())){
			return false;
		} else {
			logBookTypeVO = getLogBookTypes().get(getTabIndex());
			return logBookTypeVO.isRenderEntrySource();			
		}

	}
	
	public boolean isLogBookEntryChnaged() {
		return logBookEntryChnaged;
	}

	public void setLogBookEntryChnaged(boolean logBookEntryChnaged) {
		this.logBookEntryChnaged = logBookEntryChnaged;
	}

	public boolean isShowRelatedUnitLogEntry() {
		return showRelatedUnitLogEntry;
	}

	public void setShowRelatedUnitLogEntry(boolean showRelatedUnitLogEntry) {
		this.showRelatedUnitLogEntry = showRelatedUnitLogEntry;
	}

	
	
}
