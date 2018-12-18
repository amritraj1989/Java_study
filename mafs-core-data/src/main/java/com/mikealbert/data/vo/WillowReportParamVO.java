package com.mikealbert.data.vo;

public class WillowReportParamVO {

	private String paramName;
	private String paramValue;
	
	public WillowReportParamVO(){}
	
	public WillowReportParamVO(String name, String value){
		this.paramName = name;
		this.paramValue = value;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
}
