package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.mikealbert.util.MALUtilities;


/**
 * The persistent class for the VISION.LOG_BOOK_ENTRIES database table.
 * @author sibley
 */
@Entity
@Table(name="LOG_BOOK_ENTRIES")
public class LogBookEntry extends BaseEntity implements Serializable {  
	

	private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LBE_SEQ")    
    @SequenceGenerator(name="LBE_SEQ", sequenceName="LBE_SEQ", allocationSize=1)      
    @Column(name = "LBE_ID", nullable=false)
    private Long lbeId;
    
    @Column(name = "ENTRY_DATE", nullable = false)
    private Date entryDate;
    
    @Size(min = 1, max = 80)
    @Column(name = "ENTRY_USER")
    private String entryUser;
    
    @Size(min = 1, max = 200)
    @Column(name = "DETAIL")
    private String detail;  
    
    @Column(name = "FOLLOW_UP_DATE")
    private Date followUpDate;    
    
    @JoinColumn(name = "OLB_OLB_ID", referencedColumnName = "OLB_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ObjectLogBook objectLogBook; 
    
    public LogBookEntry() {}


	public Long getLbeId() {
		return lbeId;
	}


	public void setLbeId(Long lbeId) {
		this.lbeId = lbeId;
	}


	public Date getEntryDate() {
		return entryDate;
	}


	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}


	public String getEntryUser() {
		return entryUser;
	}


	public void setEntryUser(String entryUser) {
		this.entryUser = entryUser;
	}


	public String getDetail() {
		return detail;
	}


	public void setDetail(String detail) {
		this.detail = detail;
	}


	public Date getFollowUpDate() {
		return followUpDate;
	}


	public void setFollowUpDate(Date followUpDate) {
		this.followUpDate = followUpDate;
	}


	public ObjectLogBook getObjectLogBook() {
		return objectLogBook;
	}


	public void setObjectLogBook(ObjectLogBook objectLogBook) {
		this.objectLogBook = objectLogBook;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((entryDate == null) ? 0 : entryDate.hashCode());
		result = prime * result + ((entryUser == null) ? 0 : entryUser.hashCode());
		result = prime * result + ((followUpDate == null) ? 0 : followUpDate.hashCode());
		result = prime * result + ((lbeId == null) ? 0 : lbeId.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogBookEntry other = (LogBookEntry) obj;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (entryDate == null) {
			if (other.entryDate != null)
				return false;
		} else if (!entryDate.equals(other.entryDate))
			return false;
		if (entryUser == null) {
			if (other.entryUser != null)
				return false;
		} else if (!entryUser.equals(other.entryUser))
			return false;
		if (followUpDate == null) {
			if (other.followUpDate != null)
				return false;
		} else if (!MALUtilities.getNullSafeDatetoString(followUpDate)
				.equals(MALUtilities.getNullSafeDatetoString(other.followUpDate)))//some time date format is different 
			return false;
		if (lbeId == null) {
			if (other.lbeId != null)
				return false;
		} else if (!lbeId.equals(other.lbeId))
			return false;
		return true;
	}

	
	@Override
    public String toString() {
        return "com.mikealbert.data.entity.LogBookEntry[ code=" + getLbeId() + " ]";
    }
  	
}