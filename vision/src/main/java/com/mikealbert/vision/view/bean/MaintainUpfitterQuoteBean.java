package com.mikealbert.vision.view.bean;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalMessage;
import com.mikealbert.data.comparator.DealerAccessoryPriceComparator;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.data.enumeration.AccountStatusEnum;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.ViewConstants;

/**
* Maintain Upfitter's Quote 
* 
* <P>Backing bean
* <P> JIRA Issue Notes:
* <P>    <b>OTD-568:</b> The problem is that the selectedQuote property set to null after click the save button. 
*               I am Not sure what is clearing the value. The workaround is to validate the quote's <br/>
*               external account instead. Continue to use the quote external account as the selected upfitter. <br/>
* <P>Resources: {@link ModelService}, {@link UpfitterService}, {@link LookupCacheService} 
*  
* @author Wayne S.
*/

@Component
@Scope("view")
public class MaintainUpfitterQuoteBean extends StatefulBaseBean{
	private static final long serialVersionUID = 257518651308495085L;
	
	@Resource ModelService modelService;
	@Resource UpfitterService upfitterService;
	@Resource LookupCacheService lookupCacheService; 
	@Resource MalMessage malMessage;	
		
	private static final String UI_ID_AVAILABLE_UPFITTER_AC = "maintainUpfitterQuoteTV:availableUpfittersAC";
	private static final String UI_ID_QUOTE_NUMBER_TXT = "maintainUpfitterQuoteTV:quoteNumberTxt";
	private static final String UI_ID_UPFITTER_QUOTE_AC = "maintainUpfitterQuoteTV:upfitterQuoteAC";	
	private static final String UI_ID_AVAILABLE_UPFITTER_MNU = "maintainUpfitterQuoteTV:trimUpfitterMnu";
	private static final String UI_ID_UPFITTER_ADDRESS_TXT = "maintainUpfitterQuoteTV:upfitterAddressLbl";
	private static final String UI_ID_UPFITTER_PHONE_TXT = "maintainUpfitterQuoteTV:upfitterPhoneNumberLbl";
	
	private static final String UPFITTER_QUOTE_CANNOT_BE_UPDATED = "upfitter.quote.cannot.be.updated";	
	
	private List<Long> paramModelIds;	
	private List<Model> models;
	private String quoteNumber;
	private Model model;
	private int tabIndex; 
	private UpfitterQuote quote;
	private String dealerAccessorySearchTerm;
	private DealerAccessoryCode selectedAvailableDealerAccessoryCode;
	private List<ExternalAccount> upfitters;
	private List<DealerAccessoryCode> availableDealerAccessoryCodes;
	private DealerAccessoryPrice selectedQuotedPrice;
	private UpfitterSearchResultVO selectedUpfitter;
	private List<UpfitterSearchResultVO> avaialableUpfitters;	
	private List<UpfitterQuote> availableQuotes;
	private UpfitterQuote selectedQuote;
	private String updatedQuoteNumber;
	private List<DealerAccessoryPrice> dealerAccessoryPrices;
	private ExternalAccount selectedUpfitterAcccount;
	private Date maxDate;
	private boolean quoteBeenUsed;
	private boolean quoteBeenCopied;
	
