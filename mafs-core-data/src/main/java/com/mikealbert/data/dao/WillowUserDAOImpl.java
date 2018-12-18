package com.mikealbert.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.vo.WillowUserLovVO;
import com.mikealbert.util.MALUtilities;
import javax.annotation.Resource;

public class WillowUserDAOImpl extends GenericDAOImpl<PersonnelBase, Long> implements WillowUserDAOCustom {

	@Resource
	WillowUserDAO willowUserDAO;

	private static final long serialVersionUID = 8249709217532451721L;

	@Override
	public List<WillowUserLovVO> getWillowUserList(String willowUserNo, String willowUserName, String lovName, Pageable pageable) {
		Query query = null;
		query = generateQueryToGetWillowUserList(willowUserNo, willowUserName, lovName, false);
		
		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = (List<Object[]>)query.getResultList();
		
		return populateWillowUserList(resultList);
	}
	
	public int getWillowUserListCount(String willowUserNo, String willowUserName, String lovName) {
		int recordCount = 0;		
		Query query = generateQueryToGetWillowUserList(willowUserNo, willowUserName, lovName, true);
		Object result = (Object)query.getSingleResult();
		if (result != null) {
			recordCount = Integer.parseInt(String.valueOf(result));
		}
		
		return recordCount;
	}
	
	private Query generateQueryToGetWillowUserList(String inWillowUserNo, String inWillowUserName, String inLovName, Boolean isCountQuery) {
		
		StringBuilder sqlStmt = new StringBuilder("");
		Query query = null;
		
		if(isCountQuery){
			sqlStmt.append("SELECT COUNT(1) ");
		} else {
			sqlStmt.append("SELECT pb.employee_no, pb.first_name, pb.last_name");
		}
		
		sqlStmt.append(" FROM personnel_base pb"
				+ "		WHERE 1 = 1");
		
		Map<String,Object> parameterMap = new HashMap<String,Object>();
		
		if (!MALUtilities.isEmpty(inWillowUserNo)) {
			parameterMap.put("willowUserNo", inWillowUserNo);
			sqlStmt.append(" AND pb.employee_no = :willowUserNo");
		} else {
			if (!MALUtilities.isEmpty(inWillowUserName)) {
				String lastWillowUserName =  null;
				String firstWillowUserName =  null;
				
				String[] nameArray= inWillowUserName.split(",");			
				lastWillowUserName = inWillowUserName.split(",")[0];
				if(nameArray.length > 1){			
					firstWillowUserName =  inWillowUserName.split(",")[1];
				}
				
				if(!MALUtilities.isEmpty(lastWillowUserName)){
					
					sqlStmt.append(" AND UPPER(pb.last_name) LIKE :lastWillowUserName ");
					
					if(!MALUtilities.isEmpty(firstWillowUserName)){				
						parameterMap.put("lastWillowUserName", lastWillowUserName.toUpperCase());
					}else {
						parameterMap.put("lastWillowUserName", DataUtilities.appendWildCardToRight(lastWillowUserName.toUpperCase()));
					}		
				}
				
				if(!MALUtilities.isEmpty(firstWillowUserName)){
					sqlStmt.append(" AND UPPER(pb.first_name) LIKE :firstWillowUserName ");			
					parameterMap.put("firstWillowUserName", DataUtilities.appendWildCardToRight(firstWillowUserName.trim().toUpperCase()));
				}
			}
		}
		
		if (!MALUtilities.isEmpty(inLovName)) {
			if (inLovName.equals("requestorLOV")) {
				sqlStmt.append(" AND pb.employee_no IN (SELECT DISTINCT submitted_by FROM quote_requests)");
			} else if (inLovName.equals("assignedToLOV")) {
				sqlStmt.append(" AND pb.employee_no IN (SELECT DISTINCT assigned_to FROM quote_requests)");
			} else if (inLovName.equals("quoteRequestAssignedToLOV")) {
				sqlStmt.append(" AND pb.employee_no IN (SELECT uc.employee_no" +
							   "                          FROM permission_sets prs, work_class_permissions wps, user_contexts uc" +
							   "                         WHERE permission_set in ('MANAGE_QUOTE_REQUEST', 'EDIT_QUOTE_REQUEST')" +
							   "						   AND prs.prs_id = wps.prs_prs_id" +
							   "						   AND uc.work_class = wps.work_class)");
			}
		}
		
		sqlStmt.append(" ORDER BY pb.employee_no ASC");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		for (String paramName : parameterMap.keySet()) {
			query.setParameter(paramName, parameterMap.get(paramName));
		} 

		return query;
	}
	
	private List<WillowUserLovVO> populateWillowUserList(List<Object[]> resultList) {
		List<WillowUserLovVO> willowUserList = new ArrayList<WillowUserLovVO>();
		
		for (Object[] object : resultList) {
			int i = 0;
			WillowUserLovVO willowUser = new WillowUserLovVO();
			
			willowUser.setEmployeeNo(object[i] 		!= null ? (String) object[i] : null);
			willowUser.setFirstName(object[i+=1] 	!= null ? (String) object[i] : null);
			willowUser.setLastName(object[i+=1] 	!= null ? (String) object[i] : null);
			
			willowUserList.add(willowUser);
		}
		
		return willowUserList;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<PersonnelBase> getDebitCreditApproverList(){
		List<PersonnelBase> debitCreditApproversList = new ArrayList<PersonnelBase>();
		
		String stmt = "SELECT DISTINCT UPPER(pb.first_name), UPPER(pb.last_name), pb.employee_no " + 
		        	" FROM willow2k.user_contexts uc," +
		                 " vision.work_class_permissions wcp, " +
		                 " vision.permission_sets  ps, " +
		                 " willow2k.PERSONNEL_BASE pb" +
	                 " WHERE 1=1" +
			             " AND ps.permission_set IN ('DCMEMO_APPROVER','DCMEMO_PROCESSOR')" +
			             " AND ps.prs_id = wcp.prs_prs_id" +
			             " AND wcp.work_class = uc.work_class" +
			             " AND pb.employee_no = uc.employee_no" +
			             " AND uc.c_id = 1" +
			             " Order By 1";

		Query query = entityManager.createNativeQuery(stmt);
		List<Object[]> resultList = query.getResultList();
		for(Object[] record : resultList){					
			int i = 0;
			PersonnelBase personnelBase = new PersonnelBase();	
			personnelBase.setFirstName((String)record[i]);
			personnelBase.setLastName((String)record[i+=1]);
			personnelBase.setEmployeeNo((String)record[i+=1]);
			debitCreditApproversList.add(personnelBase);
		}
		
		return debitCreditApproversList;
	}

	@Override
	public WillowUserLovVO getWillowUserByAdUserName(String adUsername) {
		
		Query query = null;
		
		query = entityManager.createNativeQuery("SELECT employee_no FROM willow2k.user_contexts where lower(ad_username) =  lower(:adUsername)");
		query.setParameter("adUsername", adUsername);
		
		@SuppressWarnings("rawtypes")
		List results = query.getResultList();
        if (results.isEmpty()){
        	return null;
        }else{
        	
			WillowUserLovVO vo = new WillowUserLovVO();
			vo.setEmployeeNo((String)results.get(0));
			return vo;
        }
	}
}
