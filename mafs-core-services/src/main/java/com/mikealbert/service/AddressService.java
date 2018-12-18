package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.CityZipCode;
import com.mikealbert.data.entity.CountyCodePK;
import com.mikealbert.data.entity.DriverAddress;
/**
* Public Interface implemented by {@link com.mikealbert.vision.service.AddressServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.Address}(es), {@link com.mikealbert.data.entity.CountyCodePK}(s), {@link com.mikealbert.data.entity.CountyCode}(s), {@link com.mikealbert.data.entity.TownCityCodePK}(s), {@link com.mikealbert.data.entity.TownCityCode}(s), {@link com.mikealbert.data.entity.CityZipCode}(s), {@link com.mikealbert.data.entity.Country}(s), {@link com.mikealbert.data.entity.RegionCodePK}(s), {@link com.mikealbert.data.entity.RegionCode}(s), and {@link com.mikealbert.data.entity.CityZipCodePK}(s). 
* 
*  @see com.mikealbert.data.entity.Address
 * @see com.mikealbert.data.entity.CountyCodePK
 * @see com.mikealbert.data.entity.CountyCode
 * @see com.mikealbert.data.entity.TownCityCodePK
 * @see com.mikealbert.data.entity.TownCityCode
 * @see com.mikealbert.data.entity.CityZipCode
 * @see com.mikealbert.data.entity.CityZipCodePK
 * @see com.mikealbert.data.entity.Country
 * @see com.mikealbert.data.entity.RegionCodePK
 * @see com.mikealbert.data.entity.RegionCode
 * @see com.mikealbert.vision.service.AddressServiceImpl
* */
public interface AddressService {

	public List<CityZipCode> getAllCityZipCodesByZipCode(String zipCode);
	
	public String getCountyName(CountyCodePK countyCodePK);
	
	public boolean addressCompareHash(DriverAddress da1, DriverAddress da2);
	
	public boolean addressListCompare(List<DriverAddress> daList, Long drvId);
}
