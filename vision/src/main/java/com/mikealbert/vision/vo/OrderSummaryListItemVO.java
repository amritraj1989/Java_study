package com.mikealbert.vision.vo;

import com.mikealbert.data.enumeration.ReportNameEnum;

public class OrderSummaryListItemVO extends DocumentListItemVO{
	private String stockYN;

	public OrderSummaryListItemVO(){}
	
	public OrderSummaryListItemVO(Long id, ReportNameEnum reportName, String stockYN){
		super(id, reportName);
		setStockYN(stockYN);
	}

	public String getStockYN() {
		return stockYN;
	}

	public void setStockYN(String stockYN) {
		this.stockYN = stockYN;
	}	
}
