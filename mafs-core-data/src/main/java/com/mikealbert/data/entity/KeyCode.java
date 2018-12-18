package com.mikealbert.data.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the KEY_CODES database table.
 * 
 */
@Entity
@Table(name="KEY_CODES")
public class KeyCode implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "KCD_SEQ")
    @SequenceGenerator(name = "KCD_SEQ", sequenceName = "KCD_SEQ", allocationSize = 1)
	@Column(name="KCD_ID")
	private long kcdId;

	@Column(name="FMS_FMS_ID")
	private Long fmsFmsId;

	@Column(name="KEY_CODE")
	private String keyCode;

	@Column(name="KEY_NUMBER")
	private String keyNumber;

	//bi-directional many-to-one association to KeyTypeCode
	@ManyToOne
	@JoinColumn(name="KEY_TYPE")
	private KeyTypeCode keyTypeCode;

	public KeyCode() {
	}

	public long getKcdId() {
		return this.kcdId;
	}

	public void setKcdId(long kcdId) {
		this.kcdId = kcdId;
	}

	public Long getFmsFmsId() {
		return this.fmsFmsId;
	}

	public void setFmsFmsId(Long fmsFmsId) {
		this.fmsFmsId = fmsFmsId;
	}

	public String getKeyCode() {
		return this.keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public String getKeyNumber() {
		return this.keyNumber;
	}

	public void setKeyNumber(String keyNumber) {
		this.keyNumber = keyNumber;
	}

	public KeyTypeCode getKeyTypeCode() {
		return this.keyTypeCode;
	}

	public void setKeyTypeCode(KeyTypeCode keyTypeCode) {
		this.keyTypeCode = keyTypeCode;
	}

	
}