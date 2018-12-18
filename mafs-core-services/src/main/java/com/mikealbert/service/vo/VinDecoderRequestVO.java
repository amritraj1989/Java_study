package com.mikealbert.service.vo;

public class VinDecoderRequestVO {
	
	private String  enableService;
	private String vin;
	private String accountNumber;
	private String accountSecret;
	private String accountCountry = "US";//optional
	private String accountLanguage ="en";//optional
	private String accountName ="MAFS-DEMO"; 
	private String wsdlURL;//optional
	private String contectPath;//optional
	
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getAccountSecret() {
		return accountSecret;
	}
	public void setAccountSecret(String accountSecret) {
		this.accountSecret = accountSecret;
	}
	public String getAccountCountry() {
		return accountCountry;
	}
	public void setAccountCountry(String accountCountry) {
		this.accountCountry = accountCountry;
	}
	public String getAccountLanguage() {
		return accountLanguage;
	}
	public void setAccountLanguage(String accountLanguage) {
		this.accountLanguage = accountLanguage;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getWsdlURL() {
		return wsdlURL;
	}
	public void setWsdlURL(String wsdlURL) {
		this.wsdlURL = wsdlURL;
	}
	public String getContectPath() {
		return contectPath;
	}
	public void setContectPath(String contectPath) {
		this.contectPath = contectPath;
	}
	public String getEnableService() {
		return enableService;
	}
	public void setEnableService(String enableService) {
		this.enableService = enableService;
	}

	
	
}
