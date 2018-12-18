package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="FUEL_TYPES")
public class FuelType implements Serializable{
	private static final long serialVersionUID = 5055262527098298255L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FTP_SEQ")    
    @SequenceGenerator(name="FTP_SEQ", sequenceName="FTP_SEQ", allocationSize=1)	
    @Column(name = "FTP_ID")
    private Long ftpId;	
	
	@NotNull
    @Column(name = "FUEL_TYPE_CODE")
    private String code;

	@NotNull
    @Column(name = "FUEL_DESC")
    private String description;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "FuelType [ftpId=" + ftpId + ", code=" + getCode() + ", description=" + getDescription() + "]";
	}
	
}
