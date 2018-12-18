package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mikealbert.util.MALUtilities;

public class AmendmentHistoryVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String SOURCE_ORIGINAL = "Original";
	private final String SOURCE_AMENDMENT = "Amendment";
	private final String SOURCE_FORMAL_EXT = "Formal Extension";
	private final String SOURCE_REVISION = "Revision";
	private final String SOURCE_INFORMAL = "Informal";

	private Long qmdId;
	private String quote;
	private Date effectiveDate;
	private Long effectivePeriod;
	private String createdBy;
	private String informalAmendmentText;
	private String noChangeInfo;
	private BigDecimal leaseRate;
	private BigDecimal dealCost;
	private BigDecimal customerCost;
	private String amendmentSource;

	private boolean OERevision;
	private BigDecimal capitalContribution;

	private List<EleAmendmentDetailVO> afterMarketEquipments = new ArrayList<EleAmendmentDetailVO>();
	private List<EleAmendmentDetailVO> serviceElements = new ArrayList<EleAmendmentDetailVO>();
	private List<EleAmendmentDetailVO> revisionElements = new ArrayList<EleAmendmentDetailVO>();

	public String getAmendmentTypeDescription() {
		if (MALUtilities.isEmpty(amendmentSource)) {
			return null;
		} else {
			if (amendmentSource.equals("A")) {
				return SOURCE_AMENDMENT;
			} else if (amendmentSource.equals("F")) {
				return SOURCE_FORMAL_EXT;
			} else if (amendmentSource.equals("R")) {
				return SOURCE_REVISION;
			} else if (amendmentSource.equals("I")) {
				return SOURCE_INFORMAL;
			} else if (amendmentSource.equals("O")) {
				return SOURCE_ORIGINAL;
			} else {
				return SOURCE_ORIGINAL;
			}
		}
	}

	public String getAmendmentSource() {
		return amendmentSource;
	}

	public void setAmendmentSource(String amendmentSource) {
		this.amendmentSource = amendmentSource;
	}

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	}

	public String getQuote() {
		return quote;
	}

	public void setQuote(String quote) {
		this.quote = quote;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Long getEffectivePeriod() {
		return effectivePeriod;
	}

	public void setEffectivePeriod(Long effectivePeriod) {
		this.effectivePeriod = effectivePeriod;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public List<EleAmendmentDetailVO> getAfterMarketEquipments() {
		return afterMarketEquipments;
	}

	public void setAfterMarketEquipments(
			List<EleAmendmentDetailVO> afterMarketEquipments) {
		this.afterMarketEquipments = afterMarketEquipments;
	}

	public List<EleAmendmentDetailVO> getServiceElements() {
		return serviceElements;
	}

	public void setServiceElements(List<EleAmendmentDetailVO> serviceElements) {
		this.serviceElements = serviceElements;
	}

	public String getInformalAmendmentText() {
		return informalAmendmentText;
	}

	public void setInformalAmendmentText(String informalAmendmentText) {
		this.informalAmendmentText = informalAmendmentText;
	}

	public String getNoChangeInfo() {
		return noChangeInfo;
	}

	public void setNoChangeInfo(String noChangeInfo) {
		this.noChangeInfo = noChangeInfo;
	}

	public BigDecimal getLeaseRate() {
		return leaseRate;
	}

	public void setLeaseRate(BigDecimal leaseRate) {
		this.leaseRate = leaseRate;
	}

	public BigDecimal getDealCost() {
		return dealCost;
	}

	public void setDealCost(BigDecimal dealCost) {
		this.dealCost = dealCost;
	}

	public BigDecimal getCustomerCost() {
		return customerCost;
	}

	public void setCustomerCost(BigDecimal customerCost) {
		this.customerCost = customerCost;
	}

	public BigDecimal getCapitalContribution() {
		return capitalContribution;
	}

	public void setCapitalContribution(BigDecimal capitalContribution) {
		this.capitalContribution = capitalContribution;
	}

	public List<EleAmendmentDetailVO> getRevisionElements() {
		return revisionElements;
	}

	public void setRevisionElements(List<EleAmendmentDetailVO> revisionElements) {
		this.revisionElements = revisionElements;
	}

	public boolean isOERevision() {
		return OERevision;
	}

	public void setOERevision(boolean oERevision) {
		OERevision = oERevision;
	}

}
