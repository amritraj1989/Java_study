package com.mikealbert.service.vo;


public class OnbaseKeywordVO {

	private Integer position;
	private String keywordName;
	private String keywordValue;
	
	public OnbaseKeywordVO(int position, String keywordName) {		
		this.position = position;
		this.keywordName = keywordName;
	}
	public OnbaseKeywordVO( String keywordName, String keywordValue) {	
		this.keywordName = keywordName;
		this.keywordValue = keywordValue;
	}
	
	public OnbaseKeywordVO(Integer position, String keywordName, String keywordValue) {		
		this.position = position;
		this.keywordName = keywordName;
		this.keywordValue = keywordValue;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getKeywordName() {
		return keywordName;
	}
	public void setKeywordName(String keywordName) {
		this.keywordName = keywordName;
	}
	public String getKeywordValue() {
		return keywordValue;
	}
	public void setKeywordValue(String keywordValue) {
		this.keywordValue = keywordValue;
	}
	
	@Override
	public String toString() {
		return "OnbaseKeywordVO [name="
				+ keywordName + ", value=" + keywordValue + ", position=" + position + "]";
	}
	
}
