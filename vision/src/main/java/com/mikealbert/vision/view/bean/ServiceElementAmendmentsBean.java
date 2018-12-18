package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.LeaseElementDAO;
import com.mikealbert.data.dao.MulQuoteEleDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.TaxCodeDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.VehicleOdometerReadingsV;
import com.mikealbert.data.enumeration.ServiceElementOperations;
import com.mikealbert.data.vo.ServiceElementsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.MaintenanceScheduleService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

import edu.emory.mathcs.backport.java.util.Collections;

@Component
@Scope("view")
public class ServiceElementAmendmentsBean extends StatefulBaseBean {
	private static final long serialVersionUID = 4427178558940785839L;

	private int DEFAULT_DATATABLE_HEIGHT = 175;
	private Long qmdId;
	private QuotationModel quotationModel;
	private String origin;
	private List<ServiceElementsVO> availableElements;
	private List<ServiceElementsVO> assignedElements;
	private ServiceElementsVO selectedAvailableElement;
	private ServiceElementsVO selectedAssignedElement;	
	private boolean isRecalculcationNeeded = false;
	private boolean isEditable = false;
	private List<VehicleOdometerReadingsV> vehicleOdometerReadings;
	private String unitofMeasureCode;
	private Long currentOdo;
	private Date currentDate;
	private LatestOdometerReadingV latestOdometerReading;
	private String opCode;
	private FleetMaster fleetMaster;
	private boolean isFormalExtension = false;
	
	private String ODOMETER_READING_UI_ID = "currentOdometerReading";

	public static final String WILLOW_FORMAL_EXTENSION = "FLQM148";
	public static final String WILLOW_INFORMAL_AMENDMENT = "FLCM0023";
	final static String GROWL_UI_ID = "growl";

	@Resource ServiceElementService serviceElementService;
	@Resource QuotationModelDAO quotationModelDAO;
	@Resource QuotationService quotationService;
	@Resource LeaseElementDAO leaseElementDAO;
	@Resource TaxCodeDAO taxCodeDao;
	@Resource MulQuoteEleDAO mulQuoteEleDao;
	@Resource ContractService contractService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource FleetMasterDAO fleetMasterDAO;
	@Resource OdometerService odometerService;
	@Resource MaintenanceScheduleService maintenanceScheduleService;

