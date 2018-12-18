package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.MaintSchedulesBatch;

public interface MaintSchedulesBatchDAO extends MaintSchedulesBatchDAOCustom ,JpaRepository<MaintSchedulesBatch, Long>{
	@Query("from MaintSchedulesBatch  where status = 'O'")
	MaintSchedulesBatch findOpenBatch();
}
