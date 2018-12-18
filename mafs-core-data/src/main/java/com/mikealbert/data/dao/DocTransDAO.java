package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mikealbert.data.entity.DocTran;
import com.mikealbert.data.entity.DocTranPK;

public interface DocTransDAO  extends	JpaRepository<DocTran, DocTranPK>{
     
		  
	@Query("SELECT dt FROM DocTran dt WHERE  dt.id.cId = ?1 and dt.id.docType = ?2 and dt.id.tranType = ?3  and dt.disable = ?4 ")
	public List<DocTran> findDocTran(long cID, String userDef1 , String lineType, String disable);
	
	@Query("SELECT dt FROM DocTran dt WHERE  dt.id.cId = ?1 and dt.id.docType = ?2 and dt.id.tranType = ?3")
	public DocTran findByCidDocTypeTranType(long cID, String userDef1 , String lineType);
				  

}
