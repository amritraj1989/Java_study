package com.mikealbert.websvc.client;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

public abstract  class WebServicesClient extends WebServiceTemplate {
	
	private Jaxb2Marshaller jaxb2Marshaller =  new Jaxb2Marshaller();
	
	public WebServicesClient(){
		 setMarshaller(jaxb2Marshaller);
		 setUnmarshaller(jaxb2Marshaller);
	}
	
	public WebServicesClient(String wsdlURL, String  contextPath){
		 setMarshaller(jaxb2Marshaller);
		 setUnmarshaller(jaxb2Marshaller);
		 super.setDefaultUri(wsdlURL);
		 ((Jaxb2Marshaller) super.getMarshaller()).setContextPath(contextPath);
	}
	
	public void setDefaultContext(String wsdlURL, String  contextPath){
		 super.setDefaultUri(wsdlURL);
		 ((Jaxb2Marshaller) super.getMarshaller()).setContextPath(contextPath);
	}
}
