package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ProductElement;

/**
* DAO for ProductElement Entity
* @author Duncan
*/

public interface ProductElementDAO extends JpaRepository<ProductElement, Long> {
	
	@Query("SELECT prdEle FROM ProductElement prdEle WHERE prdEle.prdProductCode = ?1")
	public ProductElement findProductElementByProductCode(String prdProductCode);
	
}
