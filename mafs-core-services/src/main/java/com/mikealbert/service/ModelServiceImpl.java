package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.dao.DealerAccessoryCodeDAO;
import com.mikealbert.data.dao.DealerAccessoryDAO;
import com.mikealbert.data.dao.DealerAccessoryPriceDAO;
import com.mikealbert.data.dao.MakeModelRangeDAO;
import com.mikealbert.data.dao.ModelColourDAO;
import com.mikealbert.data.dao.ModelColourTrimDAO;
import com.mikealbert.data.dao.ModelDAO;
import com.mikealbert.data.dao.ModelPriceDAO;
import com.mikealbert.data.dao.ModelTypeDAO;
import com.mikealbert.data.dao.OptionAccessoryCategoryDAO;
import com.mikealbert.data.dao.OptionPackHeaderDAO;
import com.mikealbert.data.dao.OptionalAccessoryDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.StandardAccessoryDAO;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.MakeModelRange;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelColour;
import com.mikealbert.data.entity.ModelPrice;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.OptionPackCost;
import com.mikealbert.data.entity.OptionPackHeader;
import com.mikealbert.data.entity.OptionPrice;
import com.mikealbert.data.entity.OptionalAccessory;
import com.mikealbert.data.entity.StandardAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationType;
import com.mikealbert.ws.vehcfg.client.VehicleOptionType;

import java.util.Arrays;


/**
 *     
 * Implementation of {@link com.mikealbert.vision.service.ModelService}
 */
@Service("modelService")
public class ModelServiceImpl implements ModelService {
	@Resource ModelDAO modelDAO;
	@Resource ModelPriceDAO modelPriceDAO;	
	@Resource DealerAccessoryPriceDAO dealerAccessoryPriceDAO;
	@Resource OptionalAccessoryDAO optionalAccessoryDAO;
	@Resource OptionPackHeaderDAO optionPackHeaderDAO;
	@Resource DealerAccessoryCodeDAO dealerAccessoryCodeDAO;
	@Resource DealerAccessoryDAO dealerAccessoryDAO;
	@Resource MakeModelRangeDAO makeModelRangeDAO;
	@Resource ModelTypeDAO modelTypeDAO;
	@Resource OptionAccessoryCategoryDAO optionAccessoryCategoryDAO;
	@Resource WillowConfigService willowConfigService;
	@Resource StandardAccessoryDAO standardAccessoryDAO;
	@Resource ModelColourDAO modelColourDAO;
	@Resource ModelColourTrimDAO modelColourTrimDAO; 	
	@Resource QuotationModelDAO quotationModelDAO;
	
