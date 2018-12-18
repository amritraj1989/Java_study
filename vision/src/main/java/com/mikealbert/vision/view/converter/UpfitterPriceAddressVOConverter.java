package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.data.entity.ExtAccAddress;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.service.ModelService;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.vo.UpfitterPriceAddressVO;

/** This converter is used to convert the VendorPriceAddressVO
 */
@Component
public class UpfitterPriceAddressVOConverter implements Converter{
	@Resource ModelService modelService;
	@Resource UpfitterService upfitterService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		UpfitterPriceAddressVO upfitterPriceAddress = null; 
		DealerAccessoryPrice dealerAccessoryPrice = null;		
		
        if (!MALUtilities.isEmptyString(value)) {           	
        	upfitterPriceAddress = new UpfitterPriceAddressVO();
        	
        	dealerAccessoryPrice = modelService.getDealerAccessoryPrice(Long.parseLong(value));
        	
        	upfitterPriceAddress.setDealerAccessoryId(dealerAccessoryPrice.getDealerAccessory().getDacId());
        	upfitterPriceAddress.setDealerPriceListId(dealerAccessoryPrice.getDplId());
        	upfitterPriceAddress.setBasePrice(dealerAccessoryPrice.getBasePrice());
        	upfitterPriceAddress.setVatAmount(dealerAccessoryPrice.getVatAmount());
        	if(!MALUtilities.isEmpty(dealerAccessoryPrice.getPayeeAccount())) {
        		ExternalAccountPK externalAccountPK = dealerAccessoryPrice.getPayeeAccount().getExternalAccountPK();
        		
        		upfitterPriceAddress.setPayeeCorporateId(externalAccountPK.getCId());
            	upfitterPriceAddress.setPayeeAccountType(externalAccountPK.getAccountType());
            	upfitterPriceAddress.setPayeeAccountCode(externalAccountPK.getAccountCode());
            	upfitterPriceAddress.setPayeeAccountName(dealerAccessoryPrice.getPayeeAccount().getAccountName());
            	
            	ExtAccAddress upfitterAddress = upfitterService.getUpfitterDefaultPostAddress(externalAccountPK.getAccountCode(), externalAccountPK.getCId());
            	upfitterPriceAddress.setDefaultFormattedAddresss(upfitterService.getFormattedAddress(upfitterAddress, ","));
            	
            	if(!MALUtilities.isEmpty(dealerAccessoryPrice.getUpfitterQuote())) {
            		upfitterPriceAddress.setQuoteNumber(dealerAccessoryPrice.getUpfitterQuote().getQuoteNumber());
				} 
        	} else {
        		upfitterPriceAddress.setPayeeAccountCode("-1");
        		upfitterPriceAddress.setPayeeAccountName("");
        		upfitterPriceAddress.setDefaultFormattedAddresss("");
        	}
        } 
  
        return upfitterPriceAddress; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String retVal;
		
        if (MALUtilities.isEmpty(value)) {   
            retVal = "";   
        } else {   
        	if(value instanceof String){
        		retVal = (String) value;
        	} else {
                retVal = String.valueOf(((UpfitterPriceAddressVO) value).getDealerPriceListId());
        	}   
        }  
        
        return retVal;
	}

}
