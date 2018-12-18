package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.Quotation;
import com.mikealbert.data.entity.QuotationCapitalElementBackup;

/**
* DAO for QuotationCapitalElementBackup Entity
* @author Sibley
*/

public interface QuotationCapitalElementBackupDAO extends JpaRepository<QuotationCapitalElementBackup, Long> {}
