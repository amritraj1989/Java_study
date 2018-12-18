package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.ThirdPartyPoQueueV;

/**
* DAO for ThirdPartyPoQueueV Entity
* @author Amritraj
*/

public interface ThirdPartyPoQueueDAO extends JpaRepository<ThirdPartyPoQueueV, Long>, ThirdPartyPoQueueDAOCustom {}
