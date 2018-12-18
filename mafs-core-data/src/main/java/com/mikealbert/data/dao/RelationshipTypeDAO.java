package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mikealbert.data.entity.RelationshipType;

/**
* DAO for RelationShipType Entity
* @author sibley
*/
public interface RelationshipTypeDAO extends JpaRepository<RelationshipType, Long> {}
