package com.mikealbert.vision.specs.fleet;

import java.util.List;
import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;

import com.mikealbert.data.dao.VehicleSearchDAO;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.VehicleSearchService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.MALUtilities;

public class VehicleSearchTest extends BaseSpec {
		
	@Resource VehicleSearchService vehicleService;
	
	public boolean testVehicleSearchByUnitNo(String searchCriteria) {
		
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		
		vehicleSearchCriteria.setUnitNo(searchCriteria);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		try {
			PageRequest page = new PageRequest(0,10);
			vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
			return vehicleSearchResult.size() > 0 ? true : false;
			
		} catch (Exception malEx) {
			return false;
		}
	}
	
public boolean testVehicleSearchByActiveDrvLastName(String searchCriteria) {
		
	List<VehicleSearchResultVO> vehicleSearchResult;
	VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
	
	vehicleSearchCriteria.setDriverName(searchCriteria);
	vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);	
	
	try {
		PageRequest page = new PageRequest(0,10);
		vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
		if (vehicleSearchResult.size() > 0) {
			for (VehicleSearchResultVO vehicleSearchResultVO : vehicleSearchResult) {
				if (vehicleSearchResultVO.isDriverActive()) {
					return true;
				}
			}
		}
		
		return false;
		
		} catch (Exception malEx) {
			return false;
		}
	}

public boolean testVehicleSearchByInActiveDrvLastName(String searchCriteria) {
	
	List<VehicleSearchResultVO> vehicleSearchResult;
	VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
	
	vehicleSearchCriteria.setDriverName(searchCriteria);
	vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);	
	
	try {
		PageRequest page = new PageRequest(0,10);
		vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
		if (vehicleSearchResult.size() > 0) {
			for (VehicleSearchResultVO vehicleSearchResultVO : vehicleSearchResult) {
				if (vehicleSearchResultVO.isDriverActive()) {
					return true;
				}
			}
		}
		return false;
		
	} catch (Exception malEx) {
		return false;
	}
}

public boolean testSearchByVIN(String vin) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setVIN(vin);	
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	boolean results = false;
	for (VehicleSearchResultVO vehicleSearchResultVO : vehicleSearchResultsVOList){
		if(vehicleSearchResultVO.getVIN().contains(vin)){
			results = true;
			break;
		}
	}		
	return results;		
}

public boolean testSearchByLicensePlateNumber(String licensePlateNo) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);	
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);		
	
	if(vehicleSearchResultsVOList != null && vehicleSearchResultsVOList.size() > 0){
		return true;
	}else{
		return false;
	}
}

public boolean testSearchByFleetReferenceNumber(String fleetReferenceNo) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetReferenceNo);	
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);		
	
	if(vehicleSearchResultsVOList != null && vehicleSearchResultsVOList.size() > 0){
		return true;
	}else{
		return false;
	}
}

public boolean testSearchByPONumber(String PoNumber) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setPurchaseOrderNumber(PoNumber);	
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);		
	
	if(vehicleSearchResultsVOList != null && vehicleSearchResultsVOList.size() > 0){
		return true;
	}else{
		return false;
	}
}

public boolean testSearchByMAFSInvoiceNumber(String invoiceNo) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setInternalnvoiceNumber(invoiceNo);	
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);		
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);		
	
	if(vehicleSearchResultsVOList != null && vehicleSearchResultsVOList.size() > 0){
		return true;
	}else{
		return false;
	}
}

