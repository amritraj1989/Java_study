package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert OptionAccessoryCategory 
 * */
@Component
public class OptionAccessoryCategoryConverter implements Converter{
	@Resource LookupCacheService lookupCacheService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		OptionAccessoryCategory optionAccessoryCategory = null;
		
		if(!MALUtilities.isEmptyString(value)){
			for(OptionAccessoryCategory category : lookupCacheService.getOptionAccessoryCategories()){
				if(category.getCode().equals(value)){
					optionAccessoryCategory = category;
					break;
				}
			}
		}
		
		return optionAccessoryCategory;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {   
		String retVal = null;
		
		if(!MALUtilities.isEmpty(value)){
			retVal = ((OptionAccessoryCategory) value).getCode();
		} 
		
        return retVal;
	}

}
