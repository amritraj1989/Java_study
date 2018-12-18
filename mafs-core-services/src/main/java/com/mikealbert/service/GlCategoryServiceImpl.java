package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.GlCategoryDAO;
import com.mikealbert.data.entity.CostDatabaseCategoryCodes;
import com.mikealbert.exception.MalBusinessException;

@Service
@Transactional
public class GlCategoryServiceImpl implements GlCategoryService {
	@Resource
	private GlCategoryDAO glCategoryDAO;
	
	public List<CostDatabaseCategoryCodes> getGlCategories(String category, Pageable pageable) throws MalBusinessException {
		try {
			category = category + '%';
			return glCategoryDAO.findCategory(category, pageable);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalBusinessException("generic.error.occured.while",
					new String[] { "getting Gl Category" }, ex);
		}
	}

	public int getGlCategoryCount(String category) throws MalBusinessException {
		try {
			category = category + '%';
			Long count = glCategoryDAO.findCategoryCount(category);
			return count!= null ? Integer.parseInt(count.toString()):0;
			} catch (Exception ex) {
			ex.printStackTrace();
			throw new MalBusinessException("generic.error.occured.while",
					new String[] { "getting Gl Category Count" }, ex);
		}
	}

}
