package com.mikealbert.data.entity;
import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;


/**
 * The persistent class for the CAP_ELE_PARAMETERS database table.
 * 
 */
@Entity
@Table(name="CAP_ELE_PARAMETERS")
public class CapEleParameter extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="CEP_ID")
	private long cepId;

	@Column(name="CEL_CEL_ID")
	private Long celCelId;

	@Column(name="CER_CER_ID")
	private BigDecimal cerCerId;

	@Column(name="PARAMETER_NAME")
	private String parameterName;

	@Column(name="PARAMETER_TYPE")
	private String parameterType;

	@Column(name="SEQUENCE_NO")
	private BigDecimal sequenceNo;

    public CapEleParameter() {
    }

	public long getCepId() {
		return this.cepId;
	}

	public void setCepId(long cepId) {
		this.cepId = cepId;
	}

	public Long getCelCelId() {
		return this.celCelId;
	}

	public void setCelCelId(Long celCelId) {
		this.celCelId = celCelId;
	}

	public BigDecimal getCerCerId() {
		return this.cerCerId;
	}

	public void setCerCerId(BigDecimal cerCerId) {
		this.cerCerId = cerCerId;
	}

	public String getParameterName() {
		return this.parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public BigDecimal getSequenceNo() {
		return this.sequenceNo;
	}

	public void setSequenceNo(BigDecimal sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

}