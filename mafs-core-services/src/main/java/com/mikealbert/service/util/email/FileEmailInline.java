package com.mikealbert.service.util.email;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileEmailInline  implements EmailInline {

	private String contentId;
	private File content;
	
	public Resource getContentResource() {
		return new FileSystemResource(content);
	}
	
	public void setContent(File content) {
		this.content = content;
	}

	@Override
	public String getContentId() {
		return contentId;
	}

	@Override
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
}
