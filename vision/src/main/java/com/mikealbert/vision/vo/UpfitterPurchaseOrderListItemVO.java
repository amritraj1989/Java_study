package com.mikealbert.vision.vo;

import com.mikealbert.data.enumeration.ReportNameEnum;

public class UpfitterPurchaseOrderListItemVO extends DocumentListItemVO{
	private String stockYN;

	public UpfitterPurchaseOrderListItemVO(){}
	
	public UpfitterPurchaseOrderListItemVO(Long id, ReportNameEnum reportName, String stockYN){
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
