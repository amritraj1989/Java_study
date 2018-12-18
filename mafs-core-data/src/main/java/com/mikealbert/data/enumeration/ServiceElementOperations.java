package com.mikealbert.data.enumeration;

public enum ServiceElementOperations {
	NONE("None"),
	CHANGE("Change"),
	ADD("Add"),
	REMOVE("Remove");
	
	private String operation;
	
	private ServiceElementOperations(String operation){
		this.operation = operation;
	}
	
	public String getOperation(){
		return this.operation;
	}
}
