package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelPrice;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.OptionPackCost;
import com.mikealbert.data.entity.OptionPackHeader;
import com.mikealbert.data.entity.OptionPrice;
import com.mikealbert.data.entity.OptionalAccessory;
import com.mikealbert.data.enumeration.AccountStatusEnum;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.UpfitterPriceVO;

@Component
@Scope("view")
public class maintainModelPricingBean extends StatefulBaseBean{
	private static final long serialVersionUID = -4127920933018451915L;
		
	@Resource ModelService modelService;
	@Resource UpfitterService upfitterService;
	@Resource LookupCacheService lookupCacheService; 
	@Resource WillowConfigService willowConfigService;
		
	private static final String UI_ID_NEW_DEALER_ACCESSORY_CODE = "addDealerAccessoryCodeTab:newCodeTxt";
	private static final String UI_ID_NEW_DEALER_ACCESSORY_DESCRIPTION = "addDealerAccessoryCodeTab:newDescriptionTxt";
	private static final String UI_ID_NEW_DEALER_ACCESSORY_CATEGORY= "addDealerAccessoryCodeTab:newCategoryMnu";
	private static final String UI_ID_SELECTED_DEALER_ACCESSORY_DESCRIPTION = "selectedDealerAccessoryDescriptionTxt";
	private static final String UI_ID_SELECTED_DEALER_ACCESSORY_CATEGORY= "selectedCategoryMnu";
	private static final String UI_ID_SELECTED_DEALER_ACCESSORY_CODE_AC = "dealerAccessoryCodeAC";
//	private static final String DEFAULT_ACCESSORY_CATEGORY_CODE = "4"; //Dealer Installed Options
	
	private List<Long> paramModelIds;	
	private List<Model> models;
	private Model model;
	private List<ModelPrice> selectedModelPrices;
	private OptionPackHeader selectedOptionPackHeader;
	private OptionalAccessory selectedOptionalAccessory;
	private DealerAccessory selectedDealerAccessory;
	private List<DealerAccessoryPrice> selectedDealerAccessoryPrices;	
	private List<DealerAccessoryPrice> dealerAccessoriesEffectivePricing;
	private UpfitterSearchResultVO autoCompleteUpfitter;
	private DealerAccessoryCode autoCompleteDealerAccessory;
	private List<UpfitterSearchResultVO> availableUpfitters;
	private String upfitterSearchTerm;
    private List<UpfitterPriceVO> assignedUpfitters;
    private UpfitterSearchResultVO selectedAvailableUpfitter;
    private UpfitterPriceVO selectedAssignedUpfitter;
    private String newDealerAccessoryCode;
    private String newDealerAccessoryDescription;
    private Long newDealerAccessoryLeadTime;
    private OptionAccessoryCategory newOptionAccessoryCategory;
    private DealerAccessoryCode selectedDealerAccessoryCode;
    private boolean addMode;
	private int tabIndex;    
	private List<OptionAccessoryCategory> accessoryCategories;
	private int assignedUpfitterCount;
	private String defaultDealerAccessoryCategoryCode;

	@PostConstruct
	public void init() {
		openPage();		
		initializeDataTable(550, 850, new int[] { 2, 5, 10, 10, 70, 10, 10, 15}).setHeight(0);
		//setAccessoryCategories(lookupCacheService.getOptionAccessoryCategories());
		setAccessoryCategories(modelService.getMafsOptionAccessoryCategories());
		defaultDealerAccessoryCategoryCode = willowConfigService.getConfigValue("DA_CATEGORY_DEFAULT_CODE");
		setAvailableUpfitters(new ArrayList<UpfitterSearchResultVO>());
		setAssignedUpfitters(new ArrayList<UpfitterPriceVO>());		
		initializeDealerAccessories();	
		setTabIndex(0);	
		super.setDirtyData(false);
	}
	
	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {			
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_MODEL_PRICING);
		super.thisPage.setPageUrl(ViewConstants.MODEL_PRICING);

