package com.mikealbert.service.enumeration;



public enum NonCapitalElementEnum {
	
	BASE_VEHICLE_ELEMENT(1, 1, "Base Vehicle", false, false),	
	FACTORY_EQUIPMENT_ELEMENT (1, 2 , "Factory Equipment", true,false),
	
	AFTER_MARKET_EQUIPMENT_ELEMENT (4, 1, "Aftermarket Equipment",false,true),
	CAPITAL_CONTRIBUTION_ELEMENT (4, 2, "Capital Contribution", false, false);
	

	private final int groupOrder;
	private final int elementOrderInGroup; 
	private final String elementName; 
	private final boolean isModelAccessories;	
	private final boolean isDealerAccessories;	

	

	NonCapitalElementEnum(int groupOrder, int elementOrderInGroup , String elementName, boolean isModelAccessories, boolean isDealerAccessories) {
	    this.groupOrder = groupOrder;	 
	    this.elementOrderInGroup = elementOrderInGroup;	
	    this.elementName = elementName;
	    this.isModelAccessories =  isModelAccessories;
	    this.isDealerAccessories =  isDealerAccessories;	
	}

	public int getGroupOrder() {
		return groupOrder;
	}
	
	public int getElementOrderInGroup() {
		return elementOrderInGroup;
	}
	
	public String getElementName() {
		return elementName;
	}
	
	public boolean isModelAccessories() {
		return isModelAccessories;
	}

	public boolean isDealerAccessories() {
		return isDealerAccessories;
	}
	
}

