package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="UPFITTER_PROGRESS")
public class UpfitterProgress extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 8629514261507412372L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="UFP_SEQ")    
    @SequenceGenerator(name="UFP_SEQ", sequenceName="UFP_SEQ", allocationSize=1)	
    @Column(name = "UFP_ID")
    private Long ufpId;
    
	@NotNull
    @Column(name = "SEQUENCE_NO")
    private Long sequenceNo;

    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    
	@Column(name="UPDATED_BY")
	private String updatedBy;    
	
	@Size(min=1, max=1)
	@Column(name="BAILMENT_YN")
	private String bailmentYN;  
	
	@Transient
	private Date persistedStartDate;
	
	@Transient
	private Date persistedEndDate;
	
	@Transient
	private UpfitterProgress persistedParentUpfitterProgress;	
       
    @JoinColumn(name = "QMD_QMD_ID", referencedColumnName = "QMD_ID", insertable= false, updatable = false)
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private QuotationModel quotationModel;	    

    @Column(name = "QMD_QMD_ID")
    private Long qmdId;	
    
    @JoinColumns({
        @JoinColumn(name = "EA_C_ID", referencedColumnName = "C_ID"),
        @JoinColumn(name = "EA_ACCOUNT_TYPE", referencedColumnName = "ACCOUNT_TYPE"),
        @JoinColumn(name = "EA_ACCOUNT_CODE", referencedColumnName = "ACCOUNT_CODE")})
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private ExternalAccount upfitter;
    
	@OneToMany(mappedBy = "parentUpfitterProgress")
    private List<UpfitterProgress> childrenUpfitterProgress;  
	
    @JoinColumn(name = "UFP_UFP_ID")
    @ManyToOne
    private UpfitterProgress parentUpfitterProgress;	
    
	public UpfitterProgress(){}

	public Long getUfpId() {
		return ufpId;
	}

    public void setUfpId(Long ufpId) {
		this.ufpId = ufpId;
	}

	public Long getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Long sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	public String getBailmentYN() {
		return bailmentYN;
	}

	public void setBailmentYN(String bailmentYN) {
		this.bailmentYN = bailmentYN;
	}	

	public QuotationModel getQuotationModel() {
		return quotationModel;
	}

	public void setQuotationModel(QuotationModel quotationModel) {
		this.quotationModel = quotationModel;
	}

	public ExternalAccount getUpfitter() {
		return upfitter;
	}

	public void setUpfitter(ExternalAccount upfitter) {
		this.upfitter = upfitter;
	}

	public List<UpfitterProgress> getChildrenUpfitterProgress() {
		return childrenUpfitterProgress;
	}

	public void setChildrenUpfitterProgress(List<UpfitterProgress> childrenUpfitterProgress) {
		this.childrenUpfitterProgress = childrenUpfitterProgress;
	}

	public UpfitterProgress getParentUpfitterProgress() {
		return parentUpfitterProgress;
	}

	public void setParentUpfitterProgress(UpfitterProgress parentUpfitterProgress) {
		this.parentUpfitterProgress = parentUpfitterProgress;
	}    
	
	public Date getPersistedStartDate() {
		return persistedStartDate;
	}

	public void setPersistedStartDate(Date persistedStartDate) {
		this.persistedStartDate = persistedStartDate;
	}

	public Date getPersistedEndDate() {
		return persistedEndDate;
	}

	public void setPersistedEndDate(Date persistedEndDate) {
		this.persistedEndDate = persistedEndDate;
	}

	public UpfitterProgress getPersistedParentUpfitterProgress() {
		return persistedParentUpfitterProgress;
	}

	public void setPersistedParentUpfitterProgress(
			UpfitterProgress persistedParentUpfitterProgress) {
		this.persistedParentUpfitterProgress = persistedParentUpfitterProgress;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (this.ufpId != null ? this.ufpId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UpfitterProgress)) {
            return false;
        }
        
        UpfitterProgress other = (UpfitterProgress) object;
        if ((this.getUfpId() == null && other.getUfpId() != null) || (this.getUfpId() != null && !this.getUfpId().equals(other.getUfpId()))) {
            return false;
        }
        
        return true;
    }

	public Long getQmdId() {
		return qmdId;
	}

	public void setQmdId(Long qmdId) {
		this.qmdId = qmdId;
	} 
    
    
}
