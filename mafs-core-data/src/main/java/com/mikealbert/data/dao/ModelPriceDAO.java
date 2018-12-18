package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.ModelPrice;

/**
* DAO for ModelPrice Entity
* @author sibley
*/

public interface ModelPriceDAO extends JpaRepository<ModelPrice, Long> {}
