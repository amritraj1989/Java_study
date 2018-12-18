package com.mikealbert.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.CostDatabaseCategoryCodes;
import com.mikealbert.exception.MalBusinessException;

public interface GlCategoryService {
	List<CostDatabaseCategoryCodes>	getGlCategories(String category, Pageable pageable) throws MalBusinessException;
	public int getGlCategoryCount(String category) throws MalBusinessException;

}
