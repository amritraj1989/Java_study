package com.mikealbert.service;


import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.AccTransParameterSetDAO;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.GlCodeDAO;
import com.mikealbert.data.entity.AccTransParameterSet;
import com.mikealbert.data.enumeration.DocumentType;
import com.mikealbert.data.vo.ArCreationVO;
import com.mikealbert.data.vo.GlCodeProcParamsVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;

@Service("aRService")
public class ArServiceImpl implements ArService {

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	@Resource
	DocDAO docDAO;
	@Resource
	AccTransParameterSetDAO accTransParameterSetDAO;
	@Resource
	GlCodeDAO glCodeDAO;

	/**
	 * Given a populated VO, this method will call the DAO method that will create the Invoice AR  
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)	
	public Long createInvoiceAR(ArCreationVO aRVO) throws MalBusinessException, Exception {
		
		Long docId = null;
		try {
			docId = docDAO.createInvoiceAR(aRVO);
		} catch (Exception e) {
			logger.error(e);
			System.out.println(e);
			if (e instanceof MalException) {
				throw new MalBusinessException("generic.error.occured.while", new String[] { "creating an InvoiceAR" }, e);
			}
			throw e;
		}
		
		return docId;
	}
		

	/**
	 * This method will ensure the parameters are set for the given transaction and call the actual method to  
	 * create the Invoice AR.
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)	
	public Long createInvoiceARForType(String type, ArCreationVO aRVO) throws MalBusinessException, Exception {
		
		Long docId = null;
		String glCode = null;
		GlCodeProcParamsVO glCodeProcParamsVO = null;

		setAccountingValues(type, aRVO);
		glCodeProcParamsVO = setGlCodeParamValues(aRVO);
		glCode = glCodeDAO.findGlCodeByProc(glCodeProcParamsVO);
		aRVO.setGlCode(glCode);
		docId = createInvoiceAR(aRVO);
		
		return docId;
	}
		
	private GlCodeProcParamsVO setGlCodeParamValues(ArCreationVO aRVO){
		GlCodeProcParamsVO glCodeProcParamsVO = new GlCodeProcParamsVO();
		
		glCodeProcParamsVO.setcId(aRVO.getcId());
		glCodeProcParamsVO.setDocumentType(DocumentType.ACCOUNTS_RECEIVABLE.getdocumentType());
		glCodeProcParamsVO.setTransType(aRVO.getTransType());
		glCodeProcParamsVO.setSourceCode(aRVO.getSourceCode());
		glCodeProcParamsVO.setAnalysisCode(aRVO.getAnalysisCode());
		glCodeProcParamsVO.setAnalysisCategory(aRVO.getAnalysisCategory());	
		
		return glCodeProcParamsVO;
	}
	
	/**
	 * This method will use the given transaction type to get the corresponding parameters needed for the 
	 * transaction.
	 */
	private void setAccountingValues(String type, ArCreationVO aRVO) throws Exception {
		
		AccTransParameterSet parms = accTransParameterSetDAO.findById(type).orElse(null);
		if(parms != null) {
			aRVO.setSourceCode(parms.getSourceCode());
			aRVO.setTransType(parms.getTransType());
			aRVO.setCostDbCode(parms.getCostDbCode());
			aRVO.setCategoryType(parms.getCategoryType());
			aRVO.setAnalysisCategory(parms.getAnalysisCategory());
			aRVO.setAnalysisCode(parms.getAnalysisCode());
			aRVO.setChargeCode(parms.getChargeCode());
		} else {
			throw new Exception("Cannot find Acc Trans Parameter Set for for transaction: " + type);
		}
	}
	
	public void rechargeCapitalContribution(long cId, long contractLineId, long quotationModelId, Date docDate, String userName) throws MalBusinessException, Exception {
		try {
			docDAO.rechargeCapitalContribution(cId, contractLineId, quotationModelId, docDate, userName);
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof MalException) {
				throw new MalBusinessException("generic.error.occured.while", new String[] { "recharging the capital contribution" }, e);
			}
			throw e;
		}

	}
}
