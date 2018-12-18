package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mikealbert.data.dao.CityZipCodeDAO;
import com.mikealbert.data.dao.CountyDAO;
import com.mikealbert.data.dao.DriverAddressDAO;
import com.mikealbert.data.entity.CityZipCode;
import com.mikealbert.data.entity.CountyCodePK;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.exception.MalException;

/**
 * Implementation of {@link com.mikealbert.vision.service.AddressService}
 */
@Service("addressService")
public class AddressServiceImpl implements AddressService {
	@Resource
	CityZipCodeDAO cityZipCodeDAO;

	@Resource
	CountyDAO countyDAO;
	
	@Resource DriverAddressDAO driverAddressDAO;

	/**
	 * Gets all the cities have the same zip code as the provided parameter used in driver add/edit
	 * @param zipCode Zip Code used to search for cities
	 * @return all cities for the input zip code as a list of cityZipCode
	 */
	public List<CityZipCode> getAllCityZipCodesByZipCode(String zipCode) {
		try{
			return cityZipCodeDAO.findCityZipCodesByZipCode(zipCode);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "finding All City Zip Codes by zip code" }, ex);				
		}
	}

	/**
	 * Gets the name of the county based on the input county code
	 * @param countyCodePK County Primary Key value
	 * @return county name as a String
	 */
	public String getCountyName(CountyCodePK countyCodePK) {
		return countyDAO.findById(countyCodePK).orElse(null).getCountyName();
	}
	
	/**
	 * Compares two DriverAddresses, returning true if they are equal
	 * @return Returns true if address1 equals address2
	 */
	public boolean addressCompareHash(DriverAddress da1, DriverAddress da2){
		if(da1.hashCode() != da2.hashCode()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * Compare driver's address list on the UI and the driver's addresses from the
	 * database: 
	 * 1) Compare the two list sizes, if they are not equal return false
	 * 2) Check that all addresses are compared; If they are not all compared, return false
	 * 3) If there is a change in addresses, return false
	 * 4) If no address changes and all addresses were compared, return True
	 * 
	 * @param daList Driver Address List that may have been changed
	 * @param drvId Driver primary key used to retrieve database DriverAddresses
	 * @return True if addresses are equal
	 */
	public boolean addressListCompare(List<DriverAddress> daList, Long drvId){
		List<DriverAddress> initialDriverAddressList = driverAddressDAO.findByDrvId(drvId);	
		boolean compareEqual = false;
		int addressesCompared = 0;
		//For a driver, there will only be one record for each address type in the table
		if(daList.size() == initialDriverAddressList.size()){
			//Loop through driver addresses that exist in database
			for(DriverAddress initDA : initialDriverAddressList){
				//Loop through driver addresses that may have been changed on UI
				for(DriverAddress da : daList){
					//Compare the addresses with the same address type
					if(da.getAddressType().equals(initDA.getAddressType())){
						compareEqual = addressCompareHash(da, initDA);
						addressesCompared++; //increment every time addresses are compared
						if(!compareEqual){
							return false;
						}
					}
				}
			}
		}
		if(addressesCompared != initialDriverAddressList.size()){
			return false;
		}else{
			return true;
		}
	}
}
