package com.mikealbert.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.Pageable;

import com.mikealbert.data.dao.QuotationCapitalElementDAO;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.QuotationCapitalElement;
import com.mikealbert.data.entity.SerializedConfig;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.FormalClosedEndQuoteInputVO;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.ws.vehcfg.client.VehicleConfigurationType;

public class AutodataServiceTest extends BaseTest {
	@Resource AutodataService autodataService;
	
	@Test
	public void testSaveOrUpdateSerializedConfig() {
		final String ACODE = "TESTACODE";
		final String XML = "<XML>JUNIT TEST</XML>";
		final String LEVEL = "C4J";
		
		SerializedConfig sc = new SerializedConfig(ACODE, XML, LEVEL);
		sc = autodataService.saveOrUpdateSerializedConfig(sc);
		
		assertNotNull("Serialized config was not saved. ", sc.getId());
	}
	
	@Test
	public void testGetVehicleConfiguration() throws Exception {
		final Long CFG_ID = 134982L;
		VehicleConfigurationType vehcfg = null;
		
		try {
			vehcfg = autodataService.getVehicleConfiguration(CFG_ID);
			assertNotNull("Vehicle configuration was not retrieved", vehcfg);			
		} catch(Exception e) {
			if(!e.getMessage().contains("NOT_DEFINED")) {
				throw e;
			}
		}
	}
	
}
