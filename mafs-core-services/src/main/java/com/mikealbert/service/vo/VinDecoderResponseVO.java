package com.mikealbert.service.vo;

public class VinDecoderResponseVO {

	private String vin;
	private String bodyType;
	private String engineType;
	private String fuelType;
	private Integer modelYear;

	private String bestMakeName;
	private String bestModelName;
	private String bestStyleName;
	private String bestTrimName;
	private String marketClass;
	private String  mfrModelCode;
	private String soapResponseCode;
	private String soapError;
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}	
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public String getEngineType() {
		return engineType;
	}
	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}
	public String getFuelType() {
		return fuelType;
	}
	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}
	public Integer getModelYear() {
		return modelYear;
	}
	public void setModelYear(Integer modelYear) {
		this.modelYear = modelYear;
	}
	public String getBestMakeName() {
		return bestMakeName;
	}
	public void setBestMakeName(String bestMakeName) {
		this.bestMakeName = bestMakeName;
	}
	public String getBestModelName() {
		return bestModelName;
	}
	public void setBestModelName(String bestModelName) {
		this.bestModelName = bestModelName;
	}
	public String getBestStyleName() {
		return bestStyleName;
	}
	public void setBestStyleName(String bestStyleName) {
		this.bestStyleName = bestStyleName;
	}
	public String getBestTrimName() {
		return bestTrimName;
	}
	public void setBestTrimName(String bestTrimName) {
		this.bestTrimName = bestTrimName;
	}
	public String getMarketClass() {
		return marketClass;
	}
	public void setMarketClass(String marketClass) {
		this.marketClass = marketClass;
	}	
	public String getMfrModelCode() {
		return mfrModelCode;
	}
	public void setMfrModelCode(String mfrModelCode) {
		this.mfrModelCode = mfrModelCode;
	}
	public String getSoapResponseCode() {
		return soapResponseCode;
	}
	public void setSoapResponseCode(String soapResponseCode) {
		this.soapResponseCode = soapResponseCode;
	}
	public String getSoapError() {
		return soapError;
	}
	public void setSoapError(String soapError) {
		this.soapError = soapError;
	}
	
	@Override
	public String toString() {
		return "VinDecoderResponseVO [vin=" + vin + ", bodyType=" + bodyType
				+ ", engineType=" + engineType + ", fuelType=" + fuelType
				+ ", modelYear=" + modelYear + ", bestMakeName=" + bestMakeName
				+ ", bestModelName=" + bestModelName + ", bestStyleName="
				+ bestStyleName + ", bestTrimName=" + bestTrimName
				+ ", marketClass=" + marketClass + ", mfrModelCode="
				+ mfrModelCode + ", soapResponseCode=" + soapResponseCode + "]";
	}
	
	

}