public boolean testVehicleSearchByFleetRefLicPlateNoVIN(String fleetRefNo, String licensePlateNo, String vin) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setVIN(vin);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);		
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public boolean testVehicleSearchByFleetRefLicPlateNoDrvName(String fleetRefNo, String licensePlateNo, String driverName) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setDriverName(driverName);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);		
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public boolean testVehicleSearchByFleetRefLicPlateNoVINDrvName(String fleetRefNo, String licensePlateNo, String vin, String driverName) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setVIN(vin);
	vehicleSearchCriteriaVO.setDriverName(driverName);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public boolean testVehicleSearchByAccountFleetRefLicPlateNoVINDrvName(String accountName, String fleetRefNo, String licensePlateNo, String vin, String driverName) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientAccountName(accountName);
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setVIN(vin);
	vehicleSearchCriteriaVO.setDriverName(driverName);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public boolean testVehicleSearchByAccountFleetRefLicPlateNoVIN(String accountName, String fleetRefNo, String licensePlateNo, String vin) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientAccountName(accountName);
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setVIN(vin);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);	
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public boolean testVehicleSearchByAccountFleetRefLicPlateNoVINUnitDrvName(String accountNumber, String fleetRefNo, String licensePlateNo, String vin, String unitNo, String driverName) throws MalBusinessException{		
	VehicleSearchCriteriaVO vehicleSearchCriteriaVO = new VehicleSearchCriteriaVO();
	vehicleSearchCriteriaVO.setClientAccountNumber(accountNumber);
	vehicleSearchCriteriaVO.setClientFleetReferenceNumber(fleetRefNo);
	vehicleSearchCriteriaVO.setLicensePlateNo(licensePlateNo);
	vehicleSearchCriteriaVO.setVIN(vin);
	vehicleSearchCriteriaVO.setUnitNo(unitNo);
	vehicleSearchCriteriaVO.setDriverName(driverName);
	vehicleSearchCriteriaVO.setCorporateEntity(CorporateEntity.MAL);
	vehicleSearchCriteriaVO.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	PageRequest page = new PageRequest(0,10);
	List<VehicleSearchResultVO> vehicleSearchResultsVOList = vehicleService.findBySearchCriteria(vehicleSearchCriteriaVO, page, null);
	
	return vehicleSearchResultsVOList.size() > 0 ? true : false;
}

public VehicleStatusVO testVehicleSearchResultsStatus(String unitNumber, String status) throws MalBusinessException{		
	VehicleStatusVO retVO = new VehicleStatusVO();
	
	List<VehicleSearchResultVO> vehicleSearchResult;
	VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
	
	vehicleSearchCriteria.setUnitNo(unitNumber);
	vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
	if(status.equalsIgnoreCase("ACTIVE")){
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE);
	}else if(status.equalsIgnoreCase("INACTIVE")){
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_INACTIVE);
	}else {
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
	}

	try {
		PageRequest page = new PageRequest(0,10);
		vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
		retVO.setVehicleStatus(vehicleSearchResult.get(0).getUnitStatus());
		retVO.setVehicleStatusDate(MALUtilities.getNullSafeDatetoString(vehicleSearchResult.get(0).getUnitStatusDate()));
		
		return retVO;
		
		
	} catch (Exception malEx) {
		return retVO;
	}
}
	/**
	 * Used to Test that the Vehicle Search Results include an indicator indicating that
	 * at least one PO is open for the selected unit; This method expects only one unit to return
	 * @param searchCriteria Criteria that will return only one unit
	 * @return True if only one unit is returned and it has at least one open PO
	 */
	public boolean testVehicleSearchResultOpenPO(String searchCriteria) {
		
		List<VehicleSearchResultVO> vehicleSearchResult;
		VehicleSearchCriteriaVO vehicleSearchCriteria = new VehicleSearchCriteriaVO();
		
		vehicleSearchCriteria.setUnitNo(searchCriteria);
		vehicleSearchCriteria.setCorporateEntity(CorporateEntity.MAL);
		vehicleSearchCriteria.setUnitStatus(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_BOTH);
		
		try {
			PageRequest page = new PageRequest(0,10);
			vehicleSearchResult = vehicleService.findBySearchCriteria(vehicleSearchCriteria, page, null);
			//expects only one unit to return in search results
			if(vehicleSearchResult.size() == 1 && vehicleSearchResult.get(0).getNumOfOpenMaintPOs() > 0){
				return true;
			}else{
				return false;
			}
			
		} catch (Exception malEx) {
			return false;
		}
	}

}
