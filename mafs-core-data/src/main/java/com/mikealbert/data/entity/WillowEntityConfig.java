package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the WILLOW_ENTITY_CONFIG database table.
 * 
 */
@Entity
@Table(name="WILLOW_ENTITY_CONFIG")
public class WillowEntityConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private WillowEntityConfigPK id;

	@Column(name="CONFIG_PROMPT")
	private String configPrompt;

	@Column(name="CONFIG_VALUE")
	private String configValue;

	@Column(name="DISPLAY_ORDER")
	private BigDecimal displayOrder;

    public WillowEntityConfig() {
    }

	public WillowEntityConfigPK getId() {
		return this.id;
	}

	public void setId(WillowEntityConfigPK id) {
		this.id = id;
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

}