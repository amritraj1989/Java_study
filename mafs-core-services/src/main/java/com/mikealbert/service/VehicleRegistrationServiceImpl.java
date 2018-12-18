package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mikealbert.data.entity.VehicleRegistrationV;
import com.mikealbert.exception.MalException;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.VehicleRegistrationVDAO;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;

@Service("vehicleRegistrationService")
public class VehicleRegistrationServiceImpl implements VehicleRegistrationService {
	public MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource
	private VehicleRegistrationVDAO vehicleRegistrationVDAO;
	
	@Resource
	private OnbaseRetrievalService onbaseRetrievalService;
	
	@Override
	public VehicleRegistrationV getVehicleRegistration(Long fmsId) {
		VehicleRegistrationV vehicleRegistrationV = null;
		try {
			vehicleRegistrationV = vehicleRegistrationVDAO.findById(fmsId).orElse(null);
		} catch(Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting vehicle registration for fmsId " + fmsId }, ex);			
		}
		
		return vehicleRegistrationV;
	}

	private static String ONBASE_DOCUMENT_ID = "ID";
	
	@Override
	public String getVehicleRegistrationPDF(Long fmsId) {
		String retVal = null;
		List<Map<String, String>> keyWordDocumentList = null;
		try {
			VehicleRegistrationV vehicleRegistrationV = vehicleRegistrationVDAO.findById(fmsId).orElse(null);
			
			OnbaseKeywordVO onbaseKeywordVO = new OnbaseKeywordVO(OnbaseIndexEnum.UNIT_NO.getName(),vehicleRegistrationV.getUnitNo());
			ArrayList<OnbaseKeywordVO> onbaseKeywords = new ArrayList<OnbaseKeywordVO>();
			onbaseKeywords.add(onbaseKeywordVO);
			
			keyWordDocumentList = onbaseRetrievalService.getList(OnbaseDocTypeEnum.TYPE_REGISTRATION_HISTORY, onbaseKeywords);
			Map<String, String> documentKeyWords = keyWordDocumentList.get(0);
			retVal = onbaseRetrievalService.getDocAsBase64(documentKeyWords.get(ONBASE_DOCUMENT_ID));
			
		} catch(Exception ex) {
			logger.error(ex);
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting vehicle registration for fmsId " + fmsId }, ex);			
		}
		
		return retVal;
	}
	
	
}
