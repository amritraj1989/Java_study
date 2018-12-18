package com.mikealbert.data.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import com.mikealbert.data.entity.LogBook;
import com.mikealbert.data.vo.TALFileNoteVO;


public  class LogBookDAOImpl extends GenericDAOImpl<LogBook, Long> implements LogBookDAOCustom {

	private static final long serialVersionUID = 3019311186087012141L;

	public List<TALFileNoteVO> findTALFileNotesExcludeLetterInOut(Long fmsId){
		Query query = null;
		List<TALFileNoteVO> notes = new ArrayList<TALFileNoteVO>();
		TALFileNoteVO note;
		String stmt = "SELECT * FROM TABLE(vision.tal_wrapper.findFileNotesExcludeLeterInOut(:fmsId))";	
				
		query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("fmsId", fmsId);
		List<Object[]>results = (List<Object[]>)query.getResultList();			
		if(results != null){
			for(Object[] record : results){
				int i = 0;
				note = new TALFileNoteVO();
				note.setDryId(((BigDecimal)record[i]).longValue());
				note.setFmsId(((BigDecimal)record[i+=1]).longValue());	
				note.setDescription((String)record[i+=1]);
				note.setDetail((String)record[i+=1]);
				note.setEntryUser((String)record[i+=1]);
				note.setEntryDate((Date)record[i+=1]);	
				notes.add(note);
			}
		}
		
		return notes;
	}
	

}
