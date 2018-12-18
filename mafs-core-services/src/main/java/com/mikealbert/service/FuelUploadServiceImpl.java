package com.mikealbert.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.FuelUploadDAO;
import com.mikealbert.data.entity.FuelUpload;
import com.mikealbert.exception.MalBusinessException;

@Service
public class FuelUploadServiceImpl implements FuelUploadService{
	@Resource
	private 	FuelUploadDAO fuelUploadDAO;
	@Transactional(rollbackFor= Exception.class)
	public void	saveFuelUploadData( FuelUpload fuelUpload){
		fuelUploadDAO.save(fuelUpload);
	}
	
	public Long getNextFuelUploadLoadId(){
		return fuelUploadDAO.getNextUploadId();
	}
	
	
	public boolean validateFuelData(Long loadId, Long cId) throws MalBusinessException{
		try {
			Boolean result = fuelUploadDAO.callValidateFuelData(loadId, cId);
			return result;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
	}
	public boolean saveFmInterfaceControl(Long loadId) throws MalBusinessException{
		try {
			fuelUploadDAO.callFmInterfaceControl(loadId);
			//Boolean result = fuelUploadDAO.callValidateFuelData(loadId, cId);
			//return result;
			return true;
		} catch (Exception ex) {
			throw new MalBusinessException(ex.getMessage());
		}
		
	}
}
