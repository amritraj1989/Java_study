package com.mikealbert.data.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.aspectj.apache.bcel.generic.Type;
import org.springframework.jdbc.datasource.DataSourceUtils;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import com.mikealbert.data.entity.LeaseElement;
import com.mikealbert.data.vo.MaintenanceProgramVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class MaintenanceProgramDAOImpl extends GenericDAOImpl<LeaseElement, Long> implements MaintenanceProgramDAOCustom {

	/**
	 * Find maintenance programs per vehicle
	 */
	private static final long serialVersionUID = 1L;

	@Resource DataSource dataSource;

	@SuppressWarnings("unchecked")
	public List<MaintenanceProgramVO> getMaintenanceProgramsByQmdId(Long qmdId, Long cId, String accountType, String accountCode) {
		List<MaintenanceProgramVO> maintenanceProgramList	= new ArrayList<MaintenanceProgramVO>();
		Query query = generateMaintenanceProgramByQmdIdDataQuery(qmdId, cId, accountType, accountCode);

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				MaintenanceProgramVO maintenanceProgramVO = new MaintenanceProgramVO();				
				maintenanceProgramVO.setElementType((String)record[i]);
				maintenanceProgramVO.setElementName((String)record[i+=1]);
				maintenanceProgramVO.setElementDescription((String)record[i+=1]);
				maintenanceProgramVO.setSpecialInstructions((String)record[i+=1]);

				maintenanceProgramList.add(maintenanceProgramVO);
			}				
		}

		return maintenanceProgramList;
	}

	public Query generateMaintenanceProgramByQmdIdDataQuery(Long qmdId, Long cId, String accountType, String accountCode) {

		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("SELECT " +
				" le.element_type, le.element_name, le.description, mpn.spec_instr" +
				" FROM lease_elements le, maint_pref_notes mpn" +
				" WHERE mpn.lel_lel_id IS NOT NULL" +
				" AND mpn.lel_lel_id = le.lel_id" +
				" AND mpn.mpa_mpa_id = (SELECT mpa_id" +
				" FROM maint_pref_acct" +
				" WHERE ea_c_id = :cId" +
				" AND ea_account_type = :accountType" +
				" AND ea_account_code = :accountCode" +
				" AND active_to_date is null)" +
				" AND  mpn.lel_lel_id IN (" +
				"SELECT lel_lel_id" +
				" FROM  informal_amendments" +
				" WHERE add_remove ='A'" +
				" AND qmd_qmd_id = :qmdId" +
				" UNION" +
				" SELECT lel_lel_id" +
				" FROM quotation_elements" +
				" WHERE qmd_qmd_id = :qmdId)" +
				" ORDER BY mpn.seq NULLS LAST");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("qmdId", qmdId);
		query.setParameter("cId", cId);
		query.setParameter("accountType", accountType);
		query.setParameter("accountCode", accountCode);

		return query;
	}

	//HD-419
	public int getleaseElementbyFmdId(Long qmdId) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		/*sqlStmt.append("SELECT count(cle.lel_lel_id) "+
				"FROM contract_lines cln "+
				"JOIN quotation_models qmd ON (cln.qmd_qmd_id=qmd.qmd_id) "+
				"JOIN quotation_elements qel ON (qel.qmd_qmd_id = qmd.qmd_id) "+
				"JOIN csc_lease_elements cle ON (cle.lel_lel_id = qel.lel_lel_id) "+
				"WHERE cln.fms_fms_id = :fmsId"+
				"  AND (NVL(cln.actual_end_date,SYSDATE+1) > SYSDATE");*/
		sqlStmt.append("select count(cle.lel_lel_id) "+
              "from quotation_elements que, csc_lease_elements cle "+
              "where que.qmd_qmd_id=:qmdId "+
              "and cle.lel_lel_id = que.lel_lel_id");


		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("qmdId", qmdId);

		int count = ((BigDecimal) query.getSingleResult()).intValue();	
		if (MALUtilities.isEmpty(count)) {
			count = 0;
		}
		return count;
	}

	public String findElementOnQuote(Long qmdId,Long lelId){
		String resultString;
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call WILLOW2K.FL_GENERAL.is_element_on_quote(?,?,?,?,?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, qmdId);
			callableStatement.setLong(3,lelId);
			callableStatement.setNull(4, Types.NULL);
			callableStatement.setNull(5, Types.NULL);
			callableStatement.setNull(6, Types.NULL);
			callableStatement.setNull(7, Types.NULL);
			callableStatement.setNull(8, Types.NULL);
			//callableStatement.setTimestamp(7, new java.sql.Timestamp(new java.util.Date().getTime()));
			//callableStatement.setString(8, "Y");

			callableStatement.execute();
			resultString = callableStatement.getString(1);

		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
		finally{	
			try {
				if(callableStatement!=null){
				callableStatement.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		return resultString;

	}

	@Override
	public boolean validationCheckForInformalUnit(Long qmdId) {
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("select count(*) "+
				"FROM contract_lines cln "+
				"WHERE cln.qmd_qmd_id = :qmdId "+
				"AND add_months(cln.end_date, -ut.get_willow_config(NULL,'MNTHS_TO_CONTRACT_CHANGE')) < Sysdate");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("qmdId", qmdId);
		int count = ((BigDecimal) query.getSingleResult()).intValue();	
		if(count>0){
			return true;
		}

		return false;

	}

	public Long getContractLinesfromfmsId(Long fmsId){
		Long clnId=null;
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call WILLOW2K.FL_CONTRACT.GET_CONTRACT_LINE(?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, fmsId);
			callableStatement.setNull(3, Types.NULL);

			callableStatement.execute();
			clnId = callableStatement.getLong(1);

			if(clnId==0){
				return null;
			}
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
		finally{
			try {
				if(callableStatement!=null){
					callableStatement.close();
				}
			
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return clnId;

	}

	public Long getQmdIdfromClnId(Long clnId) {
		Query query = null;
		Long result=null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("SELECT cln.qmd_qmd_id "+
				"FROM contract_lines cln, quotation_models qm "+
				"WHERE cln.cln_id = :clnId "+
				"AND qm.qmd_id = cln.qmd_qmd_id");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("clnId", clnId);

		try{
			Object obj = query.getSingleResult();
			if(obj !=null){
				result = ((BigDecimal) obj).longValue();
			} 
			return result;
		}
		catch(NoResultException e){
			e.printStackTrace();
		}
		return result;

	}

	@SuppressWarnings("finally")
	public Long getClnIdforDisposedUnit(Long fmsId){
		Long qmdId = null;
		Query query = null;
		StringBuilder sqlStmt = new StringBuilder("");
		/*
		sqlStmt.append("SELECT cln_id "+
				"FROM contract_lines "+
				"WHERE fms_fms_id = :fmsId "+
				"AND start_date IS NULL "+
				"AND add_months(in_serv_date, -ut.get_willow_config(NULL,'MNTHS_TO_CONTRACT_CHANGE')) < Sysdate "+
				"UNION ALL "+
				"SELECT cln_id "+
				"FROM contract_lines "+
				"WHERE fms_fms_id = :fmsId "+
				"AND start_date IS NULL "+
				"AND in_serv_date is NULL");*/

		sqlStmt.append("select cln_id from contract_lines "+
				"where fms_fms_id = :fmsId "+ 
				"and NVL(contract_lines.start_date, sysdate+1) >= sysdate "+
				"and add_months(in_serv_date, -ut.get_willow_config(NULL,'MNTHS_TO_CONTRACT_CHANGE')) <= Sysdate "+ 
				"Union all "+
				"select cln_id from contract_lines "+
				"where fms_fms_id = :fmsId "+ 
				"and start_date is null "+
				"and in_serv_date is null "+
				"Union all "+ 
				"select cln_id from contract_lines "+
				"where fms_fms_id = :fmsId "+ 
				"and NVL(contract_lines.start_date, sysdate+1) >= sysdate "+
				"and add_months(in_serv_date, -ut.get_willow_config(NULL,'MNTHS_TO_CONTRACT_CHANGE')) >=Sysdate");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("fmsId", fmsId);

		try{
			Object obj = query.getSingleResult();
			if(obj !=null){
				qmdId = ((BigDecimal) obj).longValue();
			} 
			return qmdId;
		}
		catch(NoResultException e){
			//e.printStackTrace();
			System.out.println("Result is null"); //for Disposed Unit if qmdId is null it will skip this step.
			return qmdId;
		}
		catch(Exception e){
			throw new MalException(e.getMessage());
		}
		finally{ 
			if(qmdId==null){
				return 0l;
			}
			else{
				return qmdId;
			}
		}
	}
	public Long getClnIdforReleaseUnit(Long fmsId){
		Query query = null;
		Long result=null;
		StringBuilder sqlStmt = new StringBuilder("");

		sqlStmt.append("select distinct(cln.cln_id) "+
				"from contract_lines cln "+
				"where cln.fms_fms_id = :fmsId "+
				"and NVL(cln.start_date, sysdate+1) > sysdate "+
				"and cln.in_serv_date is not null "+
				"and cln.rev_no = (select max(cln1.rev_no) "+
				"from contract_lines cln1 where cln1.con_con_id = cln.con_con_id)");

		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("fmsId", fmsId);

		try{
			Object obj = query.getSingleResult();
			if(obj !=null){
				result = ((BigDecimal) obj).longValue();
			} 
			return result;
		}
		catch(NoResultException e){
			System.out.println("Result is null"); //for Re-lease Unit if cln_id is null it will skip this step.
			//e.printStackTrace();
		}
		catch(NonUniqueResultException nure){
			throw new MalException(nure.getMessage());
		}
		
		return result;
	}
}
