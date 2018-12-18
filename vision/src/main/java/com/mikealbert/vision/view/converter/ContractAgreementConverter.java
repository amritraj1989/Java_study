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

import com.mikealbert.data.entity.ContractAgreement;

@FacesConverter("ContractAgreementConverter")
public class ContractAgreementConverter implements Converter{

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<ContractAgreement> contractAgreementList = new ArrayList<ContractAgreement>();
		ContractAgreement selectedContractAgreement = null;
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		contractAgreementList = (ArrayList<ContractAgreement>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(ContractAgreement contractAgreement : contractAgreementList){
	            	if(contractAgreement.getAgreementCode().equals(value)){
	            		selectedContractAgreement = contractAgreement;
	            	}
	            }
	
				return selectedContractAgreement;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert FinanceParameterCategory: " + value + ex.getMessage());
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
        	return String.valueOf(((ContractAgreement)value).getAgreementCode());
        }  		
	}

}

