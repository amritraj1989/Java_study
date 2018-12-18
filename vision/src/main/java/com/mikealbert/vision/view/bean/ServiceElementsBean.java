package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationSchedule;
import com.mikealbert.data.entity.ThirdPartyPoQueueV;
import com.mikealbert.data.entity.User;
import com.mikealbert.data.entity.WorkClass;
import com.mikealbert.data.util.QuotationScheduleTransDateComparator;
import com.mikealbert.data.vo.FinanceParameterVO;
import com.mikealbert.data.vo.VendorInfoVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.ContractService;
import com.mikealbert.service.FinanceParameterService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.ServiceElementService;
import com.mikealbert.service.UserService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.service.AmendmentHistoryService;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.AmendmentHistoryVO;
import com.mikealbert.vision.vo.EleAmendmentDetailVO;
import com.mikealbert.vision.vo.ServiceElementVO;


@Component
@Scope("view")
public class ServiceElementsBean extends StatefulBaseBean {
	private static final long serialVersionUID = -8806821952041781659L;
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	@Resource QuotationService quotationService;
	@Resource FinanceParameterService financeParameterService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource UserService userService;
	@Resource AmendmentHistoryService	amendmentHistoryService;
	@Resource ServiceElementService serviceElementService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ContractService contractService;
	
	private List<ServiceElementVO> rowList;
	private Long incomingQmdId;
	private String outputQuote;
	private QuotationModel quotationModel;
	private List<QuotationElement> serviceElements;
	private List<FinanceParameterVO> dialogList;
	private List<FinanceParameterVO> inRateElementParameters;
	
	private ServiceElementVO selectedElement;
	private int selectedRowIndex;
	private boolean disabledSave;
	private WorkClass workClass;
	private final String MODULE_NAME = MalConstants.MODULE_VISION_QL;
	private boolean entryError;
	
	private List<AmendmentHistoryVO> amendmentHistoryList = new ArrayList<AmendmentHistoryVO>();
	private 	Date effectiveDate;
	private String effectivePeriod;
	boolean renderEdit	= false;
	boolean	hideHistory = false;
	boolean presentInHistory = false;
	public static final int QUOTE_STATUS_ON_OFFER = 1;
	public static final int QUOTE_STATUS_AWAITING_CREDIT_REVIEW = 2;
	public static final int QUOTE_STATUS_ACCEPTED = 3;
	public static final int QUOTE_STATUS_REVISED = 4;
	public static final int QUOTE_STATUS_AUTHORISED = 5;
	public static final int QUOTE_STATUS_ON_CONTRACT = 6;
	public static final int QUOTE_STATUS_CLOSED = 7;
	public static final int QUOTE_STATUS_REJECTED = 8;
	public static final int QUOTE_STATUS_AMENDMENT = 9;
	public static final int QUOTE_STATUS_CONTRACT_REVISION = 10;
	public static final int QUOTE_STATUS_FUTURE_CONTRACT_AMENDMENT = 11;
	public static final int QUOTE_STATUS_REQUEST_ORDER_REVISION = 12;
	public static final int QUOTE_STATUS_STANDARD_ORDER = 15;
	public static final int QUOTE_STATUS_FINALISED_ORDER_REVISION = 13;
	public static final int QUOTE_STATUS_RETIRED = 14;
	public static final int QUOTE_STATUS_ALLOCATED_TO_GRD = 16;
	public static final int QUOTE_STATUS_GRD_COMPLETE = 17;
	public static final int QUOTE_STATUS_STANDARD_ORDER_REVISION = 18;
	public static final int QUOTE_STATUS_INACTIVE_STANDARD_ORDER = 19;
	
	private String unitNo;
	private String accountCode;
	private String accountName;
	private String quoteType;
	private String modelDescription;
	private String orderingDealerCode;
	private String orderingDealerName;
	private String deliveringDealerCode;
	private String deliveringDealerName;
	private String previousPage;
	private String printedInd;
	
