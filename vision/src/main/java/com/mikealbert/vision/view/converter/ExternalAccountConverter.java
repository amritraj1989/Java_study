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

import com.mikealbert.data.entity.ExternalAccount;

@FacesConverter("ExternalAccountConverter")
public class ExternalAccountConverter implements Converter{

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<ExternalAccount> externalAccounts = new ArrayList<ExternalAccount>();
		ExternalAccount selectedExternalAccounts = null;
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		externalAccounts = (ArrayList<ExternalAccount>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(ExternalAccount externalAccount : externalAccounts){
	            	if(externalAccount.getExternalAccountPK().getAccountCode().equals(value)){
	            		selectedExternalAccounts = externalAccount;
	            	}
	            }
	
				return selectedExternalAccounts;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert ExternalAccounts: " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {   
            return "";   
        } else {  		
        	return String.valueOf(((ExternalAccount)value).getExternalAccountPK().getAccountCode());
        }  		
	}

}



