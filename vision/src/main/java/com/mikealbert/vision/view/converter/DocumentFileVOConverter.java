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
import com.mikealbert.data.vo.DocumentFileVO;
import com.mikealbert.util.MALUtilities;

@SuppressWarnings("unchecked")
@Component
public class DocumentFileVOConverter implements Converter{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		List<DocumentFileVO> documentFileVOs = new ArrayList<DocumentFileVO>();
		DocumentFileVO selectedDocumentFileVO = null;
		
        if (value.trim().equals("")) {   
            return null;   
        } else {  		
			try{  
	            for(UIComponent uiComponent : ((SelectOneMenu)component).getChildren()){
	            	if(uiComponent instanceof UISelectItems ) {
	            		UISelectItems uiSelectItems = (UISelectItems)uiComponent;            		   
	            		documentFileVOs = (ArrayList<DocumentFileVO>)uiSelectItems.getValue();
	            	}
	            }
	            
	            for(DocumentFileVO documentFileVO : documentFileVOs){
	            	if(documentFileVO.getFileId().equals(Long.valueOf(value))){
	            		selectedDocumentFileVO = documentFileVO;
	            		break;
	            	}
	            }
	
				return selectedDocumentFileVO;
				
			} catch(Exception ex) {
				FacesMessage message = new FacesMessage(
						"Conversion error occurred. ", "Unable to convert DocumentFileVO " + ex.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ConverterException(message);
			}
        }
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (MALUtilities.isEmpty(value)) {   
            return "";   
        } else {  		
        	return ((DocumentFileVO)value).getFileId().toString();
        }  		
	}

}
