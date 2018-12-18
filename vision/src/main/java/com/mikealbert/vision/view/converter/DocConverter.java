package com.mikealbert.vision.view.converter;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import org.primefaces.component.selectonemenu.SelectOneMenu;

import com.mikealbert.data.entity.Doc;

@FacesConverter("DocConverter")
public class DocConverter implements Converter{

	@SuppressWarnings("unchecked")
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<Doc> docs = new ArrayList<Doc>();
		Doc selectedDoc = null;
		try{  
            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
            	if(uiComponent instanceof UISelectItems ) {
            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
            		docs = (ArrayList<Doc>)uiSelectItems.getValue();
            	}
            }
            
            for(Doc doc : docs){
            	if(doc.getDocId() == Long.valueOf(value)){
            		selectedDoc = doc;
            	}
            }

			return selectedDoc;
			
		} catch(Exception ex) {
			FacesMessage message = new FacesMessage(
					"Conversion error occurred. ", "Unable to convert Doc: " + value + ex.getMessage());
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		return String.valueOf(((Doc)value).getDocId());
	}

}
