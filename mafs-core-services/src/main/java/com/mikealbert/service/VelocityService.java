package com.mikealbert.service;

import java.util.Map;

import org.apache.velocity.exception.VelocityException;


public interface VelocityService {
	
	public String getMergedTemplate(Map<String,Object> data, String templatePath) throws VelocityException;
	
}
