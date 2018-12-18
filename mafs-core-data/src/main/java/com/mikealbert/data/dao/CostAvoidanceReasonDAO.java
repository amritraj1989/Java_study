package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.CostAvoidanceReason;

/**
* DAO for CostAvoidanceReason Entity
* @author sibley
*/

public interface CostAvoidanceReasonDAO extends JpaRepository<CostAvoidanceReason, String> {}
