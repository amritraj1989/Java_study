package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.DealerAccessoryPrice;
import com.mikealbert.service.ModelService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert the DealerAccessoryPrice
 */
@Component
public class DealerAccessoryPriceConverter implements Converter{
	@Resource ModelService modelService;	
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		DealerAccessoryPrice dealerAccessoryPrice = null;		
		
        if (!MALUtilities.isEmptyString(value)) {           	
        	dealerAccessoryPrice = modelService.getDealerAccessoryPrice(Long.parseLong(value));
        } 
  
        return dealerAccessoryPrice; 
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
                retVal = String.valueOf(((DealerAccessoryPrice) value).getDplId());        		
        	}   
        }  
        
        return retVal;
	}

}
