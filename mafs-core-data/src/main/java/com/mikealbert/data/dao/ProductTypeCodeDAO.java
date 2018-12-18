package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ProductTypeCode;

/**
* DAO for AccessoryCode Entity
* @author Singh
*/

public interface ProductTypeCodeDAO extends JpaRepository<ProductTypeCode, String> {}
