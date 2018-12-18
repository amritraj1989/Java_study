package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Mapped to ODOMETERS table.
 * Note: Not all columns and associations have been mapped.
 * @author sibley
 */
@Entity
@Table(name = "ODOMETERS")
public class Odometer extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ODO_SEQ")    
    @SequenceGenerator(name="ODO_SEQ", sequenceName="ODO_SEQ", allocationSize=1)
    @Basic(optional = false)
    @NotNull
    @Column(name = "ODO_ID")
    private Long odoId;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "FITTED_DATE")    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fittedDate;
    
    @Size(max = 10)
    @Column(name = "SERIAL_NUMBER")
    private String serialNumber;
    
    @Size(max = 10)
    @Column(name = "UOM_CODE")
    private String uomCode;    
    
    @Column(name = "REPLACED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date replacedDate;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "FINAL_ODO_READING")
    private Long finalOdoReading;
        
    @OneToMany(mappedBy = "odometer", fetch = FetchType.LAZY)
    private List<OdometerReading> odometerReadingList;    
    
    @JoinColumn(name = "FMS_ID", referencedColumnName = "FMS_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private FleetMaster fleetMaster;     

    public Odometer() {}

    public Odometer(Long odoId) {
        this.odoId = odoId;
    }

    public Odometer(Long odoId, Date fittedDate, Long finalOdoReading) {
        this.odoId = odoId;
        this.fittedDate = fittedDate;
        this.finalOdoReading = finalOdoReading;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Date getFittedDate() {
        return fittedDate;
    }

    public void setFittedDate(Date fittedDate) {
        this.fittedDate = fittedDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getUomCode() {
		return uomCode;
	}

	public void setUomCode(String uomCode) {
		this.uomCode = uomCode;
	}

	public Date getReplacedDate() {
        return replacedDate;
    }

    public void setReplacedDate(Date replacedDate) {
        this.replacedDate = replacedDate;
    }

    public Long getFinalOdoReading() {
        return finalOdoReading;
    }

    public void setFinalOdoReading(Long finalOdoReading) {
        this.finalOdoReading = finalOdoReading;
    }

    public List<OdometerReading> getOdometerReadingList() {
		return odometerReadingList;
	}

	public void setOdometerReadingList(List<OdometerReading> odometerReadingList) {
		this.odometerReadingList = odometerReadingList;
	}

	public FleetMaster getFleetMaster() {
		return fleetMaster;
	}

	public void setFleetMaster(FleetMaster fleetMaster) {
		this.fleetMaster = fleetMaster;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (odoId != null ? odoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Odometer)) {
            return false;
        }
        Odometer other = (Odometer) object;
        if ((this.odoId == null && other.odoId != null) || (this.odoId != null && !this.odoId.equals(other.odoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mikealbert.entity.Odometers[ odoId=" + odoId + " ]";
    }
    
}