	@PostConstruct
	public void init() {
		openPage();		
		initializeDataTable(550, 850, new int[] { 2, 5, 10, 10, 70, 10, 10, 15}).setHeight(0);
		
		//Create a new instance of the upfitter's quote.
		this.quote = new UpfitterQuote();
		quote.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
		
		setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());		
		setAvailableDealerAccessoryCodes(new ArrayList<DealerAccessoryCode>());
		setAvaialableUpfitters(new ArrayList<UpfitterSearchResultVO>());
		setAvailableQuotes(new ArrayList<UpfitterQuote>());
		setMaxDate(Calendar.getInstance().getTime());
		maintainQuoteInitialState();

	}
	
	/**
	 * Handles the event of adding an accessory pricing to the quote
	 */
	public void addDealerAccessoryListener(){
		System.err.println("TODO need to implement addDealerAccessoryListener");
	}
	
	/**
	 * Handles the event of editing an accessory pricing to the quote
	 */	
	public void editQuotedPriceListener(){
		System.err.println("TODO need to implement editQuotedPriceListener");		
	}
	
	/**
	 * Handles the event of deleting an accessory pricing to the quote
	 */	
	public void deleteQuotedPriceListener(DealerAccessoryPrice price){
		getQuote().getDealerAccessoryPrices().remove(price);
		searchAvailableDealerAccessoriesListener();
	}	
	
	/**
	 * Handles the event of adding an accessory pricing to the quote
	 */	
	public void addQuotedPriceListener(){
		if(hasValidQuoteHeader()){
			if(!MALUtilities.isEmpty(getSelectedAvailableDealerAccessoryCode())){
				List<DealerAccessoryCode> selectedCodes = new ArrayList<DealerAccessoryCode>();
				selectedCodes.add(getSelectedAvailableDealerAccessoryCode());
				transferCodeToQuotedPrice(selectedCodes);
				setSelectedQuotedPrice(null);		
			}
		}
		
		 setFocusOnPrice();
	}
	

	/**
	 * Handles the event of adding all found accessories to the quote for pricing
	 */	
	public void addQuotedPricesListener(){
		if(hasValidQuoteHeader()){		
			if(!(MALUtilities.isEmpty(getAvailableDealerAccessoryCodes()) || getAvailableDealerAccessoryCodes().isEmpty())){
				transferCodeToQuotedPrice(getAvailableDealerAccessoryCodes());		
				setSelectedQuotedPrice(null);			
			}
		}
	}
		
	/**
	 * Handles the event of removing an accessory pricing to the quote
	 */	
	public void removeQuotedPriceListener(){
		if(hasValidQuoteHeader()){		
			if(!MALUtilities.isEmpty(getSelectedQuotedPrice())){		
				//Remove the effective price and all of its historical prices from the quote
				for(DealerAccessoryPrice price : quote.getDealerAccessoryPrices()){
					if(price.getDealerAccessory().equals(getSelectedQuotedPrice().getDealerAccessory())){
						if(!MALUtilities.isEmpty(price.getUpfitterQuote())){
							if(price.getUpfitterQuote().equals(getSelectedQuotedPrice().getUpfitterQuote())){
								price.setUpfitterQuote(null);
							}
						}
					}
				}			

				getDealerAccessoryPrices().remove(getSelectedQuotedPrice());			
				searchAvailableDealerAccessoriesListener();		
			}
		}
	}
	
	/**
	 * Handles the event of removing all quoted accessory pricing
	 */	
	public void removeQuotedPricesListener(){
		if(hasValidQuoteHeader()){		
			if(!(MALUtilities.isEmpty(getDealerAccessoryPrices()) || getDealerAccessoryPrices().isEmpty())){
				for(DealerAccessoryPrice uiPrice : getDealerAccessoryPrices()){
					for(DealerAccessoryPrice dbPrice : getQuote().getDealerAccessoryPrices()){
						if(uiPrice.getDealerAccessory().equals(dbPrice.getDealerAccessory())){
							if(uiPrice.getUpfitterQuote().equals(dbPrice.getUpfitterQuote())){
								dbPrice.setUpfitterQuote(null);
							}
						}
					}
				}

				getDealerAccessoryPrices().clear();
				searchAvailableDealerAccessoriesListener();			
			}
		}
	}
	
    public void autoCompleteUpfitterSelectListener(SelectEvent event) {
        System.err.println(((UpfitterSearchResultVO)event.getObject()).getPayeeAccountName());
        getQuote().setExternalAccount(upfitterService.getUpfitterAccount(getSelectedUpfitter().getPayeeAccountCode(), getLoggedInUser().getCorporateEntity()));
    }  	
	
	public List<UpfitterSearchResultVO> autoCompleteUpfittersListener(String criteria){
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();		
		List<UpfitterSearchResultVO> upfitters;
				
		searchCriteria.setTerm(criteria);	
		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
		upfitters = upfitterService.searchUpfittersAccessoriesOptional(getModel(), searchCriteria, new PageRequest(0,50), null);
		
    	setSelectedUpfitter(null);
    	setSelectedUpfitterAcccount(null);
    	
	    if(!upfitters.isEmpty()){
			Collections.sort(upfitters, new Comparator<UpfitterSearchResultVO>() { 
				public int compare(UpfitterSearchResultVO uf1, UpfitterSearchResultVO uf2) { 
					return uf1.getPayeeAccountName().toLowerCase().compareTo(uf2.getPayeeAccountName().toLowerCase()); 
				}
			});		    	
	    }
		
		RequestContext.getCurrentInstance().update(UI_ID_UPFITTER_ADDRESS_TXT);		
		RequestContext.getCurrentInstance().update(UI_ID_UPFITTER_PHONE_TXT);
		
		return upfitters;
	}
		
	public List<UpfitterQuote> autoCompleteUpfitterQuoteListener(String criteria){
		List<UpfitterQuote> result = new ArrayList<UpfitterQuote>();
		ExternalAccount upfitterAccount;
		RequestContext context = RequestContext.getCurrentInstance();
		
		if(!(MALUtilities.isEmptyString(criteria) || MALUtilities.isEmpty(getSelectedUpfitter()))){
			upfitterAccount = upfitterService.getUpfitterAccount(getSelectedUpfitter().getPayeeAccountCode(), getLoggedInUser().getCorporateEntity());

			result = upfitterService.searchTrimUpfitterQuotesForSetup(
					upfitterAccount.getExternalAccountPK(), getModel(), criteria, new PageRequest(0,50));
		}
		
		setSelectedQuote(null);	
		getDealerAccessoryPrices().clear();		
 
		context.update("dealerAccessoryDT");			
		
		return result;
	}
		
	/**
	 * Searches for dealer accessory codes that match the search term. From the list,
	 * code(s) will be removed if it exists on the upfitter's quote.
	 */
	public void searchAvailableDealerAccessoriesListener(){
    	if(MALUtilities.isNotEmptyString(getDealerAccessorySearchTerm())){    		 
    		setAvailableDealerAccessoryCodes(modelService.getDealerAccessoryCodesByCodeOrDescription(getDealerAccessorySearchTerm(), new PageRequest(0, 200)));
    		    		
    		for(DealerAccessoryPrice price : getDealerAccessoryPrices()){
    			getAvailableDealerAccessoryCodes().remove(price.getDealerAccessory().getDealerAccessoryCode());
    		}    		
    		
    	} else {
    		getAvailableDealerAccessoryCodes().clear();
    	}		
	}
	

	/**
	 * Handles the event of the user click on either the new or existing add dealer accessory dialog tabs.
	 * Focus seems to get lost when switching between tabs. As a result, setFocus is sent in the
	 * response to force focus on first input after n milliseconds.
	 * @param event
	 */
	public void tabChangeListener(TabChangeEvent event){
		System.err.println(event.getTab().getId() + "Active Index=" + getTabIndex());
		maintainQuoteInitialState();
		
//		if(getTabIndex() == 0){
//			RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('newCodeTxt')}, 400)");			
//		} else {
//			RequestContext.getCurrentInstance().execute("setTimeout(function(){setFocus('dealerAccessoryCodeAC_input')}, 400)");			
//		}
		
	}
	
	/**
	 * Handles the event of changing the Vendor/Upfitter on the Update tab.
	 * When the Vendor changes, the Quote No list should be updated
	 * @param AjaxBehaviorEvent
	 */
	public void trimUpfitterChangeListener(AjaxBehaviorEvent event){
		RequestContext context = RequestContext.getCurrentInstance();		
		List<UpfitterQuote> availableQuotes;
		
		if(!MALUtilities.isEmpty(getSelectedUpfitter())){
			availableQuotes = upfitterService.getUpfitterQuotes(upfitterService.getUpfitterAccount(getSelectedUpfitter().getPayeeAccountCode(), getLoggedInUser().getCorporateEntity()));
			setAvailableQuotes(availableQuotes);					
		} else {
			getAvailableQuotes().clear();
		}
		
		getDealerAccessoryPrices().clear();
		setUpdatedQuoteNumber(null);
			
	}
	
	/**
	 * Handles the event of selecting an upfitter quote to maintain.
	 * NOTE: If the quote has been assigned to another trim, the pricing
	 * detail of the quote will be copied and assigned to the selected trim.
	 * @param AjaxBehaviorEvent	 
	 */	
	public void selectUpfitterQuoteListener(AjaxBehaviorEvent event){
		Set<DealerAccessory> dealerAccessorySet; 
		List<DealerAccessoryPrice> prices;
		
		setQuote(getSelectedQuote());
		setQuoteBeenUsed(upfitterService.isReferencedByOtherTrim(getQuote(), getModel()));
		
		//OTD-2238 Detecting whether the quote pricing has been copied for this trim
		for(DealerAccessoryPrice price : getQuote().getDealerAccessoryPrices()){						
			if(isQuoteBeenUsed()){
				if (price.getDealerAccessory().getModel().getModelId().equals(getModel().getModelId())) {
					setQuoteBeenCopied(true);
					break;
				}
			}
		}
		
		setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());	
		
		if(isQuoteBeenUsed() && !isQuoteBeenCopied()){
			prices = copyUfitterQuotePrices();
			getDealerAccessoryPrices().addAll(prices);
		} else {
			prices = loadUfitterQuotePrices();
			getDealerAccessoryPrices().addAll(prices);
		}
						
		Collections.sort(getDealerAccessoryPrices(), new DealerAccessoryPriceComparator());
		
		searchAvailableDealerAccessoriesListener();		
		
		if(isQuoteBeenUsed() && !isQuoteBeenCopied()){
			RequestContext.getCurrentInstance().execute("window.alert(\"" + malMessage.getMessage(UPFITTER_QUOTE_CANNOT_BE_UPDATED) + "\")");
		}
	}
	
	private List<DealerAccessoryPrice> loadUfitterQuotePrices(){
		Set<DealerAccessory> dealerAccessorySet;
		List<DealerAccessoryPrice> prices = new ArrayList<DealerAccessoryPrice>();		
		
		//TODO 1st reference or not. 
		dealerAccessorySet = new HashSet<DealerAccessory>();
		for(DealerAccessoryPrice price : getQuote().getDealerAccessoryPrices()){
			dealerAccessorySet.add(price.getDealerAccessory());
		}
		
		setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());		
		for(DealerAccessory dealerAccessory :dealerAccessorySet){
			
			if(dealerAccessory.getModel().getModelId().equals(getModel().getModelId())){
				prices.add(upfitterService.getEffectiveQuotedPrice(getQuote(), getModel(), dealerAccessory.getDealerAccessoryPrices()));			
			}
						
		}			
		
		return prices;
	}
	
	private List<DealerAccessoryPrice> copyUfitterQuotePrices(){
		Model srcTrim = null;  
		List<DealerAccessoryPrice> prices = new ArrayList<DealerAccessoryPrice>();
		Set<DealerAccessory> dealerAccessorySet = new HashSet<DealerAccessory>();;		
		DealerAccessoryPrice targetPrice = null;
		DealerAccessory targetDealerAccessory = null;
		int counter = 0;
		
		//Copying from another model, a distinct list of the quotes dealer accessories
		for(DealerAccessoryPrice sourcePrice : getQuote().getDealerAccessoryPrices()){
			if(MALUtilities.isEmpty(srcTrim)){
				srcTrim = sourcePrice.getDealerAccessory().getModel();
			} 

			if(!srcTrim.equals(getModel()) && srcTrim.equals(sourcePrice.getDealerAccessory().getModel()) && sourcePrice.getUpfitterQuote().getUfqId().equals(getQuote().getUfqId())){
				dealerAccessorySet.add(sourcePrice.getDealerAccessory());				
			}
		}
		
		//Copying the quote's pricing lines to apply to the selected model
		counter = 0;		
		for(DealerAccessory sourceDealerAccessory : dealerAccessorySet){
			targetDealerAccessory = new DealerAccessory();			
			BeanUtils.copyProperties(sourceDealerAccessory, targetDealerAccessory, 
					new String[]{"dacId", "model", "dealerAccessoryPrices", "quotationDealerAccessories", "versionts"});

			targetDealerAccessory.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
			targetDealerAccessory.setQuotationDealerAccessories(new ArrayList<QuotationDealerAccessory>());
			targetDealerAccessory.setModel(getModel());
			
			for(DealerAccessoryPrice sourcePrice : sourceDealerAccessory.getDealerAccessoryPrices()){
				if(!MALUtilities.isEmpty(sourcePrice.getUpfitterQuote()) 
						&& sourcePrice.getUpfitterQuote().getUfqId().equals(getQuote().getUfqId())){					
					targetPrice = new DealerAccessoryPrice();
					BeanUtils.copyProperties(sourcePrice, targetPrice, 
							new String[]{"dplId", "dealerAccessory", "versionts"});	

					targetPrice.setDealerAccessory(targetDealerAccessory);
					targetPrice.setDplId(counter * -1L);

					targetDealerAccessory.getDealerAccessoryPrices().add(targetPrice);
					
					counter += 1;
				}
			}
			
			prices.add(upfitterService.getEffectiveQuotedPrice(getQuote(), getModel(), targetDealerAccessory.getDealerAccessoryPrices()));						
		}
		
		return prices;
	}
	
	public void addDealerAccessoryOnCloseListener(){
		Map<String, String> params;		
		SimpleDateFormat dateFormatter;
		String dealerAccessoryCodeId; 
		BigDecimal dealerAccessoryBasePrice;
		Date dealerAccessoryEffectiveDate;		
		Long dealerAccessoryLeadTime;
		DealerAccessoryCode addedDealerAccessoryCode;
		
		try {
			dateFormatter = new SimpleDateFormat(MALUtilities.DATE_PATTERN_TIMESTAMP);
			
			params = super.getFaceExternalContext().getRequestParameterMap();
			dealerAccessoryCodeId = params.get("dealerAccessoryCodeId");
			dealerAccessoryBasePrice = new BigDecimal(params.get("dealerAccessoryBasePrice")).setScale(2);			
			dealerAccessoryEffectiveDate = dateFormatter.parse(params.get("dealerAccessoryEffectiveDate"));	
			dealerAccessoryLeadTime = MALUtilities.isEmpty(params.get("dealerAccessoryLeadTime")) ? null : Long.valueOf(params.get("dealerAccessoryLeadTime"));
			addedDealerAccessoryCode = modelService.getDealerAccessoryCode(dealerAccessoryCodeId);
			setSelectedAvailableDealerAccessoryCode(addedDealerAccessoryCode);
			
			addQuotedPriceListener();
			
			for(DealerAccessoryPrice price : getDealerAccessoryPrices()){
				if(price.getDealerAccessory().getDealerAccessoryCode().equals(addedDealerAccessoryCode)){
					price.setBasePrice(dealerAccessoryBasePrice);
					price.setEffectiveDate(dealerAccessoryEffectiveDate);
					price.setLeadTime(dealerAccessoryLeadTime);
					break;
				}
			}
			
			if(!MALUtilities.isEmpty(dealerAccessoryCodeId)){
				super.addSuccessMessage("process.success","Save Dealer Accessory (" + dealerAccessoryCodeId + ")");				
			}
			
			setFocusOnPrice();
			
		} catch (ParseException e) {
			super.addErrorMessageSummary("custom.message", e.getMessage());
		}
	}
	
	public void addNewDealerAccessoryListener(){
		if(hasValidQuoteHeader()){
			RequestContext.getCurrentInstance().execute("showCCAddDealerAccessory()");			
		}
	}
	

	private boolean hasValidQuoteHeader(){		
		boolean isValid = true;
		
		if(MALUtilities.isEmpty(getSelectedUpfitterAcccount())) {
			super.addErrorMessageSummary(UI_ID_AVAILABLE_UPFITTER_MNU, "custom.message", "Vendor is required");
			isValid = false;
		}			
		if(MALUtilities.isEmpty(getSelectedQuote())){
			super.addErrorMessageSummary(UI_ID_UPFITTER_QUOTE_AC, "custom.message", "Quote Number is required");	
			isValid = false;
		} 		
		
		return isValid;
	}
	
    /**
     * Handles page cancel button click event. 
     * @return The calling view
     */
    public String cancel(){
    	return super.cancelPage();      	
    }
    
    /**
     * Handles page cancel button click event. 
     * @return The calling view
     */    
    public String saveQuoteListener(){
    	UpfitterQuote quote;
    	if(!isValidQuote()) return null;
    	
    	synchronizeQuoteForSave(); 
    	initializeDplId(false);
    	
    	try { 
    		quote = upfitterService.saveOrUpdateUpfitterQuote(getQuote(), getModel());
			setQuote(quote);
			super.addSuccessMessage("process.success","Save Vendor Quote (" + getQuote().getQuoteNumber() + ")");			
			return super.cancelPage();   			
		} catch (MalBusinessException e) {
			super.addErrorMessageSummary("custom.message", e.getMessage());
			initializeDplId(true);
			//synchronizeDealerAccessoryPriceWithQuote();			
			return null;
		}
    	
 	
    }
    
    private boolean isValidQuote(){
    	boolean isValid = true;
    	DealerAccessory dbDealerAccessory;
		StringBuilder message = new StringBuilder();
				
		if(MALUtilities.isEmpty(getQuote().getExternalAccount())){
			super.addErrorMessageSummary(UI_ID_AVAILABLE_UPFITTER_MNU, "custom.message", "Vendor is required");	
			isValid = false;
		}

		if(MALUtilities.isEmpty(getSelectedQuote())){
			super.addErrorMessageSummary(UI_ID_UPFITTER_QUOTE_AC, "custom.message", "Quote Number is required");	
			isValid = false;				
		}
			
		
		if(dealerAccessoryPrices.isEmpty()){
			super.addErrorMessageSummary("required.field", "Pricing");
			isValid = false;
		}
		
		for(DealerAccessoryPrice price : getDealerAccessoryPrices()){
			message.setLength(0);
			
			if(MALUtilities.isEmpty(price.getBasePrice())){
				message.append(message.length() > 0 ? ", Price " : "Price");
				isValid = false;				
			}
			
			if(MALUtilities.isEmpty(price.getEffectiveDate())){
				message.append(message.length() > 0 ? ", Effective Date " : "Effective Date");
				isValid = false;				
			} 
			
			if(message.length() > 0){ 
				super.addErrorMessageSummary("required.field", price.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode() + " " + message.toString());
				isValid = false;				
			}
			
			if(!MALUtilities.isEmpty(price.getEffectiveDate()) && MALUtilities.compateDates(price.getEffectiveDate(), Calendar.getInstance().getTime()) > 0){
				super.addErrorMessageSummary("future.date.error", price.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode() + " Effective Date");
				isValid = false;						
			}
			
			//OTD-479: Performing the unique pricing check and displaying the accessory code in message when validation fails
			try {
				if(!MALUtilities.isEmpty(price.getDealerAccessory().getDacId())) {
					dbDealerAccessory = modelService.getDealerAccessoryWithPrices(price.getDealerAccessory().getDacId());
					modelService.validateUniqueVendorEffectiveDate(dbDealerAccessory, price);
				}
			} catch (Exception e) {
				super.addErrorMessageSummary("custom.message", e.getMessage());
				isValid = false;					
			}
			
		}
								
    	return isValid;
    }
          
	/**
	 * Determines which lead time to use. If a lead time has not
	 * been specified at the upfitter price level, then use the default
	 * lead time specified at the dealer accessory level.
	 * @param dealerAccessoryPrice The effective upfitter price
	 * @return Long The determined lead time for the upfitter accessory
	 */
	public Long determineLeadTime(DealerAccessoryPrice dealerAccessoryPrice){
		return upfitterService.determineLeadTime(dealerAccessoryPrice);
	}
	
	public String formatSelectedUpfitterAddress(){
		String formattedAddress = null;
		
		if(!MALUtilities.isEmpty(getSelectedUpfitter())){
			formattedAddress = upfitterService.formatUpfitterAddress(getSelectedUpfitter());
		}
		return formattedAddress;
	}
	
	public DealerAccessoryPrice getUpfitterEffectivePrice(List<DealerAccessoryPrice> upfitterAccessoryPrices){
		//return upfitterService.getUpfitterEffectivePrice(upfitterAccessoryPrices);
		return upfitterService.getEffectiveQuotedPrice(getQuote(), getModel(), upfitterAccessoryPrices);
	}
	
    /**
     * Determines whether the pick list vcr buttons are enable or disabled
     * @return boolean
     */
    public boolean isPickListBtnActive(){
        boolean isActive = false;
        
        if(super.hasPermission() && !MALUtilities.isEmpty(selectedUpfitterAcccount)){
        	isActive = true;
        }       
        
        return isActive;
    }
    
    /**
     * Clears the properties that stores the selected value from
     * the autocomplete components.
     */
    public void clearAutoCompletes(){
    	setSelectedUpfitter(null);
    	setSelectedUpfitterAcccount(null);
    }
    
	/**
	 * Sets variables on entry to the page
	 */
	protected void loadNewPage() {			
		super.thisPage.setPageDisplayName(ViewConstants.DISPLAY_NAME_MAINT_UPFITTER_QUOTE);
		super.thisPage.setPageUrl(ViewConstants.UPFITTER_QUOTE);

		this.paramModelIds = new ArrayList<Long>();
		this.paramModelIds.add((Long)super.thisPage.getInputValues().get(ViewConstants.VIEW_PARAM_MDL_ID));
		
		this.models = new ArrayList<Model>();
		this.models.add(modelService.getModel(getParamModelIds().get(0)));
		
		this.model = this.models.get(0);
	}

	protected void restoreOldPage() {}

	/**
	 * Converts the selected code to a dealer accessory price so that
	 * it can be added to the list for quoted dealer accessory prices
	 * @param codes
	 */
	private void transferCodeToQuotedPrice(List<DealerAccessoryCode> codes){
		DealerAccessoryPrice price;
		DealerAccessory modelAccessory;
		
		List<DealerAccessoryCode> selectedCodes = new ArrayList<DealerAccessoryCode>(codes);		
		
		for(DealerAccessoryCode code : selectedCodes){	
			modelAccessory =  new DealerAccessory();
			price = new DealerAccessoryPrice();
			
			//Retrieve the dealer accessory if one exists for the trim. Otherwise, create a new one
			modelAccessory = modelService.getModelDealerAccessory(getModel(), code);
			if(MALUtilities.isEmpty(modelAccessory)){
				modelAccessory = new DealerAccessory();
				modelAccessory.setDealerAccessoryCode(code);
				modelAccessory.setModel(getModel());
				modelAccessory.setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
			}
					
			//Temporary id used for comparing, remove prior to save.
			price.setDplId(Calendar.getInstance().getTimeInMillis() * -1L);
			price.setDealerAccessory(modelAccessory);
			price.setVatAmount(new BigDecimal(0.00).setScale(2));
			price.setPayeeAccount(getQuote().getExternalAccount());
			price.setEffectiveDate(Calendar.getInstance().getTime());
			price.setLeadTime(code.getLeadTime());
			price.setUpfitterQuote(getQuote());
			modelAccessory.getDealerAccessoryPrices().add(price);
			
			getDealerAccessoryPrices().add(price);
			
			getAvailableDealerAccessoryCodes().remove(modelAccessory.getDealerAccessoryCode());			
		}				
	}
	
	private void addQuoteInitialState(){
		RequestContext context = RequestContext.getCurrentInstance();
		
		getAvaialableUpfitters().clear();
		getAvailableQuotes().clear();
		getAvailableDealerAccessoryCodes().clear();
		getDealerAccessoryPrices().clear();
		setSelectedAvailableDealerAccessoryCode(null);
		setSelectedQuote(null);
		setSelectedQuotedPrice(null);
		setSelectedUpfitter(null);
		setDealerAccessorySearchTerm(null);	
		setQuote(new UpfitterQuote());
		getQuote().setDealerAccessoryPrices(new ArrayList<DealerAccessoryPrice>());
		setSelectedUpfitterAcccount(null);
		setDealerAccessorySearchTerm(null);
		
		context.execute("PF('trimUpfitterMnuWidgetVar').selectValue('')");		
		
	}
	
	private void maintainQuoteInitialState(){
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();		
		List<UpfitterSearchResultVO> upfitters;	
				
		getAvaialableUpfitters().clear();
		getAvailableQuotes().clear();
		getAvailableDealerAccessoryCodes().clear();
		getDealerAccessoryPrices().clear();
		setSelectedAvailableDealerAccessoryCode(null);
		setSelectedQuote(null);
		setSelectedQuotedPrice(null);
		setSelectedUpfitter(null);	
		setDealerAccessorySearchTerm(null);
		setQuote(new UpfitterQuote());	
		setSelectedUpfitterAcccount(null);
		setDealerAccessorySearchTerm(null);
		
		
		searchCriteria.setModelId(getModel().getModelId());
		searchCriteria.setTerm("%");
		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
		searchCriteria.setWithQuoteNo(true);
		upfitters = upfitterService.searchUpfittersAccessoriesOptional(getModel(), searchCriteria, new PageRequest(0,50), null);			

		Collections.sort(upfitters, new Comparator<UpfitterSearchResultVO>() { 
			public int compare(UpfitterSearchResultVO uf1, UpfitterSearchResultVO uf2) { 
				return uf1.getPayeeAccountName().toLowerCase().compareTo(uf2.getPayeeAccountName().toLowerCase()); 
			}
		});					
		
		setAvaialableUpfitters(upfitters);
				
	}
	
	/**
	 * Synch up the UI quote changes (input) with the Quote entity
	 * persisted to the database.
	 */
    private void synchronizeQuoteForSave(){ 	
    	List<DealerAccessoryPrice> dbPrices =  getQuote().getDealerAccessoryPrices();
    	List<DealerAccessoryPrice> uiPrices = getDealerAccessoryPrices();
    	DealerAccessoryPrice dbPrice;
    	
    	if(!MALUtilities.isEmpty(getUpdatedQuoteNumber())){
    		getQuote().setQuoteNumber(getUpdatedQuoteNumber().toUpperCase());
    	}

    	
    	//Assigning necessary properties for adding or updating a quoted prices
    	for(DealerAccessoryPrice uiPrice : uiPrices){
    		if(dbPrices.contains(uiPrice)){ //Update    		
    			dbPrice = dbPrices.get(dbPrices.indexOf(uiPrice));
    			dbPrice.setBasePrice(uiPrice.getBasePrice());
    			dbPrice.setVatAmount(uiPrice.getVatAmount());
    			dbPrice.setTotalPrice(uiPrice.getBasePrice().add(uiPrice.getVatAmount()));    			
    			dbPrice.setEffectiveDate(uiPrice.getEffectiveDate()); 
    			dbPrice.setLeadTime(uiPrice.getLeadTime()); 
    			dbPrice.setPayeeAccount(getQuote().getExternalAccount());
    		} else {  //Add
    			uiPrice.setTotalPrice(uiPrice.getBasePrice().add(uiPrice.getVatAmount()));
    			uiPrice.setUpfitterQuote(getQuote());
    			uiPrice.setPayeeAccount(getQuote().getExternalAccount());    			
    			dbPrices.add(uiPrice);
    		}
    	}    	    	  
    }	
    
    //TODO Explain what problem is solved by this method. What is it's purpose?
    private void synchronizeDealerAccessoryPriceWithQuote(){
		Set<DealerAccessory> groupedAccessories = new HashSet<DealerAccessory>();
		
		for(DealerAccessoryPrice price : getQuote().getDealerAccessoryPrices()){
			groupedAccessories.add(price.getDealerAccessory());
		}
		
		for(DealerAccessory dealerAccessory : groupedAccessories){
			//getDealerAccessoryPrices().add(upfitterService.getUpfitterEffectivePrice(dealerAccessory.getDealerAccessoryPrices()));
			getDealerAccessoryPrices().add(upfitterService.getEffectiveQuotedPrice(getQuote(), getModel(), dealerAccessory.getDealerAccessoryPrices()));			
		}		
		
//    	Map<Long, List<DealerAccessoryPrice>> groupedAccessoryPrices = new HashMap<Long, List<DealerAccessoryPrice>>();
//		
//		//TODO Group prices by dealer accessory
//		for(DealerAccessoryPrice price : getQuote().getDealerAccessoryPrices()){
//			if(MALUtilities.isEmpty(groupedAccessoryPrices.get(price.getDealerAccessory().getDacId()))){
//				groupedAccessoryPrices.put(price.getDealerAccessory().getDacId(), new ArrayList<DealerAccessoryPrice>());
//			}
//			groupedAccessoryPrices.get(price.getDealerAccessory().getDacId()).add(price);
//		}
//		
//    	getDealerAccessoryPrices().clear();    		
//		for(Long key : groupedAccessoryPrices.keySet()){
//			getDealerAccessoryPrices().add(upfitterService.getUpfitterEffectivePrice(groupedAccessoryPrices.get(key)));
//		}    	

    }
    
    /**
     * Sets the prices dplId property used as a rowkey to a unique negative value
     * or to null for saving.
     */
    private void initializeDplId(boolean setNullToNegative){
    	for(DealerAccessoryPrice price : getQuote().getDealerAccessoryPrices()){
    		if(price.getDealerAccessory().getModel().getModelId().equals(getModel().getModelId())){
    			if(MALUtilities.isEmpty(price.getDplId()) || price.getDplId() < 0) {
    				if(setNullToNegative) { 
    					price.setDplId(Calendar.getInstance().getTimeInMillis() * -1L);
    				} else {
    					price.setDplId(null);
    				}    			
    			}			
    		}
    	}
    }
	
    
	/**
	 * Sets focus on the price field within the last row on the dealer accessories price list
	 */
	private void setFocusOnPrice(){
		int index;
		
		index = getDealerAccessoryPrices().size() - 1;
		RequestContext.getCurrentInstance().execute("goToPrice(" + index + ")");		
		
	}    
    
	public List<Long> getParamModelIds() {
		return paramModelIds;
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

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getQuoteNumber() {
		return quoteNumber;
	}

	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber.toUpperCase();
	}

	public UpfitterQuote getQuote() {
		return quote;
	}

	public void setQuote(UpfitterQuote quote) {
		this.quote = quote;
	}

	public String getDealerAccessorySearchTerm() {
		return dealerAccessorySearchTerm;
	}

	public void setDealerAccessorySearchTerm(String dealerAccessorySearchTerm) {
		this.dealerAccessorySearchTerm = dealerAccessorySearchTerm;
	}

	public List<ExternalAccount> getUpfitters() {
		return upfitters;
	}

	public void setUpfitters(List<ExternalAccount> upfitters) {
		this.upfitters = upfitters;
	}

	public List<DealerAccessoryCode> getAvailableDealerAccessoryCodes() {
		return availableDealerAccessoryCodes;
	}

	public void setAvailableDealerAccessoryCodes(
			List<DealerAccessoryCode> availableDealerAccessoryCodes) {
		this.availableDealerAccessoryCodes = availableDealerAccessoryCodes;
	}

	public DealerAccessoryCode getSelectedAvailableDealerAccessoryCode() {
		return selectedAvailableDealerAccessoryCode;
	}

	public void setSelectedAvailableDealerAccessoryCode(
			DealerAccessoryCode selectedAvailableDealerAccessoryCode) {
		this.selectedAvailableDealerAccessoryCode = selectedAvailableDealerAccessoryCode;
	}

	public DealerAccessoryPrice getSelectedQuotedPrice() {
		return selectedQuotedPrice;
	}

	public void setSelectedQuotedPrice(DealerAccessoryPrice selectedQuotedPrice) {
		this.selectedQuotedPrice = selectedQuotedPrice;
	}

	public UpfitterSearchResultVO getSelectedUpfitter() {
		return selectedUpfitter;
	}

	public void setSelectedUpfitter(UpfitterSearchResultVO selectedUpfitter) {
		this.selectedUpfitter = selectedUpfitter;
		if(!MALUtilities.isEmpty(this.selectedUpfitter)){
			this.selectedUpfitterAcccount = upfitterService.getUpfitterAccount(
					this.selectedUpfitter.getPayeeAccountCode(), getLoggedInUser().getCorporateEntity());	
			System.err.println("Selected Upffiter " + getSelectedUpfitterAcccount().getAccountName());			
		}		
	}

	public List<UpfitterSearchResultVO> getAvaialableUpfitters() {
		return avaialableUpfitters;
	}

	public void setAvaialableUpfitters(List<UpfitterSearchResultVO> avaialableUpfitters) {
		this.avaialableUpfitters = avaialableUpfitters;
	}

	public List<UpfitterQuote> getAvailableQuotes() {
		return availableQuotes;
	}

	public void setAvailableQuotes(List<UpfitterQuote> availableQuotes) {
		this.availableQuotes = availableQuotes;
	}

	public UpfitterQuote getSelectedQuote() {
		return selectedQuote;
	}

	public void setSelectedQuote(UpfitterQuote selectedQuote) {
		this.selectedQuote = selectedQuote;
	}

	public String getUpdatedQuoteNumber() {
		return updatedQuoteNumber;
	}

	public void setUpdatedQuoteNumber(String updatedQuoteNumber) {
		this.updatedQuoteNumber = updatedQuoteNumber;
	}

	public List<DealerAccessoryPrice> getDealerAccessoryPrices() {
		return dealerAccessoryPrices;
	}

	public void setDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices) {
		this.dealerAccessoryPrices = dealerAccessoryPrices;
	}

	public ExternalAccount getSelectedUpfitterAcccount() {
		return selectedUpfitterAcccount;
	}

	public void setSelectedUpfitterAcccount(ExternalAccount selectedUpfitterAcccount) {
		this.selectedUpfitterAcccount = selectedUpfitterAcccount;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	public boolean isQuoteBeenUsed() {
		return quoteBeenUsed;
	}

	public void setQuoteBeenUsed(boolean quoteBeenUsed) {
		this.quoteBeenUsed = quoteBeenUsed;
	}

	public boolean isQuoteBeenCopied() {
		return quoteBeenCopied;
	}

	public void setQuoteBeenCopied(boolean quoteBeenCopied) {
		this.quoteBeenCopied = quoteBeenCopied;
	}

}
