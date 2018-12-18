package com.mikealbert.rental.processors.inputoutput;

import java.math.BigDecimal;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationElement;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;

@Component("assetMgmtOffInvoiceProcessorInput")
@Scope(value = "prototype")
public class AssetMgmtOffInvoiceProcessorInput extends BaseRentalProcessorInput {

    private BigDecimal mDiscPrice;	//  DiscPrice of quotationModelAccessory  
    private BigDecimal mResidualAmt; 	//  ResidualAm of quotationModelAccessory  
    private BigDecimal mRechargeAmount; //  RechargeAmount of quotationModelAccessory 

    private BigDecimal dDiscPrice;	//  DiscPrice of quotationDealerAccessory  
    private BigDecimal dResidualAmt; 	//  ResidualAm of quotationDealerAccessory  
    private BigDecimal dRechargeAmount; //  RechargeAmount of quotationDealerAccessory 
    
    private BigDecimal qmQuoteCapital; //  quoteCapital of quotationModel 
    private BigDecimal qmResidualValue; // residualValue of quotationModel 
    private long  qmPaymentId; //  paymentId of quotationModel 
    private long qmContractPeriod; //  contractPeriod of quotationModel 
    
    public BigDecimal getmRechargeAmount() {
	return mRechargeAmount;
    }

    public void setmRechargeAmount(BigDecimal mRechargeAmount) {
	this.mRechargeAmount = mRechargeAmount;
    }

    public BigDecimal getmDiscPrice() {
	return mDiscPrice;
    }

    public void setmDiscPrice(BigDecimal mDiscPrice) {
	this.mDiscPrice = mDiscPrice;
    }

    public BigDecimal getmResidualAmt() {
	return mResidualAmt;
    }

    public void setmResidualAmt(BigDecimal mResidualAmt) {
	this.mResidualAmt = mResidualAmt;
    }
    
    public void setdDiscPrice(BigDecimal dDiscPrice) {
        this.dDiscPrice = dDiscPrice;
    }

    public BigDecimal getdResidualAmt() {
        return dResidualAmt;
    }

    public void setdResidualAmt(BigDecimal dResidualAmt) {
        this.dResidualAmt = dResidualAmt;
    }

    public BigDecimal getdRechargeAmount() {
        return dRechargeAmount;
    }

    public void setdRechargeAmount(BigDecimal dRechargeAmount) {
        this.dRechargeAmount = dRechargeAmount;
    }
    public BigDecimal getdDiscPrice() {
        return dDiscPrice;
    }

    public BigDecimal getQmQuoteCapital() {
        return qmQuoteCapital;
    }

    public void setQmQuoteCapital(BigDecimal qmQuoteCapital) {
        this.qmQuoteCapital = qmQuoteCapital;
    }

    public BigDecimal getQmResidualValue() {
        return qmResidualValue;
    }

    public void setQmResidualValue(BigDecimal qmResidualValue) {
        this.qmResidualValue = qmResidualValue;
    }

    public long getQmPaymentId() {
        return qmPaymentId;
    }

    public void setQmPaymentId(long qmPaymentId) {
        this.qmPaymentId = qmPaymentId;
    }

    public long getQmContractPeriod() {
        return qmContractPeriod;
    }

    public void setQmContractPeriod(long qmContractPeriod) {
        this.qmContractPeriod = qmContractPeriod;
    }

   public void loadQuoteData(){
       
       QuotationElement qeuotationElement = super.getQuotationElement();
       if(qeuotationElement.getQuotationModelAccessory() != null){
	   QuotationModelAccessory quotationModelAccessory = qeuotationElement.getQuotationModelAccessory();
	   this.setmDiscPrice(quotationModelAccessory.getDiscPrice());	   
	   this.setmRechargeAmount(quotationModelAccessory.getRechargeAmount() != null ? quotationModelAccessory.getRechargeAmount() : new BigDecimal(0));
	   this.setmResidualAmt(quotationModelAccessory.getResidualAmt());
       }
      
       if(qeuotationElement.getQuotationDealerAccessory() != null){
	   QuotationDealerAccessory quotationDealerAccessory = qeuotationElement.getQuotationDealerAccessory();
    	   this.setdDiscPrice(quotationDealerAccessory.getDiscPrice());	   
    	   this.setdRechargeAmount(quotationDealerAccessory.getRechargeAmount() != null ? quotationDealerAccessory.getRechargeAmount() : new BigDecimal(0));
    	   this.setdResidualAmt(quotationDealerAccessory.getResidualAmt());
       }
     
       if(qeuotationElement.getQuotationModel() != null){	   
	   QuotationModel quotationModel = qeuotationElement.getQuotationModel();
	   
	   this.setQmQuoteCapital(quotationModel.getQuoteCapital());
	   this.setQmResidualValue(quotationModel.getResidualValue());
	   this.setQmPaymentId( quotationModel.getPaymentId());	   
	   this.setQmContractPeriod( quotationModel.getContractPeriod());
       }
       
    }
   
   
	@Override
	public void bindFinanceParamer(int index, BigDecimal value) throws MalBusinessException {		
	}

}
