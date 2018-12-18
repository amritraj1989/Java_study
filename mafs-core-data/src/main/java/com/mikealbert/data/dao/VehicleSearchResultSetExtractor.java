package com.mikealbert.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.mikealbert.data.enumeration.ActiveVehicleStatus;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;
import com.mikealbert.data.vo.VehicleSearchResultVO;
import com.mikealbert.util.MALUtilities;

public class VehicleSearchResultSetExtractor implements ResultSetExtractor<List<VehicleSearchResultVO>>{
	private Pageable pageable;
	private VehicleSearchCriteriaVO searchCriteria;
	
	public VehicleSearchResultSetExtractor(){}
	
	public VehicleSearchResultSetExtractor(VehicleSearchCriteriaVO searchCriteria, Pageable pageable){
		this.searchCriteria = searchCriteria;
		this.pageable = pageable;
	}
	
	public List<VehicleSearchResultVO> extractData(ResultSet rs) throws SQLException, DataAccessException{
		long pageNumber = pageable.getPageNumber();
		long pageSize = pageable.getPageSize();
		
		long nextPageNumber = pageNumber + 1;
		long recStartPos = pageNumber * pageSize;
		long recEndPos = nextPageNumber * pageSize;
		
		long recordCount = 0l;

		List<VehicleSearchResultVO> vehicleSearchResults = new ArrayList<VehicleSearchResultVO>();
		try{
			while(rs.next()){
					String unitStatus = rs.getString(2);
					
				if(searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE)){
					if(unitStatus.equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription())){
						if(recordCount >= recStartPos && recordCount <= recEndPos){
							vehicleSearchResults.add(buildVehicleSearchResultVO(rs));
						}
						recordCount++;
					}
					
				}else if(searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_INACTIVE)){
					if(!(unitStatus.equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
							|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription()))){
						if(recordCount >= recStartPos && recordCount <= recEndPos){
							vehicleSearchResults.add(buildVehicleSearchResultVO(rs));
						}
						recordCount++;	
					}	
				}else{
					if(recordCount >= recStartPos && recordCount <= recEndPos){
						vehicleSearchResults.add(buildVehicleSearchResultVO(rs));
					}
					recordCount++;	
				}
				// TODO: consider not breaking so we can get a full count with 1 query
				// and refactoring everything to call this one method with a new VO
				// the returns the list and the count
				if(recordCount >= recEndPos){
					break;
				}
				
			}
		}catch(SQLException sqlEx){
			sqlEx.printStackTrace();
		}
		
		
		return vehicleSearchResults;
	}
	
	private VehicleSearchResultVO buildVehicleSearchResultVO(ResultSet rs) throws SQLException{
		
		int i = 1;
		
		VehicleSearchResultVO vehicleSearchResultVO = new VehicleSearchResultVO();				
		vehicleSearchResultVO.setFmsId(rs.getLong(i));
		vehicleSearchResultVO.setUnitStatus(rs.getString(i+=1));				
		vehicleSearchResultVO.setUnitNo(rs.getString(i+=1));
		vehicleSearchResultVO.setClientFleetReferenceNumber(rs.getString(i+=1));
		vehicleSearchResultVO.setVIN(rs.getString(i+=1));
		vehicleSearchResultVO.setLicensePlateNo(rs.getString(i+=1));
		vehicleSearchResultVO.setUnitDescription(rs.getString(i+=1));
		vehicleSearchResultVO.setClientCorpEntity(rs.getLong(i+=1));
		vehicleSearchResultVO.setClientAccountNumber(rs.getString(i+=1));
		vehicleSearchResultVO.setClientAccountName(rs.getString(i+=1));
		vehicleSearchResultVO.setClientAccountType(rs.getString(i+=1));
		vehicleSearchResultVO.setDrvId(rs.getLong(i+=1));
		vehicleSearchResultVO.setDriverStatus(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverActive(MALUtilities.convertYNToBoolean(rs.getString(i+=1)));				
		vehicleSearchResultVO.setDriverPoolManager(MALUtilities.convertYNToBoolean(rs.getString(i+=1)));
		vehicleSearchResultVO.setDriverSurname(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverForeName(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverAddressBusinessIndicator(MALUtilities.convertYNToBoolean(rs.getString(i+=1)));				
		vehicleSearchResultVO.setDriverBusinessAddressLine(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverAddress1(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverAddress2(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverCity(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverState(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverZip(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverEmail(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverAreaCode(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverPhoneNumber(rs.getString(i+=1));
		vehicleSearchResultVO.setDriverPhoneExtension(rs.getString(i+=1));
		
		vehicleSearchResultVO.setContractStartDate(rs.getDate(i+=1));
		vehicleSearchResultVO.setContractActualEndDate(rs.getDate(i+=1));
		vehicleSearchResultVO.setInServiceDate(rs.getDate(i+=1));				
		vehicleSearchResultVO.setContractOutOfServiceDate(rs.getDate(i+=1));	
		
		vehicleSearchResultVO.setMaintenanceRequestId(rs.getLong(i+=1));
		vehicleSearchResultVO.setPurchaseOrderNumber(rs.getString(i+=1));
        vehicleSearchResultVO.setInternalInvoiceNumber(rs.getString(i+=1));                
		vehicleSearchResultVO.setserviceProviderNumber(rs.getString(i+=1));				
		vehicleSearchResultVO.setServiceProviderName(rs.getString(i+=1));
		vehicleSearchResultVO.setServiceProviderInvoiceNumber(rs.getString(i+=1));
		vehicleSearchResultVO.setNumOfOpenMaintPOs(rs.getInt(i+=1));
		
		return vehicleSearchResultVO;
	}
}
