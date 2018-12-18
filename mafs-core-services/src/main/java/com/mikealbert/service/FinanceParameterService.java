package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.WorkClassAuthority;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.data.vo.FinanceParameterVO;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;


@Transactional
public interface FinanceParameterService {	
	
	public List<FormulaParameter> getFormulaParametersForElement(QuotationElement qe, String parameterType);
	public QuotationModelFinances getQuotationModelFinances(Long qmdId, String key);
	public List<FinanceParameterVO> getFinanceParameterInfoForQuotationElement(QuotationElement qe, String workClassName, String moduleName);
	public QuotationModelFinances saveFinanceParameterOnQuote(QuotationModelFinances quotationModelFinances) throws Exception;
	public void deleteFinanceParameterOnQuote(QuotationModelFinances quotationModelFinances) throws Exception;
	
	public BigDecimal getWorkclassLowerLimit(String financeParameterKey , String workclass, String moduleName ) throws MalException;
	public BigDecimal getFinanceParameterValueOnProfile(FinanceParameter fp, QuotationProfile qp);
	public WorkClassAuthority getWorkClassAuthority(String financeParameterKey, String workclass, String moduleName)
			throws MalException ;
	public FinanceParameter getEffectiveFinanceParameterByDate(String financeParameterKey, Date targetDate);
	
	public List<ClientFinanceVO> searchClientFinanceParametersByAccount(long corpId, String accountType, String accountCode, long paramCategoryId);
	public List<ClientFinanceVO> searchClientFinanceParametersByCostCentre(Long corpId, String accountType, String accountCode, String paramCategoryId);
	public List<ClientFinanceVO> searchClientFinanceParametersForGradeGroupByAccountAndParam(Long corpId, String accountType, String accountCode, String parameterId);
	public void saveUpdateOrDeleteClientFinanceParameters(List<ClientFinanceVO> parameterList, String parameterType) throws MalBusinessException;
	public List<QuotationProfileFinanceVO> getProfileFinancesByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId);
	public int countProfileFinancesByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId);
	public FinanceParameter findByParameterKey(String parameterKey);
	public List<FormulaParameter> getFormulaParametersByLeaseElement(Long elementId, String parameterType);
	
}
