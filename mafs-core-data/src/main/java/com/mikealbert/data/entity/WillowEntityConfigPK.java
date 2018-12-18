package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the WILLOW_ENTITY_CONFIG database table.
 * 
 */
@Embeddable
public class WillowEntityConfigPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="C_ID")
	private long cId;

	@Column(name="CONFIG_NAME")
	private String configName;

    public WillowEntityConfigPK() {
    }
	public long getCId() {
		return this.cId;
	}
	public void setCId(long cId) {
		this.cId = cId;
	}
	public String getConfigName() {
		return this.configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WillowEntityConfigPK)) {
			return false;
		}
		WillowEntityConfigPK castOther = (WillowEntityConfigPK)other;
		return 
			(this.cId == castOther.cId)
			&& this.configName.equals(castOther.configName);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.cId ^ (this.cId >>> 32)));
		hash = hash * prime + this.configName.hashCode();
		
		return hash;
    }
}