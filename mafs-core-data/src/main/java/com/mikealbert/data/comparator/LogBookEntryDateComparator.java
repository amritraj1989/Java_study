package com.mikealbert.data.comparator;

import java.util.Comparator;
import com.mikealbert.data.entity.LogBookEntry;
/**
 * Sorts the list of LogBookEntry on entry date in descending order.
 * 
 * @see LogBookEntry
 * @author sibley
 *
 */
public class LogBookEntryDateComparator implements Comparator<LogBookEntry> {

	@Override
	public int compare(LogBookEntry o1, LogBookEntry o2) {
		return o2.getEntryDate().compareTo(o1.getEntryDate());					
	}

}
