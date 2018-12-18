
package com.mikealbert.data.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.WillowEntityDefault;

public interface WillowEntityDefaultDAO extends JpaRepository<WillowEntityDefault, Long>{
	@Query("from WillowEntityDefault where cId = ?1")
	public WillowEntityDefault	findByContextId(Long cId);
}
