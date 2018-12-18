package com.mikealbert.vision.view.converter;

import java.util.List;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.util.MALUtilities;

@Component
public class CustomerAccountConverter implements Converter{
	@Resource CustomerAccountService customerAccountService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		List<ExternalAccount> clients;
		ExternalAccount client = null;
		
        if (MALUtilities.isNotEmptyString(value)) {    
    		clients = customerAccountService.findAllCustomerAccountsByNameOrCode(value, CorporateEntity.fromCorpId(1L), new PageRequest(0, 1));
    		client = clients.get(0);        	
        } 
		
        return client; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {   
        	return String.valueOf(((ExternalAccount)value).getExternalAccountPK().getAccountCode());
        }   
	}

}



