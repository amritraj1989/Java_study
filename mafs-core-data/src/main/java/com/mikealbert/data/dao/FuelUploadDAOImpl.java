package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

import com.mikealbert.data.entity.FuelUpload;
import com.mikealbert.data.entity.FuelUploadPK;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;


public class FuelUploadDAOImpl extends GenericDAOImpl<FuelUpload, FuelUploadPK> implements FuelUploadDAOCustom {

	private static final long serialVersionUID = 1L;
	@Resource
	private DataSource	dataSource;
	public Long getNextUploadId() {
		String stmt = "select load_id_seq.nextval from dual";
		BigDecimal rs = (BigDecimal) entityManager.createNativeQuery(stmt).getSingleResult();
		return rs.longValue();
	}

	public boolean callValidateFuelData(Long loadId, Long cId) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;

		String resultString = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ ? = call VISION.fl_if_wrapper.VALIDATE_FUEL_UPLOAD(?,?) }");
			callableStatement.registerOutParameter(1, Types.VARCHAR);
			callableStatement.setLong(2, loadId);
			callableStatement.setLong(3, cId);
			callableStatement.execute();
			resultString = callableStatement.getString(1);
			if (!MALUtilities.isEmpty(resultString)) {
				if ("TRUE".equalsIgnoreCase(resultString)) {
					return true;
				} else if ("FALSE".equalsIgnoreCase(resultString)) {
					return false;
				} else {
					throw new MalBusinessException(resultString);
				}
			}
			return false;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
	}
public boolean callFmInterfaceControl(Long loadId) throws MalBusinessException {
		
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			callableStatement = connection.prepareCall("{ call WILLOW2K.FL_IF.LOAD_INTERFACE_CONTROLS(?,?,?,?,?,?,?,?,?,?,?,?,?,?) }");
			callableStatement.setString(1, "COMDATA");
			callableStatement.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
			callableStatement.setString(3, "PDI");
			callableStatement.setString(4, null);
			callableStatement.setString(5, "OPEN");
			callableStatement.setString(6, null);
			callableStatement.setString(7, null);
			callableStatement.setDate(8, null);
			callableStatement.setInt(9, loadId.intValue());
			callableStatement.setTimestamp(10, new java.sql.Timestamp(new java.util.Date().getTime()));
			callableStatement.setString(11, "E");
			callableStatement.registerOutParameter(12, Types.NUMERIC);
			callableStatement.registerOutParameter(13, Types.NUMERIC);
			callableStatement.registerOutParameter(14, Types.VARCHAR);
			
			callableStatement.execute();
			
			return true;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
	}

}
