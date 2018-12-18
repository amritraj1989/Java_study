package com.mikealbert.data.entity;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;

import com.mikealbert.data.entity.TownCityCodePK;

public class TownCityCodePKTest extends EntityTestCaseSetup{

	@Test
	public void nullCheckForCountryCodeField() {
	
		TownCityCodePK townCityCodePK = new TownCityCodePK();
		townCityCodePK.setCountyCode("Hamilton");
		townCityCodePK.setRegionCode("OH");
		townCityCodePK.setTownName("Cincinnati");
		
		Set<ConstraintViolation<TownCityCodePK>> constraintViolations = validator.validate(townCityCodePK);
		assertEquals(2, constraintViolations.size());
		for (ConstraintViolation<TownCityCodePK> constraintViolation : constraintViolations) {
			assertEquals("countryCode",constraintViolation.getPropertyPath().toString());
		}
	}

}
