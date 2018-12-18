package com.mikealbert.data.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.vo.QuotationElementAmountsVO;
import com.mikealbert.exception.MalBusinessException;

public class QuotationElementDAOImpl extends GenericDAOImpl<QuotationElement, Long> implements QuotationElementDAOCustom {
	private static final long serialVersionUID = 1L;
	@Resource
	private QuotationElementDAO quotationElementDAO;
	@Resource
	private DataSource	dataSource;	

	public String findRecalcNeededByLeaseElementId(Long leaseElementId) {
		String stmt = "select RECALC_BASED_ON_FORMULA(?1) from dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, leaseElementId);
		String resultString = (String) query.getSingleResult();

		return resultString;
	}
	
	public QuotationElementAmountsVO callQuotationElementOriginalValues(Long qmdId, Long lelId) throws MalBusinessException {
		
		QuotationElementAmountsVO qeVO = new QuotationElementAmountsVO();
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ call WILLOW2K.GET_STATUS_BASED_RENTAL_AMTS(?,?,?,?,?,?,?) }");
			callableStatement.setLong(1, qmdId);
			callableStatement.setLong(2, lelId);
			callableStatement.registerOutParameter(3, Types.NUMERIC);
			callableStatement.registerOutParameter(4, Types.NUMERIC);
			callableStatement.registerOutParameter(5, Types.NUMERIC);
			callableStatement.registerOutParameter(6, Types.NUMERIC);
			callableStatement.registerOutParameter(7, Types.VARCHAR);
			
			callableStatement.execute();
			
			qeVO.setRentalAmount(callableStatement.getBigDecimal(3));
			qeVO.setOverheadAmount(callableStatement.getBigDecimal(4));
			qeVO.setProfitAmount(callableStatement.getBigDecimal(5));
			qeVO.setElementCost(callableStatement.getBigDecimal(6));
			qeVO.setRentalFound(callableStatement.getString(7));
			
			return qeVO;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
	}

	@Override
	public String findFinanceElementExistByProfileId(Long qprId, Long cId) {
		String stmt = "select VISION.QUOTATION_WRAPPER.GET_MANDATORY_FINANCE_ELE(?1, ?2) from dual";
		Query query = entityManager.createNativeQuery(stmt);
		query.setParameter(1, cId);
		query.setParameter(2, qprId);
		String resultString = (String) query.getSingleResult();

		return resultString;
	}	
	
}
