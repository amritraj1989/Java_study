package com.mikealbert.service;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import org.junit.Test;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.util.MALUtilities;

public class NotificationServiceTest extends BaseTest{
	@Resource NotificationService notificationService;

	@Test
	public void testNotifyPurchasingOfDriverAddressChg(){
		long hasOnOrderUnitDrvId = 0;
		Date mostRecentEntryDate = null;
		
		@SuppressWarnings("unchecked")
		List<BigDecimal>  driverList= ((List<BigDecimal>)em.createNativeQuery(TestQueryConstants.READ_DRIVER_ID_HAVING_VEHICLE_ON_ORDER).getResultList());		
		if(driverList.size() > 0){			
			hasOnOrderUnitDrvId = driverList.get(0).longValue();
		}
		
		notificationService.notifyPurchasingOfDriverAddressChgIfUnitOnOrder(hasOnOrderUnitDrvId,"DUNCAN_J");
		
		// verify an new diary entry for this driver for today
		@SuppressWarnings("unchecked")
        List<Date> diaryEntryDates = (List<Date>)em.createNativeQuery(TestQueryConstants.READ_MOST_RECENT_DIARY_DATE_FOR_DRV_ID).setParameter(1, hasOnOrderUnitDrvId).getResultList();
        if(diaryEntryDates.size() > 0){	
        	mostRecentEntryDate = diaryEntryDates.get(0);
        }
        Calendar today = new GregorianCalendar();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Date yesterday = MALUtilities.addDays(today.getTime(), -1);
        
		assertTrue(mostRecentEntryDate.after(yesterday));
        
    }

}
