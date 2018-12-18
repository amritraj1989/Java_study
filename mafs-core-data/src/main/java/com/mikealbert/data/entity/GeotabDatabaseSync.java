package com.mikealbert.data.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "GEOTAB_DATABASE_SYNC")
public class GeotabDatabaseSync implements Serializable{
	private static final long serialVersionUID = 1L;
    
	   
	@Id
    @NotNull
    @Column(name = "GDS_ID")
    private Long gdsId;

	@Column(name = "EA_C_ID")
    private Long cId;
	
	@Column(name = "EA_ACCOUNT_TYPE")
    private String accountType;
	
	@Column(name = "EA_ACCOUNT_CODE")
    private String accountCode;

    @Column(name = "GEOTAB_ACCOUNT_DATABASE")
    private String geotabAccountDatabase;
    
    @Column(name = "ACTIVE_IND")
    private String activeInd;
    
    public GeotabDatabaseSync() {
    }
    
    public GeotabDatabaseSync(Long gdsId, String accountCode, String geotabAccountDatabase) {
        this.gdsId = gdsId;
        this.accountCode = accountCode;
        this.geotabAccountDatabase = geotabAccountDatabase;
    }
    
    public Long getGdsId() {
		return gdsId;
	}

	public void setGdsId(Long gdsId) {
		this.gdsId = gdsId;
	}

	public Long getcId() {
		return cId;
	}

	public void setcId(Long cId) {
		this.cId = cId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	public String getGeotabAccountDatabase() {
		return geotabAccountDatabase;
	}

	public void setGeotabAccountDatabase(String geotabAccountDatabase) {
		this.geotabAccountDatabase = geotabAccountDatabase;
	}

	public String getActiveInd() {
		return activeInd;
	}

	public void setActiveInd(String activeInd) {
		this.activeInd = activeInd;
	}

}
