package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.SupplierCategoryCode;

/**
 * DAO for SupplierCategoryCodeDAO Entity
 * 
 */

public interface SupplierCategoryCodeDAO extends JpaRepository<SupplierCategoryCode, String> {
	@Query("FROM SupplierCategoryCode scc WHERE scc.supplierCategory IN (?1)")
	public List<SupplierCategoryCode> getVLMaintenanceSupplierCategories(List<String> shopCategoryList);
}