package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.CityZipCode;
import com.mikealbert.data.entity.CityZipCodePK;



/**
* DAO 
* @author Lizak
*/

public interface CityZipCodeDAO extends JpaRepository<CityZipCode, CityZipCodePK> {

	@Query("select c from CityZipCode c where c.cityZipCodePK.countryCode = ?1 order by c.cityZipCodePK.zipCode asc, c.zipCodeEnd asc")
	public List<CityZipCode> findCityZipCodesByCountry(String countryCode, Pageable page);
	

	@Query("select c from CityZipCode c where lower(?1) between lower(c.cityZipCodePK.zipCode) and lower(c.zipCodeEnd) order by c.cityZipCodePK.cityCode, c.cityZipCodePK.countyCode asc")
	public List<CityZipCode> findCityZipCodesByZipCode(String zipCode);

	
}
