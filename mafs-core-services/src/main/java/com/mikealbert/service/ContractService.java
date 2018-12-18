package com.mikealbert.service;

import java.util.Date;
import java.util.List;

import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.TimePeriodCalendarEnum;
import com.mikealbert.exception.MalBusinessException;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.ContractServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.vision.entity.Contract}(s) and {@link com.mikealbert.vision.entity.ContractLine}(s).
 * 
 * @see com.mikealbert.vision.entity.Contract
 * @see com.mikealbert.vision.entity.ContractLine
 * @see com.mikealbert.vision.service.ContractServiceImpl
 */
public interface ContractService {
	public ContractLine getLastActiveContractLine(FleetMaster fleetMaster, Date date);
	public ContractLine getCurrentContractLine(FleetMaster fleetMaster, Date date);//TODO Need to analyze whether method getActiveContractLine and method getCurrentContractLine can be merged.
	public ContractLine getPendingLiveContractLine(FleetMaster fleetMaster, Date date);	
	public ContractLine getOriginalContractLine(FleetMaster fleetMaster);
	public ContractLine getOriginalContractLine(FleetMaster fleetMaster, ExternalAccount currentAccount);
	public boolean isWithinTimePeriod(CorporateEntity corporateEntity, TimePeriodCalendarEnum calendarType, Date date);
	public ContractLine getQuotationModelOnContractLine(Long clnId) throws MalBusinessException;
	public ContractLine getCurrentOrFutureContractLine(FleetMaster fleetmaster);
	public ContractLine getDriverOnContractLine(Long clnId) throws MalBusinessException;
	public ContractLine getActiveContractLine(FleetMaster fleetMaster, Date date);
	public List<Contract> getContracts(FleetMaster fleetMaster);
	public List<ContractLine> getContractLinesOfLastestContract(Long fmsId);
}