	private long currentContractTerm; 
	private long conChangeEventPeriod;
	private boolean showFinanceParameterDialog = false;
	
	
	@PostConstruct
	public void init() {
		super.openPage();
		initializeDataTable(400, 770, new int[] {});
		try {
			if(incomingQmdId != null) {
				quotationModel = quotationService.getQuotationModel(incomingQmdId);
				currentContractTerm = quotationModel.getContractPeriod();
				if(!MALUtilities.isEmpty(previousPage) && previousPage.equalsIgnoreCase(ViewConstants.DISPLAY_NAME_OER)){
					showFinanceParameterDialog = true;
					currentContractTerm = quotationService.getQuotationModel(quotationModel.getRevisionQmdId()).getContractPeriod();
				}
				loadData();	
				loadAmendmentData();
				
				renderEdit = rentalCalculationService.isQuoteEditable(quotationModel) ;
				if(showFinanceParameterDialog && printedInd.equalsIgnoreCase("N")){
					renderEdit = showFinanceParameterDialog;
				}
				int quoteStatus = quotationModel.getQuoteStatus();
				if (QUOTE_STATUS_ON_OFFER == quoteStatus || QUOTE_STATUS_AWAITING_CREDIT_REVIEW == quoteStatus
						|| QUOTE_STATUS_REVISED == quoteStatus || QUOTE_STATUS_AUTHORISED == quoteStatus
						||QUOTE_STATUS_ACCEPTED == quoteStatus || QUOTE_STATUS_FUTURE_CONTRACT_AMENDMENT == quoteStatus
						||QUOTE_STATUS_ALLOCATED_TO_GRD==quoteStatus ||QUOTE_STATUS_GRD_COMPLETE == quoteStatus
						|| QUOTE_STATUS_REQUEST_ORDER_REVISION == quoteStatus || QUOTE_STATUS_STANDARD_ORDER_REVISION == quoteStatus  
						|| QUOTE_STATUS_STANDARD_ORDER == quoteStatus){
					hideHistory = true;
				}
				hideHistory = renderEdit ? renderEdit :hideHistory;
				for (AmendmentHistoryVO amendmentHistoryVO : amendmentHistoryList) {
						if(amendmentHistoryVO.getQmdId() != null && amendmentHistoryVO.getQmdId().longValue() == incomingQmdId.longValue() ){
							presentInHistory = true;
					}
				}
				if(presentInHistory){
					hideHistory = false;
				}
				if(quoteStatus == QUOTE_STATUS_CONTRACT_REVISION && !MALUtilities.isEmpty(previousPage) && previousPage.equalsIgnoreCase(ViewConstants.DISPLAY_NAME_OER)){
					hideHistory = true;
				}
				if(!hideHistory){
					if (amendmentHistoryList != null && !amendmentHistoryList.isEmpty()) {
						Long finalisedQmdId = amendmentHistoryList.get(0).getQmdId();
						QuotationModel qModelForHeader = quotationModel;
						if(finalisedQmdId != null){
							qModelForHeader = quotationService.getQuotationModel(finalisedQmdId);
						}
						ExternalAccount orderingDealer = quotationService.getOrderingDealer(finalisedQmdId);
						if(orderingDealer != null) {
							orderingDealerCode = orderingDealer.getExternalAccountPK().getAccountCode();
							orderingDealerName = orderingDealer.getAccountName();
						}
						ExternalAccount deliveringDealer = quotationService.getDeliveringDealer(finalisedQmdId);
						if(deliveringDealer != null) {
							deliveringDealerCode = deliveringDealer.getExternalAccountPK().getAccountCode();
							deliveringDealerName = deliveringDealer.getAccountName();
						}
						
						
						accountCode = qModelForHeader.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode();
						accountName = qModelForHeader.getQuotation().getExternalAccount().getAccountName();
						quoteType = quotationService.getLeaseType(qModelForHeader.getQmdId());
						if (quotationModel.getModel() != null) {
							modelDescription = qModelForHeader.getModel().getModelDescription();
						}
						unitNo	= qModelForHeader.getUnitNo();
					}
				}
				
				
				
				
			}	
		} catch (Exception ex) {
			logger.error(ex);
			handleException("generic.error", new String[]{"page load"},ex,null);
		}
	}
	private void loadAmendmentData() throws MalBusinessException{
		amendmentHistoryList = amendmentHistoryService.getAmendedQuotesWithAmendmentDetail(incomingQmdId, false, true);
		for (AmendmentHistoryVO amendmentHistoryVO : amendmentHistoryList) {
			if(amendmentHistoryVO.getServiceElements() == null ||  amendmentHistoryVO.getServiceElements().isEmpty()){
				amendmentHistoryVO.getServiceElements().add(new EleAmendmentDetailVO());
				if(amendmentHistoryVO.getAmendmentSource().equals("O")){
					amendmentHistoryVO.setNoChangeInfo("No service elements");
				}else{
					amendmentHistoryVO.setNoChangeInfo("No service element changes");
				}
				
			}
		}
	}

