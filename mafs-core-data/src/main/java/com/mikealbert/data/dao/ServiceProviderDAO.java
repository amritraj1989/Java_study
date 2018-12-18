package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ServiceProvider;

/**
 * DAO for ServiceProvider Entity
 * 
 * @author Raj
 */
// Added condition in all queries to filter suppliers with Pending_Na category (Bug 16485)
public interface ServiceProviderDAO extends ServiceProviderDAOCustom, JpaRepository<ServiceProvider, Long> {
@Query("FROM ServiceProvider sup WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND LOWER(sup.serviceProviderName) LIKE LOWER(?1)")
public List<ServiceProvider> findByServiceProviderName(String serviceProviderName, Pageable page);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND LOWER(sup.serviceProviderName) LIKE LOWER(?1) OR LOWER(sup.serviceProviderNumber) LIKE LOWER(?1)")
public List<ServiceProvider> findByServiceProviderNameOrCode(String nameOrCode, Pageable page);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA'AND LOWER(sup.serviceProviderName) LIKE LOWER(?1) OR LOWER(sup.serviceProviderNumber) LIKE LOWER(?1) AND sup.networkVendor = 'Y' AND sup.parentServiceProvider IS NULL")
public List<ServiceProvider> findByServiceProviderNameOrCodeOnlyParents(String serviceProviderNameOrCode, Pageable page);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND sup.serviceTypeCode NOT IN(?2) AND (LOWER(sup.serviceProviderName) LIKE LOWER(?1) OR LOWER(sup.serviceProviderNumber) LIKE LOWER(?1))")
public List<ServiceProvider> findByServiceProviderNameOrCodeAndType(String nameOrCode, List<String> serviceProviderType);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND sup.serviceTypeCode LIKE (?2) AND (LOWER(sup.serviceProviderName) LIKE LOWER(?1) OR LOWER(sup.serviceProviderNumber) LIKE LOWER(?1))")
public List<ServiceProvider> findByServiceProviderNameOrCodeAndServiceType(String nameOrCode, String serviceProviderType,Pageable page);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND LOWER(sup.serviceProviderNumber) = LOWER(?1) AND sup.parentServiceProvider.serviceProviderId = ?2 ")
public ServiceProvider getServiceProviderByCodeAndParent(String serviceProvideCode, Long parentProviderId);

@Query("FROM ServiceProvider sup LEFT JOIN FETCH sup.serviceProviderAddresses spa WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND sup.parentServiceProvider.serviceProviderId = ?1 ")
public  List<ServiceProvider> findByParent(Long parentProviderId);

@Query("FROM ServiceProvider sup WHERE sup.serviceProviderCategory <> 'PENDING_NA' AND LOWER(TRANSLATE(sup.serviceProviderName,'/\\:*?<>|-','___')) LIKE LOWER( ?1) AND sup.networkVendor = 'Y' AND sup.parentServiceProvider IS NULL")
public List<ServiceProvider> findParentByProviderNameInFileFmt(String serviceProvideName);

@Query("FROM ServiceProvider sup WHERE LOWER(sup.serviceProviderName) = LOWER(?1)")
public List<ServiceProvider> findByServiceProviderExactName(String serviceProviderName);

}