package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.vo.QuotationProfileFinanceVO;

public interface QuotationProfileFinanceDAOCustom {
	
	public List<QuotationProfileFinanceVO> getFinanceParamsByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId);
	
	public int countFinanceParamsByClientAndParameter(Long cId, String accountType, String accountCode, Long parameterId);

}
