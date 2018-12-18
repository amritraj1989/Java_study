package com.mikealbert.vision.view.converter;

import java.util.List;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import com.mikealbert.data.vo.ModelSearchCriteriaVO;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.service.ModelSearchService;
import com.mikealbert.util.MALUtilities;

@Component
public class ModelSearchResultVOConverter implements Converter {
	@Resource ModelSearchService modelSearchService;
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		ModelSearchCriteriaVO criteria = new ModelSearchCriteriaVO();		
		List<ModelSearchResultVO> trims;		
    	ModelSearchResultVO modelSearchResultVO = null;		
		
        if (MALUtilities.isNotEmptyString(value)) {  
        	criteria.setTrim(value);
        	trims = modelSearchService.findModels(criteria, new PageRequest(0, 1), null);  
        	modelSearchResultVO = trims.get(0);        	
        } 

        return modelSearchResultVO; 
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		ModelSearchResultVO modelSearchResultVO;
		if(MALUtilities.isEmpty(value)) {
			return "";
		} else {
			modelSearchResultVO = (ModelSearchResultVO) value;
			return modelSearchResultVO.getTrim();
		}
	}

}
