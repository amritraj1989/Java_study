package com.mikealbert.data.beanvalidation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.mikealbert.data.entity.DriverAddress;

public class MADriverAddressesValidator implements ConstraintValidator<MADriverAddresses, Object> {
    final static String REQUIRED_ADDRESS_TYPE_CODE = "GARAGED";
    
	public void initialize(MADriverAddresses checkdriveraddresses) {}

	@SuppressWarnings("unchecked")
	public boolean isValid(Object obj, ConstraintValidatorContext context) {		    
	        boolean hasRequiredAddressType = false;
	        List<DriverAddress>driverAddresses = (List<DriverAddress>)obj;
     
	        //Verify that an address exist for the required address type
	        for(DriverAddress driverAddress : driverAddresses){
	            if(driverAddress.getAddressType().getAddressType().equals(REQUIRED_ADDRESS_TYPE_CODE)){
	            	hasRequiredAddressType = true;
	                break;
	            }
	        }

	        if(!hasRequiredAddressType)
	            return false;
	        
	        //Verify that there are no duplicate address types.
	        for(int i = 0; i < driverAddresses.size(); i++){
	            for(int j = i + 1; j < driverAddresses.size(); j++){
	                if(driverAddresses.get(i).getAddressType().equals(driverAddresses.get(j).getAddressType())){
	                    return false;
	                }
	            }

	        }

	        return true;
	    }

}
