package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.QuoteRequestType;
import com.mikealbert.util.MALUtilities;

@SuppressWarnings("unchecked")
@Component
public class QuoteRequestTypeConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<QuoteRequestType> quoteRequestTypes = new ArrayList<QuoteRequestType>();
		QuoteRequestType selectedQuoteRequestType = null;
		
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		quoteRequestTypes = (ArrayList<QuoteRequestType>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(QuoteRequestType quoteRequestType : quoteRequestTypes){
	            	if(quoteRequestType.getQrtId().equals(Long.valueOf(value))){
	            		selectedQuoteRequestType = quoteRequestType;
	            		break;
	            	}
	            }
	
				return selectedQuoteRequestType;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert QuoteRequestType (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {  		
        	return ((QuoteRequestType)value).getQrtId().toString();
        }  		
	}

}
