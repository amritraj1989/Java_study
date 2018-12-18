package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.FuelUpload;
import com.mikealbert.data.entity.FuelUploadPK;

public interface FuelUploadDAO extends FuelUploadDAOCustom,JpaRepository<FuelUpload, FuelUploadPK>{
	
}
