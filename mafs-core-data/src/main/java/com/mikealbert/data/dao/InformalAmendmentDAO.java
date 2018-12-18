package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.InformalAmendment;

public interface InformalAmendmentDAO extends JpaRepository<InformalAmendment, Long> {
	
	@Query("from InformalAmendment where qmdQmdId = ?1 order by effectiveFrom DESC")
	public List<InformalAmendment>	findByQmdId(Long qmdId);

}