	@Transactional
	public Model getModel(Long mdlId){
		Model model;
		
		model = modelDAO.findById(mdlId).orElse(null);
		model.getModelPrices().size();
		
		for(OptionalAccessory optAccessory : model.getOptionalAccessories()){
			//optAccessory.getOptionPrices().size();	
			//Sorting the standard accessories in ASC order
			Collections.sort(optAccessory.getOptionPrices(), new Comparator<OptionPrice>() { 
				public int compare(OptionPrice op1, OptionPrice op2) { 
					return op2.getEffectiveDate().compareTo(op1.getEffectiveDate()); 
				}
			});			
		}
		
		for(DealerAccessory dlrAccessory : model.getDealerAccessories()){
			dlrAccessory.getDealerAccessoryPrices().size();
		}	
		
		for(OptionPackHeader oph : model.getOptionPackHeaders()){
			//oph.getOptionPackCosts().size();
			
			//Sorting the option pack price list in ASC order
			Collections.sort(oph.getOptionPackCosts(), new Comparator<OptionPackCost>() { 
				public int compare(OptionPackCost opc1, OptionPackCost opc2) { 
					return opc2.getEffectiveDate().compareTo(opc1.getEffectiveDate()); 
				}
			});	
			
			oph.getOptionalAccessories().size();
			oph.getOptionPackDetail().size();
		}
		
		//Sorting the standard accessories in ASC order
		Collections.sort(model.getStandardAccessories(), new Comparator<StandardAccessory>() { 
			public int compare(StandardAccessory sac1, StandardAccessory sac2) { 
				return sac1.getStandardAccessoryCode().getNewAccessoryCode().compareTo(sac2.getStandardAccessoryCode().getNewAccessoryCode()); 
			}
		});	
		
		//Sorting the option packs in ASC order
		Collections.sort(model.getOptionPackHeaders(), new Comparator<OptionPackHeader>() { 
			public int compare(OptionPackHeader oph1, OptionPackHeader oph2) { 
				return oph1.getOptionPackTypeCode().getCode().compareTo(oph2.getOptionPackTypeCode().getCode()); 
			}
		});				
		
		
		//Sorting the optional accessories in ASC order
		Collections.sort(model.getOptionalAccessories(), new Comparator<OptionalAccessory>() { 
			public int compare(OptionalAccessory oac1, OptionalAccessory oac2) { 
				return oac1.getAccessoryCode().getNewAccessoryCode().compareTo(oac2.getAccessoryCode().getNewAccessoryCode()); 
			}
		});				
		
		
		//Sorting the dealer accessories in ASC order
		Collections.sort(model.getDealerAccessories(), new Comparator<DealerAccessory>() { 
			public int compare(DealerAccessory dac1, DealerAccessory dac2) { 
				return dac1.getDealerAccessoryCode().getNewAccessoryCode().compareTo(dac2.getDealerAccessoryCode().getNewAccessoryCode()); 
			}
		});				
				

		return model;
	}
	

	@Transactional
	public OptionPackHeader getOptionPack(Long ophId){
		OptionPackHeader optionPack;
		optionPack = optionPackHeaderDAO.findById(ophId).orElse(null);
		optionPack.getOptionalAccessories().size();
		optionPack.getOptionPackCosts().size();
		optionPack.getOptionPackDetail().size();
		
		for(OptionalAccessory oac : optionPack.getOptionalAccessories()){
			oac.getOptionPrices().size();
		}	
		
		return optionPack;
	}
	
	@Transactional(readOnly=true)
	public List<DealerAccessoryCode> getDealerAccessoryCodes(String criteria, PageRequest page){
		List<DealerAccessoryCode> dealerAccessoryCodes;
		dealerAccessoryCodes = dealerAccessoryCodeDAO.findDealerAccessoryCodesByCodeOrDescription(DataUtilities.appendWildCardToRight(criteria), page);
		return dealerAccessoryCodes;
	}
	
