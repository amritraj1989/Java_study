package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.LeaseElement;

public interface MaintenanceProgramDAO extends MaintenanceProgramDAOCustom, JpaRepository<LeaseElement, Long> {

}
