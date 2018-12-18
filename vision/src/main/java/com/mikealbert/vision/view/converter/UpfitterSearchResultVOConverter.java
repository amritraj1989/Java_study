package com.mikealbert.vision.view.converter;

import java.util.List;

import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.data.enumeration.AccountStatusEnum;
import com.mikealbert.data.vo.UpfitterSearchCriteriaVO;
import com.mikealbert.data.vo.UpfitterSearchResultVO;
import com.mikealbert.service.UpfitterService;
import com.mikealbert.util.MALUtilities;

/** This converter is used to convert the Upfitter Search Result VO
 */
@Component
public class UpfitterSearchResultVOConverter implements Converter{
	@Resource UpfitterService upfitterService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		UpfitterSearchCriteriaVO searchCriteria = new UpfitterSearchCriteriaVO();		
		List<UpfitterSearchResultVO> upfitters;		
		String[] identifiers = value.split("\t");
    	UpfitterSearchResultVO upfitterSearchResultVO = null;		
		
        if (MALUtilities.isNotEmptyString(value)) {               
    		searchCriteria.setTerm(identifiers[0]);	
    		searchCriteria.setAccountStatus(AccountStatusEnum.OPEN);
    		upfitters = upfitterService.searchUpfitters(searchCriteria, new PageRequest(0,1), null);  
    		upfitterSearchResultVO = upfitters.get(0);        	
        } 

		
        return upfitterSearchResultVO; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		UpfitterSearchResultVO upfitterSearchResultVO;
		StringBuilder identifiers= new StringBuilder();
		
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {   
        	upfitterSearchResultVO = (UpfitterSearchResultVO) value; 
        	identifiers.append(upfitterSearchResultVO.getPayeeAccountCode());
        	identifiers.append("\t");
        	identifiers.append(upfitterSearchResultVO.getPayeeAccountName());        	
            return identifiers.toString();   
        }   
	}

}
