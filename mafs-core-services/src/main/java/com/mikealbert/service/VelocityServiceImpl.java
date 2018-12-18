package com.mikealbert.service;


import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.VelocityException;
import org.springframework.stereotype.Service;


@Service("velocityService")
public class VelocityServiceImpl implements VelocityService {


	public String getMergedTemplate(Map<String,Object> data, String templatePath) throws VelocityException {
		
		Velocity.init(getDefaultProperties());
		
	    VelocityContext context = new VelocityContext();
	    if(data != null) {
		    for (Map.Entry<String, Object> entry : data.entrySet()) {
		    	context.put(entry.getKey(), entry.getValue());	    
		    }	    		    	
	    }
	    StringWriter stringWriter = new StringWriter();
	    Velocity.mergeTemplate(templatePath, "UTF-8", context, stringWriter);
	    String text = stringWriter.toString();

	    return text;

	}

	private Properties getDefaultProperties() {
		Properties props = new Properties();
		props.setProperty("resource.loader", "class");
		props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		return props;
	}
	
}
