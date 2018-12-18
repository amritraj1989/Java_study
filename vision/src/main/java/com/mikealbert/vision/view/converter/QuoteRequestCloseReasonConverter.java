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
import com.mikealbert.data.entity.QuoteRequestCloseReason;
import com.mikealbert.util.MALUtilities;

@SuppressWarnings("unchecked")
@Component
public class QuoteRequestCloseReasonConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<QuoteRequestCloseReason> QuoteRequestCloseReasons = new ArrayList<QuoteRequestCloseReason>();
		QuoteRequestCloseReason selectedQuoteRequestCloseReason = null;
		
        if (MALUtilities.isEmpty(value)) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		QuoteRequestCloseReasons = (ArrayList<QuoteRequestCloseReason>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(QuoteRequestCloseReason quoteRequestCloseReason : QuoteRequestCloseReasons){
	            	if(quoteRequestCloseReason.getQrcrId().equals(Long.valueOf(value))){
	            		selectedQuoteRequestCloseReason = quoteRequestCloseReason;
	            		break;
	            	}
	            }
	
				return selectedQuoteRequestCloseReason;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert QuoteRequestCloseReason (qrcrId): " + value + ex.getMessage());
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
        	return String.valueOf(((QuoteRequestCloseReason)value).getQrcrId());
        }  		
	}

}
