package com.mikealbert.service;

import java.util.Date;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.OraSessionDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.DatabaseName;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.OraSessionService}
 */
@Service("oraSessionService")
public class OraSessionServiceImpl implements OraSessionService {

	@Resource
	OraSessionDAO oraSessionDAO;
	
		
	private static final long serialVersionUID = 1L;
	
	public String getDatabaseInfo(){
		String dbInfo = null;
		
		return dbInfo;
	}
	
	/**
	 * Determines whether the database that the application is using is for development or not
	 * @return True when development database
	 */
	public boolean isDevelopmentDatabase(){
		boolean isDevDb = false;
		String dbName = oraSessionDAO.getWillowConfigValue("CURRENT_DB").toUpperCase();
		
		
		try {
			if(DatabaseName.valueOf(dbName).getEnvironmentType() == MalConstants.ENVIRONMENT_DEVELOPMENT){
				isDevDb = true;			
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "Determining whether the application is connected to a development database." }, ex);				
		}
		
		return isDevDb;
	}
	
	/**
	 * Determines whether the database that the application is using is for QA or not
	 * @return True when QA database
	 */
	public boolean isQADatabase(){
		boolean isQADb = false;
		String dbName = oraSessionDAO.getWillowConfigValue("CURRENT_DB").toUpperCase();
		
		
		try {
			if(DatabaseName.valueOf(dbName).getEnvironmentType() == MalConstants.ENVIRONMENT_TESTING){
				isQADb = true;			
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "Determining whether the application is connected to a QA database." }, ex);				
		}
		
		return isQADb;
	}
	
	/**
	 * Determines whether the database that the application is using is for QA or not
	 * @return True when QA database
	 */
	public boolean isTrainingDatabase(){
		boolean isTrainingDb = false;
		String dbName = oraSessionDAO.getWillowConfigValue("CURRENT_DB").toUpperCase();
		
		
		try {
			if(DatabaseName.valueOf(dbName).getEnvironmentType() == MalConstants.ENVIRONMENT_TRAINING){
				isTrainingDb = true;			
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "Determining whether the application is connected to a training database." }, ex);				
		}
		
		return isTrainingDb;
	}

	/**
	 * Retrieves the database name if it is Development, Train, or QA
	 * @return True when development or QA database
	 */
	@Override
	public String getDatabaseNameForDevQATrain() {
		try{
			if(isDevelopmentDatabase() || isQADatabase() || isTrainingDatabase()){
				return oraSessionDAO.getWillowConfigValue("CURRENT_DB").toUpperCase();
			}
			else{
				return null;
			}
		}
		catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "Retrieving database name." }, ex);	
		}
	}

	/**
	 * Retrieves that database date for Dev, QA, and Train Environments
	 */
	@Override
	public Date getDatabaseDate() {
		try{
			if(isDevelopmentDatabase() || isQADatabase() || isTrainingDatabase()){
				return oraSessionDAO.getReleaseDate();
			}
			else{
				return null;
			}
		}
		catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "Retrieving database release date." }, ex);	
		}
	}

	/**
	 * Retrieves that database date for Dev, QA, and Train Environments
	 */
	@Override
	public String getDBRefreshdate() {
		try{
			if(isDevelopmentDatabase() || isQADatabase() || isTrainingDatabase()){

				return oraSessionDAO.getDbRefreshInfo(Long.valueOf(CorporateEntity.MAL.getCorpId()));
			}
			else{
				return null;
			}
		}
		catch(Exception ex){
			throw new MalException("generic.error.occured.while", 
					new String[] { "Retrieving database refresh info." }, ex);	
		}
	}
}
