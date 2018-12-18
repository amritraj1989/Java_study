package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.GlCodePK;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.data.vo.GlCodeProcParamsVO;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class GlCodeDAOImpl extends GenericDAOImpl<GlCode, Long> implements GlCodeDAOCustom {

	@Resource
	private GlCodeDAO glCodeDAO;
	
	private static final long serialVersionUID = -7069644411106344199L;
	
	@Resource DataSource dataSource;

	@Override
	public List<GlCodeLOVVO> findByCodeAndCorpId(String code, Long corpId, Pageable pageable) {
		List<GlCodeLOVVO> glCodeList = new ArrayList<GlCodeLOVVO>();
		
		Query query = generateGlCodeQuery(false, code, corpId);

		if(pageable != null){
			query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
			query.setMaxResults(pageable.getPageSize());
		}

		List<Object[]>resultList = (List<Object[]>)query.getResultList();
		if(resultList != null){
			for(Object[] record : resultList){
				int i = 0;

				GlCodeLOVVO glCodeLOVVO = new GlCodeLOVVO();
				glCodeLOVVO.setCode((String)record[i]);
				glCodeLOVVO.setDescription((String)record[i+=1]);
				glCodeLOVVO.setStatus((String)record[i+=1]);

				glCodeList.add(glCodeLOVVO);
			}
		}
							
		return glCodeList;
	}
	
	public int findByCodeAndCorpIdCount(String code, Long corpId) {
		
		Query countQuery = generateGlCodeQuery(true, code, corpId);
		BigDecimal count = (BigDecimal) countQuery.getSingleResult();
		
		return count.intValue();
	}
	
	@Override
	public List<GlCode> findGlCodeByDist(List<Dist> glDistList) {
		List<GlCode> glCodeList = new ArrayList<GlCode>();
		List<Long> corpIds = new ArrayList<Long>();
		List<String> glCodes = new ArrayList<String>();
		
		if(glDistList != null && !glDistList.isEmpty()){
			for(Dist dist : glDistList){
				corpIds.add(dist.getCorpId());
				glCodes.add(dist.getGlCode());
			}
		
			Query query = generateGlCodeFromDistQuery(corpIds, glCodes);
	
			List<Object[]>resultList = (List<Object[]>)query.getResultList();
			if(resultList != null){
				for(Object[] record : resultList){
					int i = 0;
	
					GlCode glCode = new GlCode();
					GlCodePK glCodePK = new GlCodePK();
					glCode.setId(glCodePK);
					glCode.getId().setCode((String)record[i]);
					glCode.setDescription((String)record[i+=1]);
					glCode.getId().setCorpId(((BigDecimal)record[i+=1]).longValue());
	
					glCodeList.add(glCode);
				}
			}
		}
							
		return glCodeList;
	}
	
	private Query generateGlCodeQuery(boolean isCountQuery, String code, Long corpId) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		if(isCountQuery == false){
			sqlStmt.append("SELECT code, description, status ");
		}else{
			sqlStmt.append("SELECT count(*) ");
		}
		
		sqlStmt.append(" FROM gl_code "
						+" WHERE status = 'O' AND ");
		if(code != null && !code.isEmpty()){
			sqlStmt.append(" 	   code LIKE :code AND ");
		}
		sqlStmt.append(" 	   c_id = :corpId "
						+" ORDER BY code");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());	
		if(code != null && !code.isEmpty()){
			query.setParameter("code", code + '%');
		}
		query.setParameter("corpId", corpId);
		

		return query;
	}

	private Query generateGlCodeFromDistQuery(List<Long> corpIds, List<String> glCodes) {
		Query query =  null;
		StringBuilder sqlStmt = new StringBuilder("");
		
		sqlStmt.append("SELECT code, description, c_id "
						+" FROM gl_code "
						+" WHERE code IN (:glCodes) AND "
						+"	     c_id IN (:corpIds)");
		
		query = entityManager.createNativeQuery(sqlStmt.toString());
		query.setParameter("glCodes", glCodes);
		query.setParameter("corpIds", corpIds);
		

		return query;
	}
	
	public String findGlCodeByProc(GlCodeProcParamsVO glCodeProcParamsVO) throws MalException {		

		Connection connection = null;
		CallableStatement callableStatement = null;
		String glCode;
		String errorMsg;

		connection = DataSourceUtils.getConnection(dataSource);

		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ call WILLOW2K.FL_BILL.FL_GET_GL_CODE(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");
			
			callableStatement.setLong(1,glCodeProcParamsVO.getcId());								
			callableStatement.setString(2,glCodeProcParamsVO.getDocumentType());					
			callableStatement.setString(3,glCodeProcParamsVO.getTransType());						
			callableStatement.setString(4,glCodeProcParamsVO.getSourceCode());						
			callableStatement.setString(5,null);													
			callableStatement.setString(6,glCodeProcParamsVO.getAnalysisCode());					
			callableStatement.setNull(7,Types.INTEGER);
			callableStatement.setNull(8,Types.INTEGER);
			callableStatement.registerOutParameter(9,Types.VARCHAR);								
			callableStatement.setString(10,null);																			 
			callableStatement.registerOutParameter(11,Types.VARCHAR);								
			callableStatement.registerOutParameter(12,Types.VARCHAR);								
			callableStatement.registerOutParameter(13,Types.VARCHAR);								
			callableStatement.setString(14,glCodeProcParamsVO.getAnalysisCategory());				
			
			callableStatement.execute();
			
			errorMsg = callableStatement.getString(13);
			glCode = callableStatement.getString(9);
			
			if (!MALUtilities.isEmpty(errorMsg)) {
				throw new MalException(errorMsg);
			}
			
			return glCode;
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	
	}	
	
}
