package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Driver;

public interface DriverDAO extends JpaRepository<Driver,Long> ,DriverDAOCustom {
	
	@Query("select d from Driver d where LOWER(d.driverSurname) like LOWER(?1) order by d.driverSurname asc, d.driverForename asc, d.externalAccount.accountName asc")
	public Page<Driver> findDriversByLastName(String lastName, Pageable pageable);
	
	@Query("select d from Driver d where LOWER(d.driverSurname) like LOWER(?1) and d.activeInd = ?2  order by d.driverSurname asc, d.driverForename asc, d.externalAccount.accountName asc")
	public Page<Driver> findDriversByLastNameWithActiveIndicator(String lastName, String activeInd,Pageable pageable);
	
	@Query("select d from Driver d where LOWER(d.driverSurname) like LOWER(?1) and LOWER(d.driverForename) like LOWER(?2)  order by d.driverSurname asc, d.driverForename asc, d.externalAccount.accountName asc")
	public Page<Driver> findDriversByLastAndFirstName(String lastName, String firstName, Pageable pageable);
	
	@Query("select d from Driver d where LOWER(d.driverSurname) like LOWER(?1) and LOWER(d.driverForename) like LOWER(?2) and d.activeInd = ?3  order by d.driverSurname asc, d.driverForename asc, d.externalAccount.accountName asc")
	public Page<Driver> findDriversByLastAndFirstNameWithActiveIndicator(String lastName, String firstName,String activeInd, Pageable pageable);
	
	@Query("select d from Driver d join d.externalAccount ea where LOWER(d.driverSurname) like LOWER(?1)  and ea.accStatus = ?2  and ea.externalAccountPK.cId = ?3 and ea.externalAccountPK.accountCode IN ?4 and ea.externalAccountPK.accountType IN ?5 order by d.driverSurname asc, d.driverForename asc, ea.accountName")
	public Page<Driver> findDriversByLastName(String lastName, String accountStatus, long contextId, Pageable pageable, List<String> accountCodes, List<String> accountTypes);
	
	@Query("select d from Driver d join d.externalAccount ea where LOWER(d.driverSurname) like LOWER(?1) and ea.accStatus = ?2 and ea.externalAccountPK.cId = ?3 and d.activeInd = ?4 and ea.externalAccountPK.accountCode IN ?5 and ea.externalAccountPK.accountType IN ?6 order by d.driverSurname asc, d.driverForename asc, ea.accountName")
	public Page<Driver> findDriversByLastNameWithActiveIndicator(String lastName, String accountStatus, long contextId, String activeInd, Pageable pageable, List<String> accountCodes, List<String> accountTypes);
	
	
	@Query("select d from Driver d join d.externalAccount ea where LOWER(d.driverSurname) like LOWER(?1) and LOWER(d.driverForename) like LOWER(?2) and ea.accStatus = ?3 and ea.externalAccountPK.cId = ?4 and ea.externalAccountPK.accountCode IN ?5 and ea.externalAccountPK.accountType IN ?6 order by d.driverSurname asc, d.driverForename asc, ea.accountName")
	public Page<Driver> findDriversByLastAndFirstName(String lastName, String firstName, String accountStatus, long contextId, Pageable pageable, List<String> accountCodes, List<String> accountTypes);
	
	@Query("select d from Driver d join d.externalAccount ea where LOWER(d.driverSurname) like LOWER(?1) and LOWER(d.driverForename) like LOWER(?2) and ea.accStatus = ?3 and ea.externalAccountPK.cId = ?4 and d.activeInd = ?5 and ea.externalAccountPK.accountCode IN ?6 and ea.externalAccountPK.accountType IN ?7 order by d.driverSurname asc, d.driverForename asc, ea.accountName")
	public Page<Driver> findDriversByLastAndFirstNameWithActiveIndicator(String lastName, String firstName, String accountStatus, long contextId, String activeInd, Pageable pageable, List<String> accountCodes, List<String> accountTypes);
	
	@Query("select d.drvId from Driver d join d.externalAccount ea where LOWER(d.driverSurname) = LOWER(?1) and LOWER(d.driverForename) = LOWER(?2) and (d.email is NULL or (LOWER(d.email) = LOWER(?3)))and ea.externalAccountPK.accountCode = ?4 and ea.externalAccountPK.cId = ?5 ")
	public List<Long> findDriverByLastAndFirstNameAndEmailForAccount(String lastName, String firstName,  String email, String accountCode, long contextId);
	
	@Query("select d from Driver d LEFT JOIN FETCH d.driverAllocationList da LEFT JOIN FETCH da.fleetMaster fm where d.drvId = ?1 AND (da.deallocationDate IS NULL OR da.deallocationDate > CURRENT_DATE) AND  da.allocationDate <= CURRENT_DATE")
	public Driver getDriverByIdWithCurrentAllocations(long drvId);
	
	@Query("select d from Driver d JOIN d.externalAccount ea WHERE d.parentRelationships.size = 0 and d.childRelationships.size = 0 and d.driverAllocationList.size = 0 and ea.accStatus = ?1 and ea.externalAccountPK.cId = ?2 and LOWER(d.driverSurname) like ?3 and  d.activeInd = ?4 and ea.externalAccountPK.accountCode IN ?5 and ea.externalAccountPK.accountType IN ?6")
	public List<Driver> findDriversByLastNameWithoutAllocationAndRelationship(String accountStatus, long contextId, String lastName ,String activeInd, List<String> accountCodes, List<String> accountTypes);		
	
}
