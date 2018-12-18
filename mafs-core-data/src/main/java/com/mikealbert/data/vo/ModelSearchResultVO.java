package com.mikealbert.data.vo;

import com.mikealbert.data.util.DisplayFormatHelper;

public class ModelSearchResultVO {
	private boolean selected;	
	private Long mdlId;
	private String year;
	private String make;
	private String model; 
	private String trim;
	private String engine;
	private String modelType;
	private String mfgCode;
	private boolean quotePermitted;
		
	public ModelSearchResultVO(){}
	
	public String formattedTrimTableDisplay(){
		return DisplayFormatHelper.formatTrimForTable(getTrim(), getEngine(), "<br/>");
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Long getMdlId() {
		return mdlId;
	}

	public void setMdlId(Long mdlId) {
		this.mdlId = mdlId;
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

	public String getTrim() {
		return trim;
	}

	public void setTrim(String trim) {
		this.trim = trim;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
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

	public boolean isQuotePermitted() {
		return quotePermitted;
	}

	public void setQuotePermitted(boolean quotePermitted) {
		this.quotePermitted = quotePermitted;
	}
	

}
