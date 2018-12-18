package com.mikealbert.data.dao;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.queue.ClientFacingQueueV;

public interface ClientFacingQueueDAO extends JpaRepository<QuotationModel, Long>{
	
	@Query("SELECT cfq FROM ClientFacingQueueV cfq")
	public List<ClientFacingQueueV> findClientFacingQueueList(Sort sort);
}
