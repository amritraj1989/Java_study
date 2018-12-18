package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.Doc;

/**
* DAO for PurchaseOrderReport
* @author ravresh
*/

public interface PurchaseOrderReportDAO extends PurchaseOrderReportDAOCustom, JpaRepository<Doc, Long>  {
	
	
}
