package com.mikealbert.service;

import java.util.List;
import java.util.Map;

public interface GeotabDatabaseSyncService {
	
	public Map<String,String> getAllRecords();
	public List<String> getGeoAccounts();

}
