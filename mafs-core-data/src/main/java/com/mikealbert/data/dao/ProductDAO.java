package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.Product;

public interface ProductDAO extends JpaRepository<Product, String>{
	
	@Query("SELECT prd FROM Product prd WHERE clientProductListInd = 'Y' ORDER BY productCode ASC")
	public List<Product> findActiveClientProductList();

}
