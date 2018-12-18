package com.mikealbert.data.dao;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.AccessoryCode;
import com.mikealbert.data.entity.QuotationModelAccessory;

/**
* DAO for QuotationModelAccessory Entity
* @author Sibley
*/

public interface QuotationModelAccessoryDAO extends JpaRepository<QuotationModelAccessory, Long> {
	@Query("select sum(residualAmt) from QuotationModelAccessory where quotationModel.qmdId= ?1")
	public BigDecimal	getSumOfResidual(Long qmdId);
}