		this.paramModelIds = new ArrayList<Long>();
		this.paramModelIds.add((Long)super.thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MDL_ID));
		
		this.models = new ArrayList<Model>();
		this.models.add(modelService.getModel(getParamModelIds().get(0)));
		
		this.model = this.models.get(0);
	}

	@SuppressWarnings("unchecked")
	protected void restoreOldPage() {
		setParamModelIds((List<Long>)thisPage.getRestoreStateValues().get(ViewConstants.VIEW_PARAM_MDL_IDS));
			
		this.models = new ArrayList<Model>();		
		this.models.add(modelService.getModel(getParamModelIds().get(0)));		
		this.model = this.models.get(0);		
	}

	public List<Long> getParamModelIds() {
		return paramModelIds;
	}
	
	public List<DealerAccessoryCode> autoCompleteDealerAccessories(String criteria){
		List<DealerAccessoryCode> searchDealerAccessoryCodes = null;
		List<DealerAccessoryCode> asisgnedDealerAccessoryCodes = null;
		
		searchDealerAccessoryCodes = modelService.getDealerAccessoryCodes(criteria, new PageRequest(0, 200, new Sort(Direction.ASC, "newAccessoryCode")));
		
		//Filter out dealer accessor(ies) that are already assigned to the model's trim		
		if(searchDealerAccessoryCodes.size() > 0){
			asisgnedDealerAccessoryCodes = findMatchingAssignedDealerAccessoryCodes(searchDealerAccessoryCodes);
		}
		
		searchDealerAccessoryCodes.removeAll(asisgnedDealerAccessoryCodes);
		setSelectedDealerAccessoryCode(null);		
		
		return searchDealerAccessoryCodes;
	}

	private List<DealerAccessoryCode> findMatchingAssignedDealerAccessoryCodes(
			List<DealerAccessoryCode> searchDealerAccessoryCodes) {
		List<DealerAccessoryCode> asisgnedDealerAccessoryCodes;
		asisgnedDealerAccessoryCodes = new ArrayList<DealerAccessoryCode>();
		for(DealerAccessory modelDealerAccessory : getModel().getDealerAccessories()){
			for(DealerAccessoryCode searchDealerAccessoryCode : searchDealerAccessoryCodes){
				if(modelDealerAccessory.getDealerAccessoryCode().equals(searchDealerAccessoryCode)){
					asisgnedDealerAccessoryCodes.add(searchDealerAccessoryCode);
					break;
				}
			}
		}
		return asisgnedDealerAccessoryCodes;
	}
    
    public void autoCompleteDealerAccessorySelectListener(SelectEvent event) {
        System.err.println(((DealerAccessoryCode)event.getObject()).getNewAccessoryCode());        
    }  
        
    public void searchAvailableUpffitersListener(){    	
    	if(MALUtilities.isNotEmptyString(getUpfitterSearchTerm())){    		
    		setAvailableUpfitters(searchAvailableUpfitters(getUpfitterSearchTerm()));        	  
    	} else {
    		getAvailableUpfitters().clear();
    	}
    	
    	initializeNoneUpfitter();
		sortAvailableUpfitters();
    }
	
	/**
	 * Sets the option pack header that was selected
	 * @param optionPackHeader OptionPackHeader 
	 */
	public void optionPackAccessoryListener(OptionPackHeader optionPackHeader){
		setSelectedOptionPackHeader(modelService.getOptionPack(optionPackHeader.getOphId()));
		sortOptionPackPrices(getSelectedOptionPackHeader().getOptionPackCosts());
		
		if(!MALUtilities.isEmpty(optionPackHeader.getOptionalAccessories()) 
				&& optionPackHeader.getOptionalAccessories().size() > 0){
			setSelectedOptionalAccessory(optionPackHeader.getOptionalAccessories().get(0));
		}
	}
	
	/**
	 * Sets the optional accessory that was selected
	 * @param DealerAccessory Selected factory accessory 
	 */
	public void modelEditPriceListener(){
		//TODO setup for editing model level pricing
		setSelectedModelPrices(modelService.getModelPrices(getModel().getModelId()));
	}	
	
	/**
	 * Sets the optional accessory that was selected
	 * @param DealerAccessory Selected factory accessory 
	 */
	public void factoryAccessoryListener(OptionalAccessory optionalAccessory){
		setSelectedOptionalAccessory(modelService.getFactoryAccessory(optionalAccessory.getOacId()));
		sortFactoryAccessoryPrices(getSelectedOptionalAccessory().getOptionPrices());
	}	
	
	/**
	 * Handles the view dealer accessory pricing history event.
	 * The selected dealer accessory and it's respective price list properties are set then sorted in DESC order.
	 * @param DealerAccessory Selected dealer accessory 
	 * @param ExternalAccount the vendor/upfitter account
	 */
	public void viewDealerAccessoryPricingListener(DealerAccessory dealerAccessory, ExternalAccount upfitterAccount){
		setSelectedDealerAccessory(dealerAccessory);
		setSelectedDealerAccessoryPrices(modelService.getDealerAccessoryPriceList(getSelectedDealerAccessory(), upfitterAccount));
		Collections.sort(getSelectedDealerAccessoryPrices(), new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) {
				int compareVal;
				
				if(dp2.getEffectiveDate().compareTo(dp1.getEffectiveDate()) == 0){
					compareVal = dp2.getDplId().compareTo(dp1.getDplId());
				} else {
					compareVal = dp2.getEffectiveDate().compareTo(dp1.getEffectiveDate()); 
				}
				return compareVal;
			}
		});	
	}
	
	/**
	 * Handles the event in which the user chooses to edit a dealer accessory pricing for a specific upfitter.
	 * This upfitter's price list is used to determine the effective price. The effective price is what is
	 * placed into the assigned vendor list for the accessory. 
	 * @param dealerAccessory The selected dealer accessory
	 * @param upfitterAccount The upfitter that supplies the accessory
	 */
	public void editDealerAccessoryListener(DealerAccessory dealerAccessory, ExternalAccount upfitterAccount){
		setAddMode(false);
		clearAddEditDealerAccessoriesDialog();		
		setSelectedDealerAccessory(dealerAccessory);	    		
		initializeAssignedUpfitters();	
		initializeNoneUpfitter();
		setValidToEditDealerAccessoriesInputs();		
	}

	/**
	 * Initializes the assigned upfitter pricing list for
	 * the selected dealer accessory
	 */
	private void initializeAssignedUpfitters() {
		UpfitterPriceVO assignedUpfitter;
		DealerAccessoryPrice dealerAccessoryEffectivePrice;
		Map<String, List<DealerAccessoryPrice>> groupedDealerAcessoryPrices;
	
		groupedDealerAcessoryPrices = groupDealerAccessoryPricing(getSelectedDealerAccessory());
		for(String vendorDACKey  : groupedDealerAcessoryPrices.keySet()){						
			assignedUpfitter = new UpfitterPriceVO();
			dealerAccessoryEffectivePrice = dealerAccessoryEffectivePricing(groupedDealerAcessoryPrices.get(vendorDACKey));

			// When a vendor is not specified, set the account code to -1 so that it 
			// can be identified by the UI as NO VENDOR
			if(!MALUtilities.isEmpty(dealerAccessoryEffectivePrice.getPayeeAccount())){
				assignedUpfitter.setPayeeAccountCode(dealerAccessoryEffectivePrice.getPayeeAccount().getExternalAccountPK().getAccountCode());
				assignedUpfitter.setPayeeAccountName((dealerAccessoryEffectivePrice.getPayeeAccount().getAccountName()));				
			} else {
				assignedUpfitter.setPayeeAccountCode("-1");
			}

			assignedUpfitter.setDealerPriceListId(dealerAccessoryEffectivePrice.getDplId());
			assignedUpfitter.setBasePrice(dealerAccessoryEffectivePrice.getBasePrice());
			assignedUpfitter.setTotalPrice(dealerAccessoryEffectivePrice.getTotalPrice());
			assignedUpfitter.setMsrp(dealerAccessoryEffectivePrice.getMsrp());
			assignedUpfitter.setEffectiveDate(dealerAccessoryEffectivePrice.getEffectiveDate());
			assignedUpfitter.setLeadTime(dealerAccessoryEffectivePrice.getLeadTime());
			
			if(!MALUtilities.isEmpty(dealerAccessoryEffectivePrice.getUpfitterQuote())){
				assignedUpfitter.setQuoteNumber(dealerAccessoryEffectivePrice.getUpfitterQuote().getQuoteNumber());				
			} 
			//assignedUpfitter.setQuoteNumber(MALUtilities.isEmpty(dealerAccessoryEffectivePrice.getUpfitterQuote()) ? null : dealerAccessoryEffectivePrice.getUpfitterQuote().getQuoteNumber());

			getAssignedUpfitters().add(assignedUpfitter);
		}
	}
	
	public void addDealerAccessoryListener(){
		setAddMode(true);
		setTabIndex(0);		
		clearAddEditDealerAccessoriesDialog();	
		initializeNoneUpfitter();	
		setValidToAddDealerAccessoriesInputs();
		setDefaultCategoryCode();
	}

	private void setDefaultCategoryCode() {
		if(!MALUtilities.isEmptyString(defaultDealerAccessoryCategoryCode)){
			for(OptionAccessoryCategory category : getAccessoryCategories()){
				if(category.getCode().equals(defaultDealerAccessoryCategoryCode)){
					setNewOptionAccessoryCategory(category);
					break;
				}
			}
		}
	}
	
	
	/**
	 * Handles the event of the user click on either the new or existing add dealer accessory dialog tabs.
	 * Focus seems to get lost when switching between tabs. As a result, setFocus is sent in the
	 * response to force focus on first input after n miliseconds.
	 * @param event
	 */
	public void newExistingTabChangeListener(TabChangeEvent event){
		System.err.println(event.getTab().getId() + "Active Index=" + getTabIndex());
		clearDealerAccessoryPostTab();
		searchAvailableUpffitersListener();
		initializeNoneUpfitter();
		
		//This a hack to force focus on the first field after switching tabs.
		if(getTabIndex() == 0){
			setDefaultCategoryCode();
			RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('newCodeTxt')}, 600)");					
			RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('newCodeTxt')}, 400)");			
		} else {
			RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('dealerAccessoryCodeAC_input')}, 600)");			
		}
	}	
	
	public void assignUpfitter(){		
		UpfitterPriceVO upfitterPriceVO;
		UpfitterSearchResultVO upfitterVO;
		
		upfitterPriceVO = new UpfitterPriceVO();
		upfitterVO = getSelectedAvailableUpfitter();
		
		upfitterPriceVO.setPayeeCorporateId(upfitterVO.getPayeeCorporateId());
		upfitterPriceVO.setPayeeAccountType(upfitterVO.getPayeeAccountType());
		upfitterPriceVO.setPayeeAccountCode(upfitterVO.getPayeeAccountCode());
		upfitterPriceVO.setPayeeAccountName(upfitterVO.getPayeeAccountName());
		
		getAssignedUpfitters().add(upfitterPriceVO);
		
		getAvailableUpfitters().remove(getSelectedAvailableUpfitter());
		
		setSelectedAssignedUpfitter(null);
		setSelectedAvailableUpfitter(null);
		
		setAssignedUpfitterCount(getAssignedUpfitters().size());
		
		super.setDirtyData(true);
		
		RequestContext context = RequestContext.getCurrentInstance(); 
		context.update("assignedUpfitterCount");
		context.execute("setDirtyData(true)");
		
	}
		
	public void assignUpfitters(){
		UpfitterPriceVO upfitterPriceVO;
		
		int i = 0;
		for(UpfitterSearchResultVO upfitterVO : getAvailableUpfitters()){
			upfitterPriceVO = new UpfitterPriceVO();			
			upfitterPriceVO.setPayeeCorporateId(upfitterVO.getPayeeCorporateId());
			upfitterPriceVO.setPayeeAccountType(upfitterVO.getPayeeAccountType());
			upfitterPriceVO.setPayeeAccountCode(upfitterVO.getPayeeAccountCode());
			upfitterPriceVO.setPayeeAccountName(upfitterVO.getPayeeAccountName());
			
			getAssignedUpfitters().add(upfitterPriceVO);
			i++;
		}
		
		getAvailableUpfitters().clear();
		
		setSelectedAssignedUpfitter(null);
		setSelectedAvailableUpfitter(null);
		
		setAssignedUpfitterCount(getAssignedUpfitters().size()-i+1);
		
		super.setDirtyData(true);
		RequestContext context = RequestContext.getCurrentInstance(); 
		context.update("assignedUpfitterCount");
		context.execute("setDirtyData(true)");
	}
	
	public void unassignUpFitter(){
		UpfitterPriceVO assignedUpfitterVO;
		UpfitterSearchResultVO availableUpfitterVO;				
		
		assignedUpfitterVO = getSelectedAssignedUpfitter();
		
		if(MALUtilities.isEmpty(assignedUpfitterVO.getDealerPriceListId())){
			availableUpfitterVO = new UpfitterSearchResultVO();
			availableUpfitterVO.setPayeeCorporateId(assignedUpfitterVO.getPayeeCorporateId());
			availableUpfitterVO.setPayeeAccountType(assignedUpfitterVO.getPayeeAccountType());
			availableUpfitterVO.setPayeeAccountCode(assignedUpfitterVO.getPayeeAccountCode());
			availableUpfitterVO.setPayeeAccountName(assignedUpfitterVO.getPayeeAccountName());

			getAvailableUpfitters().add(availableUpfitterVO);
			getAssignedUpfitters().remove(assignedUpfitterVO);
						
			sortAvailableUpfitters();

			super.setDirtyData(true);
			RequestContext.getCurrentInstance().execute("setDirtyData(true)");
		} else {			
			setSelectedAssignedUpfitter(null);
			setSelectedAvailableUpfitter(null);	
			addErrorMessageSummary("vendor.cannot.unassigned.error", "Selected") ;
		}
		
	}
	
	public void unassignUpFitters(){
		UpfitterSearchResultVO availableUpfitterVO;	
		List<UpfitterPriceVO> unassignedVendorVOs;
		List<UpfitterSearchResultVO> availableUpfitterVOs;
		
		unassignedVendorVOs = new ArrayList<UpfitterPriceVO>();
		availableUpfitterVOs = new ArrayList<UpfitterSearchResultVO>();
		boolean lockedVendor = false;
		for(UpfitterPriceVO assignedUpfitterVO : getAssignedUpfitters()){
			if(MALUtilities.isEmpty(assignedUpfitterVO.getDealerPriceListId())){
				if(isUpfitterPriceRowLocked(assignedUpfitterVO)){
					lockedVendor = true;					
				}
				
				availableUpfitterVO = new UpfitterSearchResultVO();
				availableUpfitterVO.setPayeeCorporateId(assignedUpfitterVO.getPayeeCorporateId());
				availableUpfitterVO.setPayeeAccountType(assignedUpfitterVO.getPayeeAccountType());
				availableUpfitterVO.setPayeeAccountCode(assignedUpfitterVO.getPayeeAccountCode());
				availableUpfitterVO.setPayeeAccountName(assignedUpfitterVO.getPayeeAccountName());			
				availableUpfitterVOs.add(availableUpfitterVO);

				unassignedVendorVOs.add(assignedUpfitterVO);
				//getAvailableUpfitters().add(availableUpfitterVO);		
			}else{
				lockedVendor = true ;
			}			
		}
		
		if(lockedVendor){
			addErrorMessageSummary("vendor.cannot.unassigned.error", "All") ;
			return;
		}
		
		getAssignedUpfitters().removeAll(unassignedVendorVOs);
		getAvailableUpfitters().addAll(availableUpfitterVOs);
		
		//getAssignedUpfitters().clear();		
		setSelectedAssignedUpfitter(null);
		setSelectedAvailableUpfitter(null);
		
		sortAvailableUpfitters();
		
		if(unassignedVendorVOs.size() > 0){
			super.setDirtyData(true);
			RequestContext.getCurrentInstance().execute("setDirtyData(true)");			
		}
	}
	
	public boolean hasOptionPackAccessories(OptionPackHeader optionPackHeader){
		boolean hasAccessories = false;
		hasAccessories = optionPackHeader.getOptionPackDetail().size() > 0 ? true : false;
		return hasAccessories;
	}
	
	public ModelPrice modelEffectivePricing(Model model){
		List<ModelPrice> modelPrices;		
		ModelPrice retModelPrice = null;
		
		//Sorts the list in ASC order
		modelPrices = model.getModelPrices();
		Collections.sort(modelPrices, new Comparator<ModelPrice>() { 
			public int compare(ModelPrice mp1, ModelPrice mp2) { 
				return mp1.getEffectiveDate().compareTo(mp2.getEffectiveDate()); 
			}
		});	
		
		for(ModelPrice modelPrice : modelPrices){
			if(modelPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
				retModelPrice = modelPrice;
			}
		}
		
		return retModelPrice;
	}
	
	public OptionPackCost optionPackEffectivePrice(OptionPackHeader optionPackHeader){
		List<OptionPackCost> optionPackCosts;
		OptionPackCost retOptionPackCost = null;
		
		//Sorts the list in ASC order
		optionPackCosts = new ArrayList<OptionPackCost>(optionPackHeader.getOptionPackCosts());
		Collections.sort(optionPackCosts, new Comparator<OptionPackCost>() { 
			public int compare(OptionPackCost opc1, OptionPackCost opc2) { 
				return opc1.getEffectiveDate().compareTo(opc2.getEffectiveDate()); 
			}
		});	
		
		for(OptionPackCost optionPackCost : optionPackCosts){
			if(optionPackCost.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
				retOptionPackCost = optionPackCost;
			}
		}
		
		return retOptionPackCost;
	}
			
	public OptionPrice factoryAccessoryEffectivePricing(OptionalAccessory factoryAccessory){
		List<OptionPrice> optionPrices;
		OptionPrice retOptionPrice = null;
		
		//Sorts the list in ASC order
		optionPrices = new ArrayList<OptionPrice>(factoryAccessory.getOptionPrices());
		Collections.sort(optionPrices, new Comparator<OptionPrice>() { 
			public int compare(OptionPrice op1, OptionPrice op2) { 
				return op1.getEffectiveDate().compareTo(op2.getEffectiveDate()); 
			}
		});	
		
		for(OptionPrice optionPrice : optionPrices){
			if(optionPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
				retOptionPrice = optionPrice;
			}
		}
		
		return retOptionPrice;
	}	
		
	/** 
	 * Extracts the effective price from a list of dealer accessory prices. A upfitter's effective price
	 * is defined as a the price with the max effective date that is less than or equal to today. If 
	 * one cannot be found, it is likely the price is future date. It should be returned to the caller.
	 * @param dealerAccessoryPrices 
	 * @return Either the effective or future dated pricing
	 */
	public DealerAccessoryPrice dealerAccessoryEffectivePricing(List<DealerAccessoryPrice> dealerAccessoryPrices){
		DealerAccessoryPrice retDealerAccessoryPrice = null;
		DealerAccessoryPrice futureDealerAccessoryPrice = null;
		
		//Sorts the list in ASC order
		Collections.sort(dealerAccessoryPrices, new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) {
				int compareValue;
				
				if(dp1.getEffectiveDate().compareTo(dp2.getEffectiveDate()) == 0){
					compareValue = dp1.getDplId().compareTo(dp2.getDplId());
				} else {
					compareValue = dp1.getEffectiveDate().compareTo(dp2.getEffectiveDate());
				}
				
				return compareValue; 
			}
		});	
		
		// Looking for the latest date in the list that is less than or equal to today's date
		// If an effective date was not found then looking for the first future date.		
		for(DealerAccessoryPrice dealerPrice : dealerAccessoryPrices){
			if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) <= 0){
				retDealerAccessoryPrice = dealerPrice;				
			}
			if(dealerPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) > 0
					&& MALUtilities.isEmpty(futureDealerAccessoryPrice)){
				futureDealerAccessoryPrice = dealerPrice;				
			}			
		}
		 
		// If an effective date was not found then looking for the first future date.			
		if(MALUtilities.isEmpty(retDealerAccessoryPrice)){
			retDealerAccessoryPrice = futureDealerAccessoryPrice;
		}
		
		return retDealerAccessoryPrice;
	}
		
	public void sortOptionPackPrices(List<OptionPackCost> optionPackPrices){
		//Sorting the option pack price list in ASC order
		Collections.sort(optionPackPrices, new Comparator<OptionPackCost>() { 
			public int compare(OptionPackCost opc1, OptionPackCost opc2) { 
				return opc2.getEffectiveDate().compareTo(opc1.getEffectiveDate()); 
			}
		});			
	}
	
	public void sortFactoryAccessoryPrices(List<OptionPrice> optionPrices){
		Collections.sort(optionPrices, new Comparator<OptionPrice>() { 
			public int compare(OptionPrice op1, OptionPrice op2) { 
				return op2.getEffectiveDate().compareTo(op1.getEffectiveDate()); 
			}
		});					
	}	
	
	public void sortAvailableUpfitters(){
		Collections.sort(getAvailableUpfitters(), new Comparator<UpfitterSearchResultVO>() { 
			public int compare(UpfitterSearchResultVO uf1, UpfitterSearchResultVO uf2) { 
				if(MALUtilities.isEmpty(uf1.getPayeeAccountName()) || MALUtilities.isEmpty(uf2.getPayeeAccountName())) {
					return -1;
				}
				return uf1.getPayeeAccountName().toLowerCase().compareTo(uf2.getPayeeAccountName().toLowerCase()); 
			}
		});					
	}
	
	public void sortDealerAccessoryEffectivePrices(List<DealerAccessoryPrice> dealerAccessoryPrices){
		Collections.sort(dealerAccessoryPrices, new Comparator<DealerAccessoryPrice>() { 
			public int compare(DealerAccessoryPrice dp1, DealerAccessoryPrice dp2) { 
				return dp1.getDealerAccessory()
						.getDealerAccessoryCode()
						.getNewAccessoryCode()
						.compareTo(dp2.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode()); 
			}
		});					
	}	
	
	/**
	 * Handles the event of saving changes made on the Add/Edit Dealer Accessories Dialog.
	 * In "Add" mode, determine whether the user is creating a new dealer accessory code or
	 * using an exist code that has not been previously assigned to the model. 
	 *     - New code: 
	 *         Get the dealer accessory code from the newDealerAccessoryCode and 
	 *         newDealerAccessoryDescription properties.
	 *     - Existing code:
	 *         Retrieve the dealer accessory code from the selectedDealerAccessoryCode property
	 * 
	 * In "Edit" mode, retrieve the dealer accessory from the selectedDealerAccessory property 
	 */
	public void saveDealerAccessory(boolean isStay){
		DealerAccessoryCode dealerAccessoryCode;		
		DealerAccessory dealerAccessory = null;
		DealerAccessoryPrice dealerAccessoryPrice;
		ExternalAccount payeeAccount;
		int indexOfDealerAccessory = -1;
		
		try {
			if(!validateAddDealerAccessoriesInput()){
				return;
			}
			
			//Determine whether creating a new or editing an existing dealer accessory
			if(isAddMode()){
				dealerAccessory = new DealerAccessory();
				dealerAccessory.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
				dealerAccessory.setModel(getModel());
				
				if(getTabIndex() == 0){
					dealerAccessoryCode = new DealerAccessoryCode();
					dealerAccessoryCode.setDealerAccessories(new ArrayList<DealerAccessory>());
					dealerAccessoryCode.setNewAccessoryCode(getNewDealerAccessoryCode());
					dealerAccessoryCode.setDescription(getNewDealerAccessoryDescription());
					dealerAccessoryCode.setLeadTime(getNewDealerAccessoryLeadTime());
					dealerAccessoryCode.setOptionAccessoryCategory(getNewOptionAccessoryCategory());
					dealerAccessoryCode.setCommonInd("N");
					setSelectedDealerAccessoryCode(dealerAccessoryCode);								
				} else {
					getSelectedDealerAccessoryCode().setDealerAccessories(new ArrayList<DealerAccessory>());
				}
				
				dealerAccessory.setDealerAccessoryCode(getSelectedDealerAccessoryCode());
				getSelectedDealerAccessoryCode().getDealerAccessories().add(dealerAccessory);			
			} else {
				dealerAccessory = getSelectedDealerAccessory();
				indexOfDealerAccessory = getModel().getDealerAccessories().indexOf(dealerAccessory);
			}
			
			//Maps the values from the assigned upfitter's pricing to the trim's dealer accessory price list
			for(UpfitterPriceVO assignedUpfitter : getAssignedUpfitters()){

				//Determines whether updates are being made to an existing upfitter pricing or a new upfitter is being added
				if(MALUtilities.isEmpty(assignedUpfitter.getDealerPriceListId())) {
					dealerAccessoryPrice = new DealerAccessoryPrice();
					dealerAccessoryPrice.setDealerAccessory(dealerAccessory);
				} else {
					dealerAccessoryPrice = getSelectedDealerAccessory().findDealerAccessoryPrice(assignedUpfitter.getDealerPriceListId());					
				}

				//Handles no vendor account info
				if(!assignedUpfitter.getPayeeAccountCode().equals("-1")){
					payeeAccount = upfitterService.getUpfitterAccount(assignedUpfitter.getPayeeAccountCode(), super.getLoggedInUser().getCorporateEntity());			    	
				} else {
					payeeAccount = null;
				}

				dealerAccessoryPrice.setPayeeAccount(payeeAccount);
				dealerAccessoryPrice.setLeadTime(assignedUpfitter.getLeadTime());
				dealerAccessoryPrice.setBasePrice(assignedUpfitter.getBasePrice());
				dealerAccessoryPrice.setTotalPrice(assignedUpfitter.getBasePrice());
				dealerAccessoryPrice.setMsrp(assignedUpfitter.getMsrp());
				dealerAccessoryPrice.setVatAmount(new BigDecimal(0));
				dealerAccessoryPrice.setEffectiveDate(assignedUpfitter.getEffectiveDate());
				//dealerAccessoryPrice.setDealerAccessory(dealerAccessory);

				//Add new pricing when in adding a new dealer accessory. Otherwise, we are just updating an existing price.
				if(isAddMode() || MALUtilities.isEmpty(dealerAccessoryPrice.getDplId())){
					dealerAccessory.getDealerAccessoryPrices().add(dealerAccessoryPrice);					
				} 
									
			}
					
				
			dealerAccessory = modelService.saveOrUpdateDealerAccessory(dealerAccessory);
			dealerAccessory = modelService.getDealerAccessory(dealerAccessory.getDacId());
			
			if(isAddMode()){
				getModel().getDealerAccessories().add(dealerAccessory);			
			} else {
				getModel().getDealerAccessories().set(indexOfDealerAccessory, dealerAccessory);
				setSelectedDealerAccessory(dealerAccessory);				
				initializeAssignedUpfitters();					
			}
			
			if(!isStay){
				RequestContext.getCurrentInstance().execute("PF('addDealerAccessoryDialogWidget').hide()");
				super.addSuccessMessage("process.success","Save Dealer Accessory (" + dealerAccessory.getDealerAccessoryCode().getNewAccessoryCode() + ")");
			} else {
				// OTD-736: HACK - for setting focus after save and stay
				if(isAddMode()){
					RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('newCodeTxt');}, 500)");					
				} else {
					RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('dealerAccessoryCodeAC')}, 500)");					
				}
				
				super.addInfoMessageSummary("process.success","Save Dealer Accessory (" + dealerAccessory.getDealerAccessoryCode().getNewAccessoryCode() + ")");
				clearDealerAccessoryPostSaveAndStay();
			}
						
			initializeDealerAccessories();
			
			super.setDirtyData(false);
			RequestContext.getCurrentInstance().execute("setDirtyData(false)");	
						
			
		} catch (MalBusinessException mbe){
			if(!isAddMode()){			
				dealerAccessory = modelService.getDealerAccessory(dealerAccessory.getDacId());
				getModel().getDealerAccessories().set(indexOfDealerAccessory, dealerAccessory);				
				setSelectedDealerAccessory(dealerAccessory);
				initializeDealerAccessories();
			}
			super.setDirtyData(true);
			RequestContext.getCurrentInstance().execute("setDirtyData(true)");
			super.addErrorMessageSummary("custom.message", mbe.getMessage());			
		} catch (Exception e) {			
			super.setDirtyData(true);
			RequestContext.getCurrentInstance().execute("setDirtyData(true)");
			super.addErrorMessageSummary("custom.message", e.getMessage());
		} 
		
	}
	
	public void saveModelPricing(){
		List<ModelPrice> modelPrices;
		
		modelPrices = modelService.saveOrUpdateModelPricing(getSelectedModelPrices());
		
		getModel().getModelPrices().clear();
		getModel().setModelPrices(modelPrices);
	}	
	
	public void saveOptionPackPricing(){
		OptionPackHeader optionPack;
		int index;
		
		index = getModel().getOptionPackHeaders().indexOf(getSelectedOptionPackHeader());
		optionPack = modelService.saveOrUpdateOptionPackPricing(getSelectedOptionPackHeader());
		getModel().getOptionPackHeaders().set(index, optionPack);	
		setSelectedOptionPackHeader(null);		
	}
	
	public void saveFactoryAccessoryPricing(){
	    OptionalAccessory optionalAccessory;
	    int index;
	    
	    index = getModel().getOptionalAccessories().indexOf(getSelectedOptionalAccessory());
	    optionalAccessory = modelService.saveOrUpdateFactoryAccessoryPricing(getSelectedOptionalAccessory());
	    setSelectedOptionalAccessory(optionalAccessory);
	    getModel().getOptionalAccessories().set(index, optionalAccessory);
	    
		super.addSuccessMessage("process.success","Update Factory Accessory (" + getSelectedOptionalAccessory().getAccessoryCode().getNewAccessoryCode() + ")");	    
	}
	
    /**
     * Handles page cancel button click event. 
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    
    /**
     * Navigates to the view that will used to maintain the 
     * upfitter's quote
     */
    public void maintainUpfitterQuote(){
		Map<String, Object> restoreStateValues = new HashMap<String, Object>();    	
		Map<String, Object> nextPageValues = new HashMap<String, Object>();

		restoreStateValues.put(ViewConstants.VIEW_PARAM_MDL_IDS, getParamModelIds());
		saveRestoreStateValues(restoreStateValues);
		
		nextPageValues.put(ViewConstants.VIEW_PARAM_MDL_ID, getModel().getModelId());		
		saveNextPageInitStateValues(nextPageValues);		
		
		forwardToURL(ViewConstants.UPFITTER_QUOTE);    	
    }    
    
			
	/**
	 * Used by the autocomplete control to retrieve a list of upfitters based on
	 * the user's input. At most, 5 items will be returned.
	 * @param criteria
	 * @return
	 */
	public List<UpfitterSearchResultVO> searchAvailableUpfitters(String criteria){
		List<UpfitterSearchResultVO> availableUpfitterVOs = new ArrayList<UpfitterSearchResultVO>();
		List<UpfitterSearchResultVO> availableUpfitterVOsToRemove = new ArrayList<UpfitterSearchResultVO>();
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();
		
		searchCriteria.setTerm(criteria);	
		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
		availableUpfitterVOs = upfitterService.searchUpfitters(searchCriteria, new PageRequest(0,50), null);
		
		//Filter out the upfitters that are currently assigned. An upfitter cannot be assigned to a
		//dealer accessory twice.
		for(UpfitterPriceVO assignedUpfitterVO : getAssignedUpfitters()){
			for(UpfitterSearchResultVO availableUpfitterVO : availableUpfitterVOs){
				if(assignedUpfitterVO.getPayeeAccountCode().equals(availableUpfitterVO.getPayeeAccountCode())){
					availableUpfitterVOsToRemove.add(availableUpfitterVO);
					break;
				}
			}
		}		
		availableUpfitterVOs.removeAll(availableUpfitterVOsToRemove);
		
		return availableUpfitterVOs;
	}
	
	/**
	 * Determines which lead time to display. If a lead time has not
	 * been specified at the upfitter price level, then use the default
	 * lead time specified at the dealer accessory level.
	 * @param dealerAccessoryPrice The effective upfitter price
	 * @return Long The determined lead time for the upfitter accessory
	 */
	public Long determineLeadTime(DealerAccessoryPrice dealerAccessoryPrice){
	    Long leadTime;
	    leadTime = dealerAccessoryPrice.getLeadTime();
	    if(MALUtilities.isEmpty(leadTime)){
	    	leadTime = dealerAccessoryPrice.getDealerAccessory().getDealerAccessoryCode().getLeadTime();
	    }
	    return leadTime;
	}
	
	public boolean isUpfitterPriceRowLocked(UpfitterPriceVO upfitterPriceVO){
		boolean isLocked = false;
		
		if(MALUtilities.isEmpty(upfitterPriceVO.getDealerPriceListId())){
			isLocked = false;
		} else {
			isLocked = true;
		}
		
		return isLocked;
	}
	
	public String selectedDealerAccessoryUpfitter(){
		String retUpfitterName;
		if(!MALUtilities.isEmpty(getSelectedDealerAccessoryPrices())
				&& getSelectedDealerAccessoryPrices().size() > 0 
				&& !MALUtilities.isEmpty(getSelectedDealerAccessoryPrices().get(0).getPayeeAccount())){
			retUpfitterName = getSelectedDealerAccessoryPrices().get(0).getPayeeAccount().getAccountName();
		} else {
			retUpfitterName = "NO VENDOR";
		}
		return retUpfitterName;
	}
	
	private void clearAddEditDealerAccessoriesDialog(){
		getAvailableUpfitters().clear();
		getAssignedUpfitters().clear();
		setSelectedAvailableUpfitter(null);
		setSelectedAssignedUpfitter(null);
		setSelectedDealerAccessoryCode(null);
		setSelectedDealerAccessory(null);
		setNewDealerAccessoryCode(null);
		setNewDealerAccessoryDescription(null);
		setNewDealerAccessoryLeadTime(null);
		setNewOptionAccessoryCategory(null);
		setUpfitterSearchTerm(null);
		initializeDealerAccessories();
	}
	
	private void clearDealerAccessoryPostSaveAndStay(){
		getAssignedUpfitters().clear();
		setSelectedAvailableUpfitter(null);
		setSelectedAssignedUpfitter(null);
		setSelectedDealerAccessoryCode(null);
		setSelectedDealerAccessory(null);
		setNewDealerAccessoryCode(null);
		setNewDealerAccessoryDescription(null);
		setNewDealerAccessoryLeadTime(null);
		setNewOptionAccessoryCategory(null);
		searchAvailableUpffitersListener();
	}
	
	private void clearDealerAccessoryPostTab(){
		getAvailableUpfitters().clear();
		getAssignedUpfitters().clear();
		setSelectedAvailableUpfitter(null);
		setSelectedAssignedUpfitter(null);
		setSelectedDealerAccessoryCode(null);
		setSelectedDealerAccessory(null);
		setNewDealerAccessoryCode(null);
		setNewDealerAccessoryDescription(null);
		setNewDealerAccessoryLeadTime(null);
		setNewOptionAccessoryCategory(null);
		initializeDealerAccessories();
	}	

	private void initializeDealerAccessories(){
		Map<String, List<DealerAccessoryPrice>> dacMap = new HashMap<String, List<DealerAccessoryPrice>>();	
		DealerAccessoryPrice effectiveUpfitterAccessoryPrice;
		DealerAccessoryPrice clonedEffectiveUpfitterAccessoryPrice;		
			
		//Groups each dealer accessory on payee and dealer accessory code and map them to their respective price list
		for(DealerAccessory dac : model.getDealerAccessories()){
			dacMap.putAll(groupDealerAccessoryPricing(dac));
		}		
 		
		// For each dealer accessory (payee/dealer accessory code), extract effective price from the price list
		// If accessory has an upfitter pricing that is effective in the future, set its pricing to $0. 
		// Note: Clone the price first before updating it. Otherwise, you will be updating the dealer accessory price
		//       that is reused by the dialog(s).
		setDealerAccessoriesEffectivePricing(new ArrayList<DealerAccessoryPrice>());
		for(String vendorAccessoryCodeKey : dacMap.keySet()){
			effectiveUpfitterAccessoryPrice = dealerAccessoryEffectivePricing(dacMap.get(vendorAccessoryCodeKey));			
			
			if(MALUtilities.clearTimeFromDate(effectiveUpfitterAccessoryPrice.getEffectiveDate())
					.compareTo(MALUtilities.clearTimeFromDate(Calendar.getInstance().getTime())) > 0){
				clonedEffectiveUpfitterAccessoryPrice = new DealerAccessoryPrice();
				BeanUtils.copyProperties(effectiveUpfitterAccessoryPrice, clonedEffectiveUpfitterAccessoryPrice);				
				effectiveUpfitterAccessoryPrice = clonedEffectiveUpfitterAccessoryPrice;
				effectiveUpfitterAccessoryPrice.setBasePrice(new BigDecimal(0));
				effectiveUpfitterAccessoryPrice.setMsrp(new BigDecimal(0));
				effectiveUpfitterAccessoryPrice.setTotalPrice(new BigDecimal(0));
			}
			
			getDealerAccessoriesEffectivePricing().add(effectiveUpfitterAccessoryPrice);			
		}	
		
		//Sort on dealer accessory code in ASC order
		sortDealerAccessoryEffectivePrices(getDealerAccessoriesEffectivePricing());

	}
	
	/**
	 * Adds an upfitter with no payee account. This type of upfitter is only added
	 * when it has not already been assigned to the dealer accessory. At the time of writing,
	 * Willow users can add pricing to a dealer accessory without specifying a vendor. This
	 * method will assist in emulating Willow's functionality.
	 */
	private void initializeNoneUpfitter(){
		boolean noneVendorFound = false;
		
		//If a dealer accessory was selected, i.e. adding/editing an existing dlr accessory we check
		//whether the accessory has a NO VENDOR pricing.
		if(!MALUtilities.isEmpty(getSelectedDealerAccessory())){
			for(DealerAccessoryPrice upfitterPrice : getSelectedDealerAccessory().getDealerAccessoryPrices()){
				if(MALUtilities.isEmpty(upfitterPrice.getPayeeAccount())){
					noneVendorFound = true;
					break;
				}
			}
		}
		
		
		for(UpfitterPriceVO assignedVendorVO : getAssignedUpfitters()){
			if(assignedVendorVO.getPayeeAccountCode().equals("-1")){
			    noneVendorFound = true;
			    break;
			}
		}
		
		if(!noneVendorFound){
			UpfitterSearchResultVO availableNoneUpfitter = new UpfitterSearchResultVO();
			availableNoneUpfitter.setPayeeAccountCode("-1");
			
			if(getAvailableUpfitters().size() > 0
					&& getAvailableUpfitters().get(0).getPayeeAccountCode().equals(availableNoneUpfitter.getPayeeAccountCode())){
				getAvailableUpfitters().remove(0);
			}
			
			getAvailableUpfitters().add(0, availableNoneUpfitter);
		}
	}
	
	/**
	 * Set valid to all input fields on init of add dealer accessories.
	 */
	private void setValidToAddDealerAccessoriesInputs(){				
		UIComponent comp = getComponent(UI_ID_NEW_DEALER_ACCESSORY_CODE);
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 
		comp = getComponent(UI_ID_NEW_DEALER_ACCESSORY_DESCRIPTION);
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 
		comp = getComponent(UI_ID_NEW_DEALER_ACCESSORY_CATEGORY);
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 		   	 				
	}
	
	/**
	 * Set valid to all input fields on init of add dealer accessories.
	 */
	private void setValidToEditDealerAccessoriesInputs(){				
		UIComponent comp = getComponent(UI_ID_NEW_DEALER_ACCESSORY_CODE);
		comp = getComponent(UI_ID_SELECTED_DEALER_ACCESSORY_DESCRIPTION);
   	 	if(comp!= null) ((UIInput) comp).setValid(true); 	   	 				
   	 	comp = getComponent(UI_ID_SELECTED_DEALER_ACCESSORY_CATEGORY);
	 	if(comp!= null) ((UIInput) comp).setValid(true); 
	}	
	
	/**
	 * Groups the price lists by upfitter/external account and accessory code
	 * The first if statement checks for null vendor, when one does not exist then the null vendor is 
	 * added to the list. Each subsequent iterations where the null vendor exists the flow drops into the else 
	 * statement and adds pricing to the null vendor
	 * @param dealerAccessory Dealer Accessory
	 * @return Map containing the pricess list for each vendor account dealer accessory code
	 */
	private Map<String, List<DealerAccessoryPrice>> groupDealerAccessoryPricing(DealerAccessory dealerAccessory){
		Map<String, List<DealerAccessoryPrice>> dacMap = new HashMap<String, List<DealerAccessoryPrice>>();
		List<DealerAccessoryPrice> dealerAccessoryPrices;
		String upfitterAccountCode;
		
		for(DealerAccessoryPrice dacPrice : dealerAccessory.getDealerAccessoryPrices()){
			upfitterAccountCode = MALUtilities.isEmpty(dacPrice.getPayeeAccount()) ? null : dacPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
			
			if(MALUtilities.isEmpty(dacMap.get(upfitterAccountCode + dacPrice.getDealerAccessory().getDealerAccessoryCode().getAccessoryCode()))) { 
				dealerAccessoryPrices = new ArrayList<DealerAccessoryPrice>();
				dealerAccessoryPrices.add(dacPrice); 
				dacMap.put(upfitterAccountCode + dacPrice.getDealerAccessory().getDealerAccessoryCode().getAccessoryCode(), dealerAccessoryPrices);			
			} else {				
				dacMap.get(upfitterAccountCode + dacPrice.getDealerAccessory().getDealerAccessoryCode().getAccessoryCode()).add(dacPrice);						
			}
		}
		
		return dacMap;
	}
	
	/**
	 * Performs the necessary UI input validations necessary for a save.
	 * @return
	 */
	private boolean validateAddDealerAccessoriesInput(){
		boolean isValid = true;
		StringBuilder message = new StringBuilder();
		String upfitterName;
		List<DealerAccessoryCode> dealerAccessoryCodes;
		
		
		if(isAddMode()){ //Adding a dealer accessory
			if(getTabIndex() == 0){ //"New" dealer accessory
				if(modelService.isCodeInSystem(getNewDealerAccessoryCode())){
					super.addErrorMessageSummary(UI_ID_NEW_DEALER_ACCESSORY_CODE, "custom.message", "Code already exists");	
					isValid = false;					
				}
				if(MALUtilities.isEmpty(getNewDealerAccessoryCode())){
					super.addErrorMessageSummary(UI_ID_NEW_DEALER_ACCESSORY_CODE, "required.field", "Code");	
					isValid = false;
				}
				if(MALUtilities.isEmpty(getNewDealerAccessoryDescription())){
					super.addErrorMessageSummary(UI_ID_NEW_DEALER_ACCESSORY_DESCRIPTION, "required.field", "Description");
					isValid = false;			
				}
				if(MALUtilities.isEmpty(getNewOptionAccessoryCategory())){
					super.addErrorMessageSummary(UI_ID_NEW_DEALER_ACCESSORY_CATEGORY, "required.field", "Category");
					isValid = false;			
				}
			} else { //"Existing" dealer accessory
				if(MALUtilities.isEmpty(getSelectedDealerAccessoryCode())){
					super.addErrorMessageSummary(UI_ID_SELECTED_DEALER_ACCESSORY_CODE_AC, "required.field", "Dealer Accessory");	
					isValid = false;
				}			
				if(!MALUtilities.isEmpty(getSelectedDealerAccessoryCode())){
					dealerAccessoryCodes = new ArrayList<DealerAccessoryCode>(); 
					dealerAccessoryCodes.add(getSelectedDealerAccessoryCode());
					
					if(findMatchingAssignedDealerAccessoryCodes(dealerAccessoryCodes).size() > 0){
						super.addErrorMessageSummary(UI_ID_SELECTED_DEALER_ACCESSORY_CODE_AC, "custom.message", "Dealer Accessory has previously been assigned to the trim");	
						isValid = false;						
					}
				}
			}
		} else {
			if(MALUtilities.isEmpty(getSelectedDealerAccessory().getDealerAccessoryCode().getDescription())){
				super.addErrorMessageSummary(UI_ID_SELECTED_DEALER_ACCESSORY_DESCRIPTION, "required.field", "Description");	
				isValid = false;
		}
			if(MALUtilities.isEmpty(getSelectedDealerAccessory().getDealerAccessoryCode().getOptionAccessoryCategory())){
				super.addErrorMessageSummary(UI_ID_SELECTED_DEALER_ACCESSORY_CATEGORY, "required.field", "Category");
				isValid = false;			
			}
		}
		
		//At least one upfitter pricing is required
		if(getAssignedUpfitters().size() < 1){
			super.addErrorMessageSummary("required.field", "Price");
			isValid = false;			
		}
		
//		//Checking for unique vendor and price combination when editing a dealer accessory
//		if(!MALUtilities.isEmpty(selectedDealerAccessory)){
//			for(UpfitterPriceVO assignedUpfitterPrice : getAssignedUpfitters()){			
//				String upfitterAccountCode = modelService.decodeNullString(assignedUpfitterPrice.getPayeeAccountCode(), "-1");
//				Date assignedUpfitterPriceEffectiveDate = MALUtilities.clearTimeFromDate(assignedUpfitterPrice.getEffectiveDate());
//
//				for(DealerAccessoryPrice otherAssignedUpfitterPrice : getSelectedDealerAccessory().getDealerAccessoryPrices()){
//					String otherUpfitterAccountCode = MALUtilities.isEmpty(otherAssignedUpfitterPrice.getPayeeAccount()) ? "-1" : otherAssignedUpfitterPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
//					Date otherAssignedUpfitterPriceEffectiveDate = MALUtilities.clearTimeFromDate(otherAssignedUpfitterPrice.getEffectiveDate());
//					
//					if(upfitterAccountCode.equals(otherUpfitterAccountCode)
//							&& assignedUpfitterPriceEffectiveDate.compareTo(otherAssignedUpfitterPriceEffectiveDate) == 0){
//						super.addErrorMessageSummary("custom.message", 
//								(upfitterAccountCode.equals("-1") ? "NO VENDOR" : assignedUpfitterPrice.getPayeeAccountName()) + "  already has a price for this Effective Date. Please change the Effective Date to create a unique entry");
//						isValid = false;
//						break;
//					}				
//				}
//			}
//		}
		
		//Checking assigned upfitter's required fields 
		for(UpfitterPriceVO upfitterPrice : getAssignedUpfitters()){
			message.setLength(0);
						
			if(MALUtilities.isEmpty(upfitterPrice.getBasePrice())){
				message.append(message.length() > 0 ? ", Price " : "Price");
				isValid = false;				
			}
			
			if(MALUtilities.isEmpty(upfitterPrice.getEffectiveDate())){
				message.append(message.length() > 0 ? ", Effective Date " : "Effective Date");
				isValid = false;				
			}			
			
			if(message.length() > 0){ 
				upfitterName = MALUtilities.isEmpty(upfitterPrice.getPayeeAccountName()) ? "NO VENDOR " : upfitterPrice.getPayeeAccountName();
				super.addErrorMessageSummary("required.field", upfitterName + " " + message.toString());
				isValid = false;				
			}
		}
				
		return isValid;
	}
	
	/**
	 * Performs the necessary UI input validations necessary for a save.
	 * @return
	 */
	private boolean validateEditDealerAccessoriesInput(){
		boolean isValid = false;
		StringBuilder message = new StringBuilder();
		DealerAccessory dealerAccessory = getSelectedDealerAccessory();
		
		if(MALUtilities.isEmpty(dealerAccessory.getDealerAccessoryCode())){
			message.append("Dealer accessory code is required.");
			
		}
		if(!MALUtilities.isEmpty(dealerAccessory) && MALUtilities.isEmpty(dealerAccessory.getDealerAccessoryCode().getNewAccessoryCode())){
			message.append("Dealer accessory code is required.");
		}
		if(!MALUtilities.isEmpty(dealerAccessory) && MALUtilities.isEmpty(dealerAccessory.getDealerAccessoryCode().getDescription())){
			message.append("Dealer Accessory description is required.");
		}	
		if(!MALUtilities.isEmpty(dealerAccessory.getDealerAccessoryCode().getOptionAccessoryCategory())){
			message.append("Dealer Accessory category is required.");
		}		
		
		super.addErrorMessage(message.toString());
		
		return isValid;
	}
	
	public void setParamModelIds(List<Long> paramModelIds) {
		this.paramModelIds = paramModelIds;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public List<ModelPrice> getSelectedModelPrices() {
		return selectedModelPrices;
	}

	public void setSelectedModelPrices(List<ModelPrice> selectedModelPrices) {
		this.selectedModelPrices = selectedModelPrices;
	}

	public OptionPackHeader getSelectedOptionPackHeader() {
		return selectedOptionPackHeader;
	}

	public void setSelectedOptionPackHeader(OptionPackHeader selectedOptionPackHeader) {
		this.selectedOptionPackHeader = selectedOptionPackHeader;
	}

	public OptionalAccessory getSelectedOptionalAccessory() {
		return selectedOptionalAccessory;
	}

	public void setSelectedOptionalAccessory(OptionalAccessory selectedOptionalAccessory) {
		this.selectedOptionalAccessory = selectedOptionalAccessory;
	}

	public DealerAccessory getSelectedDealerAccessory() {
		return selectedDealerAccessory;
	}

	public void setSelectedDealerAccessory(DealerAccessory selectedDealerAccessory) {
		this.selectedDealerAccessory = selectedDealerAccessory;
	}

	public List<DealerAccessoryPrice> getSelectedDealerAccessoryPrices() {
		return selectedDealerAccessoryPrices;
	}

	public void setSelectedDealerAccessoryPrices(
			List<DealerAccessoryPrice> selectedDealerAccessoryPrices) {
		this.selectedDealerAccessoryPrices = selectedDealerAccessoryPrices;
	}

	public List<DealerAccessoryPrice> getDealerAccessoriesEffectivePricing() {
		return dealerAccessoriesEffectivePricing;
	}

	public void setDealerAccessoriesEffectivePricing(
			List<DealerAccessoryPrice> dealerAccessoriesEffectivePricing) {
		this.dealerAccessoriesEffectivePricing = dealerAccessoriesEffectivePricing;
	}
	
	public UpfitterSearchResultVO getAutoCompleteUpfitter() {
		return autoCompleteUpfitter;
	}

	public void setAutoCompleteUpfitter(UpfitterSearchResultVO autoCompleteUpfitter) {
		this.autoCompleteUpfitter = autoCompleteUpfitter;
	}

	public DealerAccessoryCode getAutoCompleteDealerAccessory() {
		return autoCompleteDealerAccessory;
	}

	public void setAutoCompleteDealerAccessory(
			DealerAccessoryCode autoCompleteDealerAccessory) {
		this.autoCompleteDealerAccessory = autoCompleteDealerAccessory;
	}

	public List<UpfitterSearchResultVO> getAvailableUpfitters() {
		return availableUpfitters;
	}

	public void setAvailableUpfitters(List<UpfitterSearchResultVO> availableUpfitters) {
		this.availableUpfitters = availableUpfitters;
	}

	public String getUpfitterSearchTerm() {
		return upfitterSearchTerm;
	}

	public void setUpfitterSearchTerm(String upfitterSearchTerm) {
		this.upfitterSearchTerm = upfitterSearchTerm;
	}

	public List<UpfitterPriceVO> getAssignedUpfitters() {
		return assignedUpfitters;
	}

	public void setAssignedUpfitters(List<UpfitterPriceVO> assignedUpfitters) {
		this.assignedUpfitters = assignedUpfitters;
	}

	public UpfitterSearchResultVO getSelectedAvailableUpfitter() {
		return selectedAvailableUpfitter;
	}

	public void setSelectedAvailableUpfitter(UpfitterSearchResultVO selectedAvailableUpfitter) {
		this.selectedAvailableUpfitter = selectedAvailableUpfitter;
	}

	public UpfitterPriceVO getSelectedAssignedUpfitter() {
		return selectedAssignedUpfitter;
	}

	public void setSelectedAssignedUpfitter(UpfitterPriceVO selectedAssignedUpfitter) {
		this.selectedAssignedUpfitter = selectedAssignedUpfitter;
	}

	public String getNewDealerAccessoryCode() {
		return newDealerAccessoryCode;
	}

	public void setNewDealerAccessoryCode(String newDealerAccessoryCode) {
		this.newDealerAccessoryCode = newDealerAccessoryCode;
	}

	public String getNewDealerAccessoryDescription() {
		return newDealerAccessoryDescription;
	}

	public void setNewDealerAccessoryDescription(
			String newDealerAccessoryDescription) {
		this.newDealerAccessoryDescription = newDealerAccessoryDescription;
	}

	public Long getNewDealerAccessoryLeadTime() {
		return newDealerAccessoryLeadTime;
	}

	public void setNewDealerAccessoryLeadTime(Long newDealerAccessoryLeadTime) {
		this.newDealerAccessoryLeadTime = newDealerAccessoryLeadTime;
	}

	public OptionAccessoryCategory getNewOptionAccessoryCategory() {
		return newOptionAccessoryCategory;
	}

	public void setNewOptionAccessoryCategory(OptionAccessoryCategory newOptionAccessoryCategory) {
		this.newOptionAccessoryCategory = newOptionAccessoryCategory;
	}

	public DealerAccessoryCode getSelectedDealerAccessoryCode() {
		return selectedDealerAccessoryCode;
	}

	public void setSelectedDealerAccessoryCode(
			DealerAccessoryCode selectedDealerAccessoryCode) {
		this.selectedDealerAccessoryCode = selectedDealerAccessoryCode;
	}

	public boolean isAddMode() {
		return addMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public List<OptionAccessoryCategory> getAccessoryCategories() {
		return accessoryCategories;
	}

	public void setAccessoryCategories(List<OptionAccessoryCategory> accessoryCategories) {
		this.accessoryCategories = accessoryCategories;
	}

	public int getAssignedUpfitterCount() {
		return assignedUpfitterCount;
	}

	public void setAssignedUpfitterCount(int assignedUpfitterCount) {
		this.assignedUpfitterCount = assignedUpfitterCount;
	}
}
