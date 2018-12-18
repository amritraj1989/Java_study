package com.mikealbert.vision.view.converter;

import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.service.LookupCacheService;

@FacesConverter("AddressTypeCodeConverter")
public class AddressTypeCodeConverter implements Converter {

	@Resource LookupCacheService lookupCacheService;
	/**
	 * Converts the Address Type Code String value into the Address Type Code object.
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {	
		try{  
			List<AddressTypeCode> addressTypeCodes = lookupCacheService.getAddressTypeCodes();
			AddressTypeCode selectedAddressTypeCode = null;
			
			for(AddressTypeCode atc : addressTypeCodes){
            	if(atc.getAddressType().equals(value)){
            		selectedAddressTypeCode = atc;
            		break;
            	}
            }

			return selectedAddressTypeCode;
			
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert Cost Center: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		AddressTypeCode selectedAddressTypeCode = null;
		if(value instanceof String){
			selectedAddressTypeCode = (AddressTypeCode) this.getAsObject(context, component, (String) value);
		}else if(value instanceof AddressTypeCode){
			selectedAddressTypeCode = (AddressTypeCode) value;
		}
		
		return selectedAddressTypeCode.getAddressType();
	}

}
