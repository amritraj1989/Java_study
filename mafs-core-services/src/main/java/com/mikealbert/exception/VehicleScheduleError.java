package com.mikealbert.exception;

import com.mikealbert.util.MALUtilities;

public class VehicleScheduleError extends MalDataValidationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6032035935881557941L;
	
	public static final String MASTER_SCHEDULE_NOT_DETERMINED = "1";
	public static final String FUEL_TYPE_NOT_MATCHED = "2";
	public static final String FUEL_TYPE_MISSING = "3";
	public static final String CLIENT_SCHEDULE_TYPE_MISSING = "4";
	public static final String MAINT_PREF_MISSING = "5";
	public static final String VIN_MISSING = "6";
	public static final String FMS_ID_MISSING = "7";
	public static final String CONTRACT_LINE_MISSING = "8";	
	public static final String VIN_NOT_DECODED = "9";
	public static final String MASTER_SCHEDULED_NOT_MATCH_HIGH_MILES = "10";
	public static String VEHICLE_ALREADY_HAS_ACTIVE_SCHEDULE = "11";
	public static final String POC_MISSING = "12";

	private ErrorCodeResolver errorResolver;
	
	public VehicleScheduleError(String errorCode) {
		super(errorCode);
	}

	public VehicleScheduleError(String errorCode, Exception e) {
		super(errorCode, e);
	}

	@Override
	protected final ErrorCodeResolver getErrorCodeResolver() {
		//TODO: test the use of a static with this class as a minor optimization (most of Spring stuff is by default singleton, but exceptions are created each time)
		if(MALUtilities.isEmpty(errorResolver)){
			errorResolver = new ErrorCodeEntityResolver();
		}
		return errorResolver; 
	}

}
