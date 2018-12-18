package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItem;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ClientSystem;
import com.mikealbert.data.entity.OptionAccessoryCategory;
import com.mikealbert.data.entity.UpfitterProgress;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.UpfitProgressService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert UpfitterProgress 
 * */
@SuppressWarnings("unchecked")
@Component
public class UpfitterProgressConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<UpfitterProgress> upfitterProgressList = new ArrayList<UpfitterProgress>();		
		UpfitterProgress upfitterProgress = null;
		
		try {
			if(!MALUtilities.isEmptyString(value)){
				for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
					if(uiComponent instanceof UISelectItems ) {
						UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
						upfitterProgressList = (ArrayList<UpfitterProgress>)uiSelectItems.getValue();
					} 
				}	

				for(UpfitterProgress ufp : upfitterProgressList){
					if(ufp.getUfpId().equals(Long.valueOf(value))){
						upfitterProgress = ufp;
					}
				}            
			}
		
		} catch(Exception e) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert UpfitterProgress (Linked to): " + value);
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);   			
		}
		
		return upfitterProgress;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {   
		String retVal = null;
		
		if(!MALUtilities.isEmpty(value)){
			retVal = ((UpfitterProgress) value).getUfpId().toString();
		} 
		
        return retVal;
	}

}
