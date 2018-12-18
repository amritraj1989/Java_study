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
import javax.faces.event.AjaxBehaviorEvent;

import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.mikealbert.data.comparator.DealerAccessoryPriceComparator;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ProductDAO;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.Product;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.enumeration.AccountStatusEnum;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;
import com.mikealbert.vision.vo.QuoteDealerAccesoryVO;
import com.mikealbert.vision.vo.QuoteOverviewVO;
import com.mikealbert.vision.vo.UpfitterPriceAddressVO;

import edu.emory.mathcs.backport.java.util.Arrays;


@Component
@Scope("view")
public class MaintainQuoteDealerAccessoryBean extends StatefulBaseBean {
	private static final long serialVersionUID = 3073097329909377333L;

	@Resource QuotationService quotationService;
	@Resource FleetMasterService fleetMasterService;
	@Resource ProductDAO productDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource ModelService modelService;
	@Resource UpfitterService upfitterService;
	@Resource RentalCalculationService rentalCalculationService;
	@Resource WillowConfigService willowConfigService;
	
	private static final String UI_ID_SELECTED_DEALER_ACCESSORY = "dealerAccessoryCode";
	private static final String UI_ID_SELECTED_RECHARGE_AMOUNT = "recharge";
	private static final String UI_ID_SELECTED_VENDOR = "trimVendors";
	private static final String UI_ID_SELECTED_VENDOR_QUOTE = "vendorQuoteNumbers";
	private static final String UI_ID_SELECTED_ACCESSORY_VENDOR = "upfitter";
	
	private String qmdId;
	private QuotationModel quotationModel;
	private Model model;
	private List<QuotationDealerAccessory> quoteDealerAccessories = new ArrayList<QuotationDealerAccessory>();
	private QuotationDealerAccessory selectedQuotationDealerAccessory;
	private DealerAccessory selectedDealerAccessory;
	private List<DealerAccessoryPrice> availablePrices = new ArrayList<DealerAccessoryPrice>();
	private UpfitterPriceAddressVO selectedUpfitter;
	private UpfitterPriceAddressVO selectedNoVendorQuoteUpfitter;
	private UpfitterPriceAddressVO selectedQuoteUpfitter;
	private List<UpfitterPriceAddressVO> availableUpfitters = new ArrayList<UpfitterPriceAddressVO>();
	private List<UpfitterPriceAddressVO> availableQuoteUpfitters = new ArrayList<UpfitterPriceAddressVO>();
	private QuoteOverviewVO quoteOverviewVO;
	private QuoteDealerAccesoryVO selectedQuoteDealerAccesoryVO;
	private Long qmReplacementFmsId;
	private FleetMaster fleetMaster;
	private boolean addMode;
	private boolean noVendorPriceFound;
	private boolean addEditAllowed;
	private boolean focusAllowed;
	
	private List<UpfitterSearchResultVO> availableVendors = new ArrayList<UpfitterSearchResultVO>();
	private UpfitterSearchResultVO selectedVendor;
	private List<UpfitterQuote> availableQuotes = new ArrayList<UpfitterQuote>();
	private UpfitterQuote selectedQuote;
	
	private List<DealerAccessoryPrice> availableDealerAccessories = new ArrayList<DealerAccessoryPrice>();

	@PostConstruct
	public void init() {
		openPage();		
		initializeDataTable(350, 850, new int[] { 30, 18, 10, 6, 6, 6, 2, 4, 3,15}).setHeight(250);
		try {
			quotationModel = quotationService.getQuotationModel(Long.parseLong(getQmdId()));
			setModel(quotationModel.getModel());
			
			loadHeaderData();
			initializeQuotationDealerAccessories();
			
			setAddEditAllowed(canAddEditAccessories());
			super.setDirtyData(false);
		} catch (Exception e) {
			logger.error(e);
			if(e instanceof MalBusinessException) {
				super.addErrorMessage(e.getMessage());
			} else {
				super.addErrorMessage("generic.error.occured.while", " loading quotation dealer accessory screen.");
			}
		}
	}

	protected void loadNewPage() {
		thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_MAINT_QUOTE_DLR_ACCSRY);
		thisPage.setPageUrl(ViewConstants.QUOTE_DLR_ACCSRY);
		
