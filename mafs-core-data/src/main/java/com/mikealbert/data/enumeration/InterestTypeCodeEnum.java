package com.mikealbert.data.enumeration;

public enum InterestTypeCodeEnum {

	BASKET_L("BASKET L", "30- Day Market Basket Rate"),
	CE_PROFIT("CE_PROFIT", "Closed-End Proft"),
	CP_30("CP_30", "30-Day Dealer Commercial Paper"),
	FINCHARGE("FINCHARGE", "Finance Charge"),
	INVENTORY("INVENTORY", "Inventory interest rate"),
	LIBOR_CE("LIBOR+CE", "Libor Plus Spread - Closed-End Pricing"),
	LIBOR_30("LIBOR_30", "30-Day Libor"),
	LIBOR_90("LIBOR_90", "90-Day Libor"),
	MAL("MAL", "MAL - Obsolete"), //TODO Verify whether this should be in the list or not
	MAL_COF("MAL_COF", "MAL Cost of Funds"),
	OE_PROFIT("OE_PROFIT", "Open-End Profit"),
	OTHER("OTHER", "Other Interest"),
	PRICING("PRICING", "Applicable SWAP Rate"),
	PRIME("PRIME", "Prime Rate"),
	SWAP("SWAP", "SWAP Rate"),
	ZERO("ZERO", "Zero Interest Used Cars");

	private String code;
	private String description;
		
	private InterestTypeCodeEnum(String code, String description){
		this.setCode(code);
		this.setDescription(description);
	}

	public InterestTypeCodeEnum getInterestTypeCodeEnum(String code){
		for(InterestTypeCodeEnum type : values()){
			if(type.getCode().equals(code)){
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