	@Transactional(readOnly=true)
	public List<DealerAccessoryCode> getDealerAccessoryCodesByCodeOrDescription(String criteria, PageRequest page){
		List<DealerAccessoryCode> dealerAccessoryCodes;
		dealerAccessoryCodes = dealerAccessoryCodeDAO.getDealerAccessoryCodesByCodeOrDescription(DataUtilities.appendWildCardToRight(criteria), page);
		return dealerAccessoryCodes;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<OptionAccessoryCategory> getMafsOptionAccessoryCategories(){
		List<OptionAccessoryCategory> optionAccessoryCategories = null;
		String dealerAccessoryCategoryDropdown = willowConfigService.getConfigValue("DLR_ACCY_CAT_DROPDOWN");
		if (!MALUtilities.isEmpty(dealerAccessoryCategoryDropdown)) {
			List<String> dealerAccessoryCategoryList = Arrays.asList(dealerAccessoryCategoryDropdown.replaceAll(" ", "").split(","));
			optionAccessoryCategories = optionAccessoryCategoryDAO.getMafsOptionAccessoryCategories(dealerAccessoryCategoryList);
		}
		return optionAccessoryCategories;
	}
	
	@Transactional(readOnly=true)
	public List<DealerAccessory> getDealerAccessories(String criteria, PageRequest page){
		List<DealerAccessory> dealerAccessories;
		dealerAccessories = dealerAccessoryDAO.findDealerAccessoryByCodeOrDescription(DataUtilities.appendWildCardToRight(criteria), page);
		return dealerAccessories;
	}	
	
	@Transactional(readOnly=true)
	public DealerAccessoryCode getDealerAccessoryCode(String accessoryCodeId){
		String codeId = accessoryCodeId;
	    return dealerAccessoryCodeDAO.findById(codeId).orElse(null);	
	}
	
	@Transactional(readOnly=true)
	public boolean isCodeInSystem(String code){
		boolean isInSystem = false;
		List<DealerAccessoryCode> dealerAccessoryCodes;
		
		dealerAccessoryCodes =  dealerAccessoryCodeDAO.findDealerAccessoryCodesByCodeOrDescription(code, new PageRequest(0, 1));
		if(dealerAccessoryCodes.size() > 0){
			isInSystem = true;
		} 
		
		return isInSystem;
	}
	
	@Transactional
	public OptionalAccessory getFactoryAccessory(Long oacId){
		OptionalAccessory factoryAccessory;
		factoryAccessory = optionalAccessoryDAO.findById(oacId).orElse(null);
		factoryAccessory.getOptionPrices().size();
		return factoryAccessory;
	}
	
	@Transactional
	public List<ModelPrice> getModelPrices(Long mdlId){
		List<ModelPrice> modelPrices;
		modelPrices = modelDAO.findById(mdlId).orElse(null).getModelPrices();
		modelPrices.size();
		Collections.sort(modelPrices, new Comparator<ModelPrice>() { 
			public int compare(ModelPrice mpr1, ModelPrice mpr2) { 
				return mpr2.getEffectiveDate().compareTo(mpr1.getEffectiveDate()); 
			}
		});			
		return modelPrices;
	}
	
	@Transactional
	public DealerAccessory getDealerAccessory(Long dacId){
		DealerAccessory dealerAccessory;
		dealerAccessory = dealerAccessoryDAO.findById(dacId).orElse(null);
		dealerAccessory.getDealerAccessoryPrices().size();
		return dealerAccessory;
	}
	
	@Transactional
	public List<DealerAccessoryPrice> getDealerAccessoryPriceList(DealerAccessory dealerAccessory, ExternalAccount vendorAccount){
		List<DealerAccessoryPrice> dealerAccessoryPrices;
		
		if(MALUtilities.isEmpty(vendorAccount)){
			dealerAccessoryPrices = dealerAccessoryPriceDAO.findByAccessoryAndNullVendor(dealerAccessory.getDacId());			
		} else {
			dealerAccessoryPrices = dealerAccessoryPriceDAO.findByAccessoryAndVendor(
					dealerAccessory.getDacId(), vendorAccount.getExternalAccountPK().getCId(), 
					vendorAccount.getExternalAccountPK().getAccountType(), vendorAccount.getExternalAccountPK().getAccountCode());			
		}
		
		return dealerAccessoryPrices;
	}
	
	@Transactional
	public List<DealerAccessoryPrice> getDealerAccessoryPricesWithoutVendorQuote(DealerAccessory dealerAccessory, ExternalAccount vendorAccount){
		return dealerAccessoryPriceDAO.findByAccessoryAndVendorAndNullVendorQuote(dealerAccessory.getDacId(), vendorAccount.getExternalAccountPK().getCId(), vendorAccount.getExternalAccountPK().getAccountType(), vendorAccount.getExternalAccountPK().getAccountCode());			
	}
	
	@Transactional	
	public List<ModelPrice> saveOrUpdateModelPricing(List<ModelPrice> modelPrices){
		for(ModelPrice price : modelPrices){
			price = modelPriceDAO.saveAndFlush(price);
		}
		return modelPrices;
	}
	
	@Transactional
	public OptionPackHeader saveOrUpdateOptionPackPricing(OptionPackHeader optionPack){
		optionPack = optionPackHeaderDAO.saveAndFlush(optionPack);
		optionPack.getOptionPackCosts().size();
		optionPack.getOptionPackDetail().size();
		
		for(OptionalAccessory oac : optionPack.getOptionalAccessories()){
			oac.getOptionPrices().size();
		}
		
		return optionPack;
	}
	
	@Transactional 
	public OptionalAccessory saveOrUpdateFactoryAccessoryPricing(OptionalAccessory factoryAccessory){
		factoryAccessory = optionalAccessoryDAO.saveAndFlush(factoryAccessory);
		factoryAccessory.getOptionPrices().size();
		return factoryAccessory;
	}
	
	@Transactional
	public List<DealerAccessoryPrice> saveOrUpdateDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices){
		for(DealerAccessoryPrice price : dealerAccessoryPrices){
			price = dealerAccessoryPriceDAO.saveAndFlush(price);			
		}

		return dealerAccessoryPrices;
	}
	