		Map<String, Object> map = super.thisPage.getInputValues();		
		setQmdId((String)map.get(ViewConstants.VIEW_PARAM_QUOTE_MODEL_ID));
	}
	
	protected void restoreOldPage() {

	}
	
	public boolean canAddEditAccessories() {
		if(isReadOnlyAccess()) {
			return false;
		}
		
		boolean isFormalExtension = rentalCalculationService.isFormalExtension(quotationModel);
		if(quotationModel.getQuoteStatus() == 1 && isFormalExtension) {
			return false;
		}
		
		return rentalCalculationService.isQuoteEditable(quotationModel);
	}
	
	private void loadHeaderData() throws MalBusinessException {
		logger.debug("MaintainQuoteDealerAccessoryBean:loadHeaderData:Start");
		
		QuoteOverviewVO quoteOverview = new QuoteOverviewVO();
		
		quoteOverview.setAccountCode(quotationModel.getQuotation().getExternalAccount().getExternalAccountPK().getAccountCode());
		quoteOverview.setAccountName(quotationModel.getQuotation().getExternalAccount().getAccountName());
		quoteOverview.setQuote(Long.toString(quotationModel.getQuotation().getQuoId()) + "/" + Long.toString(quotationModel.getQuoteNo()) + "/" + Long.toString(quotationModel.getRevisionNo()));
		quoteOverview.setUnitNo(quotationModel.getUnitNo());
		quoteOverview.setUnitDesc(quotationModel.getModel().getModelDescription());

		setQmReplacementFmsId(quotationModel.getReplacementFmsId());
		if (getQmReplacementFmsId() != null) {
			setFleetMaster(fleetMasterService.getFleetMasterByFmsId(getQmReplacementFmsId()));
			quoteOverview.setUnitNoReplacement(getFleetMaster().getUnitNo());
		}

		Product	product	= productDAO.findById(quotationModel.getQuotation().getQuotationProfile().getPrdProductCode()).orElse(null);
		quoteOverview.setProductName(product.getProductName());
    	
		quoteOverview.setQuoteProfileDesc(quotationModel.getQuotation().getQuotationProfile().getDescription());
		quoteOverview.setMileageProgramDesc(quotationService.getApplicableMilageProgram(Long.parseLong(qmdId)));
		quoteOverview.setTerm(quotationModel.getContractPeriod());
		quoteOverview.setDistance(quotationModel.getContractDistance());
		setQuoteOverviewVO(quoteOverview);
		
		logger.debug("loadHeaderData:End");

	}
	
	private void initializeQuotationDealerAccessories() throws Exception {
		quoteDealerAccessories = quotationService.getQuotationModelWithCostAndAccessories(Long.parseLong(qmdId)).getQuotationDealerAccessories();
		
		Collections.sort(quoteDealerAccessories, new Comparator<QuotationDealerAccessory>() {
			public int compare(QuotationDealerAccessory qda1, QuotationDealerAccessory qda2) {
				return qda1.getDealerAccessory().getDealerAccessoryCode().getDescription().compareTo(qda2.getDealerAccessory().getDealerAccessoryCode().getDescription());
			}
		});
	}
	
	/*
	 * Populate list of Dealer Accessories for Dealer Accessory auto complete input 
	 * 
	 */
	public List<DealerAccessory> autoCompleteDealerAccessories(String criteria){
		List<DealerAccessory> searchedDealerAccessories = null;
		List<DealerAccessory> asisgnedDealerAccessoryCodes = null;
		
		searchedDealerAccessories = modelService.getEffectiveModelDealerAccessories(model.getModelId(), criteria, new PageRequest(0, 200, new Sort(Direction.ASC, "dealerAccessoryCode.description")));
		
		//Filter out dealer accessories that are already assigned to the quote		
		if(searchedDealerAccessories.size() > 0){
			asisgnedDealerAccessoryCodes = findMatchingAssignedDealerAccessoryCodes(searchedDealerAccessories);
		}
		
		if(asisgnedDealerAccessoryCodes != null) {
			searchedDealerAccessories.removeAll(asisgnedDealerAccessoryCodes);			
		}
		
		setSelectedDealerAccessory(null);		
		
		return searchedDealerAccessories;
	}

	private List<DealerAccessory> findMatchingAssignedDealerAccessoryCodes(List<DealerAccessory> searchDealerAccessories) {
		List<DealerAccessory> asisgnedDealerAccessories = new ArrayList<DealerAccessory>();
		for(QuotationDealerAccessory quoteDealerAccessory : quoteDealerAccessories){
			for(DealerAccessory searchDealerAccessory : searchDealerAccessories){
				if(quoteDealerAccessory.getDealerAccessory().getDacId().equals(searchDealerAccessory.getDacId())){
					asisgnedDealerAccessories.add(searchDealerAccessory);
					break;
				}
			}
		}
		return asisgnedDealerAccessories;
	}
	
	public void autoCompleteDealerAccessorySelectListener(SelectEvent event) {
        DealerAccessory selectedDealerAccessory = (DealerAccessory) event.getObject();
        setSelectedDealerAccessory(selectedDealerAccessory);
        populateAvailablePrices();
        checkForNoVendorPricing(getAvailablePrices());
        populateUpfitters(selectedDealerAccessory);
        if(isNoVendorPriceFound()) {
            selectNoVendorUpfitter();
        } else {
        	setSelectedUpfitter(null);
        }
        getAvailableQuoteUpfitters().clear();
        populatePrice(getSelectedUpfitter());
        populateTotalPriceFOC();
    }  
	
	/*
	 * Get all the available DealerAccessoryPrice for the TRIM and filter them for Open Vendors only
	 */
	private void populateAvailablePrices() {
		getAvailablePrices().clear();
		
		List<DealerAccessoryPrice> dealerAccessoryPriceList = null;
		try {
			dealerAccessoryPriceList = modelService.getDealerAccessoryWithPrices(getSelectedDealerAccessory().getDacId()).getDealerAccessoryPrices();
			
			for(DealerAccessoryPrice dealerAccessoryPrice : dealerAccessoryPriceList) {
				if(!MALUtilities.isEmpty(dealerAccessoryPrice.getPayeeAccount())) {
					if("O".equals(dealerAccessoryPrice.getPayeeAccount().getAccStatus())) {
						getAvailablePrices().add(dealerAccessoryPrice);
					}
				} else {
					getAvailablePrices().add(dealerAccessoryPrice);
				}
			}
		} catch (MalBusinessException e) {
			logger.error(e);
			super.addErrorMessage("generic.error", e.getMessage());
		}
	}
	
	
	private void checkForNoVendorPricing(List<DealerAccessoryPrice> vendorPriceList) {
		boolean priceFound = false;
		for(DealerAccessoryPrice dealerAccessoryPrice : vendorPriceList) {
			if(MALUtilities.isEmpty(dealerAccessoryPrice.getPayeeAccount())) {
				priceFound = true;
				break;
			}
		}
		
		setNoVendorPriceFound(priceFound);
	}
	
	
	/*
	 * Populate list of Vendors with price and other details(address etc.) for selected dealer accessory
	 * 
	 */
	private void populateUpfitters(DealerAccessory dealerAccry) {
		getAvailableUpfitters().clear();
		
		Map<String, List<DealerAccessoryPrice>> dacMap = new HashMap<String, List<DealerAccessoryPrice>>();
		String accessoryCode = dealerAccry.getDealerAccessoryCode().getNewAccessoryCode();
		if(!MALUtilities.isEmpty(getAvailablePrices())) {
			dacMap.putAll(groupDealerAccessoryPricingByAccessoryAndVendor(accessoryCode, getAvailablePrices()));
			
			DealerAccessoryPrice effectiveDealerAccessoryPrice = null;
			UpfitterPriceAddressVO processedUpfitter = null;
			ExtAccAddress upfitterDefaultPostAddress = null;
			
			//Filter for required data
			for(String vendorAccessoryCodeKey : dacMap.keySet()){
				effectiveDealerAccessoryPrice = upfitterService.getUpfitterEffectivePrice(dacMap.get(vendorAccessoryCodeKey), getModel());
				
				processedUpfitter = new UpfitterPriceAddressVO();
				if(effectiveDealerAccessoryPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) < 0){
					processedUpfitter.setDealerAccessoryId(effectiveDealerAccessoryPrice.getDealerAccessory().getDacId());
		        	processedUpfitter.setDealerPriceListId(effectiveDealerAccessoryPrice.getDplId());
		        	processedUpfitter.setBasePrice(effectiveDealerAccessoryPrice.getBasePrice());
		        	processedUpfitter.setVatAmount(effectiveDealerAccessoryPrice.getVatAmount());
		        	
					if(!MALUtilities.isEmpty(effectiveDealerAccessoryPrice.getPayeeAccount())) {
						upfitterDefaultPostAddress = upfitterService.getUpfitterDefaultPostAddress(effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK().getAccountCode(), super.getLoggedInUser().getCorporateEntity().getCorpId());
						//Only vendors which have POST address with default_ind as "Y" will be populated in the Vendor Name list
						if(!MALUtilities.isEmpty(upfitterDefaultPostAddress)) {
							ExternalAccountPK externalAccountPK = effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK();
			        		
			        		processedUpfitter.setPayeeCorporateId(externalAccountPK.getCId());
			            	processedUpfitter.setPayeeAccountType(externalAccountPK.getAccountType());
			            	processedUpfitter.setPayeeAccountCode(externalAccountPK.getAccountCode());
			            	processedUpfitter.setPayeeAccountName(effectiveDealerAccessoryPrice.getPayeeAccount().getAccountName());
							processedUpfitter.setDefaultFormattedAddresss(upfitterService.getFormattedAddress(upfitterDefaultPostAddress, ","));
							
							if(!MALUtilities.isEmpty(effectiveDealerAccessoryPrice.getUpfitterQuote())) {
								processedUpfitter.setQuoteNumber(effectiveDealerAccessoryPrice.getUpfitterQuote().getQuoteNumber());
							} 
							
							getAvailableUpfitters().add(processedUpfitter);
						}
					} else {
						/* Handle blank NO VENDOR record
						*/ 
						processedUpfitter.setPayeeAccountCode("-1");
						processedUpfitter.setPayeeAccountName("");
						processedUpfitter.setDefaultFormattedAddresss("");
						
						getAvailableUpfitters().add(processedUpfitter);
					}
				}
			}
			
			if(!isNoVendorPriceFound()) {
				getAvailableUpfitters().add(null);
			}
			
			sortAvailableVendors(getAvailableUpfitters());
		}
	}
	
	private void selectNoVendorUpfitter() {
		for(UpfitterPriceAddressVO currentUpfitter : getAvailableUpfitters()) {
			if(currentUpfitter.getPayeeAccountCode().equals("-1")) {
				setSelectedUpfitter(currentUpfitter);
				break;
			}
		}
	}
	
	/**
	 * @param accessoryCode
	 * @param dealerAccessoryPriceList
	 * @return
	 * 
	 * Method for grouping of Dealer Prices List records without quote number for a specific given accessory 
	 */
	private Map<String, List<DealerAccessoryPrice>> groupDealerAccessoryPricingByAccessoryAndVendor(String accessoryCode, List<DealerAccessoryPrice> dealerAccessoryPriceList){
		Map<String, List<DealerAccessoryPrice>> dacMap = new HashMap<String, List<DealerAccessoryPrice>>();
		List<DealerAccessoryPrice> dealerAccessoryPrices;
		String upfitterAccountCode;
		
		for(DealerAccessoryPrice dacPrice : dealerAccessoryPriceList){
			upfitterAccountCode = MALUtilities.isEmpty(dacPrice.getPayeeAccount()) ? null : dacPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
			
			if(MALUtilities.isEmpty(dacMap.get(upfitterAccountCode + accessoryCode))) { 
				dealerAccessoryPrices = new ArrayList<DealerAccessoryPrice>();
				dealerAccessoryPrices.add(dacPrice); 
				dacMap.put(upfitterAccountCode + accessoryCode, dealerAccessoryPrices);			
			} else {				
				dacMap.get(upfitterAccountCode + accessoryCode).add(dacPrice);						
			}
		}
		
		return dacMap;
	}
	
	/**
	 * @param upfitterCode
	 * @param dealerAccessoryPriceList
	 * @return
	 * 
	 * Method for grouping of Dealer Price List records with quote number for a specific vendor selected for a specific accessory  
	 */
	private Map<String, List<DealerAccessoryPrice>> groupDealerAccessoryPricingByVendorAndQuoteNumber(String upfitterCode, List<DealerAccessoryPrice> dealerAccessoryPriceList){
		Map<String, List<DealerAccessoryPrice>> dacMap = new HashMap<String, List<DealerAccessoryPrice>>();
		List<DealerAccessoryPrice> dealerAccessoryPrices;
		String upfitterQuoteNumber;
		
		for(DealerAccessoryPrice dacPrice : dealerAccessoryPriceList){
			upfitterQuoteNumber = MALUtilities.isEmpty(dacPrice.getUpfitterQuote()) ? null : dacPrice.getUpfitterQuote().getQuoteNumber();
			
			if(MALUtilities.isEmpty(dacMap.get(upfitterQuoteNumber + upfitterCode))) { 
				dealerAccessoryPrices = new ArrayList<DealerAccessoryPrice>();
				dealerAccessoryPrices.add(dacPrice); 
				dacMap.put(upfitterQuoteNumber + upfitterCode, dealerAccessoryPrices);			
			} else {				
				dacMap.get(upfitterQuoteNumber + upfitterCode).add(dacPrice);						
			}
		}
		
		return dacMap;
	}
	
	
	public void sortDealerAccessoryDescription(List<DealerAccessory> dealerAccessories){
		Collections.sort(dealerAccessories, new Comparator<DealerAccessory>() { 
			public int compare(DealerAccessory da1, DealerAccessory da2) { 
				return da1.getDealerAccessoryCode().getDescription().compareTo(da2.getDealerAccessoryCode().getDescription()); 
			}
		});					
	}
	
	public void sortAvailableVendors(List<UpfitterPriceAddressVO> availableVendors){
		Collections.sort(availableVendors, new Comparator<UpfitterPriceAddressVO>() { 
			public int compare(UpfitterPriceAddressVO v1, UpfitterPriceAddressVO v2) {
				if(MALUtilities.isEmpty(v1)) {
					return -1;
				} else if(MALUtilities.isEmpty(v2)) {
					return 1;
				} else if(v1.getPayeeAccountCode().equals("-1")) {
					return -1;
				} else if(v2.getPayeeAccountCode().equals("-1")) {
					return 1;
				} else {
					return v1.getPayeeAccountName().compareTo(v2.getPayeeAccountName());
				}
			}
		});					
	}

	public void vendorChangeListener() {
		if(!MALUtilities.isEmpty(getSelectedUpfitter())) {
			populateQuoteNumbers();
		} else {
			getAvailableQuoteUpfitters().clear();
		}
	    selectQuoteUpfitter(getSelectedUpfitter());
	    populatePrice(getSelectedUpfitter());
	    populateTotalPriceFOC();
	}
	
	private void selectQuoteUpfitter(UpfitterPriceAddressVO selectedUpfitter) {
		boolean quoteUpfitterFound = false;
		
		if(!MALUtilities.isEmpty(selectedUpfitter)) {
			for(UpfitterPriceAddressVO upfitter : getAvailableQuoteUpfitters()) {
				if(selectedUpfitter.getDealerPriceListId().equals(upfitter.getDealerPriceListId())) {
					setSelectedQuoteUpfitter(upfitter);
					quoteUpfitterFound = true;
					break;
				}
			}
		}
		
		if(!quoteUpfitterFound) {
			setSelectedQuoteUpfitter(null);
		}
	}

	/*
	 * Populate DealerAccessoryPrices for selected dealer accessory and selected vendor
	 * 
	 */
	private void populateQuoteNumbers() {
		getAvailableQuoteUpfitters().clear();
		
		UpfitterPriceAddressVO selectedUpfitter = getSelectedUpfitter();

		Map<String, List<DealerAccessoryPrice>> dapMap = new HashMap<String, List<DealerAccessoryPrice>>();
		String accountCode = selectedUpfitter.getPayeeAccountCode();

		List<DealerAccessoryPrice> quoteUpfitterList = new ArrayList<DealerAccessoryPrice>();
		if(!"-1".equals(accountCode)) {
			quoteUpfitterList = getUpfitterQuoteByDacIdAndUpfitter(selectedUpfitter.getDealerAccessoryId(), accountCode);

			if(!MALUtilities.isEmpty(quoteUpfitterList)) {
				dapMap.putAll(groupDealerAccessoryPricingByVendorAndQuoteNumber(accountCode, quoteUpfitterList));

				DealerAccessoryPrice effectiveDealerAccessoryPrice = null;
				UpfitterPriceAddressVO processedUpfitter = null;

				// Filter for required data
				for(String vendorQuoteCodeKey : dapMap.keySet()) {
					effectiveDealerAccessoryPrice = upfitterService.getUpfitterEffectivePrice(dapMap.get(vendorQuoteCodeKey), getModel());

					processedUpfitter = new UpfitterPriceAddressVO();
					if(effectiveDealerAccessoryPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) < 0) {
						processedUpfitter.setDealerAccessoryId(effectiveDealerAccessoryPrice.getDealerAccessory().getDacId());
						processedUpfitter.setDealerPriceListId(effectiveDealerAccessoryPrice.getDplId());
						processedUpfitter.setPayeeCorporateId(selectedUpfitter.getPayeeCorporateId());
						processedUpfitter.setPayeeAccountType(selectedUpfitter.getPayeeAccountType());
						processedUpfitter.setPayeeAccountCode(selectedUpfitter.getPayeeAccountCode());
						processedUpfitter.setPayeeAccountName(selectedUpfitter.getPayeeAccountName());
						processedUpfitter.setDefaultFormattedAddresss(selectedUpfitter.getDefaultFormattedAddresss());
						processedUpfitter.setBasePrice(effectiveDealerAccessoryPrice.getBasePrice());
						processedUpfitter.setVatAmount(effectiveDealerAccessoryPrice.getVatAmount());
						
//						if(!MALUtilities.isEmpty(effectiveDealerAccessoryPrice.getUpfitterQuote())) {
						processedUpfitter.setQuoteNumber(effectiveDealerAccessoryPrice.getUpfitterQuote().getQuoteNumber());
						
						getAvailableQuoteUpfitters().add(processedUpfitter);
//						}
					}
				}
			}
			
			Collections.sort(getAvailableQuoteUpfitters(), new Comparator<UpfitterPriceAddressVO>() { 
				public int compare(UpfitterPriceAddressVO v1, UpfitterPriceAddressVO v2) {
					return v1.getQuoteNumber().compareTo(v2.getQuoteNumber());
				}
			});
		}
	}
	

	private List<DealerAccessoryPrice> getUpfitterQuoteByDacIdAndUpfitter(Long dealerAccessoryId, String accountCode) {
		List<DealerAccessoryPrice> upfitterPrices = new ArrayList<DealerAccessoryPrice>();
		
		//Filter list for matching Vendor and with not null Vendor Quote No
		for(DealerAccessoryPrice availablePrice : getAvailablePrices()) {
			if(!MALUtilities.isEmpty(availablePrice.getPayeeAccount())) {
				if(availablePrice.getPayeeAccount().getExternalAccountPK().getCId() == 1L && "S".equals(availablePrice.getPayeeAccount().getExternalAccountPK().getAccountType()) 
						&& accountCode.equals(availablePrice.getPayeeAccount().getExternalAccountPK().getAccountCode()) && !MALUtilities.isEmpty(availablePrice.getUpfitterQuote())) {
					upfitterPrices.add(availablePrice);
				}
			}
		}
		
		return upfitterPrices;
	}

	public void vendorQuoteChangeListener() {
		if(!MALUtilities.isEmpty(getSelectedQuoteUpfitter())) {
			populatePrice(getSelectedQuoteUpfitter());
		} else {
			if(MALUtilities.isEmpty(getSelectedUpfitter().getQuoteNumber())) {
				populatePrice(getSelectedUpfitter());
			} else {
				selectNoVendorQuoteUpfitter();
				if(!MALUtilities.isEmpty(getSelectedNoVendorQuoteUpfitter())) {
					populatePrice(getSelectedNoVendorQuoteUpfitter());
				} else {
					populatePrice(null);
				}
			}
		}
		populateTotalPriceFOC();
	}
	
	private void selectNoVendorQuoteUpfitter() {
		ExternalAccount vendor = new ExternalAccount(selectedUpfitter.getPayeeCorporateId(), selectedUpfitter.getPayeeAccountType() , selectedUpfitter.getPayeeAccountCode());
		
		List<DealerAccessoryPrice> dealerAccessoryPriceList = modelService.getDealerAccessoryPricesWithoutVendorQuote(selectedDealerAccessory, vendor);
		DealerAccessoryPrice effectiveDealerAccessoryPrice = upfitterService.getUpfitterEffectivePriceWithoutQuote(dealerAccessoryPriceList, getModel());
		
		UpfitterPriceAddressVO processedUpfitter = null;
		if(!MALUtilities.isEmpty(effectiveDealerAccessoryPrice)) {
			processedUpfitter = new UpfitterPriceAddressVO();
			if(effectiveDealerAccessoryPrice.getEffectiveDate().compareTo(Calendar.getInstance().getTime()) < 0){
				processedUpfitter.setDealerAccessoryId(effectiveDealerAccessoryPrice.getDealerAccessory().getDacId());
	        	processedUpfitter.setDealerPriceListId(effectiveDealerAccessoryPrice.getDplId());
	        	processedUpfitter.setBasePrice(effectiveDealerAccessoryPrice.getBasePrice());
	        	processedUpfitter.setVatAmount(effectiveDealerAccessoryPrice.getVatAmount());
	        	
				if(!MALUtilities.isEmpty(effectiveDealerAccessoryPrice.getPayeeAccount())) {
					ExtAccAddress upfitterDefaultPostAddress = upfitterService.getUpfitterDefaultPostAddress(effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK().getAccountCode(), super.getLoggedInUser().getCorporateEntity().getCorpId());
					if(!MALUtilities.isEmpty(upfitterDefaultPostAddress)) {
		        		processedUpfitter.setPayeeCorporateId(effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK().getCId());
		            	processedUpfitter.setPayeeAccountType(effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK().getAccountType());
		            	processedUpfitter.setPayeeAccountCode(effectiveDealerAccessoryPrice.getPayeeAccount().getExternalAccountPK().getAccountCode());
		            	processedUpfitter.setPayeeAccountName(effectiveDealerAccessoryPrice.getPayeeAccount().getAccountName());
						processedUpfitter.setDefaultFormattedAddresss(upfitterService.getFormattedAddress(upfitterDefaultPostAddress, ","));
						
						setSelectedNoVendorQuoteUpfitter(processedUpfitter);
					}
				} 
			}
		}
	}

	/*
	 * Populate Price and Recharge amount for selected combination of Dealer Accessory, Vendor and QuoteNo
	 */
	public void populatePrice(UpfitterPriceAddressVO selectedUpfitter) {
		if(!MALUtilities.isEmpty(selectedUpfitter)) {
			getSelectedQuoteDealerAccesoryVO().setBasePrice(selectedUpfitter.getBasePrice());
			getSelectedQuoteDealerAccesoryVO().setVatAmount(selectedUpfitter.getVatAmount());
		} else {
			getSelectedQuoteDealerAccesoryVO().setBasePrice(BigDecimal.ZERO);
			getSelectedQuoteDealerAccesoryVO().setVatAmount(BigDecimal.ZERO);
		}
		
		getSelectedQuoteDealerAccesoryVO().setRechargeAmount(BigDecimal.ZERO);
	}
	
	public void populateTotalPriceFOC() {
		if(getSelectedQuoteDealerAccesoryVO().getBasePrice().compareTo(BigDecimal.ZERO) >= 0) {
			if(getSelectedQuoteDealerAccesoryVO().getRechargeAmount().compareTo(getSelectedQuoteDealerAccesoryVO().getBasePrice()) > 0) {
				super.addErrorMessageSummary(UI_ID_SELECTED_RECHARGE_AMOUNT, "custom.message", "Recharge amount can not be greater than Price");	
				return;
			}
		} else {
			if(getSelectedQuoteDealerAccesoryVO().getRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
				super.addErrorMessageSummary(UI_ID_SELECTED_RECHARGE_AMOUNT, "custom.message", "Recharge amount should be ZERO for negative Price");	
				return;
			}
		}
		
		if(getSelectedQuoteDealerAccesoryVO().getBasePrice().compareTo(BigDecimal.ZERO) >= 0) {
			getSelectedQuoteDealerAccesoryVO().setTotalPrice(getSelectedQuoteDealerAccesoryVO().getBasePrice().subtract(getSelectedQuoteDealerAccesoryVO().getRechargeAmount()));
		} else {
			getSelectedQuoteDealerAccesoryVO().setRechargeAmount(BigDecimal.ZERO);
			getSelectedQuoteDealerAccesoryVO().setTotalPrice(getSelectedQuoteDealerAccesoryVO().getBasePrice());
		}
		
		if(getSelectedQuoteDealerAccesoryVO().getTotalPrice().equals(BigDecimal.ZERO)) {
			getSelectedQuoteDealerAccesoryVO().setFreeOfCharge("Y");
		} else {
			getSelectedQuoteDealerAccesoryVO().setFreeOfCharge("N");
		}
		
	}
	
	public void addQuoteDealerAccsryListener() {
		setAddMode(true);
		clearQuoteDealerAccessoriesDialog();
		initializeSelectedQuoteDealerAccessory(true);
	}
	
	private void initializeSelectedQuoteDealerAccessory(boolean isAdd) {
		selectedQuoteDealerAccesoryVO = new QuoteDealerAccesoryVO();

		if(isAdd) {
			selectedQuoteDealerAccesoryVO.setBasePrice(BigDecimal.ZERO);
			selectedQuoteDealerAccesoryVO.setRechargeAmount(BigDecimal.ZERO);
			selectedQuoteDealerAccesoryVO.setTotalPrice(BigDecimal.ZERO);
			selectedQuoteDealerAccesoryVO.setFreeOfCharge("N");
			selectedQuoteDealerAccesoryVO.setVatAmount(BigDecimal.ZERO);
		} else {
			selectedQuoteDealerAccesoryVO.setBasePrice(getSelectedQuotationDealerAccessory().getBasicPrice());
			selectedQuoteDealerAccesoryVO.setRechargeAmount(getSelectedQuotationDealerAccessory().getRechargeAmount());
			selectedQuoteDealerAccesoryVO.setTotalPrice(getSelectedQuotationDealerAccessory().getTotalPrice());
			selectedQuoteDealerAccesoryVO.setFreeOfCharge(getSelectedQuotationDealerAccessory().getFreeOfChargeYn());
			selectedQuoteDealerAccesoryVO.setVatAmount(getSelectedQuotationDealerAccessory().getVatAmount());
		}
		
	}
	
	private UpfitterPriceAddressVO findSavedUpfitter(ExternalAccount vendorAccount) {
		UpfitterPriceAddressVO savedUpfitter = null;
		
		for(UpfitterPriceAddressVO availableVendor : getAvailableUpfitters()) {
			if(!MALUtilities.isEmpty(availableVendor)) {
				if(!MALUtilities.isEmpty(vendorAccount)) {
					if(availableVendor.getPayeeAccountCode().equals(vendorAccount.getExternalAccountPK().getAccountCode())) {
						savedUpfitter = availableVendor;
						break;
					}
				} else {
					if(availableVendor.getPayeeAccountCode().equals("-1")) {
						savedUpfitter = availableVendor;
						break;
					}
				}
			}
		}
		
		return savedUpfitter;
	}
	
	private UpfitterPriceAddressVO findSavedQuoteUpfitter(ExternalAccount vendorAccount, String upfitterQuoteNumber) {
		UpfitterPriceAddressVO savedUpfitter = null;
		
		if(!MALUtilities.isEmpty(upfitterQuoteNumber)) {
			for(UpfitterPriceAddressVO availableVendor : getAvailableQuoteUpfitters()) {
				if(!MALUtilities.isEmpty(vendorAccount)) {
					if(availableVendor.getPayeeAccountCode().equals(vendorAccount.getExternalAccountPK().getAccountCode()) && upfitterQuoteNumber.equals(availableVendor.getQuoteNumber())) {
						savedUpfitter = availableVendor;
						break;
					}
				} 
			}
		} 
		
		return savedUpfitter;
	}

	public void editQuoteDealerAccsryListener(QuotationDealerAccessory quoteDealerAccessory) {
		setAddMode(false);
		clearQuoteDealerAccessoriesDialog();
		setSelectedQuotationDealerAccessory(quoteDealerAccessory);
		setSelectedDealerAccessory(getSelectedQuotationDealerAccessory().getDealerAccessory());
		
		populateAvailablePrices();
		checkForNoVendorPricing(getAvailablePrices());
    	populateUpfitters(getSelectedDealerAccessory());
		setSelectedUpfitter(findSavedUpfitter(getSelectedQuotationDealerAccessory().getVendorAccount()));
		
		if(!MALUtilities.isEmpty(getSelectedUpfitter())) {
			populateQuoteNumbers();
		}	
		setSelectedQuoteUpfitter(findSavedQuoteUpfitter(getSelectedQuotationDealerAccessory().getVendorAccount(), getSelectedQuotationDealerAccessory().getExternalReferenceNo()));
    		
		initializeSelectedQuoteDealerAccessory(false);
	}
	
	
	private void clearQuoteDealerAccessoriesDialog(){
		setSelectedDealerAccessory(null);
		getAvailablePrices().clear();
		setSelectedUpfitter(null);
		setSelectedQuoteUpfitter(null);
		getAvailableUpfitters().clear();
		getAvailableQuoteUpfitters().clear();
		setSelectedNoVendorQuoteUpfitter(null);
	}
	
	private void clearQuoteDealerAccessoriesDialogSaveStay(){
		clearQuoteDealerAccessoriesDialog();
		initializeSelectedQuoteDealerAccessory(true);
	}
	
	public void saveDealerAccessory(boolean isStay) {
		try {
			if(!validateAddDealerAccessoriesInput()){
				return;
			}
			
			QuotationDealerAccessory quotationDealerAccessory = null;
			ExternalAccount vendorAccount = null;
			String vendorQuoteNo = null;
			QuoteDealerAccesoryVO quoteDealerAccesoryVO = getSelectedQuoteDealerAccesoryVO();
			
			if(addMode) {
				quotationDealerAccessory = new QuotationDealerAccessory();
				quotationDealerAccessory.setQuotationModel(quotationModel);
				quotationDealerAccessory.setDealerAccessory(getSelectedDealerAccessory());
			} else {
				quotationDealerAccessory = getSelectedQuotationDealerAccessory();
			}
			
			if(!(MALUtilities.isEmpty(getSelectedUpfitter()) || getSelectedUpfitter().getPayeeAccountCode().equals("-1"))) {
				vendorAccount = upfitterService.getUpfitterAccount(getSelectedUpfitter().getPayeeAccountCode(), super.getLoggedInUser().getCorporateEntity());
			}
			
			if(!MALUtilities.isEmpty(getSelectedQuoteUpfitter())) {
				vendorQuoteNo = getSelectedQuoteUpfitter().getQuoteNumber();
			} 
			
			quotationDealerAccessory.setVendorAccount(vendorAccount);
			quotationDealerAccessory.setExternalReferenceNo(vendorQuoteNo);
			quotationDealerAccessory.setBasicPrice(quoteDealerAccesoryVO.getBasePrice());
			quotationDealerAccessory.setDiscPrice(quotationDealerAccessory.getBasicPrice());
			quotationDealerAccessory.setRechargeAmount(quoteDealerAccesoryVO.getRechargeAmount());
			quotationDealerAccessory.setAccQty(Integer.valueOf(1));
			quotationDealerAccessory.setVatAmount(quoteDealerAccesoryVO.getVatAmount());
			
			if(getSelectedQuoteDealerAccesoryVO().getRechargeAmount().equals(BigDecimal.ZERO)) {
				quotationDealerAccessory.setTotalPrice(quotationDealerAccessory.getBasicPrice());
			} else {
				quotationDealerAccessory.setTotalPrice(quotationDealerAccessory.getBasicPrice().subtract(quotationDealerAccessory.getRechargeAmount()));
			}
			
			quotationDealerAccessory.setFreeOfChargeYn(quoteDealerAccesoryVO.getFreeOfCharge());
			
			if(quotationDealerAccessory.getRechargeAmount().equals(BigDecimal.ZERO)){
				quotationDealerAccessory.setDriverRechargeYn("N");
			} else {
				quotationDealerAccessory.setDriverRechargeYn("Y");
			}
			
			if(quotationDealerAccessory.getDriverRechargeYn().equals("Y")){
				quotationDealerAccessory.setCapitalisedYn("N");
			} else {
				quotationDealerAccessory.setCapitalisedYn("Y");
			}
			
			quotationDealerAccessory.setRequiredYn("Y");
			
			quotationService.saveOrUpdateQuotationDealerAccessory(quotationDealerAccessory);
			initializeQuotationDealerAccessories();
			
			if(!isStay){
				setFocusAllowed(false);
				RequestContext.getCurrentInstance().execute("PF('addQuoteDealerAccessoryDialogWidget').hide()");
				super.addSuccessMessage("custom.message","Quotation Dealer Accessory saved successfully");
			} else {
				super.addInfoMessageSummary("custom.message","Quotation Dealer Accessory saved successfully");
				clearQuoteDealerAccessoriesDialogSaveStay();
			}
			
			super.setDirtyData(false);
			RequestContext.getCurrentInstance().execute("setDirtyData(false)");
		} catch (Exception e) {
			logger.error(e);
			super.setDirtyData(true);
			RequestContext.getCurrentInstance().execute("setDirtyData(true)");
			if(e.getCause() instanceof PessimisticEntityLockException) {
    			super.addErrorMessageSummary("generic.error", "Quote or accessory is locked by another user. Please try again later.");	
    		} else {
    			super.addErrorMessageSummary("generic.error", e.getMessage());
    		}
		}	
	}

	private boolean validateAddDealerAccessoriesInput(){
		boolean isValid = true;
		
		if(isAddMode()){ //Adding a dealer accessory
			if(MALUtilities.isEmpty(getSelectedDealerAccessory())){
				super.addErrorMessageSummary(UI_ID_SELECTED_DEALER_ACCESSORY, "required.field", "Description");	
				isValid = false;
			}	
		}
		
		if(getSelectedQuoteDealerAccesoryVO().getBasePrice().compareTo(BigDecimal.ZERO) >= 0) {
			if(getSelectedQuoteDealerAccesoryVO().getRechargeAmount().compareTo(getSelectedQuoteDealerAccesoryVO().getBasePrice()) > 0) {
				super.addErrorMessageSummary(UI_ID_SELECTED_RECHARGE_AMOUNT, "custom.message", "Recharge amount can not be greater than Price");	
				isValid = false;
			}
		} else {
			if(getSelectedQuoteDealerAccesoryVO().getRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
				super.addErrorMessageSummary(UI_ID_SELECTED_RECHARGE_AMOUNT, "custom.message", "Recharge amount should be ZERO for negative Price");
				isValid = false;
			}
		}
		
		//OTD-876: Non Autodata Dlr Acc must have a Vendor
		if(MALUtilities.isEmpty(getSelectedUpfitter()) || MALUtilities.isEmpty(getSelectedUpfitter().getPayeeAccountName())) {
			
			if(MALUtilities.isEmpty(getSelectedDealerAccessory().getDealerAccessoryCode().getOptionAccessoryCategory())
					|| !Arrays.asList(willowConfigService.getConfigValue(WillowConfigService.AUTODATA_DEALER_ACCESSORIES_CATEGORIES)
							.split(",")).contains(getSelectedDealerAccessory().getDealerAccessoryCode().getOptionAccessoryCategory().getCode())){			
				super.addErrorMessageSummary(UI_ID_SELECTED_ACCESSORY_VENDOR, "required.field", "Vendor");
				isValid = false;			
			}
		}
		
		return isValid;
	}
	
	public void deleteQuoteDealerAccsryListener(QuotationDealerAccessory quoteDealerAccessory) {
		try {
			quotationService.deleteQuotationDealerAccessoryFromQuote(Long.parseLong(qmdId), quoteDealerAccessory);
			initializeQuotationDealerAccessories();
			
			super.addSuccessMessage("custom.message","Quotation Dealer Accessory deleted successfully");
    	} catch (Exception e) {
    		logger.error(e);
    		if(e.getCause() instanceof PessimisticEntityLockException) {
    			super.addErrorMessage("generic.error", "Quote or accessory is locked by another user. Please try again later.");	
    		} else {
    			super.addErrorMessage("generic.error", e.getMessage());
    		}
    	}
	}
	
	public void addDealerAccessoriesByQuoteListener() {
		setSelectedVendor(null);
		setSelectedQuote(null);
		getAvailableQuotes().clear();
		getAvailableDealerAccessories().clear();
		
		populateVendors();
	}
	
	/*
	 * Populate list of Open vendors available on a TRIM for all DealerAccessoryPrices 
	 * where Vendor Quote no is available    
	 * 
	 */
	public void populateVendors() {
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();		
		List<UpfitterSearchResultVO> upfitters;
		
		searchCriteria.setModelId(getModel().getModelId());
		searchCriteria.setTerm("%");
		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
		searchCriteria.setWithQuoteNo(true);
		upfitters = upfitterService.searchUpfitters(searchCriteria, new PageRequest(0,50), null);			

		Collections.sort(upfitters, new Comparator<UpfitterSearchResultVO>() { 
			public int compare(UpfitterSearchResultVO uf1, UpfitterSearchResultVO uf2) { 
				return uf1.getPayeeAccountName().compareTo(uf2.getPayeeAccountName()); 
			}
		});					
		
		setAvailableVendors(upfitters);
	}
	
	/*
	 * Populate list of Vendor Quote No for selected Vendor
	 * TODO: OTD: May need to revisit with config grouping requirements
	 */
	public List<UpfitterQuote> autoCompleteVendorQuoteNumber(String criteria) {
		List<UpfitterQuote> matchingAvailableQuotes = new ArrayList<UpfitterQuote>();
		
		if(!(MALUtilities.isEmptyString(criteria) || MALUtilities.isEmpty(getSelectedVendor()))){
			ExternalAccountPK vendorPK = new ExternalAccountPK(getSelectedVendor().getPayeeCorporateId(), getSelectedVendor().getPayeeAccountType(), getSelectedVendor().getPayeeAccountCode());
			matchingAvailableQuotes = upfitterService.searchTrimUpfitterQuotesForQuoting(vendorPK, getModel(), criteria, false, new PageRequest(0,50));
		}
		
		Collections.sort(matchingAvailableQuotes, new Comparator<UpfitterQuote>() {
			public int compare(UpfitterQuote ufq1, UpfitterQuote ufq2) {
				if(ufq1.getQuoteNumber().equals(ufq2.getQuoteNumber())) {
					return 0;
				} else if(ufq1.getQuoteNumber().compareTo(ufq2.getQuoteNumber()) > 0 ) {
					return -1;
				} else {
					return 1;
				}
			}
			
		});
		
		return matchingAvailableQuotes;
	}
	
	
	public void trimVendorChangeListener() {
		List<UpfitterQuote> availableQuotes = new ArrayList<UpfitterQuote>();
		if(!MALUtilities.isEmpty(getSelectedVendor())) {
			availableQuotes = upfitterService.getUpfitterQuotes(upfitterService.getUpfitterAccount(getSelectedVendor().getPayeeAccountCode(), getLoggedInUser().getCorporateEntity()));
		}
		
		setAvailableQuotes(availableQuotes);
		setSelectedQuote(null);
		getAvailableDealerAccessories().clear();
	}
	
	public String formatSelectedVendorAddress() {
		String formattedAddress = null;
		
		if(!MALUtilities.isEmpty(getSelectedVendor())){
			formattedAddress = upfitterService.formatUpfitterAddress(getSelectedVendor());
		}
		return formattedAddress;
	}
	
	
	public void autoCompleteUpfitterQuoteSelectListener(SelectEvent event) {
		UpfitterQuote selectedUpfitterQuote = (UpfitterQuote) event.getObject();
        setSelectedQuote(selectedUpfitterQuote);
        
        trimVendorQuoteChangeListener();
    }
	
	public void autoCompleteUpfitterQuoteChangeListener(AjaxBehaviorEvent event) {
		AutoComplete uIComponent = (AutoComplete) event.getComponent();
		UpfitterQuote selectedUpfitterQuote = (UpfitterQuote) uIComponent.getValue();
		
		if(!MALUtilities.isEmpty(selectedUpfitterQuote)) {
			setSelectedQuote(selectedUpfitterQuote);
	        trimVendorQuoteChangeListener();
		} else {
			getAvailableDealerAccessories().clear();
		}
        
    }
	
	/*
	 * Populate list of Dealer Accessories for selected combination of Vendor and Vendor Quote No
	 * 
	 */
	public void trimVendorQuoteChangeListener() {
		getAvailableDealerAccessories().clear();
		
		if(!MALUtilities.isEmpty(getSelectedQuote())) {
			Map<Long, List<DealerAccessoryPrice>> groupedAccessoryPrices = new HashMap<Long, List<DealerAccessoryPrice>>();
			for(DealerAccessoryPrice price : getSelectedQuote().getDealerAccessoryPrices()){
				if(price.getDealerAccessory().getModel().getModelId().equals(getModel().getModelId())){
					if(MALUtilities.isEmpty(groupedAccessoryPrices.get(price.getDealerAccessory().getDacId()))){
						groupedAccessoryPrices.put(price.getDealerAccessory().getDacId(), new ArrayList<DealerAccessoryPrice>());
					}

					groupedAccessoryPrices.get(price.getDealerAccessory().getDacId()).add(price);					
				}
			}
			
			for(Long key : groupedAccessoryPrices.keySet()){
				getAvailableDealerAccessories().add(upfitterService.getUpfitterEffectivePrice(groupedAccessoryPrices.get(key), getModel()));
			}
			
			//OTD-846: Sorting price list on dplId so that input order is reflected on display
			Collections.sort(getAvailableDealerAccessories(), new DealerAccessoryPriceComparator());			
			
			checkForExistingAccessories();
		}
	}
	
	private void checkForExistingAccessories() {
		for(QuotationDealerAccessory existingDealerAccry : getQuoteDealerAccessories()) {
			for(DealerAccessoryPrice dealerAccsryPrice : getAvailableDealerAccessories()) {
				if(existingDealerAccry.getDealerAccessory().equals(dealerAccsryPrice.getDealerAccessory())) {
					super.addErrorMessageSummary("custom.message", existingDealerAccry.getDealerAccessory().getDealerAccessoryCode().getDescription() +" already added on quotation");
					break;
				}
			}
		}
		
	}

	public void saveDealerAccessoriesByQuote() {
		try {
			if(!validateQuoteDealerAccessories()){
				return;
			}
			
			List<QuotationDealerAccessory> quotationDealerAccessories = new ArrayList<QuotationDealerAccessory>();
			QuotationDealerAccessory quotationDealerAccessory = null;
			ExternalAccount vendorAccount = null;
			String vendorQuoteNo = null;
			
			for(DealerAccessoryPrice dealerAccessoryPrice : getAvailableDealerAccessories()) {
				quotationDealerAccessory = new QuotationDealerAccessory();
				quotationDealerAccessory.setQuotationModel(quotationModel);
				quotationDealerAccessory.setDealerAccessory(dealerAccessoryPrice.getDealerAccessory());
				
				vendorAccount = upfitterService.getUpfitterAccount(getSelectedVendor().getPayeeAccountCode(), super.getLoggedInUser().getCorporateEntity());
				quotationDealerAccessory.setVendorAccount(vendorAccount);
				
				vendorQuoteNo = getSelectedQuote().getQuoteNumber();
				quotationDealerAccessory.setExternalReferenceNo(vendorQuoteNo);
				
				quotationDealerAccessory.setBasicPrice(dealerAccessoryPrice.getBasePrice());
				quotationDealerAccessory.setDiscPrice(quotationDealerAccessory.getBasicPrice());
				quotationDealerAccessory.setRechargeAmount(BigDecimal.ZERO);
				quotationDealerAccessory.setAccQty(Integer.valueOf(1));
				quotationDealerAccessory.setVatAmount(dealerAccessoryPrice.getVatAmount());
				quotationDealerAccessory.setTotalPrice(quotationDealerAccessory.getBasicPrice().subtract(quotationDealerAccessory.getRechargeAmount()));
				
				if(quotationDealerAccessory.getTotalPrice().equals(BigDecimal.ZERO)) {
					quotationDealerAccessory.setFreeOfChargeYn("Y");
				} else {
					quotationDealerAccessory.setFreeOfChargeYn("N");
				}
				
				if(quotationDealerAccessory.getRechargeAmount().equals(BigDecimal.ZERO)){
					quotationDealerAccessory.setDriverRechargeYn("N");
				} else {
					quotationDealerAccessory.setDriverRechargeYn("Y");
				}
				
				if(quotationDealerAccessory.getDriverRechargeYn().equals("Y")){
					quotationDealerAccessory.setCapitalisedYn("N");
				} else {
					quotationDealerAccessory.setCapitalisedYn("Y");
				}
				
				quotationDealerAccessory.setRequiredYn("Y");
				
				quotationDealerAccessories.add(quotationDealerAccessory);
			}
			
			quotationService.saveDealerAccessories(quotationDealerAccessories);
			initializeQuotationDealerAccessories();
			
			setFocusAllowed(false);
			RequestContext.getCurrentInstance().execute("PF('dealerAccessoryByQuoteDialogWidget').hide()");
			super.addSuccessMessage("custom.message","Quotation Dealer Accessories saved successfully");
		} catch (Exception e) {
			logger.error(e);
			if(e.getCause() instanceof PessimisticEntityLockException) {
    			super.addErrorMessageSummary("generic.error", "Quote or accessory is locked by another user. Please try again later.");	
    		} else {
    			super.addErrorMessageSummary("generic.error", e.getMessage());
    		}
		}
	}
	
	private boolean validateQuoteDealerAccessories() {
		boolean isValid = true;
		
		if(MALUtilities.isEmpty(getSelectedQuote())){
			if(MALUtilities.isEmpty(getSelectedVendor())) {
				super.addErrorMessageSummary(UI_ID_SELECTED_VENDOR, "required.field", "Vendor");
				isValid = false;
			}
			
			super.addErrorMessageSummary(UI_ID_SELECTED_VENDOR_QUOTE, "required.field", "Vendor Quote No");	
			isValid = false;
			
		} else if(getAvailableDealerAccessories().size() < 1) {
			super.addErrorMessageSummary("custom.message", "No Dealer Accessory to save");	
			isValid = false;
		}
		
		for(QuotationDealerAccessory existingDealerAccry : getQuoteDealerAccessories()) {
			for(DealerAccessoryPrice dealerAccsryPrice : getAvailableDealerAccessories()) {
				if(existingDealerAccry.getDealerAccessory().equals(dealerAccsryPrice.getDealerAccessory())) {
					super.addErrorMessageSummary("custom.message", existingDealerAccry.getDealerAccessory().getDealerAccessoryCode().getDescription() +" already added on quotation");
					isValid = false;
					break;
				}
			}
		}
		
		return isValid;
	}
	
	public Long getAccessoryLeadTime(QuotationDealerAccessory quotationDealerAccessory){
		Long leadTime = quotationService.getQuoteAccessoryLeadTimeByQdaId(quotationDealerAccessory.getQdaId()); 
		
		if(leadTime.equals(0L)) {
			return null;
		}
		
		return leadTime;
	}
	
	public Long getTotalLeadTime(){
		Long totalLeadTime = 0L;
		
		for(QuotationDealerAccessory dealerAccessory : quoteDealerAccessories) {
			totalLeadTime += quotationService.getQuoteAccessoryLeadTimeByQdaId(dealerAccessory.getQdaId());
		}
		
		if(totalLeadTime.equals(0L)) {
			return null;
		}
		
		return totalLeadTime;
	}

	public String getQmdId() {
		return qmdId;
	}

	public void setQmdId(String qmdId) {
		this.qmdId = qmdId;
	}

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public List<QuotationDealerAccessory> getQuoteDealerAccessories() {
		return quoteDealerAccessories;
	}

	public void setQuoteDealerAccessories(List<QuotationDealerAccessory> quoteDealerAccessories) {
		this.quoteDealerAccessories = quoteDealerAccessories;
	}

	public QuotationDealerAccessory getSelectedQuotationDealerAccessory() {
		return selectedQuotationDealerAccessory;
	}

	public void setSelectedQuotationDealerAccessory(QuotationDealerAccessory selectedQuotationDealerAccessory) {
		this.selectedQuotationDealerAccessory = selectedQuotationDealerAccessory;
	}

	public DealerAccessory getSelectedDealerAccessory() {
		return selectedDealerAccessory;
	}

	public void setSelectedDealerAccessory(DealerAccessory selectedDealerAccessory) {
		this.selectedDealerAccessory = selectedDealerAccessory;
	}

	public List<DealerAccessoryPrice> getAvailablePrices() {
		return availablePrices;
	}

	public void setAvailablePrices(List<DealerAccessoryPrice> availablePrices) {
		this.availablePrices = availablePrices;
	}

	public UpfitterPriceAddressVO getSelectedUpfitter() {
		return selectedUpfitter;
	}

	public void setSelectedUpfitter(UpfitterPriceAddressVO selectedUpfitter) {
		this.selectedUpfitter = selectedUpfitter;
	}

	public UpfitterPriceAddressVO getSelectedQuoteUpfitter() {
		return selectedQuoteUpfitter;
	}

	public void setSelectedQuoteUpfitter(UpfitterPriceAddressVO selectedQuoteUpfitter) {
		this.selectedQuoteUpfitter = selectedQuoteUpfitter;
	}

	public List<UpfitterPriceAddressVO> getAvailableUpfitters() {
		return availableUpfitters;
	}

	public void setAvailableUpfitters(List<UpfitterPriceAddressVO> availableUpfitters) {
		this.availableUpfitters = availableUpfitters;
	}

	public List<UpfitterPriceAddressVO> getAvailableQuoteUpfitters() {
		return availableQuoteUpfitters;
	}

	public void setAvailableQuoteUpfitters(List<UpfitterPriceAddressVO> availableQuoteUpfitters) {
		this.availableQuoteUpfitters = availableQuoteUpfitters;
	}

	public QuoteOverviewVO getQuoteOverviewVO() {
		return quoteOverviewVO;
	}

	public void setQuoteOverviewVO(QuoteOverviewVO quoteOverviewVO) {
		this.quoteOverviewVO = quoteOverviewVO;
	}

	public Long getQmReplacementFmsId() {
		return qmReplacementFmsId;
	}

	public void setQmReplacementFmsId(Long qmReplacementFmsId) {
		this.qmReplacementFmsId = qmReplacementFmsId;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	public boolean isAddMode() {
		return addMode;
	}

	public void setAddMode(boolean addMode) {
		this.addMode = addMode;
	}

	public QuoteDealerAccesoryVO getSelectedQuoteDealerAccesoryVO() {
		return selectedQuoteDealerAccesoryVO;
	}

	public void setSelectedQuoteDealerAccesoryVO(QuoteDealerAccesoryVO selectedQuoteDealerAccesoryVO) {
		this.selectedQuoteDealerAccesoryVO = selectedQuoteDealerAccesoryVO;
	}

	public boolean isNoVendorPriceFound() {
		return noVendorPriceFound;
	}

	public void setNoVendorPriceFound(boolean noVendorPriceFound) {
		this.noVendorPriceFound = noVendorPriceFound;
	}

	public boolean isAddEditAllowed() {
		return addEditAllowed;
	}

	public void setAddEditAllowed(boolean addEditAllowed) {
		this.addEditAllowed = addEditAllowed;
	}

	public boolean isFocusAllowed() {
		return focusAllowed;
	}

	public void setFocusAllowed(boolean focusAllowed) {
		this.focusAllowed = focusAllowed;
	}

	public List<UpfitterSearchResultVO> getAvailableVendors() {
		return availableVendors;
	}

	public void setAvailableVendors(List<UpfitterSearchResultVO> availableVendors) {
		this.availableVendors = availableVendors;
	}

	public List<UpfitterQuote> getAvailableQuotes() {
		return availableQuotes;
	}

	public void setAvailableQuotes(List<UpfitterQuote> availableQuotes) {
		this.availableQuotes = availableQuotes;
	}

	public List<DealerAccessoryPrice> getAvailableDealerAccessories() {
		return availableDealerAccessories;
	}

	public void setAvailableDealerAccessories(List<DealerAccessoryPrice> availableDealerAccessories) {
		this.availableDealerAccessories = availableDealerAccessories;
	}

	public UpfitterSearchResultVO getSelectedVendor() {
		return selectedVendor;
	}

	public void setSelectedVendor(UpfitterSearchResultVO selectedVendor) {
		this.selectedVendor = selectedVendor;
	}

	public UpfitterQuote getSelectedQuote() {
		return selectedQuote;
	}

	public void setSelectedQuote(UpfitterQuote selectedQuote) {
		this.selectedQuote = selectedQuote;
	}

	public UpfitterPriceAddressVO getSelectedNoVendorQuoteUpfitter() {
		return selectedNoVendorQuoteUpfitter;
	}

	public void setSelectedNoVendorQuoteUpfitter(UpfitterPriceAddressVO selectedNoVendorQuoteUpfitter) {
		this.selectedNoVendorQuoteUpfitter = selectedNoVendorQuoteUpfitter;
	}
	
}
