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

import com.mikealbert.data.entity.RejectReason;
import com.mikealbert.util.MALUtilities;

@FacesConverter("RejectReasonConverter")
public class RejectReasonConverter implements Converter{
	
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<RejectReason> RejectReasons = new ArrayList<RejectReason>();
		RejectReason selectedRejectReason = null;
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		RejectReasons = (ArrayList<RejectReason>)uiSelectItems.getValue();
            	}
            }
            
            for(RejectReason RejectReason : RejectReasons){
            	if(RejectReason.getRejectReasonCode().equals(value)){
            		selectedRejectReason = RejectReason;
            	}
            }

		return selectedRejectReason;
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage("Conversion error occurred. ", "Unable to convert Reject Reason: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (!MALUtilities.isEmpty(value)) {
			return String.valueOf(((RejectReason)value).getRejectReasonCode());
		} else {
			return "";
		}
	}
	
}
