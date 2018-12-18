package com.mikealbert.data.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
public class InterfaceErrorsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6523357857485253350L;
		
    public static final HashMap<String, String> INTERFACE_TYPES = new HashMap<String, String>(){
        {
        	put("Vendor Locations", "errors.vendorLocations");
        	put("Vendor Maint Codes", "errors.vendorCodes");
        }
    };
    
    public static final Set<String> getInterfaceTypesKeys(){
    	return INTERFACE_TYPES.keySet();
    }
    
	private Object sourceObject;
	private String[] errorMessages;
	private String[] errorFields;
	
	private String messageId;
	private Long parentProviderId;
	
	public String[] getErrorMessages() {
		return errorMessages;
	}
	public void setErrorMessages(String[] errorMessages) {
		this.errorMessages = errorMessages;
	}
	public String[] getErrorFields() {
		return errorFields;
	}
	public void setErrorFields(String[] errorFields) {
		this.errorFields = errorFields;
	}
	public Object getSourceObject() {
		return sourceObject;
	}
	public void setSourceObject(Object sourceObject) {
		this.sourceObject = sourceObject;
	}
	public String getMessageId() {
		return messageId;
	}
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	public Long getParentProviderId() {
		return parentProviderId;
	}
	public void setParentProviderId(Long parentProviderId) {
		this.parentProviderId = parentProviderId;
	}
}
