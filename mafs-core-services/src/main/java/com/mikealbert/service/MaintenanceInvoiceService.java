package com.mikealbert.service;

import com.mikealbert.data.entity.MaintenanceRequest;


public interface MaintenanceInvoiceService {	
	public boolean hasPostedInvoice(MaintenanceRequest mrq);	
}
