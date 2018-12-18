package com.mikealbert.data.dao;

import javax.persistence.Query;

import com.mikealbert.data.entity.ServiceProviderInvoiceHeader;

public class ServiceProviderInvoiceDAOImpl extends GenericDAOImpl<ServiceProviderInvoiceHeader, Long> implements ServiceProviderInvoiceDAOCustom {
	private static final long serialVersionUID = 6794593393250317890L;

	@Override
	public void processServiceProviderInvoiceLoad(Long loadId, Long cId,
			String userName) {
		String sqlStmt = "BEGIN MAINTENANCE_REQUEST_WRAPPER.process_maintenance_invoices(?, ?, ?); END;";
		Query query = entityManager.createNativeQuery(sqlStmt);
    	query.setParameter(1, loadId);
    	query.setParameter(2, cId);
    	query.setParameter(3, userName);
    	query.executeUpdate();
		
	}

	
}
