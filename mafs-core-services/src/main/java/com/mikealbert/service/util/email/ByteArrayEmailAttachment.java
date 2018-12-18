package com.mikealbert.service.util.email;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class ByteArrayEmailAttachment implements EmailAttachment {

	private String fileName;
	private byte[] content;
	
	public Resource getContentResource() {
		return new ByteArrayResource(content);
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
