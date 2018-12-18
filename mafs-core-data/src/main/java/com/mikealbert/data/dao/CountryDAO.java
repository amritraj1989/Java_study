package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.Country;

/**
* DAO for TitleCode Entity
* @author sibley
*/

public interface CountryDAO extends JpaRepository<Country, String> {}
