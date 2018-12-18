package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.QuoteModelPropertyValue;

public interface QuoteModelPropertyValueDAO extends JpaRepository<QuoteModelPropertyValue, Long>{
	
	@Query("SELECT qmpv FROM QuoteModelPropertyValue qmpv WHERE  qmpv.quoteModelProperty.name = ?1 AND qmpv.quotationModel.qmdId = ?2")
	public QuoteModelPropertyValue findByNameAndQmdId(String dpName, Long qmdId);
	
	@Query("SELECT qmpv FROM QuoteModelPropertyValue qmpv WHERE qmpv.quotationModel.qmdId = ?1")
	public List<QuoteModelPropertyValue> findAllByQmdId(Long qmdId);

}
