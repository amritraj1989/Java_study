package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.Quotation;

/**
* DAO for Quotation Entity
* @author Singh
*/

public interface QuotationDAO extends QuotationDAOCustom, JpaRepository<Quotation, Long> {}
