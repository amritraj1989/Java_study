package com.mikealbert.data;

import com.mikealbert.util.MALUtilities;

public class DataUtilities {
	/**
	 * Appends a wildcard character to the string only 
	 * when one does not exist.
	 * @param str String to append wildcard to
	 * @return String Resulting string
	 */
	public static String appendWildCardToRight(String str){	
		if(MALUtilities.isEmptyString(str)) {
			str = "%";
		} else if(!str.endsWith("%")) {
			str += "%";
		}
		return str ;
	}
	
	/**
	 * Prepends a wildcard character to the string only 
	 * @param str String to prepend wildcard to
	 * @return String Resulting string
	 */
	public static String prependWildCardToLeft(String str){	
		if(MALUtilities.isEmptyString(str)) {
			str = "%";
		} else if(!str.startsWith("%")) {
			str = "%" + str;
		}
		return str ;
	}	
	
	
	/**
	 * Test for null, and returns either tested value when 
	 * it is not null or the alternate value when it is null
	 * @param value String to test
	 * @param result Substitute value when test value is null
	 * @return String either tested or substitute value
	 */
	public static String decodeNullString(String value, String result){
		String retVal;
		
		if(MALUtilities.isEmpty(value)){
			retVal = result;
		} else {
			retVal = value;
		}
		
		return retVal;
	}	

}
