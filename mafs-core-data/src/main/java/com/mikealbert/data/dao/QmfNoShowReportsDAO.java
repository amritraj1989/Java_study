package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QmfNoShowReports;
import com.mikealbert.data.entity.QmfNoShowReportsPK;


public interface QmfNoShowReportsDAO extends JpaRepository<QmfNoShowReports, QmfNoShowReportsPK> {

	@Query("SELECT q FROM QmfNoShowReports q WHERE q.id.qmfQmfId = ?1")
	public List<QmfNoShowReports> findByQmfId(Long qmfQmfId);	

	
}
