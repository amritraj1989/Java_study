package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.ClientRule;

/**
* DAO for ClientRule Entity
* @author sibley
*/

public interface ClientRuleDAO extends JpaRepository<ClientRule, Long> {}
