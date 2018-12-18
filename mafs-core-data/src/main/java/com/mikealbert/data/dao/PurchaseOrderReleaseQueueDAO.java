package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.PurchaseOrderReleaseQueueV;

/**
* DAO for PurchaseOrderReleaseQueueV Entity
* @author Amritraj
*/

public interface PurchaseOrderReleaseQueueDAO extends JpaRepository<PurchaseOrderReleaseQueueV, Long>, PurchaseOrderReleaseQueueDAOCustom {}
