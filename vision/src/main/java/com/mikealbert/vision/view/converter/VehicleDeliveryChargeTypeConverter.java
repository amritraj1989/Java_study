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

import com.mikealbert.data.entity.VehicleDeliveryChargeType;
import com.mikealbert.util.MALUtilities;

@SuppressWarnings("unchecked")
@Component
public class VehicleDeliveryChargeTypeConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<VehicleDeliveryChargeType> vehicleDeliveryChargeTypes = new ArrayList<VehicleDeliveryChargeType>();
		VehicleDeliveryChargeType selectedVehicleDeliveryChargeType = null;
		
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		vehicleDeliveryChargeTypes = (ArrayList<VehicleDeliveryChargeType>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(VehicleDeliveryChargeType vehicleDeliveryChargeType : vehicleDeliveryChargeTypes){
	            	if(vehicleDeliveryChargeType.getName().equals(value)){
	            		selectedVehicleDeliveryChargeType = vehicleDeliveryChargeType;
	            		break;
	            	}
	            }
	
				return selectedVehicleDeliveryChargeType;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert VehicleDeliveryChargeType (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {  		
        	return ((VehicleDeliveryChargeType)value).getName();
        }  		
	}

}
