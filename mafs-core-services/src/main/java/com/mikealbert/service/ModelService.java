package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.data.entity.DealerAccessoryCode;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.MakeModelRange;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.entity.ModelPrice;
import com.mikealbert.data.entity.ModelType;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.OptionPackHeader;
import com.mikealbert.data.entity.OptionalAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationType;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.ModelServiceImpl} 
 *
 * @see com.mikealbert.data.entity.Model
 * @see com.mikealbert.vision.service.ModelServiceImpl
 **/
public interface ModelService {	
	public static String NO_VENDOR_ACCOUNT_CODE = "-1";
	public static String NO_VENDOR_ACCOUNT_NAME = "NO VENDOR";
	
	public Model getModel(Long mdlId);	
	
	public OptionalAccessory getFactoryAccessory(Long oacId);
	
	public OptionPackHeader getOptionPack(Long ophId);
	
	public List<DealerAccessoryCode> getDealerAccessoryCodes(String criteria, PageRequest page);
	public DealerAccessoryCode getDealerAccessoryCode(String accessoryCodeId);
	public boolean isCodeInSystem(String code);	 
	
	public List<ModelPrice> getModelPrices(Long mdlId);
	
	public DealerAccessory getDealerAccessory(Long dacId);
	
	public List<DealerAccessoryPrice> getDealerAccessoryPriceList(DealerAccessory dealerAccessory, ExternalAccount vendorAccount);

	public OptionPackHeader saveOrUpdateOptionPackPricing(OptionPackHeader optionPack);
	
	public OptionalAccessory saveOrUpdateFactoryAccessoryPricing(OptionalAccessory factoryAccessory);
	
	public List<ModelPrice> saveOrUpdateModelPricing(List<ModelPrice> modelPrices);		
	
	public List<DealerAccessoryPrice> saveOrUpdateDealerAccessoryPrices(List<DealerAccessoryPrice> dealerAccessoryPrices);
	
	public Model saveOrUpdateModel(Model model);
	
	public DealerAccessory saveOrUpdateDealerAccessory(DealerAccessory dealerAccessory) throws MalBusinessException;	
	
	public Long decodeNullLong(Long value, Long result);
	
	public BigDecimal decodeNullBigDecimal(BigDecimal value, BigDecimal result);
	
	public String decodeNullString(String value, String result);	
	
	public Object decodeNullObject(Object value, Object result);
	
	public Model getModelWithDealerAccessories(Long modelId);
	
	public List<DealerAccessory> getModelDealerAccessories(Long modelId, String criteria, PageRequest page);
	public List<DealerAccessory> getEffectiveModelDealerAccessories(Long modelId, String criteria, PageRequest page);
 	public List<DealerAccessory> getDealerAccessories(String criteria, PageRequest page);	
	
	public DealerAccessory getDealerAccessoryWithPrices(Long dacId) throws MalBusinessException;
	
	public DealerAccessoryPrice getDealerAccessoryPrice(Long dplId);
	
	public DealerAccessory getModelDealerAccessory(Model model, DealerAccessoryCode dealerAccessoryCode);
	
	public List<DealerAccessoryPrice> getUpfitterQuoteByDacIdAndUpfitter(Long dacId, String accountCode);
	
	public void validateUniqueVendorEffectiveDate(DealerAccessory dealerAccessory, DealerAccessoryPrice updatedUpfitterPrice) throws MalBusinessException;
	
	public List<DealerAccessoryPrice> getDealerAccessoryPricesWithoutVendorQuote(DealerAccessory dealerAccessory, ExternalAccount vendorAccount);
	
	public Model getModelById(Long mdlId);
	public Model getModelByStandardEDINo(String standardEDINo);	
	public ModelType getModelTypeById(Long mtpId);
	public MakeModelRange getMakeModelRangeById(Long mrgId);	
	
	public List<OptionAccessoryCategory> getMafsOptionAccessoryCategories();
	
	public List<DealerAccessoryCode> getDealerAccessoryCodesByCodeOrDescription(String criteria, PageRequest page);
	
	public boolean isDealerAccessory(Model model, String code);
	
	public boolean isOptionPack(Model model, String code);
	
	public boolean isOptionalAccessory(Model model, String code);
	
	public boolean isStandardAccessory(Model model, String code);
	
	public boolean isInteriorColor(Model model, String code);
	
	public boolean isExteriorColor(Model model, String code);
	
	public List<String> listInvalidAccessories(VehicleConfigurationType vehicleConfiguration, Model model);
	
	public Long getModelIdByQmdId(Long qmdId);
}

