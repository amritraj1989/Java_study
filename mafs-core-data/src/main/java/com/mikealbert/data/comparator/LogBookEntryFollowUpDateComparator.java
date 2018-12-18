package com.mikealbert.data.comparator;

import java.util.Comparator;
import com.mikealbert.data.entity.LogBookEntry;
import com.mikealbert.util.MALUtilities;
/**
 * Sorts the list of LogBookEntry on follow up date in ascending order.
 * Follow up dates that are null will fall to the bottom of the list.
 * 
 * @see LogBookEntry
 * @author sibley
 *
 */
public class LogBookEntryFollowUpDateComparator implements Comparator<LogBookEntry> {

	@Override
	public int compare(LogBookEntry o1, LogBookEntry o2) {
		if(MALUtilities.isEmpty(o1.getFollowUpDate()) || MALUtilities.isEmpty(o2.getFollowUpDate())){
			if(MALUtilities.isEmpty(o1.getFollowUpDate())){
				return 1;
			} else {
			    return -1;	
			}
		} else {
			return MALUtilities
					.clearTimeFromDate(o1.getFollowUpDate()).compareTo(MALUtilities.clearTimeFromDate(o2.getFollowUpDate()));					
		}
	}

}
