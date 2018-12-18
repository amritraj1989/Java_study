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
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.util.MALUtilities;


@SuppressWarnings("unchecked")
@Component
public class QuotationProfileConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<QuotationProfile> quotationProfiles = new ArrayList<QuotationProfile>();
		QuotationProfile selectedQuotationProfile = null;
		
		if(!MALUtilities.isEmptyString(value)){ 		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		quotationProfiles = (ArrayList<QuotationProfile>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(QuotationProfile quotationProfile : quotationProfiles){
	            	if(quotationProfile.getQprId() == Long.valueOf(value)){
	            		selectedQuotationProfile = quotationProfile;
	            		break;
	            	}
	            }	
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert PlateTypeCode (Code): " + value + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
		
		return selectedQuotationProfile;		
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String retVal = null;
		
		if(!MALUtilities.isEmpty(value)){		
        	retVal = Long.toString(((QuotationProfile)value).getQprId());
        }  
        
        return retVal;
	}

}
