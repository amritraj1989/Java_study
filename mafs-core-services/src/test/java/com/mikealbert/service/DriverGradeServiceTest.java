package com.mikealbert.service;

import static org.junit.Assert.*;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.service.CustomerAccountService;
import com.mikealbert.service.DriverGradeService;

public class DriverGradeServiceTest extends BaseTest{
	
	@Value("${generic.externalAccount.customer}")  String customerAccount;
	
	@Resource CustomerAccountService externalAccountService;
	@Resource DriverGradeService driverGradeService;

	@Test
	public void testGetExternalAccountDriverGrades() {

		List<DriverGrade> driverGradeList = driverGradeService
				.getExternalAccountDriverGrades(externalAccountService.getOpenCustomerAccountByCode(customerAccount));

		assertTrue("Zero elements in Driver Grade List",
				driverGradeList.size() > 0);
	}
	
	@Test
	public void testGetDriverGrade() {
		final String GRADE_CODE = "U";
		
		assertNotNull(driverGradeService.getDriverGrade(GRADE_CODE));
	}

}
