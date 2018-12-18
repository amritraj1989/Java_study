package com.mikealbert.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mikealbert.data.beanvalidation.MANotNull;

/**
* Mapped to LATEST_ODOMETER_READINGS_V view.
* @author sibley
*/
@Entity
@Table(name = "LATEST_ODOMETER_READINGS_V")
public class LatestOdometerReadingV implements Serializable  {
	private static final long serialVersionUID = 1L;
	
    @Id
    @Column(name = "FMS_ID")
    private Long fmsId;
    
    @Column(name = "ODO_ID")
    private Long odoId;
    
    @Column(name = "ODOR_ID")
    private Long odorId;
    
    @Column(name = "ODO_READING")
    private Long odoReading;
    
    @Column(name = "ODO_READING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date odoReadingDate;
    
    @Size(max = 10)
    @Column(name = "ODO_READING_TYPE")
    private String odoReadingType;
    
    @Size(max = 10)
    @Column(name = "ODO_SRC")
    private String odoSrc;
    
    @Column(name = "ODO_PRIOR")
    private Long odoPrior;
    
    @Column(name = "CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    public Long getFmsId() {
        return fmsId;
    }

    public void setFmsId(Long fmsId) {
        this.fmsId = fmsId;
    }

    public Long getOdoId() {
        return odoId;
    }

    public void setOdoId(Long odoId) {
        this.odoId = odoId;
    }

    public Long getOdorId() {
        return odorId;
    }

    public void setOdorId(Long odorId) {
        this.odorId = odorId;
    }

    public Long getOdoReading() {
        return odoReading;
    }

    public void setOdoReading(Long odoReading) {
        this.odoReading = odoReading;
    }

    public Date getOdoReadingDate() {
        return odoReadingDate;
    }

    public void setOdoReadingDate(Date odoReadingDate) {
        this.odoReadingDate = odoReadingDate;
    }

    public String getOdoReadingType() {
        return odoReadingType;
    }

    public void setOdoReadingType(String odoReadingType) {
        this.odoReadingType = odoReadingType;
    }

    public String getOdoSrc() {
        return odoSrc;
    }

    public void setOdoSrc(String odoSrc) {
        this.odoSrc = odoSrc;
    }

    public Long getOdoPrior() {
        return odoPrior;
    }

    public void setOdoPrior(Long odoPrior) {
        this.odoPrior = odoPrior;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
   
}