	@Transactional
	public Model saveOrUpdateModel(Model model){
		Model savedModel;
		savedModel = modelDAO.saveAndFlush(model);		
		return savedModel;
	}
	
	@Transactional
	public DealerAccessory saveOrUpdateDealerAccessory(DealerAccessory dealerAccessory) throws MalBusinessException{		
		DealerAccessory retDealerAccessory;	
		
		if(MALUtilities.isEmpty(dealerAccessory.getDacId())){
			retDealerAccessory = saveNewDealerAccessoryAssignment(dealerAccessory);
		} else {
			retDealerAccessory = saveUpdatedDealerAccessoryAssignment(dealerAccessory);
		}
		
		return retDealerAccessory;
	}
	
	@Transactional
	private DealerAccessory saveNewDealerAccessoryAssignment(DealerAccessory dealerAccessory){
		DealerAccessory retDealerAccessory = null;
		DealerAccessoryCode dealerAccessoryCode;		
		
		//Assign the dealer accessory code to the new accessory code, they must be the same.
		if(MALUtilities.isEmpty(dealerAccessory.getDealerAccessoryCode().getAccessoryCode())){
			dealerAccessory.getDealerAccessoryCode().setAccessoryCode(dealerAccessory.getDealerAccessoryCode().getNewAccessoryCode());		
		}  else {			
			dealerAccessoryCode = dealerAccessoryCodeDAO.findById(dealerAccessory.getDealerAccessoryCode().getAccessoryCode()).orElse(null);
			dealerAccessoryCode.getDealerAccessories().add(dealerAccessory);
			dealerAccessory.setDealerAccessoryCode(dealerAccessoryCode);				
		}	
		
		retDealerAccessory = dealerAccessoryDAO.saveAndFlush(dealerAccessory);
		
		if(!(MALUtilities.isEmpty(retDealerAccessory.getDealerAccessoryPrices()) 
				|| retDealerAccessory.getDealerAccessoryPrices().isEmpty())){
			retDealerAccessory.getDealerAccessoryPrices().size();				
		}		
		
		return retDealerAccessory;
	}
	
	/**
	 * Detect whether changes were made to price. 
	 * If the new price does not have a corresponding record in the system, true is returned 
	 * @param originalPrice Original pricing information
	 * @param newPrice New pricing information
	 * @return boolean
	 */
	private boolean hasDealerAccessoryPriceChanged(DealerAccessoryPrice originalPrice, DealerAccessoryPrice newPrice){
		boolean hasChanged = false;
		String originalUpfitterAccountCode, newUpfitterAccountCode;

		//OTD-479: No original price means that the price is being added, i.e. it does not exist in the system. No need to continue
		if(MALUtilities.isEmpty(newPrice.getDplId()) 
				|| newPrice.getDplId() < 0) 
			return true;
				
		originalPrice = dealerAccessoryPriceDAO.findById(newPrice.getDplId()).orElse(null);
		
		if(!originalPrice.getDplId().equals(newPrice.getDplId()))
			return true;
		
	    originalUpfitterAccountCode = MALUtilities.isEmpty(originalPrice.getPayeeAccount()) ? "-1" : originalPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
	    newUpfitterAccountCode = MALUtilities.isEmpty(newPrice.getPayeeAccount()) ? "-1" : newPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();

	    if(!(originalUpfitterAccountCode.equals(newUpfitterAccountCode)
				&& originalPrice.getBasePrice().compareTo(newPrice.getBasePrice()) == 0
				&& decodeNullBigDecimal(originalPrice.getMsrp(), new BigDecimal(-1)).compareTo(decodeNullBigDecimal(newPrice.getMsrp(), new BigDecimal(-1))) == 0
				&& decodeNullLong(originalPrice.getLeadTime(), -1L).equals(decodeNullLong(newPrice.getLeadTime(), -1L))						
				&& MALUtilities.clearTimeFromDate(originalPrice.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(newPrice.getEffectiveDate())) == 0)){
	    	hasChanged = true;
	    }
	    
	    if(!hasChanged && !MALUtilities.isEmpty(originalPrice.getUpfitterQuote())){
	    	if(MALUtilities.isEmpty(newPrice.getUpfitterQuote())){
	    		hasChanged = true;
	    	} else {
	    		if(!originalPrice.getUpfitterQuote().equals(newPrice.getUpfitterQuote())){
	    			hasChanged = true;
	    		}
	    	}
	    }
	    		
		return hasChanged;
	}	
	
