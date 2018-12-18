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

import com.mikealbert.data.entity.DelayReason;
import com.mikealbert.util.MALUtilities;

@FacesConverter("DelayReasonConverter")
public class DelayReasonConverter implements Converter{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<DelayReason> delayReasons = new ArrayList<DelayReason>();
		DelayReason selectedDelayReason = null;
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		delayReasons = (ArrayList<DelayReason>)uiSelectItems.getValue();
            	}
            }
            
            for(DelayReason delayReason : delayReasons){
            	if(delayReason.getDelayReasonCode().equals(value)){
            		selectedDelayReason = delayReason;
            	}
            }

		return selectedDelayReason;
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage("Conversion error occurred. ", "Unable to convert DelayReason: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (!MALUtilities.isEmpty(value)) {
			return String.valueOf(((DelayReason)value).getDelayReasonCode());
		} else {
			return "";
		}
	}
	
}
