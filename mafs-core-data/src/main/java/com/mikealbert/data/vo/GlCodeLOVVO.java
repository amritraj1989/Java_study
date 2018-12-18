package com.mikealbert.data.vo;



public class GlCodeLOVVO {
	private long cId;
	private String code;
	private String alternateKey;
	private String defaultSign;
	private String description;
	private String glType;
	private String status;
	private String compositeKey;
	
	public String getCompositeKey() {
		return this.cId+"-"+this.code;
	}
	public void setCompositeKey(String compositeKey) {
		this.compositeKey = compositeKey;
	}
	public long getcId() {
		return cId;
	}
	public void setcId(long cId) {
		this.cId = cId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getAlternateKey() {
		return alternateKey;
	}
	public void setAlternateKey(String alternateKey) {
		this.alternateKey = alternateKey;
	}
	public String getDefaultSign() {
		return defaultSign;
	}
	public void setDefaultSign(String defaultSign) {
		this.defaultSign = defaultSign;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGlType() {
		return glType;
	}
	public void setGlType(String glType) {
		this.glType = glType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
