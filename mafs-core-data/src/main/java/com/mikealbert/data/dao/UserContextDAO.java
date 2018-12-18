package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.UserContext;
import com.mikealbert.data.entity.UserContextPK;

public interface UserContextDAO extends JpaRepository<UserContext, UserContextPK>, UserContextDAOCustom {
	public List<UserContext> findByWorkClass(String workClass);
	
	@Query("SELECT ucx FROM UserContext ucx WHERE lower(ucx.adUsername)=lower(?1) AND ucx.id.cId = ?2")
	public UserContext findByADUsername(String adUsername, long cId);
}
