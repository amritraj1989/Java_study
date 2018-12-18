package com.mikealbert.vision.service;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.EmailRequestEventEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.PurchasingEmailServiceImpl}.
 * 
 * @see com.mikealbert.vision.service.PurchasingEmailServiceImpl
 * */
public interface PurchasingEmailService {		
	public void emailClientConfirmation(JasperPrint print, Long docId, String username);	
	public boolean isClientConfirmationEmailable(Long docId);
	public boolean isVehicleReadyNotificationEmailable(SupplierProgressHistory supplierProgressHistory, QuotationModel qmd);
	public void emailVehicleReadyNotification(SupplierProgressHistory sph,  Doc doc, FleetMaster fms, QuotationModel qmd ,Driver driver, String username, CorporateEntity coproateEntity) throws MalBusinessException, MalException;
		
	public void requestEmail(Doc purchaseOrder, EmailRequestEventEnum emailType, boolean scheduled, String username);
	public void requestEmail(Long docId, EmailRequestEventEnum emailType, boolean scheduled, String username);	
}
