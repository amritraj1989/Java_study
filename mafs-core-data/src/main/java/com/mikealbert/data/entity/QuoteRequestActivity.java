package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the QUOTE_REQUEST_ACTIVITY database table.
 * 
 */
@Entity
@Table(name="QUOTE_REQUEST_ACTIVITIES")
public class QuoteRequestActivity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="QRA_SEQ")    
    @SequenceGenerator(name="QRA_SEQ", sequenceName="QRA_SEQ", allocationSize=1)
    @Column(name = "QRA_ID")
    private Long qraId;
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="ACTIVITY_DATE")
	private Date activityDate;
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="SUBMITTED_DATE")
	private Date submittedDate;    
    
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name="COMPLETED_DATE")
	private Date completedDate;     
    
	@ManyToOne
	@JoinColumn(name="QRQ_QRQ_ID")
	private QuoteRequest quoteRequest;    
	
	@ManyToOne
	@JoinColumn(name="QRAT_QRAT_ID")
	private QuoteRequestActivityType quoteRequestActivityType;  
	
	
    public QuoteRequestActivity() {}
    
	public Long getQraId() {
		return qraId;
	}

	public void setQraId(Long qraId) {
		this.qraId = qraId;
	}


	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

	public QuoteRequestActivityType getQuoteRequestActivityType() {
		return quoteRequestActivityType;
	}

	public void setQuoteRequestActivityType(QuoteRequestActivityType quoteRequestActivityType) {
		this.quoteRequestActivityType = quoteRequestActivityType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getQraId() == null) ? 0 : getQraId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof QuoteRequestActivity))
			return false;
		QuoteRequestActivity other = (QuoteRequestActivity) obj;
		if (getQraId() == null) {
			if (other.getQraId() != null)
				return false;
		} else if (!getQraId().equals(other.getQraId()))
			return false;
		return true;
	}

	
}