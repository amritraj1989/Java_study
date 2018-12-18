package com.mikealbert.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Mapped to TELEMATICS_MILEAGE table
 * @author Scholle
 */
@Entity
@Table(name = "TELEMATICS_MILEAGE")
public class TelematicsMileage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TELEMATICS_MILEAGE_SEQ")
    @SequenceGenerator(name="TELEMATICS_MILEAGE_SEQ", sequenceName="TELEMATICS_MILEAGE_SEQ", allocationSize=1)
    @NotNull
    @Column(name = "ODOR_ID")
	private Long odorID;

	@NotNull
	@Column(name = "FMS_ID")
	private Long fmsID;

	@NotNull
	@Column(name = "ODO_READING")
	private Long odoReading;

	@NotNull
	@Column(name = "ODO_READING_DATE")
	private Date odoReadingDate;

	@NotNull
	@Column(name = "CREATE_DATE")
	private Date createDate;

	public Long getOdorID() {
		return odorID;
	}

	public Long getFmsID() {
		return fmsID;
	}

	public Long getOdoReading() {
		return odoReading;
	}

	public Date getOdoReadingDate() {
		return odoReadingDate;
	}

	public Date getCreateDate() {
		return createDate;
	}	
	
	public void setOdorID(Long odorID) {
		this.odorID = odorID;
	}
	
	public void setFmsID(Long fmsID) {
		this.fmsID = fmsID;
	}	
	
	public void setOdoReading(Long odoReading) {
		this.odoReading = odoReading;
	}	
	
	public void setOdoReadingDate(Date odoReadingDate) {
		this.odoReadingDate = odoReadingDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
}
