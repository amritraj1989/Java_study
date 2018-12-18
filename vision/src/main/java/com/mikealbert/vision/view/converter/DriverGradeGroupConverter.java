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

import com.mikealbert.data.entity.DriverGradeGroupCode;
import com.mikealbert.util.MALUtilities;


@SuppressWarnings("unchecked")
@Component
public class DriverGradeGroupConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<DriverGradeGroupCode> gradeGroups = new ArrayList<DriverGradeGroupCode>();
		DriverGradeGroupCode selectedGradeGroup = null;
		
		if(!MALUtilities.isEmptyString(value)){ 		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		gradeGroups = (ArrayList<DriverGradeGroupCode>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(DriverGradeGroupCode gradeGroup : gradeGroups){
	            	if(gradeGroup.toString().equals(value)){
	            		selectedGradeGroup = gradeGroup;
	            		break;
	            	}
	            }	
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert Grade Group (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
		
		return selectedGradeGroup;		
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
