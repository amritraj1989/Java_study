package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ClientContactMethod;

/**
* DAO for ClientContactMethod Entity
* @author sibley
*/

public interface ClientContactMethodDAO extends JpaRepository<ClientContactMethod, Long> {}
