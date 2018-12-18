package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.AddressTypeCode;

/**
* DAO for AddressTypeCode Entity
* @author sibley
*/

public interface AddressTypeCodeDAO extends JpaRepository<AddressTypeCode, String> {}
