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
import com.mikealbert.data.entity.QuoteRequestPaymentType;
import com.mikealbert.util.MALUtilities;


@SuppressWarnings("unchecked")
@Component
public class PaymentTypeConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<QuoteRequestPaymentType> paymentTypes = new ArrayList<QuoteRequestPaymentType>();
		QuoteRequestPaymentType selectedPaymentType = null;
		
		if(!MALUtilities.isEmptyString(value)){ 		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		paymentTypes = (ArrayList<QuoteRequestPaymentType>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(QuoteRequestPaymentType paymentType : paymentTypes){
	            	if(paymentType.toString().equals(value)){
	            		selectedPaymentType = paymentType;
	            		break;
	            	}
	            }	
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert Payment Type (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
		
		return selectedPaymentType;		
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
