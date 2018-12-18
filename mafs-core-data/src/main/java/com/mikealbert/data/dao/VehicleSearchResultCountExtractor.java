package com.mikealbert.data.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.mikealbert.data.enumeration.ActiveVehicleStatus;
import com.mikealbert.data.vo.VehicleSearchCriteriaVO;

public class VehicleSearchResultCountExtractor implements ResultSetExtractor<Integer>{
	
	private VehicleSearchCriteriaVO searchCriteria;
	
	public VehicleSearchResultCountExtractor(){}
	
	public VehicleSearchResultCountExtractor(VehicleSearchCriteriaVO searchCriteria){
		this.searchCriteria = searchCriteria;		
	}
	
	public Integer extractData(ResultSet rs) throws SQLException, DataAccessException{
		int recordCount = 0;		
		try{
			while(rs.next()){
				
					String unitStatus = rs.getString(1);
					
					if(searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_ACTIVE)){
						if(unitStatus.equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription())){
							recordCount++;
						}
						
					}else if(searchCriteria.getUnitStatus().equals(VehicleSearchDAO.VEHICLE_SEARCH_STATUS_INACTIVE)){
						if(!(unitStatus.equals(ActiveVehicleStatus.STATUS_ON_ORDER.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_PENDING_LIVE.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_CONTRACT.getDescription())
								|| unitStatus.equals(ActiveVehicleStatus.STATUS_ON_STOCK.getDescription()))){							
							recordCount++;	
						}	
					}else{						
						recordCount++;	
					}				
			}
		}catch(SQLException sqlEx){
			sqlEx.printStackTrace();
		}
		
		
		return recordCount;
	}
}
