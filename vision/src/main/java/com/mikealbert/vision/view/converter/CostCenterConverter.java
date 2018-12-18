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

import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.util.MALUtilities;

@FacesConverter(forClass=CostCentreCode.class)
public class CostCenterConverter implements Converter {

	/**
	 * Converts the Cost Center String value into the Cost Center object.
	 * 
	 * NOTE: Cost Center will be selected from a menu, therefore, only input from  
	 * the UISelect component is supported. Otherwise, validation will fail.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {	
		List<CostCentreCode> costCenters = new ArrayList<CostCentreCode>();
		CostCentreCode selectedCostCenter = null;
		
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		costCenters = (ArrayList<CostCentreCode>)uiSelectItems.getValue();
            	}
            }
            
            for(CostCentreCode ccc : costCenters){
            	if(ccc.getCostCentreCodesPK().getCostCentreCode().equals(value)){
            		selectedCostCenter = ccc;
            		break;
            	}
            }

			return selectedCostCenter;
			
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert Cost Center: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {			
		return ((CostCentreCode)value).getCostCentreCodesPK().getCostCentreCode();
	}

}
