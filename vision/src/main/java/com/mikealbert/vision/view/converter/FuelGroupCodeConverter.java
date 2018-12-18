package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import com.mikealbert.data.entity.FuelGroupCode;
import com.mikealbert.service.FuelService;


@FacesConverter("FuelGroupCodeConverter")
public class FuelGroupCodeConverter implements Converter {

	@Resource FuelService fuelService;

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {	
  
			List<FuelGroupCode> fuelGroupCodes = new ArrayList<FuelGroupCode>();
			FuelGroupCode selected = null;
	        if (value.trim().equals("")) {   
	            return null;   
	        } else {  		
				try{  
		            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
		            	if(uiComponent instanceof UISelectItems ) {
		            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
		            		fuelGroupCodes = (ArrayList<FuelGroupCode>)uiSelectItems.getValue();
		            	}
		            }
		            
		            for(FuelGroupCode fuelGroupCode : fuelGroupCodes){
		            	if(fuelGroupCode.getFuelGroupCode().equals((value))){
		            		selected = fuelGroupCode;
		            		break;
		            	}
		            }
		
					return selected;
			
			
				} catch(Exception ex) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert Fuel Group Code: " + value + ex.getMessage());
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
        	return ((FuelGroupCode) value).getFuelGroupCode();
        }  		

	}

}
