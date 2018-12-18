package com.mikealbert.vision.specs.rentalcalcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.common.MalConstants;
import com.mikealbert.data.dao.ProductTypeCodeDAO;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.dao.WillowConfigDAO;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationStepStructure;
import com.mikealbert.service.CapitalCostService;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.QuotationService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.data.vo.QuotationStepStructureVO;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.vision.service.CapitalCostOverviewService;

public class RentalCalcsCompareOETest extends BaseSpec {


    @Resource RentalCalculationService rentalCalculationService;
    @Resource QuotationElementDAO quotationElementDAO;
    @Resource ProfitabilityService profitabilityService;    
    @Resource QuotationModelDAO quotationModelDAO;
    @Resource QuotationService quotationService;
    @Resource FleetMasterService fleetMasterService;
    @Resource CapitalCostService capitalCostService;
    @Resource ProductTypeCodeDAO  productTypeCodeDAO;
    @Resource WillowConfigDAO willowConfigDAO;
    @Resource CapitalCostOverviewService capitalCostOverviewService;

   
   
    @PostConstruct
    public void RentalCalcsCompareInit() {
	
	URL dataUrl = this.getClass().getResource("/qmdData_OE.csv");
	URL xmlTemplateUrl = this.getClass().getResource("/com/mikealbert/vision/specs/rentalcalcs/RentalCalcsCompare_OE_Template.html");
	URL xmlUrl = this.getClass().getResource("/com/mikealbert/vision/specs/rentalcalcs/RentalCalcsCompareOE.html");

	File qmdData = new File(dataUrl.getFile());
	File compareTemplateXml = new File(xmlTemplateUrl.getFile());
	File compareXml = new File(xmlUrl.getFile());

	BufferedReader rdr = null;
	String line = null;
	String[] qmdDataArray;
	Document doc;
	try {
	    // load the file in resources
	    rdr = new BufferedReader(new FileReader(qmdData));
	    // open the concordion template
	    Builder builder = new Builder();
	    doc = builder.build(compareTemplateXml);

	    // calcItems from the template (remove the existing, copy in new
	    // ones)
	    Element root = doc.getRootElement();
	    Element body = root.getFirstChildElement("body");
	    Element table = body.getFirstChildElement("table");
	    Element itemTable = table.getChildElements().get(1).getChildElements().get(0).getFirstChildElement("table");
	    int lineCount = 0;
	    // while we have data in the file
	    while ((line = rdr.readLine()) != null) {
		lineCount = lineCount + 1;
		if(lineCount == 1) continue ;
		System.out.println("lineCount=="+lineCount);
		Element clonedItemRow = (Element) itemTable.getChildElements().get(1).copy();

		// split the data line into the qmd id and the total(s)
		qmdDataArray = line.split(",");
		// insert the data into the template
		
		String scenario = qmdDataArray[1];
		String  stepInfo = qmdDataArray[2];
		String  increments = qmdDataArray[3];
		String  term = qmdDataArray[4];
		String quoteId = qmdDataArray[5];
		String  unit = qmdDataArray[6];
		String qmdId = qmdDataArray[7];
		String invoiceAdj = qmdDataArray[8];
		String intRate = qmdDataArray[9];
		String intAdj = qmdDataArray[10];
		String admFactor = qmdDataArray[11];
		String deprFactor = qmdDataArray[12];
		
		// variables for willow output
		String leaseRateStep1 = qmdDataArray[13];
		String nbvStep1 = qmdDataArray[14];
		String leaseRateStep2 = qmdDataArray[15];
		String nbvStep2 = qmdDataArray[16];
		String leaseRateStep3 = qmdDataArray[17];
		String nbvStep3 = qmdDataArray[18];
		String leaseRateStep4 = qmdDataArray[19];
		String nbvStep4 = qmdDataArray[20];
		String leaseRateStep5 = qmdDataArray[21];
		String nbvStep5 = qmdDataArray[22];		
		String IRR = qmdDataArray[23];
		
		String mikealbertCost = qmdDataArray[24];
		String clientCost = qmdDataArray[25];
		
		// variables for rate sheet output
		String leaseRateStep1RSheet = qmdDataArray[26];
		String nbvStep1RSheet = qmdDataArray[27];
		String leaseRateStep2RSheet = qmdDataArray[28];
		String nbvStep2RSheet = qmdDataArray[29];
		String leaseRateStep3RSheet = qmdDataArray[30];
		String nbvStep3RSheet = qmdDataArray[31];
		String leaseRateStep4RSheet = qmdDataArray[32];
		String nbvStep4RSheet = qmdDataArray[33];
		String leaseRateStep5RSheet = qmdDataArray[34];
		String nbvStep5RSheet = qmdDataArray[35];		
		String IRRRSheet = qmdDataArray[36];
		
		//clonedItemRow.getChildElements().get(0).appendChild(sQmdId);
		
		itemTable.appendChild(clonedItemRow);
		
		
		clonedItemRow.getChildElements().get(0).appendChild(quoteId);
		clonedItemRow.getChildElements().get(1).appendChild(qmdId);
		if(increments.trim().length() == 0)
		    increments = term;		
		clonedItemRow.getChildElements().get(2).appendChild(increments);
		clonedItemRow.getChildElements().get(3).appendChild(clientCost);
		clonedItemRow.getChildElements().get(4).appendChild(mikealbertCost);
		
		clonedItemRow.getChildElements().get(5).appendChild(invoiceAdj);
		clonedItemRow.getChildElements().get(6).appendChild(intRate);
		clonedItemRow.getChildElements().get(7).appendChild(intAdj);
		clonedItemRow.getChildElements().get(8).appendChild(admFactor);
		clonedItemRow.getChildElements().get(9).appendChild(deprFactor);
		
		
		clonedItemRow.getChildElements().get(10).getChildElements().get(1).appendChild(leaseRateStep1);
		clonedItemRow.getChildElements().get(10).getChildElements().get(4).appendChild(nbvStep1); 
		clonedItemRow.getChildElements().get(10).getChildElements().get(9).appendChild(leaseRateStep1RSheet);
		clonedItemRow.getChildElements().get(10).getChildElements().get(12).appendChild(nbvStep1RSheet); 
		
		
		clonedItemRow.getChildElements().get(11).getChildElements().get(1).appendChild(leaseRateStep2);
		clonedItemRow.getChildElements().get(11).getChildElements().get(4).appendChild(nbvStep2); 
		clonedItemRow.getChildElements().get(11).getChildElements().get(9).appendChild(leaseRateStep2RSheet);
		clonedItemRow.getChildElements().get(11).getChildElements().get(12).appendChild(nbvStep2RSheet); 
		
		clonedItemRow.getChildElements().get(12).getChildElements().get(1).appendChild(leaseRateStep3);
		clonedItemRow.getChildElements().get(12).getChildElements().get(4).appendChild(nbvStep3);
		clonedItemRow.getChildElements().get(12).getChildElements().get(9).appendChild(leaseRateStep3RSheet);
		clonedItemRow.getChildElements().get(12).getChildElements().get(12).appendChild(nbvStep3RSheet);
		
		clonedItemRow.getChildElements().get(13).getChildElements().get(1).appendChild(leaseRateStep4);
		clonedItemRow.getChildElements().get(13).getChildElements().get(4).appendChild(nbvStep4); 
		clonedItemRow.getChildElements().get(13).getChildElements().get(9).appendChild(leaseRateStep4RSheet);
		clonedItemRow.getChildElements().get(13).getChildElements().get(12).appendChild(nbvStep4RSheet);
		
		clonedItemRow.getChildElements().get(14).getChildElements().get(1).appendChild(leaseRateStep5);
		clonedItemRow.getChildElements().get(14).getChildElements().get(4).appendChild(nbvStep5); 
		clonedItemRow.getChildElements().get(14).getChildElements().get(9).appendChild(leaseRateStep5RSheet);
		clonedItemRow.getChildElements().get(14).getChildElements().get(12).appendChild(nbvStep5RSheet); 
		
		
		clonedItemRow.getChildElements().get(15).getChildElements().get(1).appendChild(IRR);
		clonedItemRow.getChildElements().get(15).getChildElements().get(6).appendChild(IRRRSheet);
                
		
		
	    }
	    // remove the template before saving
	    itemTable.removeChild(itemTable.getChildElements().get(1));

	    // save the file back
	    FileOutputStream fos = new FileOutputStream(compareXml);
	    Serializer output = new Serializer(fos, "ISO-8859-1");
	    output.setIndent(2);
	    output.write(doc);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    
    
    public RentalCalcsCompareOEVO testCalculateRental(Long qmdId, Double inRate, Double intAdj , Double depFact) {
	
	RentalCalcsCompareOEVO  retVal  = new  RentalCalcsCompareOEVO();

	
	try {
	   //if(qmdId.longValue() !=  232285)
		//return null;;
	    
		depFact = depFact/100;
		
	    	System.out.println("qmdId ==  " + qmdId +"===depFact="+depFact);
	    
        	QuotationModel calcQuotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,null);
        	long qprId =  calcQuotationModel.getQuotation().getQuotationProfile().getQprId();
        	BigDecimal mainElementResidual =   calcQuotationModel.getResidualValue(); 
        	BigDecimal equipmentResidual =  rentalCalculationService.getEquipmentResidual(calcQuotationModel);
        	BigDecimal finalResidual =  mainElementResidual.add(equipmentResidual); 
        	
        	Double adminFactorFinV = quotationService.getFinanceParam("ADMIN_FACT", qmdId, qprId);
        	
    	
        	BigDecimal customerCost = capitalCostOverviewService.getQuoteCost(calcQuotationModel).getCustomerCost();
        	
        	
        	BigDecimal period = BigDecimal.valueOf(calcQuotationModel.getContractPeriod());
        	
        	BigDecimal interestRate = new BigDecimal(String.valueOf(inRate+intAdj)); //quotationService.getInterestRate(qmdId );
        	System.out.println("interestRate>>" +interestRate);
        	
        	interestRate = interestRate.divide(new BigDecimal(1200), CommonCalculations.MC);  	// divide by 1200 to willow data UI
        	BigDecimal adminFactor =  new BigDecimal(String.valueOf(adminFactorFinV));				// as it is of willow UI	
    
        	//residual_percentage := 100 * ( 1 / contract_period ) * ( 1 - (total_residual / total_capital_cost));             willow code		
        	//BigDecimal depreciationFactor =   BigDecimal.ONE.subtract( (finalResidual .divide(customerCost,  CommonCalculations.MC) )).divide(period , CommonCalculations.MC);         	
        	//depreciationFactor = depreciationFactor.setScale(7, BigDecimal.ROUND_HALF_UP);
    	
        	BigDecimal depreciationFactor =  new BigDecimal(String.valueOf(depFact)) ;
        	
        	List<QuotationStepStructure>  quotationStepStructureList = quotationService.getQuotationModelStepStructure(qmdId);
        	
        	List<QuotationStepStructureVO>  stepStructureVOList = new ArrayList<QuotationStepStructureVO>();
        	if(quotationStepStructureList.size() == 0){
        	    	QuotationStepStructureVO  stepStructureVO = new QuotationStepStructureVO();
        	    	stepStructureVO.setFromPeriod(1L);
        	    	stepStructureVO.setToPeriod(calcQuotationModel.getContractPeriod());
        	    	stepStructureVOList.add(stepStructureVO);
        		
        		customerCost =  calcQuotationModel.getCalculatedCapCost(); //this need to be done for brand new quote
        	}else{
        	    for (QuotationStepStructure stepStructure : quotationStepStructureList) {
        		
        		QuotationStepStructureVO  stepStructureVO = new QuotationStepStructureVO();
        	    	stepStructureVO.setFromPeriod(stepStructure.getId().getFromPeriod());
        	    	stepStructureVO.setToPeriod(stepStructure.getToPeriod().longValue());
        	    	stepStructureVOList.add(stepStructureVO);
			
		    } 
        	}
        	
        	System.out.println("customerCost>>" +customerCost);
        	System.out.println("depreciationFactor>>" +depreciationFactor);        			
        	System.out.println("adminFactor>>" +adminFactor);
        	System.out.println("customerCost>>" +customerCost);
    	
        	stepStructureVOList = rentalCalculationService.updateQuotationStepStructureWithElementsCost(calcQuotationModel, stepStructureVOList);
        	List<QuotationStepStructureVO> quotationStepResponseList = profitabilityService.calculateOEStepLease(depreciationFactor,finalResidual, interestRate, adminFactor, stepStructureVOList);		
        	System.out.println("++++++++++++++++++++++OUTPUT++++++++++++++++++++++");
        	int stepCount = 1;
                for (QuotationStepStructureVO quotationStep : quotationStepResponseList) {
                
                    System.out.println("Step from==  " + quotationStep.getFromPeriod());
                    System.out.println("Step to==  " + quotationStep.getToPeriod());                  
                    System.out.println("finalRental==  " + quotationStep.getLeaseRate());
                    System.out.println("netBookValue==  " + quotationStep.getNetBookValue());
                    
        		if (stepCount == 1) {
        		    
        		    retVal.setStep1Rental(quotationStep.getLeaseRate());
        		    retVal.setStep1NBV(quotationStep.getNetBookValue());
        		    
        		} else if (stepCount == 2) {
        		    
        		    retVal.setStep2Rental(quotationStep.getLeaseRate());
        		    retVal.setStep2NBV(quotationStep.getNetBookValue());
        		    
        		} else if (stepCount == 3) {
        		    
        		    retVal.setStep3Rental(quotationStep.getLeaseRate());
        		    retVal.setStep3NBV(quotationStep.getNetBookValue());
        
        		} else if (stepCount == 4) {
        		    
        		    retVal.setStep4Rental(quotationStep.getLeaseRate());
        		    retVal.setStep4NBV(quotationStep.getNetBookValue());
        
        		} else if (stepCount == 5) {
        		    
        		    retVal.setStep5Rental(quotationStep.getLeaseRate());
        		    retVal.setStep5NBV(quotationStep.getNetBookValue());
        		}
        		stepCount++;
                	
                }
               
                BigDecimal costToCompany =  capitalCostOverviewService.getQuoteCost(calcQuotationModel).getDealCost();
                System.out.println("ostToCompany ==  " + costToCompany);
            	BigDecimal adminFee = profitabilityService.getFinanceParameter(MalConstants.CLOSED_END_LEASE_ADMIN, calcQuotationModel.getQmdId(), calcQuotationModel.getQuotation().getQuotationProfile().getQprId());
            	
                BigDecimal calculatedIrr = profitabilityService.calculateIrrFromOEStep(quotationStepResponseList, costToCompany,adminFee);
                // we have to do save after getting all these calc done mainly during  UI integration
                
                System.out.println("================== calculatedIrr ==  " + calculatedIrr);
                
                retVal.setIrr(calculatedIrr);
            
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return retVal;
    }
    
   

}
