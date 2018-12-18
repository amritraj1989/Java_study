package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.Driver;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.vo.DriverInfoVO;
import com.mikealbert.data.vo.DriverLOVVO;
import com.mikealbert.data.vo.DriverSearchVO;


public interface DriverDAOCustom  {	
	
	public int searchLOVDriverCount(String driverName, List<String> acctCodes,String acctType, String accContextId,boolean fetchOpenAccontOnly ,  boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly);
	public List<DriverLOVVO> searchLOVDriver(String driverName, List<String> acctCodes,String acctType, String accContextId,boolean fetchOpenAccontOnly ,  boolean fetchRelatedDriversAlso,boolean fetchActiveDriverOnly, PageRequest pageReq);
	
	public int searchDriverCount(String driverName, Long driverId, String customerName, String customerNo,String unitNo,String vin, String regNo, String licPlate, boolean showRecentUnits ,String driverActiveInd);
	public  List<DriverSearchVO> searchDriver(String driverName, Long driverId, String customerName, String customerNo,String unitNo,String vin,String regNo, String licPlate, boolean showRecentUnits, String driverActiveInd, Pageable pageable ,Sort sort);
	public String getDriverStatus(Long driverId);
	public List<String> getOnOrderUnitNumbersByDriverId(Long driverId, boolean includeStock);

	public List<DriverInfoVO> searchDriverInfo(String driverName, Long driverId, Long fmsId, ExternalAccountPK externalAccountPK, Pageable pageable , Sort sort);
	public int searchDriverInfoCount(String driverName, ExternalAccountPK externalAccountPK);
}
