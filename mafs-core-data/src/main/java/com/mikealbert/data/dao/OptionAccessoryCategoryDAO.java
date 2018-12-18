package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.OptionAccessoryCategory;

/**
* DAO for OptionAccessoryCategory Entity
* @author sibley
*/

public interface OptionAccessoryCategoryDAO extends JpaRepository<OptionAccessoryCategory, String> {
	@Query("SELECT oac FROM OptionAccessoryCategory oac WHERE oac.code IN ?1 order by oac.description asc")
	public List<OptionAccessoryCategory> getMafsOptionAccessoryCategories(List<String> dealerAccCatCodeList);
}