	/**
	 * Detects whether an upfitter's quote has been removed/disassociated from a price
	 * @param originalPrice Original pricing information
	 * @param newPrice New pricing information
	 * @return boolean 
	 */
	private boolean hasQuoteBeenRemoved(DealerAccessoryPrice originalPrice, DealerAccessoryPrice newPrice){
		boolean isRemoved = false;
		    if(!MALUtilities.isEmpty(originalPrice.getUpfitterQuote())){
		    	if(MALUtilities.isEmpty(newPrice.getUpfitterQuote())){
		    		isRemoved = true;
		    	}		    	
		    }
		return isRemoved;
	}
	
	@Transactional
	private DealerAccessory saveUpdatedDealerAccessoryAssignment(DealerAccessory dealerAccessory) throws MalBusinessException{	
		DealerAccessory dbDealerAccessory;
		DealerAccessory retDealerAccessory;
			
		//Price specific changes and validation
		dbDealerAccessory = dealerAccessoryDAO.findById(dealerAccessory.getDacId()).orElse(null);		
		for(DealerAccessoryPrice price : dealerAccessory.getDealerAccessoryPrices()){ 
			validateUniqueVendorEffectiveDate(dbDealerAccessory, price);				
		}
		
		//Trusting hibernate to determine changes and persist the data accordingly
		dealerAccessoryPriceDAO.saveAll(dealerAccessory.getDealerAccessoryPrices());
		dealerAccessoryPriceDAO.flush();			

		//Retrieving the dealer accessory from ORM to prevent issues with stale object
		retDealerAccessory = dealerAccessoryDAO.findById(dealerAccessory.getDacId()).orElse(null);
		retDealerAccessory.getDealerAccessoryPrices().size();	
	
		//Save changes that could have been made to the dealer accessory's code. 
		//The fields that can be changed are Description, Category Code, or Lead Time
		retDealerAccessory.getDealerAccessoryCode().setDescription(dealerAccessory.getDealerAccessoryCode().getDescription());
		retDealerAccessory.getDealerAccessoryCode().setLeadTime(dealerAccessory.getDealerAccessoryCode().getLeadTime());
		retDealerAccessory.getDealerAccessoryCode().setOptionAccessoryCategory(dealerAccessory.getDealerAccessoryCode().getOptionAccessoryCategory());
		retDealerAccessory = dealerAccessoryDAO.saveAndFlush(retDealerAccessory);
		
		return retDealerAccessory;	
	}
	
