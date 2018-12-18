package com.mikealbert.vision.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("PoolManagerConverter")
public class PoolManagerConverter implements Converter{
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return convertPoolManager(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return convertPoolManager(value.toString());
	}
	
	/* Converts Pool Manager Flag from 'Y' to '(Pool Mgr)' */
	public String convertPoolManager(String poolManagerFlag){
		String poolManagerDesc = null;
		if(poolManagerFlag.contains("Y")){
			poolManagerDesc = "(Pool Mgr)";
		}
		return poolManagerDesc;
	}

}
