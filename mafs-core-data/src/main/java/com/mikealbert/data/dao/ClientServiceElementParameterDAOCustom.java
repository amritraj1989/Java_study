package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.List;

import com.mikealbert.data.entity.ClientServiceElementParameter;
import com.mikealbert.data.vo.ClientServiceElementParameterVO;

public interface ClientServiceElementParameterDAOCustom {
	
	public List<ClientServiceElementParameterVO> getServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	
	public BigDecimal getParameterDefaultValuesSum(Long leaseElementId, Long clientServiceElementId);
	
	public List<ClientServiceElementParameterVO> getGradeGroupServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);
	
	public List<ClientServiceElementParameterVO> getProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);

	public List<ClientServiceElementParameterVO> getGradeGroupProductServiceElementParametersByClientServiceElement(Long leaseElementId, Long clientServiceElementId);

}