	public void validateUniqueVendorEffectiveDate(DealerAccessory dealerAccessory, DealerAccessoryPrice updatedUpfitterPrice) throws MalBusinessException{
		String upfitterAccountCode, updatedUpfitterAccountCode, updatedUpfitterAccountName;
		StringBuilder messageBuilder = new StringBuilder();
		
		if(MALUtilities.isEmpty(dealerAccessory.getDacId())) return;
		
		for(DealerAccessoryPrice upfitterPrice : dealerAccessory.getDealerAccessoryPrices()){
			if(MALUtilities.isEmpty(upfitterPrice.getPayeeAccount())){
				upfitterAccountCode = NO_VENDOR_ACCOUNT_CODE;
			} else {
				upfitterAccountCode = upfitterPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
			}
			
			if(MALUtilities.isEmpty(updatedUpfitterPrice.getPayeeAccount())){
				updatedUpfitterAccountCode = NO_VENDOR_ACCOUNT_CODE;
				updatedUpfitterAccountName = NO_VENDOR_ACCOUNT_NAME;
			} else {
				updatedUpfitterAccountCode = updatedUpfitterPrice.getPayeeAccount().getExternalAccountPK().getAccountCode();
				updatedUpfitterAccountName = updatedUpfitterPrice.getPayeeAccount().getAccountName();
			}			
			
			//Validation will fail when the accessory has a existing price with the same effective date and upfitter. 
			//However, there is a special case in which the validation should be skipped and that is when removing 
			//the quote from the price.
			if(upfitterPrice.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode().equals(updatedUpfitterPrice.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode())){
				if(!updatedUpfitterPrice.equals(upfitterPrice)) {					
					if(upfitterAccountCode.equals(updatedUpfitterAccountCode)){ 
						if( MALUtilities.clearTimeFromDate(upfitterPrice.getEffectiveDate()).compareTo(MALUtilities.clearTimeFromDate(updatedUpfitterPrice.getEffectiveDate())) == 0){
							if(hasDealerAccessoryPriceChanged(upfitterPrice, updatedUpfitterPrice)) {
								throw new MalBusinessException("unique.upfitter.effective.date.required", new String[]{updatedUpfitterAccountName, upfitterPrice.getDealerAccessory().getDealerAccessoryCode().getNewAccessoryCode()});
							}
						}
					}
				}
			}			
		}						
	}
	
	//TODO Move to MALUtilities or start a new Decode utility static class
	public Long decodeNullLong(Long value, Long result){
		Long retResult;
		
		if(MALUtilities.isEmpty(value)){
			retResult = result;
		} else {
			retResult = value;
		}
		
		return retResult;
	}
	
	//TODO Move to MALUtilities or start a new Decode utility static class
	public BigDecimal decodeNullBigDecimal(BigDecimal value, BigDecimal result){
		BigDecimal retResult;
		
		if(MALUtilities.isEmpty(value)){
			retResult = result;
		} else {
			retResult = value;
		}
		
		return retResult;
	}	
	
	//TODO Move to MALUtilities or start a new Decode utility static class
	public String decodeNullString(String value, String result){
		String retResult;
		
		if(MALUtilities.isEmpty(value)){
			retResult = result;
		} else {
			retResult = value;
		}
		
		return retResult;
	}
	
	//TODO Move to MALUtilities or start a new Decode utility static class
	public Object decodeNullObject(Object value, Object result){
		Object retResult;
		
		if(MALUtilities.isEmpty(value)){
			retResult = result;
		} else {
			retResult = value;
		}
		
		return retResult;
	}	
	
	@Transactional
	public Model getModelWithDealerAccessories(Long modelId) {
		Model model = modelDAO.findById(modelId).orElse(null);
		
		Collections.sort(model.getDealerAccessories(), new Comparator<DealerAccessory>() { 
			public int compare(DealerAccessory dac1, DealerAccessory dac2) { 
				return dac1.getDealerAccessoryCode().getDescription().compareTo(dac2.getDealerAccessoryCode().getDescription()); 
			}
		});	
		
		return model;
	}
	
	@Transactional
	public List<DealerAccessory> getModelDealerAccessories(Long modelId, String criteria, PageRequest page) {
		List<DealerAccessory> dealerAccessories = new ArrayList<DealerAccessory>();
		
		dealerAccessories = dealerAccessoryDAO.findByModelAndCodeOrDescription(modelId, DataUtilities.appendWildCardToRight(criteria), page);
		
		return dealerAccessories;
	}
	
