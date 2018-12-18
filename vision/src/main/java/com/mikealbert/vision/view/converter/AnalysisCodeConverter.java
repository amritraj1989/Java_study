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

import com.mikealbert.data.entity.AnalysisCode;

@FacesConverter("AnalysisCodeConverter")
public class AnalysisCodeConverter implements Converter{

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<AnalysisCode> analysisCodes = new ArrayList<AnalysisCode>();
		AnalysisCode selectedAnalysisCode = null;
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		analysisCodes = (ArrayList<AnalysisCode>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(AnalysisCode analysisCode : analysisCodes){
	            	if(analysisCode.getId().getAnalysisCode().equals(value)){
	            		selectedAnalysisCode = analysisCode;
	            		break;
	            	}
	            }
	
				return selectedAnalysisCode;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert AnalysisCode: " + value + ex.getMessage());
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
        	return String.valueOf(((AnalysisCode)value).getId().getAnalysisCode());
        }  		
	}

}


