package com.mikealbert.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ClientFinanceDAO;
import com.mikealbert.data.dao.ClientFinanceTypeDAO;
import com.mikealbert.data.dao.CostCenterDAO;
import com.mikealbert.data.dao.ExternalAccountDAO;
import com.mikealbert.data.dao.ExternalAccountGradeGroupDAO;
import com.mikealbert.data.dao.FinanceParameterDAO;
import com.mikealbert.data.dao.FormulaParameterDAO;
import com.mikealbert.data.dao.QuotationModelFinancesDAO;
import com.mikealbert.data.dao.QuotationProfileFinanceDAO;
import com.mikealbert.data.dao.WorkClassAuthorityDAO;
import com.mikealbert.data.entity.ClientFinance;
import com.mikealbert.data.entity.CostCentreCode;
import com.mikealbert.data.entity.CostCentreCodePK;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.ExternalAccountGradeGroup;
import com.mikealbert.data.entity.ExternalAccountPK;
import com.mikealbert.data.entity.FinanceParameter;
import com.mikealbert.data.entity.FormulaParameter;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModelFinances;
import com.mikealbert.data.entity.QuotationProfile;
import com.mikealbert.data.entity.QuotationProfileFinance;
import com.mikealbert.data.entity.QuotationProfileFinancePK;
import com.mikealbert.data.entity.WorkClassAuthority;
import com.mikealbert.data.enumeration.ClientFinanceTypeCodes;
import com.mikealbert.data.vo.ClientFinanceVO;
import com.mikealbert.data.vo.FinanceParameterVO;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

@Service("financeParameterService")
public class FinanceParameterServiceImpl implements  FinanceParameterService {	
	@Resource FormulaParameterDAO formulaParameterDAO;
	@Resource QuotationModelFinancesDAO quotationModelFinancesDAO;
	@Resource WorkClassAuthorityDAO workClassAuthorityDAO;
	@Resource FinanceParameterDAO financeParameterDAO;
	@Resource QuotationProfileFinanceDAO quotationProfileFinanceDAO;
	@Resource ClientFinanceDAO clientFinanceDAO;
	@Resource ExternalAccountDAO externalAccountDAO;
	@Resource CostCenterDAO costCenterDAO;
	@Resource ExternalAccountGradeGroupDAO externalAccountGradeGroupDAO;
	@Resource ClientFinanceTypeDAO clientFinanceTypeDAO;
	/**
	 * This method  
	 *  
	 */

	@Override
	public List<FormulaParameter> getFormulaParametersForElement(QuotationElement qe, String parameterType) {
		return formulaParameterDAO.findByLeaseElementIdAndParameterType(qe.getLeaseElement().getLelId(), parameterType);
	}
	
	public List<FormulaParameter> getFormulaParametersByLeaseElement (Long leaseElementId, String parameterType) {
		return formulaParameterDAO.findByLeaseElementIdAndParameterType(leaseElementId, parameterType);
	}

	@Override
	public QuotationModelFinances getQuotationModelFinances(Long qmdId, String key) {
		return quotationModelFinancesDAO.findByQmdIdAndParameterKey(qmdId, key);
	}

	private void setFinanceParameterAndWorkClassAuthority(FinanceParameterVO financeParameterVO, FormulaParameter formulaParameter, 
			String workClassName, String moduleName ) {
		Date today = Calendar.getInstance().getTime();	
		FinanceParameter financeParameter = getEffectiveFinanceParameterByDate(formulaParameter.getParameterName(), today);
		if(financeParameter != null) {
			WorkClassAuthority wca = 
					workClassAuthorityDAO.findByWorkClassAndModuleAndParameterId(workClassName, 1l, moduleName, financeParameter.getParameterId());
			if(wca != null) {
				financeParameterVO.setFinanceParameter(financeParameter);
				financeParameterVO.setWorkClassAuthority(wca);
			}
		}
	}
	

