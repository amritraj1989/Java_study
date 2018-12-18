package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.MaintenanceCode;
import com.mikealbert.data.entity.MaintenanceRequestTask;
import com.mikealbert.data.vo.HistoricalMaintCatCodeVO;
import com.mikealbert.data.vo.MaintenanceCodeVO;

public class MaintenanceRequestTaskDAOImpl extends GenericDAOImpl<MaintenanceRequestTask, Long> implements MaintenanceRequestTaskDAOCustom {
	@Resource
	private MaintenanceRequestTaskDAO maintenanceRequestTaskDAO;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Retrieve Service History based on a list of fmsIds, maintCatCode, and removing the current mrtId
	 */
	@SuppressWarnings("unchecked")
	public List<HistoricalMaintCatCodeVO> getHistoricalMaintCatCode(List<Long> fmsIds, String maintCatCode, Long mrqId){
		List<HistoricalMaintCatCodeVO> maintenanceServiceHistoryList	= new ArrayList<HistoricalMaintCatCodeVO>();
		
		if(!fmsIds.isEmpty()) {
			Query query = generateHistoricalMaintCatCodeQuery(fmsIds, maintCatCode, mrqId);

			List<Object[]>resultList = (List<Object[]>)query.getResultList();
			if(resultList != null){
				for(Object[] record : resultList){
					int i = 0;

					HistoricalMaintCatCodeVO historicalMaintCatCodeVO = new HistoricalMaintCatCodeVO();				
					historicalMaintCatCodeVO.setMrtId(((BigDecimal)record[i]).longValue());
					historicalMaintCatCodeVO.setMaintCode((String)record[i+=1]);
					historicalMaintCatCodeVO.setVendorCodeDesc((String)record[i+=1]);
					historicalMaintCatCodeVO.setActualStartDate((Date)record[i+=1]);
					historicalMaintCatCodeVO.setOdo(((BigDecimal)record[i+=1]).intValue());
					historicalMaintCatCodeVO.setPoNumber((String)record[i+=1]);

					maintenanceServiceHistoryList.add(historicalMaintCatCodeVO);
				}

			}
		}
							
		return maintenanceServiceHistoryList;
	}
	
