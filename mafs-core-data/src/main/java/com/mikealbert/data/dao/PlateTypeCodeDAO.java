package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.PlateTypeCode;

public interface PlateTypeCodeDAO extends JpaRepository<PlateTypeCode, Long> {

	@Query("SELECT ptc FROM PlateTypeCode ptc order by plateTypeCode asc")
	public List<PlateTypeCode> getAllPlateTypeCodes();
}
