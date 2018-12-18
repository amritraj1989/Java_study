package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.enumeration.DocumentSourceEnum;
import com.mikealbert.data.enumeration.DocumentStatus;
import com.mikealbert.data.enumeration.DocumentType;

@Service("maintenanceInvoiceService")
public class MaintenanceInvoiceServiceImpl implements MaintenanceInvoiceService {
	
	@Resource DocDAO docDAO;
	
	/**
	 * Determines whether an Invoice has been posted for a given Maintenance Request
	 * @param mrq Maintenance Request entity
	 * @return true when a posted invoice has been found.
	 */
	public boolean hasPostedInvoice(MaintenanceRequest mrq){
		boolean isInvoicePosted = false;
		List<Doc> docs = docDAO.findInvoiceByExtIdAndDocTypeAndSourceCode(
				mrq.getMrqId(), DocumentType.ACCOUNTS_PAYABLE.getdocumentType(), DocumentSourceEnum.FLEET_MAINTENANCE.getCode());
		
		for(Doc doc : docs){
			if(doc.getDocStatus().equals(DocumentStatus.INVOICE_ACCOUNTS_PAYABLE_STATUS_POSTED.getCode())){
				isInvoicePosted = true;
				break;			
			}
		}
		
		return isInvoicePosted;
	}
	
}
