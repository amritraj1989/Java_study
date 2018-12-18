package com.mikealbert.service;

import java.util.List;
import java.util.Map;

public interface FleetmaticsEmailService {
	
	public void emailForGeotabInvalidRecords(List<String> invalidData, String invlidType, Map<String,String> emailConfig);
	public void emailForFleetCompleteInvalidRecords(List<String> invalidData, String invlidType, Map<String,String> emailConfig);	
}
