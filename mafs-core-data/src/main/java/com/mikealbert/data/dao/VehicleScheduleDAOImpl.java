package com.mikealbert.data.dao;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.VehicleSchedule;

public class VehicleScheduleDAOImpl extends GenericDAOImpl<VehicleSchedule,Long> implements VehicleScheduleDAOCustom{
	@Resource
	private VehicleScheduleDAO vehicleScheduleDAO;
	
	private static final long serialVersionUID = 1L;

	public BigDecimal	getNextVehicleScheduleSequence(){
		String queryStr = "select WILLOW2K.VEHICLE_SCH_SEQ.NEXTVAL FROM DUAL";
		Query  query = entityManager.createNativeQuery(queryStr);
		return (BigDecimal)query.getSingleResult();
	}
	
}
