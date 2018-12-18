package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.vo.CostCenterHierarchicalVO;

public interface CostCenterDAOCustom {

	public List<CostCenterHierarchicalVO> findCostCenterHierarchicalVOByAccount(ExternalAccount account, Pageable pageable, Sort sort);
}
