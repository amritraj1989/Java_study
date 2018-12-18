package com.mikealbert.service.util.email;

import org.springframework.core.io.Resource;

public interface EmailAttachment {
	public Resource getContentResource();
	public String getFileName();
	public void setFileName(String fileName);
}
