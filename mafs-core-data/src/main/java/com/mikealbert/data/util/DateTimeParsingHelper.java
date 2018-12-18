package com.mikealbert.data.util;

import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class DateTimeParsingHelper {
	private Parser dateStringParser = new Parser();
	/**
	 * @param dateString 	string to test for date or time components
	 * @return true/false	indicating whether the string passed in contains date or time expressions
	 */
	public boolean isDateString(String dateString){
		List<DateGroup> dateGroupParts = dateStringParser.parse(dateString);
		
		if(dateGroupParts.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
}
