package com.mikealbert.data.vo;

public class VehicleConfigModelVO {
	
	private Long rowKey;
	private Long vcfId;
	private Long vcmId;
	private Long modelId;
	private Long mrgId;
	private Long mtpId;
	private String mfgCode;
	private String make;
	private String model;
	private String modelCode;
	private String year;
	private String trim;
	private String modelType;
	private String status;

	public Long getRowKey() {
		return this.vcmId != null ? this.vcmId : this.hashCode();
	}

	public void setRowKey(Long rowKey) {
		this.rowKey = rowKey;
	}

	public Long getVcfId() {
		return vcfId;
	}

	public void setVcfId(Long vcfId) {
		this.vcfId = vcfId;
	}

	public Long getVcmId() {
		return vcmId;
	}

	public void setVcmId(Long vcmId) {
		this.vcmId = vcmId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getMrgId() {
		return mrgId;
	}

	public void setMrgId(Long mrgId) {
		this.mrgId = mrgId;
	}

	public Long getMtpId() {
		return mtpId;
	}

	public void setMtpId(Long mtpId) {
		this.mtpId = mtpId;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getMfgCode() {
		return mfgCode;
	}

	public void setMfgCode(String mfgCode) {
		this.mfgCode = mfgCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
