package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import javax.annotation.Resource;
import javax.persistence.Query;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import com.mikealbert.data.entity.Quotation;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
* This class will be used to perform database transactions via the EntityManager.
* @author sibley
*/
public class QuotationDAOImpl extends GenericDAOImpl<Quotation, Long> implements QuotationDAOCustom {
	@Resource DataSource dataSource;
	@Resource QuotationDAO quotationDAO;
	
	private static final long serialVersionUID = 3887041763030256618L;
	

	public Long generateId() {
		String stmt;
		Long quoId;
		Query query;
		
		stmt = "SELECT quo_seq.nextval FROM DUAL";
		
		query = entityManager.createNativeQuery(stmt.toString());
		quoId = ((BigDecimal) query.getSingleResult()).longValue();		
		
		return quoId;
	}
	
	public Long createQuotation(Long cId, String accountCode, Long qprId, String driverGradeGroupCode, Long term, Long distance, Long cfgId, 
			String orderTypeCode, String unitNo,  Long odoReading, String employeeNo, Long desiredQuoId) throws MalException {
		Long quoId;
		Connection connection;
		CallableStatement callableStatement;
		String stmt;
		
		if(MALUtilities.isEmpty(desiredQuoId)) {
			stmt = "{ ? = call QUOTATION_WRAPPER.CREATE_QUOTE(?,?,?,?,?,?,?,?,?,?,?) } ";
		} else {
			stmt = "{ ? = call QUOTATION_WRAPPER.CREATE_QUOTE(?,?,?,?,?,?,?,?,?,?,?,?) }";			
		}
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);

			callableStatement = connection.prepareCall(stmt);
			callableStatement.registerOutParameter(1, Types.NUMERIC);
			callableStatement.setLong(2, cId);
			callableStatement.setString(3, accountCode);
			callableStatement.setLong(4, qprId);
			callableStatement.setString(5, driverGradeGroupCode);
			callableStatement.setLong(6, term);
			callableStatement.setLong(7, distance);
			callableStatement.setLong(8, cfgId);
			callableStatement.setString(9, orderTypeCode);
			callableStatement.setString(10, unitNo);
			callableStatement.setLong(11, odoReading);
			callableStatement.setString(12, employeeNo);			

			if(!MALUtilities.isEmpty(desiredQuoId)) {		
				callableStatement.setLong(13, desiredQuoId);
			}

			callableStatement.execute();
			quoId = callableStatement.getLong(1);		

		} catch(Exception e) {
			throw new MalException(e.getMessage());			
		}
		
		if(MALUtilities.isEmpty(quoId) ){
			throw new MalException("QUOTATION_WRAPPER.CREATE_QUOTE did not return a Quote No");
		}
		
		return quoId;
	}

}
