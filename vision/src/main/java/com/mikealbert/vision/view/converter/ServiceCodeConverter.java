package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.util.MALUtilities;
/** This converter is used to convert MAFS Maintenance Codes
 *  It is a little non-standard because of the following limitation:
 *  
 *  Until JSF 2.2, converters are no injection targets. Make the converter a 
 *  JSF or Spring managed bean instead.
 *  @see <a href="http://stackoverflow.com/questions/10229396/how-to-inject-spring-bean-into-jsf-converter/10229586#10229586">http://stackoverflow.com/questions/10229396/how-to-inject-spring-bean-into-jsf-converter/10229586#10229586</a>
 */
@Component
public class ServiceCodeConverter implements Converter{
	
	@Resource private MaintenanceCodeService maintanenceCodeService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String codeValue) {
        if (codeValue.trim().equals("")) {   
            return null;   
        } else {   
            try {   
        		if(codeValue.length() > codeValue.indexOf("#") + 1 && codeValue.indexOf("#") > 0){
    				String sml = codeValue.substring(codeValue.indexOf("#") + 1);
					
    				ServiceProviderMaintenanceCode retVal = maintanenceCodeService.getExactServiceProviderMaintenanceCode(Long.parseLong(sml));
    				
    				if(!MALUtilities.isEmpty(retVal)){
    					return retVal;
    				}else{
    					return null; 
    				}
        		}
            } catch(NumberFormatException exception) {   
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Service Code"));   
            }   
        }   
  
        return null; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {   
            return "";   
        } else {   
            return String.valueOf(((ServiceProviderMaintenanceCode) value).getServiceProvider().getServiceProviderId()) + "#" + String.valueOf(((ServiceProviderMaintenanceCode) value).getSmlId());   
        }   
	}

}
