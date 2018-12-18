package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.RejectReason;

/**
* DAO for RejectReason Entity
* @author Roshan
*/

public interface RejectReasonDAO extends JpaRepository<RejectReason, String> {
	@Query("FROM RejectReason rr ORDER BY rr.rejectReasonDescription ASC")
	public List<RejectReason> getRejectReason();
}
