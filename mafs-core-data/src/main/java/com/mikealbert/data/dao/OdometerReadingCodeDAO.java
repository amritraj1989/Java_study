package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.OdometerReadingCode;

/**
* DAO for OdometerReadingCode Entity
* @author sibley
*/

public interface OdometerReadingCodeDAO extends JpaRepository<OdometerReadingCode, String> {}
