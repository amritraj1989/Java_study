package com.mikealbert.exception;

import javax.annotation.Resource;

import com.mikealbert.data.dao.ErrorCodeDAO;
import com.mikealbert.data.entity.ErrorCode;
import com.mikealbert.util.MALUtilities;

/**
 * Resolves a passed in errorCode string against the error codes stored in the ErrorCode entity (and ERROR_CODE table)
 * by using ErrorCodeDAO to translate them directly
 * 
 * @author Duncan
 *
 */

public class ErrorCodeEntityResolver implements ErrorCodeResolver {
	@Resource ErrorCodeDAO errorCodeDao;

	@Override
	public ErrorCodeVO resolveErrorCode(String errorCode) {
		ErrorCodeVO retVal = null; 
		
		//TODO: handle parse exceptions?!
		ErrorCode code = errorCodeDao.findById(Long.parseLong(errorCode)).orElse(null);
		if(!(MALUtilities.isEmpty(code))){
			retVal = new ErrorCodeVO();
			retVal.setErrorCode(code.getErrorCode().toString());
			retVal.setErrorMessage(code.getErrorDescription());
		}

		return retVal;
	}
	
}
