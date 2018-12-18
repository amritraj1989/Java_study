package com.mikealbert.data.dao;

import java.util.List;
import com.mikealbert.data.vo.TALFileNoteVO;


public interface LogBookDAOCustom  {	
	public List<TALFileNoteVO> findTALFileNotesExcludeLetterInOut(Long fmsId);
	
}
