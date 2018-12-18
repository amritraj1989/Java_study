package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.DriverAllocation;
import com.mikealbert.data.vo.DriverAllocationVO;
import com.mikealbert.util.MALUtilities;

public class DriverAllocationDAOImpl extends GenericDAOImpl<DriverAllocation, Long> implements DriverAllocationDAOCustom{
	
	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unchecked")
	public List<DriverAllocation> getCurrentDriverAllocationByDrvId(long drvId){
		String queryStr = "select * from driver_allocations where drv_drv_id =:drvId  and (to_date is null or to_date > sysdate) and  from_date <= sysdate";
		
		Query query = entityManager.createNativeQuery(queryStr, DriverAllocation.class);
		query.setParameter("drvId", drvId);
		List<DriverAllocation> allocationList = query.getResultList();
		
		return allocationList;
	}

	@Override
	public List<DriverAllocationVO> getCurrentDriverAllocationVOs(Long drvId) {
		return this.getDriverAllocationVOs(drvId, true);
	}
	
	@Override
	public List<DriverAllocationVO> getPreviousDriverAllocationVOs(Long drvId) {
		return this.getDriverAllocationVOs(drvId, false);
	}
	
	private List<DriverAllocationVO> getDriverAllocationVOs(Long drvId, boolean current) {
		List<DriverAllocationVO> driverAllocationList	= new ArrayList<DriverAllocationVO>();
		
		Query query;
		if(current) {
			query = generateCurrentAllocationQuery(drvId);
		}else {
			query = generatePreviousAllocationQuery(drvId);
		}
		
		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;
				DriverAllocationVO driverAllocationVO = new DriverAllocationVO();				
				driverAllocationVO.setDrvId(record[i] != null ? ((BigDecimal)record[i]).longValue() : null);
				driverAllocationVO.setFromDate(this.formattedOrNullDate((Date)record[i+=1]));
				driverAllocationVO.setToDate(this.formattedOrNullDate((Date)record[i+=1]));				
				driverAllocationVO.setFmsId(record[i+=1] != null ? ((BigDecimal)record[i]).longValue() : null);
				driverAllocationVO.setUnitNo((String)record[i+=1]);
				
				driverAllocationList.add(driverAllocationVO);
			}				
		}

		return driverAllocationList;
	}


	
	private Query generateCurrentAllocationQuery(Long drvId) {
		
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("select da.drv_drv_id, da.from_date, da.to_date, fm.fms_id, fm.unit_no from driver_allocations da "
				+ "INNER join fleet_masters fm ON da.fms_fms_id = fm.fms_id "
				+ "where drv_drv_id =:drvId  "
				+ "and (to_date is null or to_date > sysdate) "
				+ "and  from_date <= sysdate");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("drvId", drvId);
		
		return query;
	}
	
	private Query generatePreviousAllocationQuery(Long drvId) {
		
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("select da.drv_drv_id, da.from_date, da.to_date, fm.fms_id, fm.unit_no from driver_allocations da "
				+ "INNER join fleet_masters fm ON da.fms_fms_id = fm.fms_id "
				+ "where drv_drv_id =:drvId  "
				+ "and to_date is not null and to_date <= sysdate ");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("drvId", drvId);
		
		return query;
	}
	
	private Date formattedOrNullDate(Date date) {
		if(date !=null) {
			return MALUtilities.clearTimeFromDate(date);
		}else {
			return date;
		}
	}
}
