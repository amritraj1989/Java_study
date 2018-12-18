package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QuotationStandardAccessory;

/**
* DAO for QuotationStandardAccessory Entity
* @author Ravresh
*/

public interface QuotationStandardAccessoryDAO extends JpaRepository<QuotationStandardAccessory, Long> { 
	
	@Query("Select qsa.standardAccessory.standardAccessoryCode.description from QuotationStandardAccessory qsa where qsa.quotationModel.qmdId = ?1")
	public List<String> getStandardAccessoriesDesc(Long qmdId); 
}
