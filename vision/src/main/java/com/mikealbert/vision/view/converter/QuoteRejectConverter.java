package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.selectonemenu.SelectOneMenu;

import com.mikealbert.data.entity.QuoteRejectCode;
import com.mikealbert.util.MALUtilities;

@FacesConverter("QuoteRejectConverter")
public class QuoteRejectConverter implements Converter{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<QuoteRejectCode> QuoteRejectCodes = new ArrayList<QuoteRejectCode>();
		QuoteRejectCode selectedQuoteRejectCode = null;
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		QuoteRejectCodes = (ArrayList<QuoteRejectCode>)uiSelectItems.getValue();
            	}
            }
            
            for(QuoteRejectCode QuoteRejectCode : QuoteRejectCodes){
            	if(QuoteRejectCode.getRejectCode().equals(value)){
            		selectedQuoteRejectCode = QuoteRejectCode;
            	}
            }

		return selectedQuoteRejectCode;
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage("Conversion error occurred. ", "Unable to convert Quote Reject Reason: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (!MALUtilities.isEmpty(value)) {
			return String.valueOf(((QuoteRejectCode)value).getRejectCode());
		} else {
			return "";
		}
	}
	
}
