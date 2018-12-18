package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.AddressTypeCode;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.DriverAddress;
import com.mikealbert.data.entity.DriverAddressHistory;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.PhoneNumber;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.DriverAddressVO;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.DriverSearchVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.StockUnitVO;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.DriverServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.Driver}(s), {@link com.mikealbert.data.entity.DriverAddressHistory}(s), {@link com.mikealbert.data.entity.CostCentreCode}(s), {@link com.mikealbert.data.entity.ExternalAccount}(s), and {@link com.mikealbert.data.entity.DriverGrade}(s).
 *
 * @see com.mikealbert.data.entity.Driver
 * @see com.mikealbert.data.entity.DriverGrade
 * @see com.mikealbert.data.entity.ExternalAccount
 * @see com.mikealbert.data.entity.DriverAddressHistory
 * @see com.mikealbert.data.entity.CostCentreCode
 * @see com.mikealbert.vision.service.DriverServiceImpl
 **/
public interface DriverService {
	public static final String REQUIRED_ADDRESS_TYPE_CODE = "GARAGED";	
	public static final String GARAGED_ADDRESS_TYPE = "GARAGED";
	public static final String POST_ADDRESS_TYPE = "POST";
	
	public List<Driver>               getDrivers(String driverName, List<String> customerAcctCodes, CorporateEntity corpEntity,String activeInd , PageRequest pageReq);
	public List<Long>  findDriverByLastAndFirstNameAndEmailForAccount(String lastName, String firstName,  String email, String accountCode, long contextId);
	public Driver getDriver(Long driverId);
	public Driver getActiveDriver(Long driverId);
	public Driver getActiveDriverForUnit(FleetMaster unit);
	public String getDriverStatus(Long driverId);
	public Driver saveOrUpdateDriver(ExternalAccount externalAccount, DriverGrade driverGrade, Driver driver, List<DriverAddress> addresses, List<PhoneNumber> phoneNumbers, String userName, CostCentreCode driverCostCenter) throws MalBusinessException;	
	public List<AddressTypeCode> getDriverAddressTypeCodes();		
	public String[] splitDriverNameIntoLastAndFirst(String driverName);
	public DriverAddress getDriverAddress(Long drvId, String type);
	public List<DriverAddressHistory> getDriverAddressHistoryListAsc(Long drvId, String type);
	public boolean validCostCenter(Driver driver, CostCentreCode driverCostCenter);
	public List<String> getOnOrderUnitNumbersByDriverId(Long driverId, boolean includeStock);
	public int searchDriverCount(String driverName, Long driverId, String customerName, String customerNo, String unitNo ,String vin, String regNo,String licPlate,boolean showRecentUnits ,String driverActiveInd);	
	public  List<DriverSearchVO> searchDriver(String driverName, Long driverId, String customerName, String customerNo, String unitNo, String vin,String regNo,String licPlate,boolean showRecentUnits,String driverActiveInd , Pageable pageable , Sort sort);
	public int searchLOVDriverCount(String driverName, List<String> acctCodeList,String acctType, String accContextId,boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly);
	public List<DriverLOVVO> searchLOVDriver(String driverName, List<String> acctCodeList,String acctType, String accContextId, boolean fetchOpenAccontOnly , boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly , PageRequest pageReq);
	public List<DriverAddressHistory> getDriverAddressesByType(Long driverId, String type);
	public List<DriverAddressVO> getAddressVOList(List<DriverAddressHistory> addressList);
	public boolean hasActiveIndicatorChanged(Driver driver);
	public boolean isCostCenterCodeAreSame(CostCentreCode  costCentreCode1, CostCentreCode costCentreCode2);
	public DriverAddress getDriverAddress(Long draId);
	public List<DriverInfoVO> searchDriverInfo(String driverName, Long driverId, Long fmsId, ExternalAccountPK externalAccountPK, Pageable pageable , Sort sort);
	public int searchDriverInfoCount(String driverName, ExternalAccountPK externalAccountPK);
	public Driver getDriverWithCurrentAllocation(Long drvId);
	public Driver getDriverExcludeAllocations(Long drvId);
}
