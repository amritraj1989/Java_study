package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.Doc;

public interface MaintenanceInvoiceDAO extends MaintenanceInvoiceDAOCustom, JpaRepository<Doc, Long> {

}
