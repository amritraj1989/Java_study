package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.ExtAccTaxExempt;

public interface ExtAccTaxExemptDAO extends JpaRepository<ExtAccTaxExempt, Long>{

	@Query("from ExtAccTaxExempt eate where eate.client.externalAccountPK.cId = ?1 and eate.client.externalAccountPK.accountType =?2 and eate.client.externalAccountPK.accountCode =?3")
	public List<ExtAccTaxExempt> getExtAccTaxExemptsByAccount(long cid, String accountType, String accountCode);
	
}
