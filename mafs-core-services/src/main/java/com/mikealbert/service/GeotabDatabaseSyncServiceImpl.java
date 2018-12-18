package com.mikealbert.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.GeotabDatabaseSyncDAO;
import com.mikealbert.data.entity.GeotabDatabaseSync;

@Service("geotabDatabaseSyncService")
public class GeotabDatabaseSyncServiceImpl implements GeotabDatabaseSyncService{
	@Resource GeotabDatabaseSyncDAO geotabDatabaseSyncDAO;
	
	@Override
	public Map<String,String> getAllRecords() {
		Map<String,String> geoAccountAndTabMap = new HashMap<String, String>();
		for(GeotabDatabaseSync geotabDatabaseSync : geotabDatabaseSyncDAO.findActiveDatabaseName()){
			geoAccountAndTabMap.put(geotabDatabaseSync.getAccountCode(),geotabDatabaseSync.getGeotabAccountDatabase());			
		}
		return geoAccountAndTabMap;
	}

	@Override
	public List<String> getGeoAccounts() {
		return geotabDatabaseSyncDAO.getGeoAccount();
	}

}
