package com.mikealbert.vision.vo;

import com.mikealbert.data.enumeration.ReportNameEnum;

public class MainPurchaseOrderListItemVO extends DocumentListItemVO{
	private String stockYN;

	public MainPurchaseOrderListItemVO(){}
	
	public MainPurchaseOrderListItemVO(Long id, ReportNameEnum reportName, String stockYN){
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
