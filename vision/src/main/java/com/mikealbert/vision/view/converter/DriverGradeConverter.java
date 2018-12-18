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

import com.mikealbert.data.entity.DriverGrade;

@FacesConverter(forClass=DriverGrade.class)
public class DriverGradeConverter implements Converter {

	/**
	 * Converts the Drive Grade Code String value into the DriverGrade object.
	 * 
	 * NOTE: DriverGrade will be selected from a menu, therefore, only input from  
	 * the UISelect component is supported. Otherwise, validation will fail.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {	
		List<DriverGrade> driverGrades = new ArrayList<DriverGrade>();
		DriverGrade selectedDriverGrade = null;
		
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		driverGrades = (ArrayList<DriverGrade>)uiSelectItems.getValue();
            	}
            }
            
            for(DriverGrade dg : driverGrades){
            	if(dg.getGradeCode().equals(value))
            		selectedDriverGrade = dg;
            }

			return selectedDriverGrade;
			
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert Driver Grade: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {			
		return ((DriverGrade)value).getGradeCode();
	}

}
