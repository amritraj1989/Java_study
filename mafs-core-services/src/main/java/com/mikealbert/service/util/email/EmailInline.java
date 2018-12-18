package com.mikealbert.service.util.email;

import org.springframework.core.io.Resource;

public interface EmailInline {
	public Resource getContentResource();
	
	public String getContentId();
	public void setContentId(String contentId);
}
