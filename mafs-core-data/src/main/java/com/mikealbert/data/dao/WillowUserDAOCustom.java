package com.mikealbert.data.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.mikealbert.data.entity.PersonnelBase;
import com.mikealbert.data.vo.WillowUserLovVO;

public interface WillowUserDAOCustom{
	
	List<WillowUserLovVO> getWillowUserList(String willowUserNo, String willowUserName, String lovName, Pageable pageable);
	public int getWillowUserListCount(String willowUserNo, String willowUserName, String lovName);
	public List<PersonnelBase> getDebitCreditApproverList();
	
	public WillowUserLovVO getWillowUserByAdUserName(String adUsername);
}
