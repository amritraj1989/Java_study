package com.mikealbert.service.util.email;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileEmailAttachment  implements EmailAttachment {

	private String fileName;
	private File content;
	
	public Resource getContentResource() {
		return new FileSystemResource(content);
	}
	
	public void setContent(File content) {
		this.content = content;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