	@Override
	protected void loadNewPage() {

		thisPage.setPageDisplayName("Service Elements");
		thisPage.setPageUrl(ViewConstants.SERVICE_ELEMENTS);

		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID) != null){
		    	incomingQmdId = (Long)thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID);			
		} else {
			String paramQmdId = getRequestParameter("qmdId");
			if (paramQmdId != null)
				incomingQmdId = Long.valueOf(paramQmdId);
		}
		if(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_PREVIOUS_PAGE) != null){
			previousPage = String.valueOf(thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_PREVIOUS_PAGE));
		}
		
		if(thisPage.getInputValues().get(ViewConstants.CONTRACT_CHANGE_PERIOD) != null){
			conChangeEventPeriod = (Long)thisPage.getInputValues().get(ViewConstants.CONTRACT_CHANGE_PERIOD);
		}
		if(thisPage.getInputValues().get("PRINTED_IND") != null){
			printedInd = String.valueOf(thisPage.getInputValues().get("PRINTED_IND"));
		}else{
			printedInd = "N";
		}
		
	}

	@Override
	protected void restoreOldPage() {}
	

	public String cancel(){
    	return super.cancelPage();      	
    }

    public String save(){  
    	if(saveElements()){
    		super.addSuccessMessage("process.success",  "Values saved - ");
    		return super.cancelPage();
    	}else
    		return null;
    }
	
	public void loadData() {
		outputQuote = getFormattedQuote();
		effectivePeriod = quotationModel.getContractChangeEventPeriod() != null ? quotationModel
				.getContractChangeEventPeriod().toString() : null;
		disabledSave = true;
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		workClass = userService.findWorkClassByUser(user);
		
		try {
			serviceElements = serviceElementService.getServiceElementDetails(quotationModel);

			loadRows();
			if(rowList.size()>0) {
				selectedElement = rowList.get(0);
			}
		} catch (Exception e) {
			logger.error(e);
			 if(e  instanceof MalBusinessException){				 
				 super.addErrorMessage(e.getMessage()); 
			 }else{
				 super.addErrorMessage("generic.error.occured.while", " building service elements.");
			 }
		}
	}

	
	private String getFormattedQuote() {
		return String.valueOf(quotationModel.getQuotation().getQuoId())+ "/" + 
							  quotationModel.getQuoteNo() + "/" + 
							  quotationModel.getRevisionNo();
	}
	
	
	//TODO: there is serious concept overlap between this page and the ServiceElementVO
	// the the work being done in service standards and the introduction of the ServiceElementsVO
	// within core data. This code should be gradually cleaned up. for the time being I've moved 
	// some of it up into the ServiceElementService although duplicating it in the process.
	private void loadRows() throws MalBusinessException {
		rowList = new ArrayList<ServiceElementVO>();
		ServiceElementVO serviceElementVO;
		for(QuotationElement qe : serviceElements) {
			serviceElementVO = new ServiceElementVO();
			serviceElementVO.setDescription(qe.getLeaseElement().getDescription());
			if(qe.getRental() != null) {
				BigDecimal cost = qe.getRental().divide(getRentalPeriods(qe), 2, BigDecimal.ROUND_HALF_UP);
				serviceElementVO.setMonthlyCost(cost);				
			}
			serviceElementVO.setMonthlyCost(serviceElementVO.getMonthlyCost() != null ? serviceElementVO.getMonthlyCost() : BigDecimal.ZERO);			
			serviceElementVO.setTotalCost(qe.getRental());
			Date billingDate = getBillingDate(qe);
			if(effectiveDate == null){
				effectiveDate = billingDate;
			}
			if(billingDate != null) {
				serviceElementVO.setEffectiveBilling(billingDate);				
			} else {
				serviceElementVO.setEffectiveBilling(null);
			}
			serviceElementVO.setInRateTreatment(MALUtilities.convertYNToBoolean(qe.getLeaseElement().getInRateTreatmentYn()));
			setFinanceParameters(serviceElementVO, qe);

			rowList.add(serviceElementVO);
		}
		Collections.sort(rowList, svcElementVOComparator);
	}
	
	Comparator<ServiceElementVO> svcElementVOComparator = new Comparator<ServiceElementVO>() {
		public int compare(ServiceElementVO r1, ServiceElementVO r2) {
			int compareResult = 0;
			Date date1 = r1.getEffectiveBilling();
			Date date2 = r2.getEffectiveBilling();

			if (date1 == null && date2 == null) {
				compareResult = 0;
			} else if (date1 == null) {
				compareResult = -1;
			} else if (date2 == null) {
				compareResult = 1;
			} else {
				compareResult = date1.compareTo(date2);
			}
			if (compareResult != 0) {
				return compareResult;
			} else {
				String desc1 = r1.getDescription();
				String desc2 = r2.getDescription();
				if (desc1 != null && desc2 != null) {
					compareResult = desc1.compareToIgnoreCase(desc2);
				}
			}
			return compareResult;
		}
	};

        private void setFinanceParameters(ServiceElementVO serviceElementVO, QuotationElement quotationElement) throws MalBusinessException {
    
        	serviceElementVO.setEditable(true);
        	serviceElementVO.setFinanceParameters(financeParameterService.getFinanceParameterInfoForQuotationElement(quotationElement,
        		workClass.getWorkClassName(), MODULE_NAME));
        	List<FinanceParameterVO> current = new ArrayList<FinanceParameterVO>();
        	List<FinanceParameterVO> editable = new ArrayList<FinanceParameterVO>();
        	
        	for (FinanceParameterVO fpVo : serviceElementVO.getFinanceParameters()) {
        	    //if there are overrides (if getQuotationModelFinances() != null)
        		//set this as a value
        		if (fpVo.getQuotationModelFinances() != null) {
        	    	fpVo.setValue(fpVo.getQuotationModelFinances().getnValue());
        	    	current.add(fpVo);
        	    }
        		// if you have the authority they this element should be in edit mode
        	    if (fpVo.getWorkClassAuthority() != null) {
        	    	editable.add(fpVo);
        	   }

        	    // get the finance parameter value (i.e. the default)
        	    Double financeParamValue = quotationService.getFinanceParam(fpVo.getFormulaParameter().getParameterName(),  quotationElement.getQuotationModel().getQmdId(), 
        	    		quotationElement.getQuotationModel().getQuotation().getQuotationProfile().getQprId(),null,true);
        	    
        	    // set the finance parameter value as the default
        	    fpVo.setDefaultValue(new BigDecimal(String.valueOf(financeParamValue)));
        	    
        	    if(fpVo.getValue() != null){
        	    	if(quotationElement.getLeaseElement().getProcessorName().equalsIgnoreCase("overheadProfitCostProcessor")){
        	    		fpVo.setTotalValueExcludingInterest(fpVo.getValue().multiply(new BigDecimal(currentContractTerm), CommonCalculations.MC));
        	    		fpVo.setMonthlyValue(fpVo.getValue());
            	    	fpVo.setTotalUnpaidAmount((fpVo.getValue().multiply(new BigDecimal(currentContractTerm), CommonCalculations.MC)).subtract((fpVo.getValue().multiply(new BigDecimal(conChangeEventPeriod), CommonCalculations.MC))));
        	    	}else{
        	    		fpVo.setTotalValueExcludingInterest(fpVo.getValue());
        	    		fpVo.setMonthlyValue(fpVo.getValue().divide(new BigDecimal(currentContractTerm), CommonCalculations.MC));
        	    		fpVo.setTotalUnpaidAmount((fpVo.getValue()).subtract((fpVo.getValue().multiply(new BigDecimal(conChangeEventPeriod), CommonCalculations.MC).divide(new BigDecimal(currentContractTerm), CommonCalculations.MC)), CommonCalculations.MC));
        	    	}
        	    }
        	    
        	    // just initialize the value to "unchanged"
        	    fpVo.setChanged(false);
        	}
        
        	Collections.sort(current, financeParameterVOComparator);
        	Collections.sort(editable, financeParameterVOComparator);
        
        	serviceElementVO.setCurrentFinanceParameters(current);
        	serviceElementVO.setEditableFinanceParameters(editable);
        
        	
        	if (!rentalCalculationService.isQuoteEditable(quotationModel)) {
        	    serviceElementVO.setEditable(false);
        	}
        	if(showFinanceParameterDialog){
    			if(!serviceElementVO.isInRateTreatment()) {
    				serviceElementVO.setEditable(false);
    			}else{
    				if(printedInd.equalsIgnoreCase("N")){
    					serviceElementVO.setEditable(true);
    				}else{
    					serviceElementVO.setEditable(false);
    				}
    			}
        	}
        }
	
	Comparator<FinanceParameterVO> financeParameterVOComparator = new Comparator<FinanceParameterVO>() {
		public int compare(FinanceParameterVO r1, FinanceParameterVO r2) {
			FinanceParameter f1 = r1.getFinanceParameter();
			FinanceParameter f2 = r2.getFinanceParameter();
			if (f1 == null && f2 == null) {
				return 0;
			} else if (f1 == null) {
				return -1;
			} else if (f2 == null) {
				return 1;
			} else {
				String desc1 = f1.getDescription();
				String desc2 = f1.getDescription();
				return desc1.compareToIgnoreCase(desc2);
			}

		}
	};
	
	
    private boolean entryErrors() {
    	entryError=false;
    	int i = 0;
    	boolean isErrorMessageAdded = false;
    	String 	elementForFocus = null;
    	for(FinanceParameterVO fpVo : dialogList) {
    		i++;
    		//if(fpVo.getValue() != null) {
        		if((fpVo.getValue() != null) && 
        				((fpVo.getWorkClassAuthority().getLowerLimit() != null && fpVo.getValue().compareTo(fpVo.getWorkClassAuthority().getLowerLimit()) < 0) || 
        				fpVo.getWorkClassAuthority().getUpperLimit() != null && fpVo.getValue().compareTo(fpVo.getWorkClassAuthority().getUpperLimit()) > 0)) {
        			String htmlId = "fpDataTable:"+(i-1)+":fpInput_input";
            		String javaScriptFunction = "markRedForError('"+htmlId+"')";
        			if(!isErrorMessageAdded){
        				addSimplErrorSummaryMessage(talMessage.getMessage("limits_exceeded"));
        				isErrorMessageAdded = true;
        				elementForFocus = htmlId;
        			}
            		
            		entryError=true;
            		
            		/*UIComponent	inputField = getComponent(htmlId);
            		if(inputField != null && inputField  instanceof UIInput){
            			((UIInput)inputField).setValid(false);
            		}*/
            		RequestContext requestContext = RequestContext.getCurrentInstance();  
            		requestContext.execute(javaScriptFunction);
        		}else{
        			String htmlId = "fpDataTable:"+(i-1)+":fpInput_input";
        			
            		String javaScriptFunction = "unMarkRedForError('"+htmlId+"')";
            		
            		RequestContext requestContext = RequestContext.getCurrentInstance();  
            		requestContext.execute(javaScriptFunction);
        		}
    		//}
    		
    	}
    	if(entryError && elementForFocus != null){
    		RequestContext requestContext = RequestContext.getCurrentInstance();  
    		String setFocusFunction = "setElementFocus('"+elementForFocus+"')";
    		requestContext.execute(setFocusFunction);
    	}
    	return entryError;
    
    }
    
	private BigDecimal getRentalPeriods(QuotationElement qe) {
		BigDecimal rentalPeriods;
		if(qe.getNoRentals() != null && qe.getNoRentals() != BigDecimal.ZERO) {
			rentalPeriods = qe.getNoRentals();
		} else {
			rentalPeriods = new BigDecimal(quotationModel.getContractPeriod()); 
		}
		return rentalPeriods;
	}
	
	private Date getBillingDate(QuotationElement qe) {
		List<QuotationSchedule> qs = qe.getQuotationSchedules(); 
		if(qs != null && qs.size() > 0) {
			Collections.sort(qs, new QuotationScheduleTransDateComparator());
			return qs.get(0).getTransDate();
		} else {
			return null;
		}
	}
	
    public void onRowSelect() {
    	entryError = false;

		selectedElement = rowList.get(getSelectedRowIndex());
		
		// reset values back to original in case the dialog was closed with bad values still in it
		for(FinanceParameterVO fpVo : selectedElement.getEditableFinanceParameters()) {
			fpVo.setValue(getResetValue(fpVo));
		}

		dialogList = selectedElement.getEditableFinanceParameters();
    }

    
    private BigDecimal getResetValue(FinanceParameterVO fpVo) {
    	if(selectedElement.getCurrentFinanceParameters().size() == 0) {
    		return null;
    	} else {
    		for(FinanceParameterVO current : selectedElement.getCurrentFinanceParameters()) {
    			if(current.getFinanceParameter() != null) {
        			if(fpVo.getFinanceParameter().getParameterId().equals(current.getFinanceParameter().getParameterId())) {
        				return current.getValue();
        			}
        		}
    		}
    	}
    	return null;
    }

    
    
    public void processUpdateDialog() {
    	Boolean found;
    	Boolean clear = false;
    	
    	if(entryErrors()) {
        	RequestContext context = RequestContext.getCurrentInstance();
    		context.addCallbackParam("failure", true);
    		disabledSave = true;
    	} else {
        	if(selectedElement != null) {
            	for(FinanceParameterVO editable : selectedElement.getEditableFinanceParameters()) {
            		found = false;
            		if(editable.getValue() != null) {
                		editable.setValue(CommonCalculations.getRoundedValue(editable.getValue(), 5));            			
            		}
            		for(FinanceParameterVO current : selectedElement.getCurrentFinanceParameters()) {
            			if(current.getFinanceParameter() != null) {
                			if(editable.getFinanceParameter().getParameterId().equals(current.getFinanceParameter().getParameterId())) {
                				current.getQuotationModelFinances().setnValue(editable.getValue());
                				current.setValue(editable.getValue());
                				current.setChanged(true);
                				found = true;
                				clear = true;
                				break;
                			}
            			}
            		}
            		// add if not found and has a value.  We are adding a skeleton element to the list so it shows up while displaying and editing
            		//   later when the data is persisted to the database, the rest of the info will be populated.
            		if(!found && editable.getValue() != BigDecimal.ZERO && editable.getValue() != null) {
            			QuotationModelFinances qmf = new QuotationModelFinances();
            			qmf.setDescription(editable.getFinanceParameter().getDescription());
            			qmf.setParameterId(editable.getFinanceParameter().getParameterId());
            			qmf.setnValue(editable.getValue());
            			editable.setQuotationModelFinances(qmf);
            			selectedElement.getCurrentFinanceParameters().add(getClonedFinanceParameter(editable));
            			clear = true;
            		}
            		found = false;
            	}
        	}
        	if(clear) {
        		selectedElement.setMonthlyCost(null);
        		selectedElement.setTotalCost(null);
        		disabledSave = false;
        	}
        	
    	}
    }
	
	private FinanceParameterVO getClonedFinanceParameter(FinanceParameterVO fpVo) {
		FinanceParameterVO newFpVo = new FinanceParameterVO();
		newFpVo.setChanged(fpVo.isChanged());
		newFpVo.setDefaultValue(fpVo.getDefaultValue());
		newFpVo.setFinanceParameter(fpVo.getFinanceParameter());
		newFpVo.setFormulaParameter(fpVo.getFormulaParameter());
		newFpVo.setQuotationModelFinances(fpVo.getQuotationModelFinances());
		newFpVo.setValue(fpVo.getValue());
		newFpVo.setWorkClassAuthority(fpVo.getWorkClassAuthority());
		
		return newFpVo;
	}
    
    public void processCancelDialog() {
    	
    }
    
    private Boolean saveElements() {
    	for(ServiceElementVO serviceElementVO : rowList) {
    		if(serviceElementVO.getCurrentFinanceParameters() != null) {
        		for(FinanceParameterVO financeParameterVO : serviceElementVO.getCurrentFinanceParameters()) {
        			if(financeParameterVO.getValue() != null) {
        				if(financeParameterVO.getQuotationModelFinances().getQmfId() == null) {
        					insert(financeParameterVO.getQuotationModelFinances(), financeParameterVO.getFinanceParameter(), financeParameterVO.getValue());
        				} else {
        					if(financeParameterVO.isChanged()) {
        						financeParameterVO.getQuotationModelFinances().setnValue(financeParameterVO.getValue());
        						save(financeParameterVO.getQuotationModelFinances());
        					}
        				}
        			} else {
        				if(financeParameterVO.getQuotationModelFinances().getQmfId() != null) {
        					delete(financeParameterVO.getQuotationModelFinances());
        				}
        			}
        		}
    		}
    	}
    	setRecalcFlag();
    	return true;
    }
    
    
    
    private void save(QuotationModelFinances qmf) {
    	try {
        	financeParameterService.saveFinanceParameterOnQuote(qmf);
    	} catch (Exception e) {
    		super.addErrorMessage("generic.error", e.getMessage());	
    	}
    }

    private void insert(QuotationModelFinances qmf, FinanceParameter fp, BigDecimal newValue) {
    	qmf.setDescription(fp.getDescription());
    	qmf.setcValue(fp.getCvalue());
    	qmf.setEffectiveFromDate(fp.getEffectiveFrom());
    	qmf.setnValue(newValue);
    	qmf.setParameterId(fp.getParameterId());
    	qmf.setParameterKey(fp.getParameterKey());
    	qmf.setStatus(fp.getStatus());
    	qmf.setQuotationModel(getQuotationModel());
    	save(qmf);    	
		
    }
    
    private void delete(QuotationModelFinances qmf) {
    	try {
    		financeParameterService.deleteFinanceParameterOnQuote(qmf);
    	} catch (Exception e) {
    		super.addErrorMessage("generic.error", e.getMessage());	
    	}
    }
    
    private void setRecalcFlag() {
    	try {
    		QuotationModel qm = quotationService.getQuotationModel(incomingQmdId);
    		qm.setReCalcNeeded("Y");
    		quotationService.updateQuotationModel(qm);
    	} catch (Exception e) {
    		super.addErrorMessage("generic.error", e.getMessage());
    	}
    }
    
    public void getSelectedServiceElement(ServiceElementVO serviceElementVO) {
    	setSelectedElement(serviceElementVO);
    	setInRateElementParameters(serviceElementVO.getFinanceParameters());
	}
    
	public String getOutputQuote() {
		return outputQuote;
	}

	public void setOutputQuote(String outputQuote) {
		this.outputQuote = outputQuote;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public List<QuotationElement> getServiceElements() {
		return serviceElements;
	}

	public void setServiceElements(List<QuotationElement> serviceElements) {
		this.serviceElements = serviceElements;
	}

	public List<ServiceElementVO> getRowList() {
		return rowList;
	}

	public void setRowList(List<ServiceElementVO> rowList) {
		this.rowList = rowList;
	}

	public ServiceElementVO getSelectedElement() {
		return selectedElement;
	}

	public void setSelectedElement(ServiceElementVO selectedElement) {
		this.selectedElement = selectedElement;
	}

	public int getSelectedRowIndex() {
		return selectedRowIndex;
	}

	public void setSelectedRowIndex(int selectedRowIndex) {
		this.selectedRowIndex = selectedRowIndex;
	}

	public boolean getDisabledSave() {
		return disabledSave;
	}

	public void setDisabledSave(boolean disabledSave) {
		this.disabledSave = disabledSave;
	}

	public boolean isEntryError() {
		return entryError;
	}

	public void setEntryError(boolean entryError) {
		this.entryError = entryError;
	}

	public List<FinanceParameterVO> getDialogList() {
		return dialogList;
	}

	public void setDialogList(List<FinanceParameterVO> dialogList) {
		this.dialogList = dialogList;
	}

	public List<AmendmentHistoryVO> getAmendmentHistoryList() {
		return amendmentHistoryList;
	}

	public void setAmendmentHistoryList(List<AmendmentHistoryVO> amendmentHistoryList) {
		this.amendmentHistoryList = amendmentHistoryList;
	}
	
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getEffectivePeriod() {
		return effectivePeriod;
	}
	public void setEffectivePeriod(String effectivePeriod) {
		this.effectivePeriod = effectivePeriod;
	}
	public boolean isRenderEdit() {
		return renderEdit;
	}
	public void setRenderEdit(boolean renderEdit) {
		this.renderEdit = renderEdit;
	}
	
	public boolean isPresentInHistory() {
		return presentInHistory;
	}
	public void setPresentInHistory(boolean presentInHistory) {
		this.presentInHistory = presentInHistory;
	}
	public boolean isHideHistory() {
		return hideHistory;
	}
	public void setHideHistory(boolean hideHistory) {
		this.hideHistory = hideHistory;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getQuoteType() {
		return quoteType;
	}
	public void setQuoteType(String quoteType) {
		this.quoteType = quoteType;
	}
	public String getModelDescription() {
		return modelDescription;
	}
	public void setModelDescription(String modelDescription) {
		this.modelDescription = modelDescription;
	}
	public String getOrderingDealerCode() {
		return orderingDealerCode;
	}
	public void setOrderingDealerCode(String orderingDealerCode) {
		this.orderingDealerCode = orderingDealerCode;
	}
	public String getOrderingDealerName() {
		return orderingDealerName;
	}
	public void setOrderingDealerName(String orderingDealerName) {
		this.orderingDealerName = orderingDealerName;
	}
	public String getDeliveringDealerCode() {
		return deliveringDealerCode;
	}
	public void setDeliveringDealerCode(String deliveringDealerCode) {
		this.deliveringDealerCode = deliveringDealerCode;
	}
	public String getDeliveringDealerName() {
		return deliveringDealerName;
	}
	public void setDeliveringDealerName(String deliveringDealerName) {
		this.deliveringDealerName = deliveringDealerName;
	}
	public List<FinanceParameterVO> getInRateElementParameters() {
		return inRateElementParameters;
	}
	public void setInRateElementParameters(
			List<FinanceParameterVO> inRateElementParameters) {
		this.inRateElementParameters = inRateElementParameters;
	}
	public long getCurrentContractTerm() {
		return currentContractTerm;
	}
	public void setCurrentContractTerm(long currentContractTerm) {
		this.currentContractTerm = currentContractTerm;
	}
	public boolean isShowFinanceParameterDialog() {
		return showFinanceParameterDialog;
	}
	public void setShowFinanceParameterDialog(boolean showFinanceParameterDialog) {
		this.showFinanceParameterDialog = showFinanceParameterDialog;
	}
	

}