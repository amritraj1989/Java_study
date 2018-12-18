package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientServiceElement;

public interface ClientServiceElementsDAO extends ClientServiceElementsDAOCustom,JpaRepository<ClientServiceElement, Long> {

	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and ce.endDate is null and ce.clientServiceElementType = 1 and ce.product.productCode is null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getClientServiceElementsByAccount(long cid, String accountType, String accountCode);

	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and ce.endDate is null and ce.clientServiceElementType = 1 and ce.product.productCode is not null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getClientProductServiceElementsByAccount(long cid, String accountType, String accountCode);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and (ce.endDate is NULL OR ce.removedFlag = 'Y') and ce.clientServiceElementType = 1 and ce.product.productCode is not null ORDER BY ce.product.productCode ASC, ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getClientProductServiceElementsByAccountIncludeRemoved(long cid, String accountType, String accountCode);	

	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.clientContractServiceElement.leaseElement.lelId = ?4 and ce.startDate <= SYSDATE and ((ce.endDate is NULL and ce.product.productCode is null) OR (ce.removedFlag = 'Y' and ce.product.productCode is not null)) and ce.clientServiceElementType = 1 ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getClientServiceElementsByAccountAndElementIncludeRemoved(long cid, String accountType, String accountCode, Long leaseElementId);	
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.clientContractServiceElement.leaseElement.lelId = ?4 and start_date <= SYSDATE and end_date is null and ce.clientServiceElementType = 1 and ce.product.productCode is null")
	public ClientServiceElement getClientServiceElementByAccountAndElement(long cid, String accountType, String accountCode, Long leaseElementId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.clientContractServiceElement.leaseElement.lelId = ?4 and start_date <= SYSDATE and end_date is null and ce.clientServiceElementType = 1 and ce.product.productCode = ?5")
	public ClientServiceElement getClientProductServiceElementByAccountAndElementAndProduct(long cid, String accountType, String accountCode, Long leaseElementId, String productCode);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE  AND ce.clientServiceElementType.clientServiceElementTypeId = ?4 and ce.product.productCode is null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC, ce.startDate ASC")
	public List<ClientServiceElement> getClientServiceElementsHistoryByAccount(long cid, String accountType, String accountCode, Long clientServiceElementTypeId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE  AND ce.clientServiceElementType.clientServiceElementTypeId = ?4 and ce.product.productCode is null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC, ce.externalAccountGradeGroup.driverGradeGroup.driverGradeGroup ASC, ce.startDate ASC")
	public List<ClientServiceElement> getGradeGroupServiceElementsHistoryByAccount(long cid, String accountType, String accountCode, Long clientServiceElementTypeId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and ce.endDate is NULL AND ce.externalAccountGradeGroup.eagId = ?4 ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getClientServiceElementsByAccountAndGradeGroup(long cid, String accountType, String accountCode, Long gradeGroupId);

	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and (ce.endDate is NULL OR ce.removedFlag = 'Y') AND ce.externalAccountGradeGroup.eagId = ?4 and ce.product.productCode is null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> 	getClientServiceElementsByAccountAndGradeGroupWRemoved(long cid, String accountType, String accountCode, Long gradeGroupId);

	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.startDate <= SYSDATE and ce.removedFlag = 'Y' AND ce.externalAccountGradeGroup.eagId = ?4 and ce.clientContractServiceElement.leaseElement.lelId = ?5 and ce.clientServiceElementType = 2 and ce.product.productCode is null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public ClientServiceElement getGradeGroupServiceElementByGradeGroupAndElementWRemoved(long cid, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.externalAccountGradeGroup.eagId = ?4 and ce.clientContractServiceElement.leaseElement.lelId = ?5 and start_date <= SYSDATE and end_date is null and ce.clientServiceElementType = 2 and ce.product.productCode is null")
	public ClientServiceElement getGradeGroupServiceElementByGradeGroupAndElement(Long cId, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.externalAccountGradeGroup.eagId = ?4 and ce.startDate <= SYSDATE and ce.endDate is null and ce.clientServiceElementType = 2 and ce.product.productCode is not null ORDER BY ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getGradeGroupProductServiceElementsByAccountAndGradeGroup(long cId, String accountType, String accountCode, Long gradeGroupId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.externalAccountGradeGroup.eagId = ?4 and ce.clientContractServiceElement.leaseElement.lelId = ?5 and ce.product.productCode = ?6 and start_date <= SYSDATE and end_date is null and ce.clientServiceElementType = 2")
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupAndElementAndProduct(long cid, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.externalAccountGradeGroup.eagId = ?4 and ce.startDate <= SYSDATE and (ce.endDate is NULL OR ce.removedFlag = 'Y') and ce.clientServiceElementType = 2 and ce.product.productCode is not null ORDER BY ce.product.productCode ASC, ce.clientContractServiceElement.leaseElement.elementName ASC")
	public List<ClientServiceElement> getGradeGroupProductServiceElementsByGradeGroupIncludeRemoved(long cid, String accountType, String accountCode, Long gradeGroupId);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.externalAccountGradeGroup.eagId = ?4 and ce.clientContractServiceElement.leaseElement.lelId = ?5 and ce.product.productCode = ?6 and start_date <= SYSDATE and (ce.endDate is NULL OR ce.removedFlag = 'Y') and ce.clientServiceElementType = 2")
	public ClientServiceElement getGradeGroupProductServiceElementByGradeGroupElementAndProductWRemoved(long cid, String accountType, String accountCode, Long gradeGroupId, Long leaseElementId, String productCode);
	
	@Query("SELECT ce FROM ClientServiceElement ce WHERE ce.externalAccount.externalAccountPK.cId = ?1 and ce.externalAccount.externalAccountPK.accountType =?2 and ce.externalAccount.externalAccountPK.accountCode =?3 and ce.clientContractServiceElement.leaseElement.lelId = ?4 and ce.product.productCode = ?5 and start_date <= SYSDATE and (ce.endDate is NULL OR ce.removedFlag = 'Y') and ce.clientServiceElementType = 1")
	public ClientServiceElement getClientProductServiceElementByElementAndProductWRemoved(long cid, String accountType, String accountCode, Long leaseElementId, String productCode);
	
}
