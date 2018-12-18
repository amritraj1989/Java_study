package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.UpfitterQuote;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert the UpfitterQuote entity
 */
@Component
public class UpfitterQuoteConverter implements Converter{
	@Resource UpfitterService upfitterService;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<UpfitterQuote> upfitterQuotes = null;
		UpfitterQuote upfitterQuote = null;
		
		if(MALUtilities.isEmpty(value)) return null;
		
		if(component instanceof AutoComplete){
			if(value.matches("\\d+")){
				upfitterQuote = upfitterService.getUpfitterQuote(Long.valueOf(value));
			}
		} else if(component instanceof SelectOneMenu){		
			for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
				if(uiComponent instanceof UISelectItems ) {
					UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
					upfitterQuotes = (ArrayList<UpfitterQuote>)uiSelectItems.getValue();
				}
			}
        
			for(UpfitterQuote quote : upfitterQuotes){
				if(quote.getUfqId().equals(Long.valueOf(value))){
					upfitterQuote = quote;
				}
			} 
        
		} else {
			upfitterQuote = null;
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
