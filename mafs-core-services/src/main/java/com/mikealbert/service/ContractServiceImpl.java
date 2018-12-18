package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ContractLineDAO;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.TimePeriodDAO;
import com.mikealbert.data.entity.Contract;
import com.mikealbert.data.entity.ContractLine;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.TimePeriod;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.enumeration.TimePeriodCalendarEnum;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.ContractService}
 */
@Service("contractService")
@Transactional
public class ContractServiceImpl implements ContractService {
	@Resource
	ContractLineDAO contractLineDAO;

	@Resource
	TimePeriodDAO timePeriodDAO;

	@Resource
	FleetMasterDAO fleetMasterDAO;

	/**
	 * Retrieves the last (latest) active contract line for a given unit on a
	 * given date; In order for a contract line to be considered last active,
	 * the contract must have started before the input parameter date. <br>
	 * 
	 * @param fleetMaster
	 *            Unit to find contract lines for
	 * @param date
	 *            Used to check whether the contract line was active on this
	 *            date
	 * @return Active contract line as ContractLine
	 * @see com.mikealbert.vision.service.ContractService
	 * @see com.mikealbert.vision.entity.ContractLine
	 * */
	public ContractLine getLastActiveContractLine(FleetMaster fleetMaster,
			Date date) {
		List<Contract> contracts = new ArrayList<Contract>();
		List<ContractLine> contractLines = new ArrayList<ContractLine>();
		ContractLine activeContractLine = null;
		Date inServiceDate = null;

		try {
			List<ContractLine> allContractLines = fleetMasterDAO.getContractLineList(fleetMaster.getFmsId());
			Collections.sort(allContractLines, new Comparator<ContractLine>() {
				public int compare(ContractLine cn1, ContractLine cn2) {
					return cn2.getClnId().compareTo(cn1.getClnId());
				}
			});
			for(ContractLine cl : allContractLines){
				if(cl.getInServDate() != null){
					inServiceDate = cl.getInServDate();
					break;
				}
			}
			// Retrieve a distinct list of parent contract(s) for the contract
			// line(s)
			contracts = contracts(allContractLines);
			// Retrieve the contract line with the latest revision for each of
			// the contracts
			contractLines = orderLinesByRevision(contracts,
					MalConstants.SORT_DESC);

			// Retrieve the contract line with the latest start date before
			// specified date.
			activeContractLine = activeLine(contractLines, date, inServiceDate);

		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "getting last active contract line: "
							+ fleetMaster }, ex);
		}
		return activeContractLine;
	}

	/**
	 * Retrieves the active contract line for a given unit on a given date; In
	 * order for a contract line to be considered active, the contract must have
	 * started before the input parameter date and cannot have ended before the
	 * input parameter date <br>
	 * 
	 * @param fleetMaster
	 *            Unit to find contract lines for
	 * @param date
	 *            Used to check whether the contract line was active on this
	 *            date
	 * @return Active contract line as ContractLine
	 * @see com.mikealbert.vision.service.ContractService
	 * @see com.mikealbert.vision.entity.ContractLine
	 * */
	public ContractLine getActiveContractLine(FleetMaster fleetMaster, Date date) {
		ContractLine activeContractLine = null;
		try {
			activeContractLine = getLastActiveContractLine(fleetMaster, date);
			if (activeContractLine != null) {
				// if the end date is in the past
				if (activeContractLine.getActualEndDate() != null
						&& activeContractLine.getActualEndDate()
								.compareTo(date) < 0) {
					// return "null" because this contract has already been
					// terminated
					activeContractLine = null;
				}
			}
		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "getting an active contract line: "
							+ fleetMaster }, ex);
		}
		return activeContractLine;
	}

	/**
	 * This method is replica of method getActiveContractLine() with minor
	 * difference. This will be used for story FM-1361 TODO We will create an
	 * AIC story to determine whether these changes should be merged and the QA
	 * effort around this.
	 * */
	public ContractLine getCurrentContractLine(FleetMaster fleetMaster,
			Date date) {
		List<Contract> contracts = new ArrayList<Contract>();
		List<ContractLine> contractLines = new ArrayList<ContractLine>();
		ContractLine activeContractLine = null;

		try {
			// Retrieve a distinct list of parent contract(s) for the contract
			// line(s)
			contracts = contracts(fleetMasterDAO.getContractLineList(fleetMaster.getFmsId()));
			// Retrieve the contract line with the latest revision for each of
			// the contracts
			contractLines = orderLinesByRevision(contracts,
					MalConstants.SORT_DESC);

			// Retrieve the contract line with the latest start date before
			// specified date.
			activeContractLine = currentLine(contractLines, date);

		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "getting a current contract line: "
							+ fleetMaster }, ex);
		}
		return activeContractLine;
	}

	/**
	 * Retrieves the pending live contract line for a given unit on a given
	 * date; In order for a contract line to be considered pending live, the
	 * contract must have an in service date and a null start date or a start
	 * date that is in the future. <br>
	 * 
	 * @param fleetMaster
	 *            Unit to find contract lines for
	 * @param date
	 *            Used to check whether the contract line is active on this date
	 * @return Pending Live contract line as ContractLine
	 * @see com.mikealbert.vision.service.ContractService
	 * @see com.mikealbert.vision.entity.ContractLine
	 * */
	public ContractLine getPendingLiveContractLine(FleetMaster fleetMaster,
			Date date) {
		List<Contract> contracts = new ArrayList<Contract>();
		List<ContractLine> contractLines = new ArrayList<ContractLine>();
		ContractLine activeContractLine = null;

		try {
			// Retrieve a distinct list of parent contract(s) for the contract
			// line(s)
			contracts = contracts(fleetMasterDAO.getContractLineList(fleetMaster.getFmsId()));
			// Retrieve the contract line with the latest revision for each of
			// the contracts
			contractLines = orderLinesByRevision(contracts,
					MalConstants.SORT_DESC);

			// Retrieve the contract line with the latest start date before
			// specified date.
			activeContractLine = pendingLiveLine(contractLines, date);

		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "getting an active contract line: "
							+ fleetMaster }, ex);
		}
		return activeContractLine;
	}

	/**
	 * Retrieves the original contract line for a given unit. An original
	 * contract line is defined as the first contract line ever to be created
	 * for the unit. <br>
	 * NOTE: Method overloading to support requests for the original contract
	 * line of the current client account as well. <br>
	 * Used by: <br>
	 * 1) TBD <br>
	 * 
	 * @param fleetMaster
	 *            Unit to find contract lines for
	 * @return Original contract line as ContractLine
	 * @see com.mikealbert.vision.service.ContractService
	 * @see com.mikealbert.vision.entity.ContractLine
	 * */
	public ContractLine getOriginalContractLine(FleetMaster fleetMaster) {

		return getOriginalContractLine(fleetMaster, null);
	}

	public ContractLine getOriginalContractLine(FleetMaster fleetMaster,
			ExternalAccount currentAccount) {
		List<Contract> contracts = new ArrayList<Contract>();
		Contract contract = null;
		ContractLine contractLine = null;
		ContractLine originalContractLine = null;

		try {
			// Retrieve a distinct list of contract(s) for the contract line(s)
			List<ContractLine> contractLineList = fleetMasterDAO.getContractLineList(fleetMaster.getFmsId());
			for (ContractLine cl : contractLineList) {
				if (!contracts.contains(cl.getContract()))
					contracts.add(cl.getContract());
			}
			List<Contract> filteredContracts = new ArrayList<Contract>(); // Used
																			// a
																			// New
																			// list
																			// for
																			// Bug
																			// 16418
			// filter list by current account if passed in
			filteredContracts.addAll(contracts);
			if (currentAccount != null) {
				for (Contract c : filteredContracts) { // Used a New list for
														// Bug 16418
					if (!c.getExternalAccount().getExternalAccountPK()
							.equals(currentAccount.getExternalAccountPK())) {
						contracts.remove(c);
					}
				}
			}

			// Sorting the list of distinct contracts on rev date in ASC order
			Collections.sort(contracts, new Comparator<Contract>() {
				public int compare(Contract c1, Contract c2) {
					return c1.getRevDate().compareTo(c2.getRevDate());
				}
			});

			// From the sorted distinct list of contracts, the first contract
			// should be the original
			// of the unit. Using this contract to determine the first contract
			// line.
			if (contracts.size() > 0) {
				contract = contracts.get(0);
				for (Iterator<ContractLine> cl = contract.getContractLineList()
						.iterator(); cl.hasNext()
						&& originalContractLine == null;) {
					contractLine = cl.next();
					if (contractLine.getRevNo() == 1) {
						originalContractLine = contractLine;
					}
				}
			}

		} catch (Exception ex) {
			throw new MalException("generic.error.occured.while",
					new String[] { "getting an original contract line: "
							+ fleetMaster }, ex);
		}

		return originalContractLine;
	}

	/**
	 * Retrieve a distinct list of parent contract(s) for the contract line(s)
	 * 
	 * @param contractLines
	 * @return List of contracts
	 */
	private List<Contract> contracts(List<ContractLine> contractLines) {
		List<Contract> contracts = new ArrayList<Contract>();
		for (ContractLine cl : contractLines) {
			if (!contracts.contains(cl.getContract()))
				contracts.add(cl.getContract());
		}
		return contracts;
	}

	/**
	 * Retrieve the contract line with the latest revision
	 * 
	 * @param contracts
	 * @param sort
	 *            DESC gets the contracts' line with the max revision. ASC gets
	 *            the contracts' line with the lowest revision.
	 * @return
	 */
	private List<ContractLine> orderLinesByRevision(List<Contract> contracts,
			String sort) {
		List<ContractLine> contractLines = new ArrayList<ContractLine>();
		for (Contract c : contracts) {
			if (sort.equals(MalConstants.SORT_DESC)) {
				Collections.sort(c.getContractLineList(),
						new Comparator<ContractLine>() {
							public int compare(ContractLine cn1,
									ContractLine cn2) {
								return cn2.getRevNo().compareTo(cn1.getRevNo());
							}
						});
			} else {
				Collections.sort(c.getContractLineList(),
						new Comparator<ContractLine>() {
							public int compare(ContractLine cn1,
									ContractLine cn2) {
								return cn1.getRevNo().compareTo(cn2.getRevNo());
							}
						});
			}

			contractLines.add(c.getContractLineList().get(0));
		}
		return contractLines;
	}

	/**
	 * Retrieves the active contract line for a given unit on a given date. In
	 * order for a contract line to be considered active, the contract must have
	 * started before the input parameter date.
	 * 
	 * @param contractLines
	 * @param date
	 * @return Active contract line
	 */
	private ContractLine activeLine(List<ContractLine> contractLines, Date date, Date inserviceDate) {
		ContractLine activeContractLine = null;
		Collections.sort(contractLines, new Comparator<ContractLine>() {
			public int compare(ContractLine cn1, ContractLine cn2) {
				if (cn1.getStartDate() == null || cn2.getStartDate() == null)
					return 0;
				return cn2.getStartDate().compareTo(cn1.getStartDate());
			}
		});
		if(inserviceDate != null){
			if(inserviceDate.compareTo(date) <= 0){
				if (contractLines.size() > 0){
					activeContractLine = contractLines.get(0);
				}
			}
		}
		if(activeContractLine == null){
			if (contractLines.size() > 0) {
				for (ContractLine cl : contractLines) {
					if (cl.getStartDate() != null) {
						if (cl.getStartDate().compareTo(date) <= 0) {
							activeContractLine = cl;
							break;
						}
					} else {
						activeContractLine = cl;
					}
				}
			}
		}
		return activeContractLine;
	}

	/**
	 * This method is replica of method activeLine() with minor difference. This
	 * will be used for story FM-1361 TODO We will create an AIC story to
	 * determine whether these changes should be merged and the QA effort around
	 * this.
	 * */
	private ContractLine currentLine(List<ContractLine> contractLines, Date date) {
		ContractLine activeContractLine = null;
		Collections.sort(contractLines, new Comparator<ContractLine>() {
			public int compare(ContractLine cn1, ContractLine cn2) {
				if (cn1.getStartDate() == null || cn2.getStartDate() == null)
					return 0;
				return cn2.getStartDate().compareTo(cn1.getStartDate());
			}
		});
		if (contractLines.size() > 0) {
			for (ContractLine cl : contractLines) {
				if (cl.getStartDate() == null) { // HPS-1973 Check for the
													// pending live unit
					if (cl.getInServDate().compareTo(
							MALUtilities.clearTimeFromDate(date)) <= 0) {
						activeContractLine = cl;
						break;
					}
				} else {// HPS-1973 Po start date should lie between in service
						// date and actual end date of contract, if actual end
						// date is NULL then PO start date>= Start date of
						// contract
					if (cl.getInServDate() != null) {
						if ((cl.getInServDate().compareTo(
								MALUtilities.clearTimeFromDate(date)) <= 0 && cl
								.getActualEndDate() == null)
								|| (cl.getInServDate().compareTo(
										MALUtilities.clearTimeFromDate(date)) <= 0 && MALUtilities
										.clearTimeFromDate(
												cl.getActualEndDate())
										.compareTo(
												MALUtilities
														.clearTimeFromDate(date)) >= 0)) {
							activeContractLine = cl;
							break;
						}
					} else {
						if ((cl.getStartDate().compareTo(
								MALUtilities.clearTimeFromDate(date)) <= 0 && cl
								.getActualEndDate() == null)
								|| (cl.getStartDate().compareTo(
										MALUtilities.clearTimeFromDate(date)) <= 0 && MALUtilities
										.clearTimeFromDate(
												cl.getActualEndDate())
										.compareTo(
												MALUtilities
														.clearTimeFromDate(date)) >= 0)) {
							activeContractLine = cl;
							break;

						}
					}
				}
			}
		}
		return activeContractLine;
	}

	/**
	 * Retrieves the pending live contract line for a given unit on a given
	 * date; In order for a contract line to be considered pending live, the
	 * contract must have an in service date and a null start date or a start
	 * date that is in the future.
	 * 
	 * @param contractLines
	 * @param date
	 * @return
	 */
	private ContractLine pendingLiveLine(List<ContractLine> contractLines,
			Date date) {
		ContractLine pendingLiveContractLine = null;

		Collections.sort(contractLines, new Comparator<ContractLine>() {
			public int compare(ContractLine cn1, ContractLine cn2) {
				if (cn1.getStartDate() == null || cn2.getStartDate() == null)
					return 0;
				return cn2.getStartDate().compareTo(cn1.getStartDate());
			}
		});

		if (contractLines.size() > 0) {
			for (ContractLine cl : contractLines) {
				if ((MALUtilities.isEmpty(cl.getStartDate()) || cl
						.getStartDate().compareTo(date) > 0)
						&& (!MALUtilities.isEmpty(cl.getInServDate()))) {
					pendingLiveContractLine = cl;
					break;
				}
			}
		}
		return pendingLiveContractLine;
	}

	/**
	 * TODO: add javadoc
	 */
	public boolean isWithinTimePeriod(CorporateEntity corporateEntity,
			TimePeriodCalendarEnum calendarType, Date date) {
		boolean isInTimePeriod;
		TimePeriod timePeriod = timePeriodDAO
				.findByStartDateAndEndDateAndCalendarAndCorporateId(date, date,
						calendarType.getCode(), corporateEntity.getCorpId());
		isInTimePeriod = MALUtilities.isEmpty(timePeriod) ? false : true;
		return isInTimePeriod;
	}

	// TODO: add javadoc
	public ContractLine getQuotationModelOnContractLine(Long clnId)
			throws MalBusinessException {

		ContractLine contractLine = null;
		if (clnId != null) {
			contractLine = contractLineDAO.findByClnIdWithQuotationModel(clnId);
		} else {
			throw new MalBusinessException("service.validation",
					new String[] { "Contract Line ID must be not null." });
		}
		return contractLine;
	}

	// TODO: add javadoc
	public ContractLine getCurrentOrFutureContractLine(FleetMaster fleetMaster) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DATE, 365);
		return getActiveContractLine(fleetMaster, calendar.getTime());
	}

	// TODO: add javadoc
	@Override
	public ContractLine getDriverOnContractLine(Long clnId)
			throws MalBusinessException {

		ContractLine contractLine = null;
		if (clnId != null) {
			contractLine = contractLineDAO.findByClnIdWithDriver(clnId);
		} else {
			throw new MalBusinessException("service.validation",
					new String[] { "Contract Line ID must be not null." });
		}
		return contractLine;

	}

	public List<Contract> getContracts(FleetMaster fleetMaster) {
		List<Contract> contracts = new ArrayList<Contract>();
		contracts = contracts(fleetMasterDAO.getContractLineList(fleetMaster.getFmsId()));
		return contracts;
	}
	
	public List<ContractLine> getContractLinesOfLastestContract(Long fmsId) {		
		
		List<ContractLine> latestContractLines = new ArrayList<ContractLine>();
		
		List<ContractLine>  allContractLines = contractLineDAO.findByFmsIdOrderByRev(fmsId);		
		 if(allContractLines != null && allContractLines.size() > 0){	
			 latestContractLines = allContractLines.get(allContractLines.size() -1).getContract().getContractLineList();
			 
			 Collections.sort(latestContractLines, new Comparator<ContractLine>() {
														public int compare(ContractLine cn1, ContractLine cn2) {
															return cn1.getRevNo().compareTo(cn2.getRevNo());
														}
													});
		 }
		 
		return latestContractLines;
	}
}
