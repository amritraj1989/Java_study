package com.mikealbert.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.ReportsSpoolDAO;
import com.mikealbert.data.entity.ReportsSpool;
import com.mikealbert.exception.MalException;


@Service("reportsSpoolService")
public class ReportsSpoolServiceImpl implements ReportsSpoolService {
	
	@Resource
	ReportsSpoolDAO reportsSpoolDAO;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addReportsSpool(List<ReportsSpool> reportsSpoolList) {
		try {
			reportsSpoolDAO.saveAll(reportsSpoolList);
			reportsSpoolDAO.flush();
			
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "saving report spool" }, ex);
		}
	}

	@Override
	public Long generateId() {
		return reportsSpoolDAO.generateId();
	}

	

}
