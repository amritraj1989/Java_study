package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.VehicleReplacementV;
import com.mikealbert.data.entity.VehicleReplacementVPK;

/**
* DAO for ExternalAccont Entity
* @author sibley
*/
public interface VehicleReplacementVDAO extends JpaRepository<VehicleReplacementV, VehicleReplacementVPK>{}