	@PostConstruct
	public void init() {
		initializeDataTable(600, 200, new int[] { 17, 18, 4, 15 }).setHeight(
				DEFAULT_DATATABLE_HEIGHT);
		super.openPage();
		try {
			loadData();
		} catch (Exception ex) {
			logger.error(ex);
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}

	public void loadData() {
		try {
			availableElements = new ArrayList<ServiceElementsVO>();
			assignedElements = new ArrayList<ServiceElementsVO>();
						
			populateServiceElements();
			
			if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
				isFormalExtension = true;
			}
			
			// set the editable flag based upon the business rules for SS-734
			// if informal amendments - if quote status is ON_COTNRACT - we can always edit
			if(quotationModel.getQuoteStatus() == QuotationService.STATUS_ON_CONTRACT){
				isEditable = true;
			} else if (quotationModel.getQuoteStatus() == QuotationService.STATUS_AMENDED){ // if formal amendments STATUS_AMENDED
				// then we can always edit.
				isEditable = true;
			} else { // if formal extension - use rental calcs logic
				isEditable = rentalCalculationService.isQuoteEditable(quotationModel);
			}
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while",
						" building screen.");
			}
		}
	}

	private void populateOdometerReadings(){
		QuotationModel qm = quotationModelDAO.findById(getQmdId()).orElse(null);
		setFleetMaster(fleetMasterDAO.getOdoReadingWithFleetMaster(qm.getUnitNo()));
		Odometer odometer = odometerService.getCurrentOdometer(getFleetMaster());
		
		if(!MALUtilities.isEmpty(odometer)){
			setLatestOdometerReading(getFleetMaster().getLatestOdometerReading());
			setUnitofMeasureCode(odometer.getUomCode()); 
			setVehicleOdometerReadings(getFleetMaster().getVehicleOdometerReadings());
			setCurrentOdo(0L);
		}	
		
		setCurrentDate(MALUtilities.clearTimeFromDate(new Date()));
		setOpCode(getLoggedInUser().getEmployeeNo());
	}
	
	private void warnOfFormalExtensionPriceChange(QuotationModel origQuoteModel, List<ServiceElementsVO> originalAssignedElements) throws MalBusinessException{
		if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
			boolean hasPriceChange = false;
			String msgSubject = "Assigned Service Elements";
			StringBuffer msgBody = new StringBuffer();
			msgBody.append("The pricing on the listed service element(s) has changed. Please review the change with a Transition Manager.<p/><br/>");
			
			// for each service element 
			for(ServiceElementsVO assignedElement : originalAssignedElements){
				ServiceElementsVO updatedElement = serviceElementService.copyServiceElementsVOWithNewAmounts(assignedElement,origQuoteModel);
				
				//For SS-733
				// compare the original assigned element and it's prices to the new amounts for the same elements
				BigDecimal assignedMonthlyCost = CommonCalculations.getRoundedValue(assignedElement.getMonthlyCost(), 2);
				BigDecimal updatedMonthlyCost = CommonCalculations.getRoundedValue(updatedElement.getMonthlyCost(), 2);
				
				// compare prices 
				if(updatedMonthlyCost.compareTo(assignedMonthlyCost) != 0){
					// if they are different then add a warning line to the message.
					msgBody.append(assignedElement.getDescription());
					msgBody.append("&nbsp;-&nbsp;");
					msgBody.append("Orig:&nbsp;");
					msgBody.append(DecimalFormat.getCurrencyInstance(Locale.US).format(assignedMonthlyCost));
					msgBody.append(" Current:&nbsp;");
					msgBody.append(DecimalFormat.getCurrencyInstance(Locale.US).format(updatedMonthlyCost));
					msgBody.append("<br/><br/>");
					if(!hasPriceChange){
						hasPriceChange = true;
					}
				}
				
				
			}
			
			msgBody.append("<p/>");
			
			if(hasPriceChange){
				FacesContext context = FacesContext.getCurrentInstance();
			    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, msgSubject,msgBody.toString()));
			    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
			}
		}
	}
		
		
	/**
	 * Handles the row selection event.
	 * 
	 * @param event
	 */
	public void onRowSelect(SelectEvent event) {
		//not needed
	}

	public void addElement() {
		logger.debug("addElement:start");
		//an element on the assigned list that is also on the available list does not have an AvailableOperation
		if (this.selectedAvailableElement != null && (this.selectedAvailableElement.getAvailableOperation() == null || !this.selectedAvailableElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation()))) {
			for (ServiceElementsVO assigned : this.assignedElements) {
				if (assigned.getLelId() == this.selectedAvailableElement.getLelId()) {
					this.assignedElements.remove(assigned);
					this.availableElements.add(assigned);
					break;
				}
			}
			this.assignedElements.add(this.selectedAvailableElement);
			this.availableElements.remove(this.selectedAvailableElement);
			this.selectedAvailableElement = null;
		}
		logger.debug("addElement:end");
	}

	public void removeElement() {
		logger.debug("removeElement:start");
		//an element on the assigned list that is also on the available list does not have an AvailableOperation
		if (this.selectedAssignedElement != null  && (this.selectedAssignedElement.getAvailableOperation() == null || !this.selectedAssignedElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation()))){
			this.availableElements.add(this.selectedAssignedElement);
			this.assignedElements.remove(this.selectedAssignedElement);
			this.selectedAssignedElement = null;
		}
		logger.debug("removeElement:end");
	}

	public void reset() {
		loadData();		
	}
	
	public void cancel() {}	
	
	public void closeOdoReadingsDialog() {
		setCurrentOdo(0L);
	}
	
	public void store() {
		try {	
			List<LeaseElement> maintenanceLeaseElements = maintenanceScheduleService.getAllMaintenanceLeaseElements();
			List<ServiceElementsVO> elementsAddedOrChanged = new ArrayList<ServiceElementsVO>();
			
			if (!getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
				//loop thru assigned service elements getting only ADDED and CHANGED elements
				//move to service
				for (ServiceElementsVO assignedElement : getAssignedElements()) {
					if(assignedElement.getAvailableOperation() != null){
						if(assignedElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation()) || 
								(assignedElement.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation()))) {
							
							elementsAddedOrChanged.add(assignedElement);
						}
					}
				}
			}

			if (!getOrigin().equals(WILLOW_FORMAL_EXTENSION) && !elementsAddedOrChanged.isEmpty() && elementsAddedOrChanged.size() > 0 && isMaintenanceElement(maintenanceLeaseElements, elementsAddedOrChanged)){
				populateOdometerReadings();
				RequestContext.getCurrentInstance().execute("PF('odoReadingsDialogWidget').show();");
			}
			else {
				saveServiceElements();
			}				
		} catch (Exception ex) {
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	
	public Boolean isMaintenanceElement(List<LeaseElement> maintenanceLeaseElements, List<ServiceElementsVO> elementsAddedOrChanged){
		boolean matchFound = false; 
		
		for(ServiceElementsVO eac : elementsAddedOrChanged){
			for(LeaseElement mle : maintenanceLeaseElements){
				if (mle.getLelId() == eac.getLelId()){
					matchFound = true;
				 	return matchFound;
				}
			}
		}
		return matchFound;
	}
	
	public String save() {
		boolean validationFail = false;

		if(currentOdo != null && currentOdo < getLatestOdometerReading().getOdoReading()){
			if(currentOdo == 0){
				super.addErrorMessageSummary(ODOMETER_READING_UI_ID, "err.value.zero.message", "Odometer Reading ");
				validationFail = true;
			} else {
				super.addErrorMessageSummary(ODOMETER_READING_UI_ID, "odometer.reading.lower.than.last", "");
				validationFail = true;
			}
		}
		if(currentOdo == null) {
			super.addErrorMessageSummary(ODOMETER_READING_UI_ID, "required.field", "Odometer Reading ");
			validationFail = true;
		}

		if(!validationFail){	
			saveOdometerReading();
			saveServiceElements();
		}else{
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
			return "validationFailure";
		}
		return null;
	}	
	
	public void saveOdometerReading() {
		try {
			odometerService.saveOdometerReading(getFleetMaster(), OdometerService.ODO_READING_TYPE_ESTODO, getCurrentOdo(), getOpCode());
		} catch (Exception ex) {
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	
	public void saveServiceElements() {
		try {
			List<ServiceElementsVO> changesList = serviceElementService.findElementsWithChanges(getAssignedElements(), getAvailableElements());
			if(!changesList.isEmpty()  && changesList.size() > 0){
				if (getOrigin().equals(WILLOW_FORMAL_EXTENSION) && isRecalculcationNeeded){
					//in case of navigating back from rental calcs and making changes we need to set the recalc flag to Y
					quotationService.updateRecalcNeededFlag(getQmdId(),true);
				}
				serviceElementService.saveOrUpdateServiceElements(quotationModel, changesList);
				
				if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)) {
					super.addSuccessMessage("process.success","Save Service Elements Formal Extension");
				} else {
					super.addSuccessMessage("process.success","Save Service Elements Amendments");
				}
			}
			//For a formal extension, we need to be taken to the quote overview screen after the STORE button is clicked.
			if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
				initNextPageValues();
				String leaseType = quotationService.getLeaseType(getQmdId());
        	    if (leaseType.equals(QuotationService.OPEN_END_LEASE)) {
        	    	forwardToURL(ViewConstants.QUOTE_OVERVIEW_OE);
        	    } else {
        	    	forwardToURL(ViewConstants.QUOTE_OVERVIEW);
        	    }
			} 	
			//If not a formal extension, then show growl reminding user to recalculate
			else { 
				//If an Informal Amendment, then don't show growl message
				if (!getOrigin().equals(WILLOW_INFORMAL_AMENDMENT)) {
					FacesContext context = FacesContext.getCurrentInstance();
				    context.addMessage("warnings", new FacesMessage(FacesMessage.SEVERITY_WARN, "Service Elements have been changed.", "Recalculate to apply service element changes."));
				    RequestContext.getCurrentInstance().update(GROWL_UI_ID);
				}						
			}
		} catch (Exception ex) {
			logger.error(ex);		
			super.addErrorMessage("generic.error", ex.getMessage());
		}
	}
	
    /*
     * Initialize the next page values
     */
	private void initNextPageValues() {
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();
		restoreStateValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, Long.valueOf(getQmdId()));
		restoreStateValues.put(ViewConstants.VIEW_PARAM_ORIGIN, getOrigin());
		saveRestoreStateValues(restoreStateValues);
		
		Map<String, Object> nextPageValues = new HashMap<String, Object>();		
		nextPageValues.put(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID, Long.valueOf(getQmdId()));	
		nextPageValues.put(ViewConstants.VIEW_PARAM_ORIGIN, getOrigin());
		saveNextPageInitStateValues(nextPageValues);
	}	
	
	private void saveServiceElementsForFormalExtension(QuotationModel newQuotationModel, QuotationModel originalQuotationModel){
		try {
		    if (!FacesContext.getCurrentInstance().isPostback()) {
		    	serviceElementService.removeServiceElementsForFormalExtension(newQuotationModel);
				serviceElementService.saveServiceElementsForFormalExtension(newQuotationModel, originalQuotationModel);		    
		    }	
		} catch (MalBusinessException e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}		    
	}

	private void populateServiceElements() {
		try {
			// variable for the current as well as the original quote
			QuotationModel currQuotationModel = null;
			QuotationModel originalQuotationModel = null;
			
			currQuotationModel = quotationModelDAO.findById(getQmdId()).orElse(null);
			
			//if formal extension from QM148.  could change to this: rentalCalculationService.isFormalExtension(newQuotationModel)
			if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
				
				// we need to load in the Original Quote Model using a specific method.
				currQuotationModel = quotationService.getOriginalQuoteModelOnContractLine(getQmdId());
				// now we can get the original model off of the "connected" contract line property
				originalQuotationModel = currQuotationModel.getContractLine().getQuotationModel();

				//if we are dealing with a formal extension after it has been calculated for the first time.
				if(!isFormalExtensionBeforeCalculate(currQuotationModel, originalQuotationModel)){
					// just set things up so that we are using the new quote
					setQuotationModel(currQuotationModel);
					//TODO: also flip a flag? 
					isRecalculcationNeeded = true;
				}else{
					//save service elements from the original quote to the mul_quote_ele table when a formal extension is being performed
					saveServiceElementsForFormalExtension(currQuotationModel, originalQuotationModel);	
					isRecalculcationNeeded = false;
					// we need to temporarily use the original quote model to get things fully loaded 
					setQuotationModel(originalQuotationModel);
				}
				
			}
			else {
				// for most scenerios (not Formal Extensions) we should be loading the current Quote Model
				setQuotationModel(currQuotationModel);
			}

			if (quotationModel.getQmdId() != null) {	
				// load the service elements from the quote model
				populateServiceElemsFromQuoteModel();

				// now finally set the quotation model to the current one (just in case we were using the original to load off of)
				setQuotationModel(currQuotationModel);
				
				availableElements = sortElements(availableElements);				
				setAvailableElements(availableElements);
				
				//if we are dealing with a formal extension before it has been calculated for the first time.
				// this call needs loaded AssignedElements for comparison
				if(isFormalExtensionBeforeCalculate(currQuotationModel, originalQuotationModel)){
					//TODO: the assigned elements are from the original quote
					//to detect price changes do we need to calculate each element under the new quote as if it were newly added
					warnOfFormalExtensionPriceChange(originalQuotationModel,getAssignedElements());
				}
				
			}

		} catch (MalBusinessException e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	
	
	private boolean isFormalExtensionBeforeCalculate(QuotationModel currQuotationModel, QuotationModel originalQuotationModel){
		if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)){
			//if finance element exists in quotation elements table, then quote elements have already been saved
			QuotationElement qe = rentalCalculationService.getMainQuotationModelElement(currQuotationModel);
			//if we could find an originalQuotationModel then it's another indicator of Formal Extension (and an important one)
			if((qe == null) && (!MALUtilities.isEmpty(originalQuotationModel))){
				return true;
			}else{ // if we don't have an original formal extension then we are also not setup correctly
				return false;
			}
		}else{
			return false;
		}
		
	}
	
	
	
	private void populateServiceElemsFromQuoteModel() throws MalBusinessException{
		
		// load all of the service elements and set the correct operators and indicators (below)
		List<ServiceElementsVO> list = serviceElementService.determineElementsWithChanges(quotationModel);		
		
		// For Assigned Elements
		for (ServiceElementsVO element : serviceElementService.getAssignedServiceElements(quotationModel)) {
			for (ServiceElementsVO elementsWithChangedFlag : list) {
				if (element.getLelId().equals(elementsWithChangedFlag.getLelId())) {
					// because it is assigned to the quote we can only REMOVE
					// mark NONE and REMOVE
					if(elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.REMOVE.getOperation()) ||
							elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation())){
						element.setAvailableOperation(elementsWithChangedFlag.getAvailableOperation());
					}
					
					// we don't want to propogate "CHANGE" onto this list because the user cannot really "CHANGE" it's already on the quote
					if(elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.REMOVE.getOperation()) ||
							elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation())){
						element.setAvailableOperationMarker("-");
					}
					assignedElements.add(element);
				}							
			}
		}
			
		assignedElements = sortElements(assignedElements);
		setAssignedElements(assignedElements);
			
		// For Available Elements
		for (ServiceElementsVO element : serviceElementService.getAvailableServiceElements(quotationModel)) {
			for (ServiceElementsVO elementsWithChangedFlag : list) {
				if (element.getLelId().equals(elementsWithChangedFlag.getLelId())) {
					// because it is available but not on the quote yet we can ADD, CHANGE (or NONE) but REMOVE does not make sense!
					// mark CHANGE, ADD, and NONE
					if(elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation()) ||
							elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation()) ||
							elementsWithChangedFlag.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation())){
						element.setAvailableOperation(elementsWithChangedFlag.getAvailableOperation());
					}
					
					if(element.getAvailableOperation().getOperation().equals(ServiceElementOperations.CHANGE.getOperation()) ||
							element.getAvailableOperation().getOperation().equals(ServiceElementOperations.ADD.getOperation())){
						element.setAvailableOperationMarker("+");
					}
					availableElements.add(element);	
				}						
			}
		}
	}
	
	

	private List<ServiceElementsVO> sortElements(List<ServiceElementsVO> elements) {
		Collections.sort(elements, new Comparator<ServiceElementsVO>() {
				public int compare(ServiceElementsVO vo1, ServiceElementsVO vo2) {
					
					if(vo1.getAvailableOperation() == null) {
						return 1;
					} else if(vo2.getAvailableOperation() == null) {
						return -1;
					} else {
						if(vo1.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation()) && vo2.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation())) {
							return vo1.getName().compareToIgnoreCase(vo2.getName());
						} else if(vo1.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation())) {
							return -1;
						} else if(vo2.getAvailableOperation().getOperation().equals(ServiceElementOperations.NONE.getOperation())) {
							return 1;
						} else {
							return vo1.getName().compareToIgnoreCase(vo2.getName());
						}
					}
					 
				}
		});
		
		return elements;
	}
	
	@Override
	protected void loadNewPage() {
		Map<String, Object> map = super.thisPage.getInputValues();
		thisPage.setPageUrl(ViewConstants.SERVICE_ELEMENT_AMENDMENTS);		
		this.setQmdId((Long) map.get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID));
		this.setOrigin((String) map.get(ViewConstants.VIEW_PARAM_ORIGIN));		
		if (getOrigin().equals(WILLOW_FORMAL_EXTENSION)) {
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_SERVICE_ELEMENT_FORMAL_EXTENSION);
		} else if (getOrigin().equals(WILLOW_INFORMAL_AMENDMENT)) {
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_SERVICE_ELEMENT_INFORMAL_AMENDMENTS);			
		} else {
			thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_SERVICE_ELEMENT_AMENDMENTS);
		}
	}
	
	@Override
	protected void restoreOldPage() {
		Map<String, Object> map = super.thisPage.getRestoreStateValues();
		setQmdId((Long) map.get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID));
		setOrigin((String) map.get(ViewConstants.VIEW_PARAM_ORIGIN));
	}

	public List<ServiceElementsVO> getAvailableElements() {
		return availableElements;
	}

	public void setAvailableElements(List<ServiceElementsVO> availableElements) {
		this.availableElements = availableElements;
	}

	public List<ServiceElementsVO> getAssignedElements() {
		return assignedElements;
	}

	public void setAssignedElements(List<ServiceElementsVO> assignedElements) {
		this.assignedElements = assignedElements;
	}

	public ServiceElementsVO getSelectedAvailableElement() {
		return selectedAvailableElement;
	}

	public void setSelectedAvailableElement(ServiceElementsVO selectedAvailableElement) {
		this.selectedAvailableElement = selectedAvailableElement;
	}

	public ServiceElementsVO getSelectedAssignedElement() {
		return selectedAssignedElement;
	}

	public void setSelectedAssignedElement(ServiceElementsVO selectedAssignedElement) {
		this.selectedAssignedElement = selectedAssignedElement;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public List<VehicleOdometerReadingsV> getVehicleOdometerReadings() {
		return vehicleOdometerReadings;
	}

	public void setVehicleOdometerReadings(List<VehicleOdometerReadingsV> vehicleOdometerReadings) {
		this.vehicleOdometerReadings = vehicleOdometerReadings;
	}
	
	public String getUnitofMeasureCode() {
		return unitofMeasureCode;
	}

	public void setUnitofMeasureCode(String unitofMeasureCode) {
		this.unitofMeasureCode = unitofMeasureCode;
	}

	public Long getCurrentOdo() {
		return currentOdo;
	}

	public void setCurrentOdo(Long currentOdo) {
		this.currentOdo = currentOdo;
	}

	public LatestOdometerReadingV getLatestOdometerReading() {
		return latestOdometerReading;
	}

	public void setLatestOdometerReading(LatestOdometerReadingV latestOdometerReadingV) {
		this.latestOdometerReading = latestOdometerReadingV;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public String getOpCode() {
		return opCode;
	}

	public void setOpCode(String opCode) {
		this.opCode = opCode;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public boolean isFormalExtension() {
		return isFormalExtension;
	}

	public void setFormalExtension(boolean isFormalExtension) {
		this.isFormalExtension = isFormalExtension;
	}	
}