	private Query generateHistoricalMaintCatCodeQuery(List<Long> fmsIds, String maintCatCode, Long mrqId) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT MRT.MRT_ID, MRT.MAINT_CODE, MRT.VENDOR_CODE_DESC, MR.ACTUAL_START_DATE, MR.CURRENT_ODO, MR.JOB_NO"
				+"		FROM maintenance_requests mr, maintenance_request_tasks mrt"
				+"		WHERE MR.MRQ_ID = MRT.MRQ_MRQ_ID AND"
				+"		    MRT.MCG_MAINT_CAT_CODE = :maintCatCode"
				+"			AND MR.FMS_FMS_ID IN :fmsIds");
		if(mrqId != null && mrqId > 0){
			sqlStmt.append(" AND MR.MRQ_ID <> :mrqId");
		}
				
		sqlStmt.append("      ORDER BY MR.ACTUAL_START_DATE DESC, MRT.MAINT_CODE ASC");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("maintCatCode", maintCatCode);
		query.setParameter("fmsIds", fmsIds);
		if(mrqId != null && mrqId > 0){
			query.setParameter("mrqId", mrqId);
		}

		return query;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getDistinctHistoricalCatCodes(List<Long> fmsIds, Long mrqId){
		List<String> distinctMaintCategoriesList	= new ArrayList<String>();
		
		if(!fmsIds.isEmpty()) {		
			Query query = generateDistinctHistoricalMaintCatCodeQuery(fmsIds, mrqId);

			List<Object>resultList = (List<Object>)query.getResultList();
			if(resultList != null){
				for(Object record : resultList){	
					distinctMaintCategoriesList.add(record.toString());
				}			
			}
		}
		
		return distinctMaintCategoriesList;
	}
	/**
	 * Query that returns a distinct list of category codes that have been used in previous
	 * maintenance requests for a specific vin's history; Oil Changes, Tire Services, Misc. Maint,
	 * and Misc. Services will not be included in the returned list
	 * @param fmsIds
	 * @param mrqId
	 * @return Query
	 */
	private Query generateDistinctHistoricalMaintCatCodeQuery(List<Long> fmsIds, Long mrqId) {
		Query query =  null;
		List<String> categoriesToRemove;
		StringBuilder sqlStmt = new StringBuilder("");
		
		categoriesToRemove = getCategoriesToRemove();
		
		sqlStmt.append("SELECT DISTINCT MRT.MCG_MAINT_CAT_CODE"
				+"		FROM maintenance_requests mr, maintenance_request_tasks mrt"
				+"		WHERE MR.MRQ_ID = MRT.MRQ_MRQ_ID"
				+"			AND MR.FMS_FMS_ID IN :fmsIds"
				+" 			AND MRT.MCG_MAINT_CAT_CODE NOT IN :categoriesToRemove");
		if(mrqId != null && mrqId > 0){
			sqlStmt.append(" AND MR.MRQ_ID <> :mrqId");
		}
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		query.setParameter("fmsIds", fmsIds);
		if(mrqId != null && mrqId > 0){
			query.setParameter("mrqId", mrqId);
		}
		query.setParameter("categoriesToRemove", categoriesToRemove);

		return query;
	}
	
	private List<String> getCategoriesToRemove(){
		final String OIL_CHANGE = "OIL_CHANGE";
		final String TIRE_SVCS = "TIRE_SVCS";
		final String MISC_MAINT = "MISC_MAINT";
		final String MISC_SVCS = "MISC_SVCS";
		final String MARKUPNA = "MARKUPNA";
		
		List<String> categoriesToRemove = new ArrayList<String>();
		categoriesToRemove.add(OIL_CHANGE);
		categoriesToRemove.add(TIRE_SVCS);
		categoriesToRemove.add(MISC_MAINT);
		categoriesToRemove.add(MISC_SVCS);
		categoriesToRemove.add(MARKUPNA);
		return categoriesToRemove;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MaintenanceCodeVO> findTasksMaintenanceCodes(List<Long> mrqIdList) {
		Query query =  null;
		List<MaintenanceCodeVO> maintenanceCodes;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT MRT.MAINT_CODE, NVL (MRT.VENDOR_CODE_DESC, MC.MAINT_CODE_DESC) MAINT_CODE_DESC, MRT.MRQ_MRQ_ID"
		        +"  FROM MAINTENANCE_REQUEST_TASKS MRT, MAINTENANCE_CODES MC"
		        +"         WHERE MC.MAINT_CODE = MRT.MAINT_CODE"
		        +"");

		if(mrqIdList != null) {
			sqlStmt.append("   AND MRT.MRQ_MRQ_ID IN (:mrqId)");
		}

		sqlStmt.append(" GROUP BY MRT.MAINT_CODE, NVL (MRT.VENDOR_CODE_DESC, MC.MAINT_CODE_DESC), MRT.MRQ_MRQ_ID");
		sqlStmt.append(" ORDER BY MIN(MRT.MRT_ID)");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		
		if(mrqIdList != null) {
			query.setParameter("mrqId", mrqIdList);
		}
		
		List<Object[]> list = query.getResultList();
		maintenanceCodes = new ArrayList<MaintenanceCodeVO>();
		for (Object[] objects : list) {
			MaintenanceCodeVO maintenanceCodeVO = new MaintenanceCodeVO();
			MaintenanceCode maintenanceCode  = new MaintenanceCode();
			maintenanceCode.setCode((String)(objects[0]));
			maintenanceCode.setDescription((String)(objects[1]));
			maintenanceCodeVO.setMrqId(((BigDecimal)objects[2]).longValue());
			
			maintenanceCodeVO.setMaintenanceCode(maintenanceCode);
			maintenanceCodes.add(maintenanceCodeVO);
		}
		return maintenanceCodes;
	}
}
