package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the KEY_TYPE_CODES database table.
 * 
 */
@Entity
@Table(name="KEY_TYPE_CODES")
public class KeyTypeCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="KEY_TYPE")
	private String keyType;

	private String description;

	//bi-directional many-to-one association to KeyCode
	@OneToMany(mappedBy="keyTypeCode")
	private List<KeyCode> keyCodes;

	public KeyTypeCode() {
	}

	public String getKeyType() {
		return this.keyType;
	}

	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<KeyCode> getKeyCodes() {
		return this.keyCodes;
	}

	public void setKeyCodes(List<KeyCode> keyCodes) {
		this.keyCodes = keyCodes;
	}

	
	public KeyCode addKeyCodes(KeyCode keyCodes) {
		getKeyCodes().add(keyCodes);
		keyCodes.setKeyTypeCode(this);

		return keyCodes;
	}

	public KeyCode removeKeyCodes(KeyCode keyCodes) {
		getKeyCodes().remove(keyCodes);
		keyCodes.setKeyTypeCode(null);

		return keyCodes;
	}
}