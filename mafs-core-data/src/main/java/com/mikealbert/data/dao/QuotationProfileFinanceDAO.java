package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.QuotationProfileFinance;
import com.mikealbert.data.entity.QuotationProfileFinancePK;

public interface QuotationProfileFinanceDAO extends QuotationProfileFinanceDAOCustom, JpaRepository<QuotationProfileFinance,QuotationProfileFinancePK> {
	
}
