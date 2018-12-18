package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MakeCountrySuppliers;

public interface MakeCountrySuppliersDAO extends JpaRepository<MakeCountrySuppliers,Long> {
	
	@Query("select mcs from MakeCountrySuppliers mcs where LOWER(mcs.makeCode) = LOWER(?1) AND LOWER(mcs.countryCode) = LOWER(?2) AND mcs.supId = (?3)")
	public MakeCountrySuppliers findByMakeCountrySupplier(String makeCode, String country, Long suppId );

	@Query("select mcs from MakeCountrySuppliers mcs where LOWER(mcs.makeCode) = LOWER(?1) AND LOWER(mcs.countryCode) = LOWER(?2)")
	public MakeCountrySuppliers findByMakeAndCountry(String makeCode, String country);
}
