package com.mikealbert.data.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.SupplierProgressHistory;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

public class SupplierProgressHistoryDAOImpl extends GenericDAOImpl<SupplierProgressHistory, Long> implements SupplierProgressHistoryDAOCustom {
	private static final long serialVersionUID = 6794593393250317890L;
	
	@Resource
	private DataSource dataSource;	

	@Override
	public void performPostSupplierUpdateJob(String checkWillowConfig, String checkRemainder, Long cId, String progressType, Date enteredDate, Date actionDate, 
			Long fmsId, Long makeId, Long qmdId, Long docId, String enteredBy, String supplier) throws MalException {
		Connection connection = null;
		CallableStatement callableStatement = null;
		
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			
			callableStatement = connection.prepareCall("{ ? = call vision.fl_general_wrapper.create_eta(?,?,?,?,?,?,?,?,?,?,?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setString(2, checkWillowConfig);
			callableStatement.setString(3, checkRemainder);
			callableStatement.setLong(4, cId);
			callableStatement.setString(5, progressType);
			callableStatement.setDate(6, new java.sql.Date(enteredDate.getTime()));
			callableStatement.setDate(7, new java.sql.Date(actionDate.getTime()));
			callableStatement.setLong(8, fmsId);
			callableStatement.setLong(9, makeId);
			if(qmdId != null){
				callableStatement.setLong(10, qmdId);
			}else{
				callableStatement.setNull(10,  Types.INTEGER);
			}
			callableStatement.setLong(11, docId);
			callableStatement.setString(12, enteredBy);
			callableStatement.setString(13, supplier);
			
			callableStatement.execute();
			String resultString = callableStatement.getString(1);
			
			if (!MALUtilities.isEmpty(resultString)) {
				throw new MalException(resultString);
			}
			
		} catch (Exception ex) {
			throw new MalException(ex.getMessage());
		}
	}
	
	
}
