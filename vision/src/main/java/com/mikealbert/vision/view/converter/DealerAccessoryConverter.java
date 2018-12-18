package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.DealerAccessory;
import com.mikealbert.service.ModelService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert the DealerAccessory
 */
@Component
public class DealerAccessoryConverter implements Converter{
	@Resource ModelService modelService;	
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		DealerAccessory dealerAccessory = null;		
		
        if (!MALUtilities.isEmptyString(value)) {           	
        	dealerAccessory = modelService.getDealerAccessory(Long.parseLong(value));
        } 
  
        return dealerAccessory; 
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
                retVal = String.valueOf(((DealerAccessory) value).getDacId());        		
        	}   
        }  
        
        return retVal;
	}

}
