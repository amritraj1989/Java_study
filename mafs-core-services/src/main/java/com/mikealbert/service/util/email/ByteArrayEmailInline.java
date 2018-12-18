package com.mikealbert.service.util.email;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class ByteArrayEmailInline implements EmailInline {

	private String contentId;
	private byte[] content;
	
	public Resource getContentResource() {
		return new ByteArrayResource(content);
	}
	public void setContent(byte[] content) {
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
