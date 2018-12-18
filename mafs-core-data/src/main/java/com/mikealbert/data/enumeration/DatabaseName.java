package com.mikealbert.data.enumeration;

import com.mikealbert.common.MalConstants;

public enum DatabaseName {

	MALDEV(MalConstants.ENVIRONMENT_DEVELOPMENT), 
	DEV1(MalConstants.ENVIRONMENT_DEVELOPMENT),
	DEV2(MalConstants.ENVIRONMENT_DEVELOPMENT),
	DEV3(MalConstants.ENVIRONMENT_DEVELOPMENT),
	DEV4(MalConstants.ENVIRONMENT_DEVELOPMENT),
	DEV5(MalConstants.ENVIRONMENT_DEVELOPMENT),
	MALSTAGE(MalConstants.ENVIRONMENT_DEVELOPMENT), 
	QA1(MalConstants.ENVIRONMENT_DEVELOPMENT), 
	QA2(MalConstants.ENVIRONMENT_TESTING),
	QA3(MalConstants.ENVIRONMENT_TESTING),
	QA4(MalConstants.ENVIRONMENT_TESTING),	
	QA5(MalConstants.ENVIRONMENT_TESTING),
	TRN1(MalConstants.ENVIRONMENT_TRAINING), 
	PRODDB1(MalConstants.ENVIRONMENT_PRODUCTION),
	STG1(MalConstants.ENVIRONMENT_DEVELOPMENT);
	
	
	private String environmentType;
	
	private DatabaseName(String environmentType){
		this.environmentType = environmentType;
	}
	
	public String getEnvironmentType(){
		return this.environmentType;
	}
	
	
}
