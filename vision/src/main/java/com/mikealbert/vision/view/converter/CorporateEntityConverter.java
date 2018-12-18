package com.mikealbert.vision.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.mikealbert.data.enumeration.CorporateEntity;

@FacesConverter("CorporateEntityConverter")
public class CorporateEntityConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		CorporateEntity corpEntity;
		// TODO: less hard coding more dynamic enumeration code!
		if(value.equalsIgnoreCase("1")){
			corpEntity = CorporateEntity.MAL;
		}else {
			corpEntity = CorporateEntity.LTD;
		}
		return corpEntity;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return String.valueOf(((CorporateEntity)value).getCorpId());
	}
	
}
