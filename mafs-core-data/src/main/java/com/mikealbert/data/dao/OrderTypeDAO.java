package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mikealbert.data.entity.OrderType;

/**
* DAO for OrderType Entity
* @author ravresh
*/

public interface OrderTypeDAO extends JpaRepository<OrderType, String> {}
