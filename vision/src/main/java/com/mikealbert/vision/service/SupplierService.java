package com.mikealbert.vision.service;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.Supplier;
import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.UnitProgressCodeEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


public interface SupplierService {	
	
	public void createSupplierProgress(Long docId, String progressType, String opCode, Date actionDate, Date enteredDate , String supplier);
	
	public List<SupplierProgressHistory> getSupplierProgressHistory(Long docId, Sort sort);
	
	public List<SupplierProgressHistory> getSupplierProgressHistory(Long docId, boolean fetchAll);
	
	public List<SupplierProgressHistory>  getSupplierProgress(Long docId, String progressType, Date actionDate);
	
	public void saveSupplierProgressHistory(SupplierProgressHistory supplierProgressHistory) throws MalBusinessException, MalException;	
	
	public void saveVehicleReadyAndSendNotification(SupplierProgressHistory supplierProgressHistory,  Doc doc, 
			FleetMaster fleetMaster, QuotationModel quotationModel ,Driver driver, String username, CorporateEntity coproateEntity) throws MalBusinessException, MalException;
	
	public void deleteSupplierProgressHistory(Long sphId);
	
	public void performPostSupplierUpdateJob(String checkWillowConfig, String checkRemainder, Long cId,
											String progressType, Date enteredDate, Date actionDate, Long fmsId,
											Long makeId, Long qmdId, Long docId, String enteredBy,
											String supplier) throws MalBusinessException;

	public SupplierProgressHistory getSupplierProgressHistoryForDocAndType(Long docId, String progressType);
	public SupplierProgressHistory getSupplierProgressHistoryByDocAndTypeExcludeSphId(SupplierProgressHistory sph, String progressType);	
	
	public boolean hasProgressStatus(Doc purchaseOrder, UnitProgressCodeEnum progressType);
	
	public List<Supplier> getSuppliers(CorporateEntity corprateEntity, String accountCode);
}