	@Override
	public List<FinanceParameterVO> getFinanceParameterInfoForQuotationElement(QuotationElement qe, String workClassName, String moduleName) {
	
		List<FinanceParameterVO> list = new ArrayList<FinanceParameterVO>();
		Long qmdId = qe.getQuotationModel().getQmdId();
		for(FormulaParameter fp : getFormulaParametersForElement(qe, "F")) {
			FinanceParameterVO financeParameterVO = new FinanceParameterVO();
			financeParameterVO.setFormulaParameter(fp);
			setFinanceParameterAndWorkClassAuthority(financeParameterVO, fp, workClassName, moduleName);
			QuotationModelFinances qmf = getQuotationModelFinances(qmdId, fp.getParameterName());
			financeParameterVO.setQuotationModelFinances(qmf);
			if(qmf != null) {
				financeParameterVO.setValue(qmf.getnValue());
			}
			list.add(financeParameterVO);				

		}
		
		return list;	
	}

	
	@Transactional	
	public QuotationModelFinances saveFinanceParameterOnQuote(QuotationModelFinances quotationModelFinances) throws Exception {
		try {
			QuotationModelFinances qmf = quotationModelFinancesDAO.saveAndFlush(quotationModelFinances);
			return qmf;
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving a finance parameter" }, ex);
		}		
	}

