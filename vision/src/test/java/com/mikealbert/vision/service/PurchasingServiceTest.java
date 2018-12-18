package com.mikealbert.vision.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.TestQueryConstants;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.MaintenanceRequestDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetNotes;
import com.mikealbert.data.entity.MaintenancePreferenceAccount;
import com.mikealbert.data.entity.MaintenanceRechargeCode;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.entity.RegionCode;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderMaintenanceCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.MaintenanceContactsVO;
import com.mikealbert.data.vo.MaintenancePreferencesVO;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.data.vo.MaintenanceServiceHistoryVO;
import com.mikealbert.data.vo.VehicleInformationVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.DriverService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.MaintenanceCodeService;
import com.mikealbert.service.MaintenanceHistoryService;
import com.mikealbert.service.MaintenanceRequestService;
import com.mikealbert.service.OdometerService;
import com.mikealbert.service.ServiceProviderService;
import com.mikealbert.service.util.email.Email;
import com.mikealbert.testing.BaseTest;
import com.mikealbert.util.MALUtilities;

public class PurchasingServiceTest extends BaseTest{
	
	@Resource VehicleMaintenanceService maintReqService;

	
	@Value("${maintenanceRequest.vin}")		String vin;
	
	final String PO_STATUS_BOOKED_IN = "B";
	final String PO_STATUS_COMPLETED = "C";	
	final String PO_TYPE_DEFAULT = "MAINT";
	final String ODO_UOM_DEFAULT = "MILE";
	final String LINE_CATEGORY_DEFAULT = "MISC_MAINT";
	final String USERNAME = "SETUP";	
	final String SPL_SERVICE_PROVIDER_CODE = "MC-COMDATA";
	final String SPL_PAYEE_NO = "00160419";
	final String taxExemptedCustNo = "00024275";
	final long FMS_ID_WITH_MAINT_PROG = 976078;
	
	@Test
	public void testClientOrderConfirmationEmail(){
	}
	
	
	
}