	@Transactional
	public List<DealerAccessory> getEffectiveModelDealerAccessories(Long modelId, String criteria, PageRequest page) {
		List<DealerAccessory> dealerAccessories = new ArrayList<DealerAccessory>();
		
		dealerAccessories = dealerAccessoryDAO.findEffectiveByModelAndCodeOrDescription(modelId, DataUtilities.appendWildCardToRight(criteria), page);
		
		return dealerAccessories;
	}
	
	@Transactional
	public DealerAccessory getDealerAccessoryWithPrices(Long dacId) throws MalBusinessException {
		DealerAccessory dealerAccessory = null;
		
		if(!MALUtilities.isEmpty(dacId)) {
			dealerAccessory = dealerAccessoryDAO.findById(dacId).orElse(null);
			Hibernate.initialize(dealerAccessory.getDealerAccessoryPrices());
		} else {
			throw new MalBusinessException("service.validation", new String[]{ "Dealer Accessory id must not be null."});
		}
		
		return dealerAccessory;
	}
	
	
	public DealerAccessoryPrice getDealerAccessoryPrice(Long dplId) {
		return dealerAccessoryPriceDAO.findById(dplId).orElse(null);
	}
	
	@Transactional
	public DealerAccessory getModelDealerAccessory(Model model, DealerAccessoryCode dealerAccessoryCode){
		DealerAccessory dealerAccessory;		  
		dealerAccessory = dealerAccessoryDAO.findByModelAndAccessoryCode(model.getModelId(), dealerAccessoryCode.getAccessoryCode());
		if(!MALUtilities.isEmpty(dealerAccessory)) {
			dealerAccessory.getDealerAccessoryPrices().size();
		}
		return dealerAccessory;
	}
	
	public List<DealerAccessoryPrice> getUpfitterQuoteByDacIdAndUpfitter(Long dacId, String accountCode) {
		List<DealerAccessoryPrice> upfitterWithQuotes = new ArrayList<DealerAccessoryPrice>();
		upfitterWithQuotes = dealerAccessoryPriceDAO.findByAccessoryAndVendor(dacId, 1L, "S", accountCode);
		return upfitterWithQuotes;
	}
	
	public Model getModelById(Long mdlId) {
		return modelDAO.findById(mdlId).orElse(null);
	}
	
	public Model getModelByStandardEDINo(String standardEDINo) {
		return modelDAO.findByStandardEDINo(standardEDINo);
	}	
	
	public ModelType getModelTypeById(Long mtpId) {
		return modelTypeDAO.findById(mtpId).orElse(null);
	}
	
	public MakeModelRange getMakeModelRangeById(Long mrgId) {
		return makeModelRangeDAO.findById(mrgId).orElse(null);
	}


	@Override
	public boolean isDealerAccessory(Model model, String code) {
		return isDealerAccessoryCore(model, code);
	}


	@Override
	public boolean isOptionPack(Model model, String code) {
		return isOptionPackCore(model, code);
	}


	@Override
	public boolean isOptionalAccessory(Model model, String code) {
		return isOptionalAccessoryCore(model, code);
	}


	@Override
	public boolean isStandardAccessory(Model model, String code) {
		return isStandardAccessoryCore(model, code);
	}
	
	@Override
	public boolean isInteriorColor(Model model, String code) {		
		return isInteriorColorCore(model, code);		
	}

	@Override
	public boolean isExteriorColor(Model model, String code) {		
		return isExteriorColorCore(model, code);
	}	