	@Transactional	
	public void deleteFinanceParameterOnQuote(QuotationModelFinances quotationModelFinances) throws Exception {
		try {
			quotationModelFinancesDAO.deleteById(quotationModelFinances.getQmfId());
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "deleting a finance parameter" }, ex);
		}		
	}
	
	@Transactional	
	public BigDecimal getWorkclassLowerLimit(String financeParameterKey , String workclass ,String moduleName ) throws MalException{
	    
	    BigDecimal lowerLimit = null;
		Date today = Calendar.getInstance().getTime();
		FinanceParameter  financeParameter = getEffectiveFinanceParameterByDate(financeParameterKey, today);
		List<WorkClassAuthority> workClassAuthorities = financeParameter.getWorkClassAuthorities();
		for (WorkClassAuthority workClassAuthority : workClassAuthorities) {
		    if(workClassAuthority.getWorkClass().getWorkClassName().equals(workclass)
			    && workClassAuthority.getModuleName().equals(moduleName)){
			lowerLimit  =  workClassAuthority.getLowerLimit();
		    }
		}
	    
	    return lowerLimit;
	}

	@Override
	public BigDecimal getFinanceParameterValueOnProfile(FinanceParameter fp, QuotationProfile qp) {
		QuotationProfileFinancePK qpfPK = new QuotationProfileFinancePK();
		qpfPK.setParameterId(fp.getParameterId());
		qpfPK.setQprQprId(qp.getQprId());
		QuotationProfileFinance qpf = quotationProfileFinanceDAO.findById(qpfPK).orElse(null);
		if(qpf != null) {
			return qpf.getNvalue();
		} else
			return null;
	}
	
	
	@Transactional
	public WorkClassAuthority getWorkClassAuthority(String financeParameterKey, String workclass, String moduleName)
			throws MalException {
		
		Date today = Calendar.getInstance().getTime();
		FinanceParameter  financeParameter = getEffectiveFinanceParameterByDate(financeParameterKey, today);

		if(financeParameter != null) {
			List<WorkClassAuthority> workClassAuthorities = financeParameter.getWorkClassAuthorities();
			for (WorkClassAuthority workClassAuthority : workClassAuthorities) {
				if (workClassAuthority.getWorkClass().getWorkClassName().equals(workclass)
						&& workClassAuthority.getModuleName().equals(moduleName)) {
					return workClassAuthority;
				}
			}
		}
		return null;
	}

	@Override
	public FinanceParameter getEffectiveFinanceParameterByDate(String financeParameterKey, Date targetDate) {
		List<FinanceParameter> financeParameters = financeParameterDAO.findByParameterKeyAndStatus(financeParameterKey, "A");

		for(FinanceParameter fp : financeParameters) {
			if(targetDate.after(fp.getEffectiveFrom())) {
				return fp;
			}
		}
		return null;
	}

	@Transactional(readOnly = true)
	public List<ClientFinanceVO> searchClientFinanceParametersByAccount(long corpId, String accountType, String accountCode, long paramCategoryId){		
		return clientFinanceDAO.searchClientFinanceParametersByAccount(corpId, accountType, accountCode, paramCategoryId);		
	}
	
	@Transactional(rollbackFor = MalBusinessException.class)
	public void saveUpdateOrDeleteClientFinanceParameters(List<ClientFinanceVO> parameterList, String parameterType) throws MalBusinessException{
			List<ClientFinance> financeParamListToSave = new ArrayList<ClientFinance>();
			List<ClientFinance> financeParamListToDelete = new ArrayList<ClientFinance>();
			boolean modified = false;
			boolean deleted = false;
			Pattern patternDigitBeforeDecimal = Pattern.compile("^-?(\\d+(\\.?\\d+)?)$"); //This allows: one or more digits and optional decimal with required digit. Example: 1 or 1.1 or 1.11
			Pattern patternNoDigitBeforeDecimal = Pattern.compile("^-?(\\.\\d+)$"); //This allows: No digits and required decimal with digit. Ex: .1 or .11
			Matcher matcherPatternDigitBeforeDecimal;
			Matcher matcherPatternNoDigitBeforeDecimal;
			if (!MALUtilities.isEmpty(parameterList) && !parameterList.isEmpty()){
				for(ClientFinanceVO clientFinanceVO : parameterList){				

					ClientFinance clientFinance = null;
					String financeParameterValue = null;
					if(!MALUtilities.isEmpty(clientFinanceVO.getClientFinanceId())){
						clientFinance = clientFinanceDAO.findById(clientFinanceVO.getClientFinanceId()).orElse(null);
					}else if(MALUtilities.isEmpty(clientFinance)){
						clientFinance = new ClientFinance();
						if(parameterType.equals(ClientFinanceTypeCodes.DRIVER_COST_CENTRE.getCode())){
							if(!MALUtilities.isEmpty(clientFinanceVO.getClientCostCentreCode())){
								CostCentreCodePK costCentreCodePK = new CostCentreCodePK();
								costCentreCodePK.setCostCentreCode(clientFinanceVO.getClientCostCentreCode());
								costCentreCodePK.setEaCId(clientFinanceVO.getEaCId());
								costCentreCodePK.setEaAccountType(clientFinanceVO.getEaAccountType());
								costCentreCodePK.setEaAccountCode(clientFinanceVO.getEaAccountCode());							
								CostCentreCode costCentreCode = costCenterDAO.findById(costCentreCodePK).orElse(null);
								clientFinance.setCostCentreCode(costCentreCode.getCostCentreCodesPK().getCostCentreCode());
								clientFinance.setClientFinanceType(clientFinanceTypeDAO.findById(ClientFinanceTypeCodes.DRIVER_COST_CENTRE.getId()).orElse(null));
							}
						}else if(parameterType.equals(ClientFinanceTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getCode())){
							if(!MALUtilities.isEmpty(clientFinanceVO.getClientGradeGroupId()) && clientFinanceVO.getClientGradeGroupId() != 0l){
								ExternalAccountGradeGroup externalAccountGradeGroup = externalAccountGradeGroupDAO.findById(clientFinanceVO.getClientGradeGroupId()).orElse(null);
								clientFinance.setExternalAccountGradeGroup(externalAccountGradeGroup);
								clientFinance.setClientFinanceType(clientFinanceTypeDAO.findById(ClientFinanceTypeCodes.EXTERNAL_ACCOUNT_GRADE_GROUP.getId()).orElse(null));
							}
						} else {
							clientFinance.setClientFinanceType(clientFinanceTypeDAO.findById(ClientFinanceTypeCodes.ACCOUNT.getId()).orElse(null));
						}
						
						ExternalAccountPK externalAccouintPK = new ExternalAccountPK();
						externalAccouintPK.setCId(clientFinanceVO.getEaCId());
						externalAccouintPK.setAccountType(clientFinanceVO.getEaAccountType());
						externalAccouintPK.setAccountCode(clientFinanceVO.getEaAccountCode());
						ExternalAccount externalAccount = externalAccountDAO.findById(externalAccouintPK).orElse(null);
						clientFinance.setExternalAccount(externalAccount);
						FinanceParameter financeParameter = financeParameterDAO.findById(clientFinanceVO.getParameterId()).orElse(null);
						clientFinance.setFinanceParameter(financeParameter);
					}
					modified = false;
					deleted = false;

					if (!MALUtilities.isEmpty(clientFinance.getNvalue())) {
						financeParameterValue = clientFinance.getNvalue().toString();
					} else if (!MALUtilities.isEmpty(clientFinance.getCvalue())) {
						financeParameterValue = clientFinance.getCvalue();
					}
					if(!MALUtilities.isEmpty(financeParameterValue) || !MALUtilities.isEmpty(clientFinanceVO.getFinanceParameterValue())){
						if(!MALUtilities.isEmpty(financeParameterValue) && !MALUtilities.isEmpty(clientFinanceVO.getFinanceParameterValue())){
							if(!financeParameterValue.equals(clientFinanceVO.getFinanceParameterValue())){
								modified = true;
							}
						}else if(!MALUtilities.isEmpty(financeParameterValue) && MALUtilities.isEmpty(clientFinanceVO.getFinanceParameterValue())){
							deleted = true;
						}else{
							modified = true;
						}
						if(modified){
							matcherPatternDigitBeforeDecimal = patternDigitBeforeDecimal.matcher(clientFinanceVO.getFinanceParameterValue().trim());
							matcherPatternNoDigitBeforeDecimal = patternNoDigitBeforeDecimal.matcher(clientFinanceVO.getFinanceParameterValue().trim());
							if (matcherPatternDigitBeforeDecimal.find() || matcherPatternNoDigitBeforeDecimal.find()) {
								clientFinance.setNvalue((new BigDecimal(clientFinanceVO.getFinanceParameterValue().trim())).setScale(5, BigDecimal.ROUND_HALF_UP));
								clientFinance.setCvalue(null);
							}else {
								clientFinance.setNvalue(null);
								clientFinance.setCvalue(clientFinanceVO.getFinanceParameterValue());
							}
							
							if (!validateClientFinanceParams(clientFinance)) {
								throw new MalBusinessException("service.validation", new String[]{"Finance Parameter value is larger than allowed."});
							}
							
							clientFinance.setLastUpdated(new Date());
							clientFinance.setStatus("A"); //TODO create constant for this.
							financeParamListToSave.add(clientFinance);
							
						}else if(deleted){
							financeParamListToDelete.add(clientFinance);
						}
					}					
				}
				if(!financeParamListToSave.isEmpty()){
					clientFinanceDAO.saveAll(financeParamListToSave);
				}
				if(!financeParamListToDelete.isEmpty()){
					clientFinanceDAO.deleteAll(financeParamListToDelete);
				}
			}
	}
	
	private Boolean validateClientFinanceParams(ClientFinance clientFinance) {
		Boolean isValid = true;
		if (!MALUtilities.isEmpty(clientFinance.getNvalue())) {
			Pattern pattern = Pattern.compile("^-?(\\d{1,6}.?\\d{1,5})$");
			String str = clientFinance.getNvalue().toPlainString();
			Matcher matcher = pattern.matcher(str);
			if (!matcher.find()) {
				isValid = false;
			}
		} else {
			if (clientFinance.getCvalue().length() > 40) {
				isValid = false;
			}
			}
		return isValid;
	}

	public List<QuotationProfileFinanceVO> getProfileFinancesByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId) {
		try {
			return quotationProfileFinanceDAO.getFinanceParamsByClientAndParameter(cId, accountType, accountCode, parameterId);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Quotaton Profile Finances By Client and Parameter Id" }, ex);
		}
	}
	
	public int countProfileFinancesByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId) {
		try {
			return quotationProfileFinanceDAO.countFinanceParamsByClientAndParameter(cId, accountType, accountCode, parameterId);
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding All Quotaton Profile Finances By Client and Parameter Id" }, ex);
		}
	}

	@Override
	public List<ClientFinanceVO> searchClientFinanceParametersByCostCentre(Long corpId, String accountType, String accountCode, String parameterId) {
		return clientFinanceDAO.searchClientFinanceParametersByCostCentre(corpId, accountType, accountCode, parameterId);
	}	
	
	/**
	 * Looks in the Client_Finances table for records with a type of 'GRADE', account values like the ones passed in, 
	 *  and the finance parameter id.  It returns all these records.
	 *  @param corpId account's corp Id
	 *  @param accountType account's account type
	 *  @param accountCode account's account code
	 *  @param parameterId the id of the finance parameter
	 *  @return List of ClientFinanceVO
	 */
	@Override
	public List<ClientFinanceVO> searchClientFinanceParametersForGradeGroupByAccountAndParam(Long corpId, String accountType, String accountCode, String parameterId){
		try{
			return clientFinanceDAO.searchClientFinanceParametersByGradeGroup(corpId, accountType, accountCode, parameterId);
		}catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Grade Group Client Finances by account and parameter" }, ex);
		}
	}

	@Override
	public FinanceParameter findByParameterKey(String parameterKey) {
		try{
			return financeParameterDAO.findByParameterKey(parameterKey);
		}catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "finding Finance Parameter by Parameter Key" }, ex);
		}
	}
}
