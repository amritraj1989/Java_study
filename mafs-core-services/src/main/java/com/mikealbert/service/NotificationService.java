package com.mikealbert.service;

import java.io.Serializable;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.NotificationServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.DiaryEntry}(s).
 *
 * @see com.mikealbert.data.entity.DiaryEntry
 * @see com.mikealbert.vision.service.NotificationServiceImpl
 **/
public interface NotificationService extends Serializable {
	
	public void notifyPurchasingOfDriverAddressChgIfUnitOnOrder(long DriverId, String userName);
	
}
