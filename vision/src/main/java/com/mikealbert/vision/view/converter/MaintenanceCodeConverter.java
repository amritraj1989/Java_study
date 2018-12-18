package com.mikealbert.vision.view.converter;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import org.springframework.stereotype.Component;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.service.LookupCacheService;
import com.mikealbert.service.MaintenanceCodeService;

/** This converter is used to convert MAFS Maintenance Codes
 *  It is a little non-standard because of the following limitation:
 *  
 *  Until JSF 2.2, converters are no injection targets. Make the converter a 
 *  JSF or Spring managed bean instead.
 *  @see <a href="http://stackoverflow.com/questions/10229396/how-to-inject-spring-bean-into-jsf-converter/10229586#10229586">http://stackoverflow.com/questions/10229396/how-to-inject-spring-bean-into-jsf-converter/10229586#10229586</a>
 */
@Component
public class MaintenanceCodeConverter implements Converter{
	
	@Resource private MaintenanceCodeService maintanenceCodeService;
	
	MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String code) {
        if (code.trim().equals("")) {   
            return null;   
        } else {   
            try {   
            	logger.debug("Converted maint code " + code);
            	return maintanenceCodeService.getExactMaintenanceCodeByNameOrCode(code);
            } catch(NumberFormatException exception) {   
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid Maintenance Code Id"));   
            }   
        }   
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.equals("")) {   
            return "";   
        } else {   
        	return String.valueOf(((MaintenanceCode) value).getCode());   
        }   
	}
	

}