	@Override
	public List<String> listInvalidAccessories(VehicleConfigurationType vehicleConfiguration, Model model) {
		List<String> missingAccessories = new ArrayList<>();
				
		for(VehicleOptionType  option : vehicleConfiguration.getVehicleOptions().getOption()) {
			String optCodeLevel = option.getOptionCode() + "-" + option.getOptionLevel();
			
			if(option.getState().name().equals("NOT_AVAIL")) {
				missingAccessories.add(option.getCode() + ": " + option.getDescription() + " does not exist or has invalid content.");				
			} else if(option.getState().name().equals("CANTHAVE")) {
				missingAccessories.add(optCodeLevel + ": " + option.getDescription() + " cannot be selected in the current Configuration.");				
			} else if(option.getState().name().equals("ADDED")) {
				missingAccessories.add(optCodeLevel + ": " + option.getDescription() + " is now selected or has new content added.");				
			} else {
				if( !(isDealerAccessoryCore(model, optCodeLevel)
						|| isOptionPackCore(model, optCodeLevel)
						|| isOptionalAccessoryCore(model, optCodeLevel)
						|| isStandardAccessoryCore(model, optCodeLevel)
						|| isInteriorColor(model, optCodeLevel)  //TODO May need to check option category for colors
						|| isExteriorColor(model, optCodeLevel)  //TODO May need to check option category for colors
						|| optCodeLevel.equals("PNTTBL-01")) ) {
					missingAccessories.add(optCodeLevel + ": " + option.getDescription() + " cannot be found.");
				}			
			}
		}		
		
		return missingAccessories;
	}
	
	private boolean isDealerAccessoryCore(Model model, String code) {
		boolean isDealerAccessory = false;
		
		List<DealerAccessory> dlrAccessories = dealerAccessoryDAO.findByModelAndNewAccessoryCode(model.getModelId(), code);
		
		if(!dlrAccessories.isEmpty()) {
			if(dlrAccessories.size() == 1) {
				isDealerAccessory = true;				
			} else {
				throw new MalException("generic.error", new String[]{"Too many dealer accessory records returned for option code " + code + " on model id " + model.getModelId()});							
			}
		}

		return isDealerAccessory;
	}
	
	private boolean isOptionPackCore(Model model, String code) {
		boolean isOptionPack = false;
		
		OptionPackHeader oph = optionPackHeaderDAO.findByModelAndCode(model.getModelId(), code);
		if(!MALUtilities.isEmpty(oph)) {
			isOptionPack = true;
		}
				
		return isOptionPack;
	}

	private boolean isOptionalAccessoryCore(Model model, String code) {
		boolean isOptionalAccessory = false;
		
		List<OptionalAccessory> optAccessories = optionalAccessoryDAO.findByModelAndNewAccessoryCode(model.getModelId(), code);
		if(!optAccessories.isEmpty()) {
			if(optAccessories.size() == 1) {
				isOptionalAccessory = true;
			} else {
				throw new MalException("generic.error", new String[]{"Too many option accessory records returned for option code " + code + " on model id " + model.getModelId()});				
			}
		}
		
		return isOptionalAccessory;
	}

	private boolean isStandardAccessoryCore(Model model, String code) {
		boolean isStandardAccessory = false;
		
		List<StandardAccessory> stdAccessories = standardAccessoryDAO.findByModelAndNewAccessoryCode(model.getModelId(), code);
		if(!stdAccessories.isEmpty()) {
			if(stdAccessories.size() == 1) {
				isStandardAccessory = true;
			} else {
				throw new MalException("generic.error", new String[]{"Too many standard accessory records returned for option code " + code + " on model id " + model.getModelId()});				
			}
		}		
		
		return isStandardAccessory;
	}


	private boolean isInteriorColorCore(Model model, String code) {
		boolean isModelInteriorColor = false;	
		
		if(!MALUtilities.isEmpty(modelColourTrimDAO.findByModelAndTrimCode(model.getModelId(), code))) {
			isModelInteriorColor = true;
		}
		
		return isModelInteriorColor;		
	}


	
	private boolean isExteriorColorCore(Model model, String code) {
		boolean isModelExteriorColor = false;	
		
		if(!MALUtilities.isEmpty(modelColourDAO.findByModelAndColourCode(model.getModelId(), code))) {
			isModelExteriorColor = true;
		}
		
		return isModelExteriorColor;
	}


	@Override
	public Long getModelIdByQmdId(Long qmdId) {
		return quotationModelDAO.findModelIdByQmdId(qmdId);
	}	
}
