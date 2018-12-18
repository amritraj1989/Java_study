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

import com.mikealbert.data.entity.QuoteRequestDepreciationMethod;
import com.mikealbert.util.MALUtilities;


@SuppressWarnings("unchecked")
@Component
public class DepreciationMethodConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<QuoteRequestDepreciationMethod> depreciationMethods = new ArrayList<QuoteRequestDepreciationMethod>();
		QuoteRequestDepreciationMethod selectedDepreciationMethod = null;
		
		if(!MALUtilities.isEmptyString(value)){ 		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		depreciationMethods = (ArrayList<QuoteRequestDepreciationMethod>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(QuoteRequestDepreciationMethod depreciationMethod : depreciationMethods){
	            	if(depreciationMethod.toString().equals(value)){
	            		selectedDepreciationMethod = depreciationMethod;
	            		break;
	            	}
	            }	
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert Depreciation Method (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
		
		return selectedDepreciationMethod;		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String retVal = null;
		
		if(!MALUtilities.isEmpty(value)){		
        	retVal = value.toString();
        }  
        
        return retVal;
	}

}
