package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.service.UpfitterQuoteService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert the UpfitterQuote entity for autocomplete component
 */
@Component
public class UpfitterQuoteACConverter implements Converter{
	@Resource UpfitterQuoteService upfitterQuoteService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		UpfitterQuote upfitterQuote = null;
		
		if(!MALUtilities.isEmpty(value))  {
			upfitterQuote = upfitterQuoteService.getUpfitterQuote(Long.valueOf(value));
		}
		
        return upfitterQuote; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		UpfitterQuote upfitterQuote;
		
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {   
        	upfitterQuote = (UpfitterQuote) value;        	
            return upfitterQuote.getUfqId().toString();   
        }   
	}

}
