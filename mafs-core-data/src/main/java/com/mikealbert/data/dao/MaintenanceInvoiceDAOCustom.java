package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.entity.DoclPK;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.vo.MaintenanceInvoiceCreditVO;
import com.mikealbert.data.vo.InvoiceDateAndNumberVO;

public interface MaintenanceInvoiceDAOCustom {
	public InvoiceDateAndNumberVO getMaintenanceRequestMafsInvoiceNumber(Long mrqId);
	public boolean isMaintenanceRequestInvoiceCredit(Long mrqId);
	public InvoiceDateAndNumberVO getMaintenanceRequestPayeeInvoiceData(Long mrqId);
	public List<MaintenanceInvoiceCreditVO> getMaintenanceCreditAPLines(MaintenanceRequest mrq);
	public List<Long> getMaintenanceCreditAPDocIds(MaintenanceRequest mrq);
	public List<DoclPK> getMaintenanceCreditARMarkupDoclPKs(MaintenanceRequest mrq);
	public List<DoclPK> getMaintenanceCreditARTaxDoclPKs(MaintenanceRequest mrq);
	public List<DoclPK> getMaintenanceCreditARDoclPKsWithoutTaxAndMarkupLines(MaintenanceRequest mrq);	
}
