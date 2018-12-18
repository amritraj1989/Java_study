package com.mikealbert.vision.view.bean.components;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.LogBookService;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.WillowConfigService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;
 
@Component
@Scope("view")
public class AddDealerAccessoryBean extends BaseBean {
	private static final long serialVersionUID = -4027924859742064351L;	
	private static final String UI_ID_DEALER_ACCESSORY_CODE = "ccAccessoryCodeTxt";
	private static final String UI_ID_DEALER_ACCESSORY_DESCRIPTION = "ccAccessoryDescriptionTxt";
	private static final String UI_ID_DEALER_ACCESSORY_CATEGORY = "ccCategoryMnu";
	private static final String UI_ID_DEALER_ACCESSORY_PRICE = "ccBasePrice";	
	private static final String UI_ID_DEALER_ACCESSORY_EFFECTIVE_DATE = "ccEffectiveDate";	
//	private static final String DEFAULT_ACCESSORY_CATEGORY_CODE = "4"; //Dealer Installed Options
	private static final String VIEW_NAME = "addDealerAccessory";
	
	@Resource LogBookService logBookService;
	@Resource ModelService modelService;
	@Resource WillowConfigService willowConfigService;
	

	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());	
	private String clientId;
	private ExternalAccount upfitter;
	private Model trim;
	private List<Model> trims;
	private String onCloseCallback;
	private String resource;
	private DealerAccessoryCode dealerAccessoryCode;
	private DealerAccessory dealerAccessory;
	private DealerAccessoryPrice dealerAccessoryPrice;
	private List<OptionAccessoryCategory> accessoryCategories;	
	private String defaultDealerAccessoryCategoryCode;
		
	/**
	 * Initializes the bean
	 */
    public void init(){ 
		FacesContext fc;	

    	try {
    		clearEverything();
    		
    		fc = FacesContext.getCurrentInstance(); 
    		setUpfitter(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.upfitter}", ExternalAccount.class));  
    		setTrim(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.trim}", Model.class)); 
    		setOnCloseCallback(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.onClose}", String.class));
    		setResource(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.resource}", String.class));  
    		setClientId(fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.clientId}", String.class));
    		   	
    		setTrims(new ArrayList<Model>());
    		getTrims().add(getTrim());

    		setDealerAccessoryCode(new DealerAccessoryCode());
    		getDealerAccessoryCode().setDealerAccessories(new ArrayList<DealerAccessory>());
    		getDealerAccessoryCode().setCommonInd("N");
    				  		
    		setDealerAccessory(new DealerAccessory());
    		getDealerAccessory().setModel(getTrim());
    		getDealerAccessory().setDealerAccessoryCode(getDealerAccessoryCode());
    		    		
    		//This price is not saved. It is merely used to capture the desired price for the 
    		//dealer accessory and pass it back to the calling view for further processing. 
    		setDealerAccessoryPrice(new DealerAccessoryPrice());   		
    		getDealerAccessoryPrice().setEffectiveDate(Calendar.getInstance().getTime());
    		
    		//OTD-753: Default the category to Dealer Installed Options
    		setAccessoryCategories(modelService.getMafsOptionAccessoryCategories());
    		defaultDealerAccessoryCategoryCode = willowConfigService.getConfigValue("DA_CATEGORY_DEFAULT_CODE");
    		for(OptionAccessoryCategory category : getAccessoryCategories()){
    			if(category.getCode().equals(defaultDealerAccessoryCategoryCode)){
    				getDealerAccessory().getDealerAccessoryCode().setOptionAccessoryCategory(category);
    				break;
    			}
    		}    		
    		    		
			super.setDirtyData(false);

		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
			logger.error(e);
		}    	
      	    	     	
    }


    /**
     * Clears the component's attributes to bring it back to an initial state.
     */
	private void clearEverything() {
		setUpfitter(null);
		setTrim(null);
		setTrims(null);
		setOnCloseCallback(null);
		setResource(null);
		setDealerAccessoryCode(null);
		setDealerAccessory(null);
		setDealerAccessoryPrice(null);
		setAccessoryCategories(null);
	}

		
    /**
     * Initializes the parameters to pass back to the callback function
     * Here is were we pass back the saved dealer accessory code and its
     * unsaved pricing information.
     */
	public void onHideListener(){			
		SimpleDateFormat dateFormatter;
		
		dateFormatter = new SimpleDateFormat(MALUtilities.DATE_PATTERN_TIMESTAMP);

		if(!MALUtilities.isEmpty(getDealerAccessoryCode().getAccessoryCode())){
			RequestContext.getCurrentInstance().addCallbackParam("dealerAccessoryCode", getDealerAccessoryCode().getAccessoryCode());
			RequestContext.getCurrentInstance().addCallbackParam("dealerAccessoryBasePrice", getDealerAccessoryPrice().getBasePrice());
			RequestContext.getCurrentInstance().addCallbackParam("dealerAccessoryEffectiveDate", dateFormatter.format(getDealerAccessoryPrice().getEffectiveDate()));
			
			if(!MALUtilities.isEmpty(getDealerAccessoryCode().getLeadTime())){
				RequestContext.getCurrentInstance().addCallbackParam("dealerAccessoryLeadTime", getDealerAccessoryCode().getLeadTime());				
			}			
		}	
	}
		
	/**
	 * 	Saves the dealer accessory code and assigns it to the trim
	 *  with a no vendor pricing in the process.
	 */
	public void save(){
		RequestContext context;
		DealerAccessory savedDealerAccessory;
		
		context = RequestContext.getCurrentInstance();
		
		try {
			if(isValidInput()){
				getDealerAccessoryCode().setNewAccessoryCode(
						getDealerAccessoryCode().getNewAccessoryCode().toUpperCase());	
				savedDealerAccessory = modelService.saveOrUpdateDealerAccessory(getDealerAccessory());
				setDealerAccessory(savedDealerAccessory);
				setDealerAccessoryCode(getDealerAccessory().getDealerAccessoryCode());				
				context.execute("ccPostSaveBtnHandler()");	
			}
								
		} catch (MalBusinessException e) {
			super.addErrorMessage("generic.error", e.getMessage());
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
		
		if(modelService.isCodeInSystem(getDealerAccessoryCode().getAccessoryCode()) 
				|| modelService.isCodeInSystem(getDealerAccessoryCode().getNewAccessoryCode())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_CODE, "custom.message", "Code already exists");
			isValid = false;			
		}
		if(MALUtilities.isEmpty(getDealerAccessoryCode().getNewAccessoryCode())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_CODE, "required.field", "Code");	
			isValid = false;
		}
		if(MALUtilities.isEmpty(getDealerAccessoryCode().getDescription())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_DESCRIPTION, "required.field", "Description");	
			isValid = false;
		}
		if(MALUtilities.isEmpty(getDealerAccessoryCode().getOptionAccessoryCategory())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_CATEGORY, "required.field", "Category");	
			isValid = false;
		}
		if(MALUtilities.isEmpty(getDealerAccessoryPrice().getBasePrice())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_PRICE, "required.field", "Price");	
			isValid = false;
		}
		if(MALUtilities.isEmpty(getDealerAccessoryPrice().getEffectiveDate())){
			super.addErrorMessage(getClientId() + ":" + UI_ID_DEALER_ACCESSORY_EFFECTIVE_DATE, "required.field", "Effective Date");	
			isValid = false;
		}					
		
		return isValid;
	}
	
	/**
	 * Since this is a re-usable component, permission should be inherited 
	 * from the the calling resource. Otherwise
	 * @return boolean
	 */
	public boolean hasPermission(){
		boolean isPermitted = false;
	
		isPermitted = super.hasPermission(getResource());
		
		return isPermitted;
	}

	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public ExternalAccount getUpfitter() {
		return upfitter;
	}

	public void setUpfitter(ExternalAccount upfitter) {
		this.upfitter = upfitter;
	}

	public Model getTrim() {
		return trim;
	}

	public void setTrim(Model trim) {
		this.trim = trim;
	}

	public List<Model> getTrims() {
		return trims;
	}

	public void setTrims(List<Model> trims) {
		this.trims = trims;
	}

	public String getOnCloseCallback() {
		return onCloseCallback;
	}

	public void setOnCloseCallback(String onCloseCallback) {
		this.onCloseCallback = onCloseCallback;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public DealerAccessoryCode getDealerAccessoryCode() {
		return dealerAccessoryCode;
	}

	public void setDealerAccessoryCode(DealerAccessoryCode dealerAccessoryCode) {
		this.dealerAccessoryCode = dealerAccessoryCode;
	}

	public DealerAccessory getDealerAccessory() {
		return dealerAccessory;
	}

	public void setDealerAccessory(DealerAccessory dealerAccessory) {
		this.dealerAccessory = dealerAccessory;
	}

	public DealerAccessoryPrice getDealerAccessoryPrice() {
		return dealerAccessoryPrice;
	}

	public void setDealerAccessoryPrice(DealerAccessoryPrice dealerAccessoryPrice) {
		this.dealerAccessoryPrice = dealerAccessoryPrice;
	}

	public List<OptionAccessoryCategory> getAccessoryCategories() {
		return accessoryCategories;
	}

	public void setAccessoryCategories(List<OptionAccessoryCategory> accessoryCategories) {
		this.accessoryCategories = accessoryCategories;
	}
		
}
