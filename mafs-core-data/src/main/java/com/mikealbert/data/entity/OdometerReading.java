package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
* Mapped to ODOMETERS_READINGS table.
* Note: Not all columns and associations have been mapped.
* @author sibley
*/
@Entity
@Table(name = "ODOMETERS_READINGS")
public class OdometerReading extends BaseEntity implements Serializable {
   private static final long serialVersionUID = 1L;
   
   @Id
   @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ODOR_SEQ")    
   @SequenceGenerator(name="ODOR_SEQ", sequenceName="ODOR_SEQ", allocationSize=1)
   @Basic(optional = false)
   @NotNull
   @Column(name = "ODOR_ID")
   private Long odorId;
   
   @Basic(optional = false)
   @NotNull
   @Size(min = 1, max = 10)
   @Column(name = "ODO_READING_TYPE")
   private String readingCode; 
   
   @Basic(optional = false)
   @NotNull
   @Column(name = "ODO_READING")
   private Long reading;
   
   @Basic(optional = false)
   @NotNull
   @Column(name = "ODO_READING_DATE")
   @Temporal(TemporalType.TIMESTAMP)
   private Date readingDate;
   
   @Size(max = 25)
   @Column(name = "USER_ID")
   private String userId;
   
   @Basic(optional = false)
   @NotNull
   @Size(min = 1, max = 1)
   @Column(name = "STMNT_IND")
   private String stmntInd;
   
   @Column(name = "STMNT_DATE")
   @Temporal(TemporalType.TIMESTAMP)
   private Date stmntDate;
   
/*   @Column(name = "MRQ_ID")
   private Long mrqId;   */
               
   @JoinColumn(name = "ODO_ID", referencedColumnName = "ODO_ID")
   @ManyToOne(optional = false)
   private Odometer odometer;

   
   @JoinColumn(name = "MRQ_ID", referencedColumnName = "MRQ_ID")
   @ManyToOne(fetch=FetchType.LAZY, optional = true)
   private MaintenanceRequest maintenanceRequest;
   
   public OdometerReading() {}

   public OdometerReading(Long odorId) {
       this.odorId = odorId;
   }

   public OdometerReading(String readingType, Long reading, Date odoReadingDate, String stmntInd) {
       this.readingCode = readingType;
       this.reading = reading;
       this.readingDate = odoReadingDate;
       this.stmntInd = stmntInd;
   }

   public Long getOdorId() {
       return odorId;
   }

   public void setOdorId(Long odorId) {
	   this.odorId = odorId;
   }

   public String getReadingCode() {
	   return readingCode;
   }

   public void setReadingCode(String readingCode) {
	   this.readingCode = readingCode;
   }

   public Long getReading() {
	   return reading;
   }

   public void setReading(Long reading) {
	   this.reading = reading;
   }

   public Date getReadingDate() {
       return readingDate;
   }

   public void setReadingDate(Date readingDate) {
       this.readingDate = readingDate;
   }

   public String getUserId() {
       return userId;
   }

   public void setUserId(String userId) {
       this.userId = userId;
   }

   public String getStmntInd() {
       return stmntInd;
   }

   public void setStmntInd(String stmntInd) {
       this.stmntInd = stmntInd;
   }

   public Date getStmntDate() {
       return stmntDate;
   }

   public void setStmntDate(Date stmntDate) {
       this.stmntDate = stmntDate;
   }

/*   public Long getMrqId() {
	   return mrqId;
   }*/

/*   public void setMrqId(Long mrqId) {
	   this.mrqId = mrqId;
   }*/

   public Odometer getOdometer() {
	   return odometer;
   }

    public void setOdometer(Odometer odometer) {
    	this.odometer = odometer;
	}

    public MaintenanceRequest getMaintenanceRequest() {
		return maintenanceRequest;
	}

	public void setMaintenanceRequest(MaintenanceRequest maintenanceRequest) {
		this.maintenanceRequest = maintenanceRequest;
	}

	@Override
    public int hashCode() {
    	int hash = 0;
    	hash += (odorId != null ? odorId.hashCode() : 0);
    	return hash;
    }

    @Override
    public boolean equals(Object object) {
    	// TODO: Warning - this method won't work in the case the id fields are not set
    	if (!(object instanceof OdometerReading)) {
    		return false;
    	}
    	OdometerReading other = (OdometerReading) object;
    	if ((this.odorId == null && other.odorId != null) || (this.odorId != null && !this.odorId.equals(other.odorId))) {
    		return false;
    	}
    	return true;
    }

    @Override
    public String toString() {
    	return "com.mikealbert.entity.OdometerReading[ odorId=" + odorId + " ]";
    }

}
