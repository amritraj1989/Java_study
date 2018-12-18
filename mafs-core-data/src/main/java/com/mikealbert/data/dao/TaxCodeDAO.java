package com.mikealbert.data.dao;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.TaxCode;

public interface TaxCodeDAO extends JpaRepository<TaxCode, Long>{
	@Query("from TaxCode where UPPER(taxCode) = UPPER(?1) and effectiveDate = (select max(effectiveDate) from TaxCode where UPPER(taxCode) = UPPER(?1) and effectiveDate <= ?2)")
	TaxCode	findByTaxCodeAndMaxEffectiveDate(String taxCode , Date date);
}
