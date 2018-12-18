package com.mikealbert.service;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.DocPropertyValue;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.DocPropertyEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;
import com.mikealbert.service.DriverService;

public class DocPropertyValueServiceTest extends BaseTest {

	@Resource DocPropertyValueService docPropertyValueService;
	
	static final Long DOC_ID = 5077003L;  
	static final String DOC_PROPERTY_CONFIRM_DOC_DATE = "CONFIRM_DOC_EMAIL_DATE";  	

	
	@Test
	public void testSaveOrUpdateDocumentPropertyValue(){
		DocPropertyValue propValue;
		docPropertyValueService.saveOrUpdateDocumentPropertyValue(DOC_ID, DocPropertyEnum.CONFIRM_DOC_DATE, Calendar.getInstance().getTime().toString());
		propValue = docPropertyValueService.findByNameDocId(DocPropertyEnum.CONFIRM_DOC_DATE, DOC_ID);
		assertTrue("Doc property was not created for doc_id: " + DOC_ID, !MALUtilities.isEmpty(propValue));
	}

}
