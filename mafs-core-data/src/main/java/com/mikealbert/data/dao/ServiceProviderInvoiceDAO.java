package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ServiceProviderInvoiceHeader;

/**
* DAO for ServiceProviderInvoiceHeader and ServiceProviderInvoiceDetail Entities
* @author duncan
*/

public interface ServiceProviderInvoiceDAO extends ServiceProviderInvoiceDAOCustom, JpaRepository<ServiceProviderInvoiceHeader, Long> {

	
}
