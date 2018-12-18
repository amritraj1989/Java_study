package com.mikealbert.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.enumeration.NonCapitalElementEnum;
import com.mikealbert.service.vo.QuoteCostElementVO;

@Transactional
public interface CapitalCostElementService {	
	
	public List<QuoteCostElementVO> getCapitalCostElementList(Long qmdId) throws MalBusinessException;
	
	public QuoteCostElementVO createCapitalElementVO(String elementGroupName, int groupOrder, int orderInGroup, CapitalElement capitalElement);
	public QuoteCostElementVO createModelAccessoryVO(int groupOrder, int orderInGroup, QuotationModelAccessory modelAccessory);
	public QuoteCostElementVO createDealerAccessoryVO(int groupOrder, int orderInGroup, QuotationDealerAccessory dealerAccessory);
	public QuoteCostElementVO createNonCapitalElementVO(NonCapitalElementEnum nonCapitalElement);
}
