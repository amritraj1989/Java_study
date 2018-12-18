package com.mikealbert.data.dao;

import java.util.List;


import com.mikealbert.data.vo.MaintenanceProgramVO;

public interface MaintenanceProgramDAOCustom {
	public List<MaintenanceProgramVO> getMaintenanceProgramsByQmdId(Long qmdId, Long cId, String accountType, String accountCode);
	//HD-419
	public boolean validationCheckForInformalUnit(Long qmdId);
	public String findElementOnQuote(Long qmdId,Long lelId);
	public int getleaseElementbyFmdId(Long qmdId);
	public Long getContractLinesfromfmsId(Long fmsId);
	public Long getQmdIdfromClnId(Long clnId);
	public Long getClnIdforDisposedUnit(Long fmsId);
	public Long getClnIdforReleaseUnit(Long fmsId);
}
