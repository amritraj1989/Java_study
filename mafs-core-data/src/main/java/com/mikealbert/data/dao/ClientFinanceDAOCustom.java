package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.entity.ClientFinance;
import com.mikealbert.data.vo.ClientFinanceVO;

public interface ClientFinanceDAOCustom {

	public List<ClientFinanceVO> searchClientFinanceParametersByAccount(Long corpId, String accountType, String accountCode, Long paramCategoryId);
	public List<ClientFinanceVO> searchClientFinanceParametersByGradeGroup(Long corpId, String accountType, String accountCode, String parameterId);
	public List<ClientFinanceVO> searchClientFinanceParametersByCostCentre(Long corpId, String accountType, String accountCode, String parameterId);
}
