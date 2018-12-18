package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.MaintenanceProperty;

/**
* DAO for MaintenanceProperty Entity
* @author sibley
*/

public interface MaintenancePropertyDAO extends JpaRepository<MaintenanceProperty, Long> {}
