package com.mikealbert.service.util.email;

import org.springframework.core.io.Resource;

public class ResourceEmailInline implements EmailInline {

	private String contentId;
	private Resource content;
	
	public Resource getContentResource() {
		return content;
	}
	public void setContent(Resource content) {
		this.content = content;
	}
	@Override
	public String getContentId() {
		// TODO Auto-generated method stub
		return contentId;
	}
	@Override
	public void setContentId(String contentId) {
		// TODO Auto-generated method stub
		this.contentId = contentId;
	}
	
}
