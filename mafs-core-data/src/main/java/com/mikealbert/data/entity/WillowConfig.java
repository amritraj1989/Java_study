package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the WILLOW_CONFIG database table.
 * 
 */
@Entity
@Table(name="WILLOW_CONFIG")
public class WillowConfig extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CONFIG_NAME")
	private String configName;

	@Column(name="CONFIG_PROMPT")
	private String configPrompt;

	@Column(name="CONFIG_VALUE")
	private String configValue;

	@Column(name="DISPLAY_ORDER")
	private BigDecimal displayOrder;

	@Column(name="PRIMARY_CAT")
	private String primaryCat;

	@Column(name="SECONDARY_CAT")
	private String secondaryCat;

    public WillowConfig() {
    }

	public String getConfigName() {
		return this.configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigPrompt() {
		return this.configPrompt;
	}

	public void setConfigPrompt(String configPrompt) {
		this.configPrompt = configPrompt;
	}

	public String getConfigValue() {
		return this.configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public BigDecimal getDisplayOrder() {
		return this.displayOrder;
	}

	public void setDisplayOrder(BigDecimal displayOrder) {
		this.displayOrder = displayOrder;
	}

	public String getPrimaryCat() {
		return this.primaryCat;
	}

	public void setPrimaryCat(String primaryCat) {
		this.primaryCat = primaryCat;
	}	

	public String getSecondaryCat() {
		return this.secondaryCat;
	}

	public void setSecondaryCat(String secondaryCat) {
		this.secondaryCat = secondaryCat;
	}
	
	@Override
    public String toString() {
        return "com.mikealbert.vision.entity.WillowConfig[ configName=" + configName +" , configValue "+ configValue+" ]";
    }

}