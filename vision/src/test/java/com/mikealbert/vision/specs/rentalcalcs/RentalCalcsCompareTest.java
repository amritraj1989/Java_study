package com.mikealbert.vision.specs.rentalcalcs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.mikealbert.common.CommonCalculations;
import com.mikealbert.data.dao.QuotationElementDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.service.ProfitabilityService;
import com.mikealbert.service.RentalCalculationService;
import com.mikealbert.testing.BaseSpec;
import com.mikealbert.util.SpringAppContext;

public class RentalCalcsCompareTest extends BaseSpec {


    @Resource  RentalCalculationService rentalCalculationService;
    @Resource  QuotationElementDAO quotationElementDAO;
    @Resource  ProfitabilityService profitabilityService;
    @Resource  QuotationModelDAO quotationModelDAO;

    private static int QMD_ID_IDX = 0;
    private static int RATESHEET_MIN_IDX = 2;

    private static int VEHICLE_RENT_POS = 1;
    private static int SERVICE_RENT_POS = 4;
    private static int TOTAL_RENT_POS = 7;    
    private static int PERCENT_PROFIT_POS = 10;
   
    @PostConstruct
    public void RentalCalcsCompareInit() {
	
	URL dataUrl = this.getClass().getResource("/qmdData.csv");
	URL xmlTemplateUrl = this.getClass().getResource("/com/mikealbert/vision/specs/rentalcalcs/RentalCalcsCompare_Template.html");
	URL xmlUrl = this.getClass().getResource("/com/mikealbert/vision/specs/rentalcalcs/RentalCalcsCompare.html");

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
		if(lineCount == 1 ) continue ;
		
		Element clonedItemRow = (Element) itemTable.getChildElements().get(1).copy();

		// split the data line into the qmd id and the total(s)
		qmdDataArray = line.split(",");
		// insert the data into the template
		String sQmdId = qmdDataArray[QMD_ID_IDX];
		
		RentalCalcsCompareVO  rentalCalcsCompareVO  = loadDataForQuotationModel(Long.valueOf(sQmdId));
		clonedItemRow.getChildElements().get(0).appendChild(sQmdId);
		
		itemTable.appendChild(clonedItemRow);
		clonedItemRow.getChildElements().get(1).getChildElements().get(VEHICLE_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getFinanceRental()));
		clonedItemRow.getChildElements().get(1).getChildElements().get(SERVICE_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getServiceRental())); 
		clonedItemRow.getChildElements().get(1).getChildElements().get(TOTAL_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getTotalRental())); 
		clonedItemRow.getChildElements().get(1).getChildElements().get(PERCENT_PROFIT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getProfitPercentage())); 

		clonedItemRow.getChildElements().get(2).getChildElements().get(VEHICLE_RENT_POS).appendChild(qmdDataArray[RATESHEET_MIN_IDX]);

		clonedItemRow.getChildElements().get(3).getChildElements().get(VEHICLE_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getFinanceRental())); 
		clonedItemRow.getChildElements().get(3).getChildElements().get(SERVICE_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getServiceRental())); 
		clonedItemRow.getChildElements().get(3).getChildElements().get(TOTAL_RENT_POS).appendChild(String.valueOf(rentalCalcsCompareVO.getTotalRental())); 

		clonedItemRow.getChildElements().get(4).getChildElements().get(VEHICLE_RENT_POS).appendChild(qmdDataArray[RATESHEET_MIN_IDX]);
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

    
    public RentalCalcsCompareVO loadDataForQuotationModel(Long qmdId) {

	RentalCalcsCompareVO retVal = new RentalCalcsCompareVO();
	Double financeValue = null;
	Double serviceValue = null;
	Double otherValue = null;
	Connection connection = null;
	PreparedStatement selectStatement = null;
	
	try {
	    DataSource  dataSource  =  (DataSource)SpringAppContext.getBean("dataSource");	
	    connection = dataSource.getConnection();
	    connection.setAutoCommit(false);

	    Double percentagProfit = 00.00;

	    selectStatement = connection.prepareStatement("SELECT WILLOW2K.quotation1.get_profitability(?, ?, ?) from dual ");
	    selectStatement.setLong(1, qmdId);
	    selectStatement.setString(2, "P");
	    selectStatement.setString(3, "N");

	    ResultSet rs = selectStatement.executeQuery();
	    while (rs.next()) {
		percentagProfit = rs.getDouble(1);
	    }

	    selectStatement = connection
		    .prepareStatement("select sum(rental) from quotation_elements qel, lease_elements lel	where qmd_qmd_id = ?  "
			    + "and QEL.LEL_LEL_ID = LEL.LEL_ID and LEL.ELEMENT_TYPE = ?");

	    selectStatement.setLong(1, qmdId);
	    selectStatement.setString(2, "FINANCE");
	    ResultSet rs2 = selectStatement.executeQuery();
	    while (rs2.next()) {
		financeValue = rs2.getDouble(1);
	    }
	    // get the baseline "SERVICE" rental
	    selectStatement.setLong(1, qmdId);
	    selectStatement.setString(2, "SERVICE");
	    ResultSet rs3 = selectStatement.executeQuery();
	    while (rs3.next()) {
		serviceValue = rs3.getDouble(1);
	    }

	    // add in all others that are not "FINANCE" or "SERVICE" to the
	    // service total (this is what Willow does today!)
	    selectStatement = connection
		    .prepareStatement("select sum(rental) from quotation_elements qel, lease_elements lel	where qmd_qmd_id = ?  "
			    + "and QEL.LEL_LEL_ID = LEL.LEL_ID and LEL.ELEMENT_TYPE NOT IN ('FINANCE','SERVICE')");

	    // get the baseline "SERVICE" rental
	    selectStatement.setLong(1, qmdId);
	    ResultSet rs4 = selectStatement.executeQuery();
	    while (rs4.next()) {
		otherValue = rs4.getDouble(1);
	    }

	    int contractPeriod = 1;
	    selectStatement = connection.prepareStatement("select CONTRACT_PERIOD from quotation_models where qmd_id = ?");
	    selectStatement.setLong(1, qmdId);

	    ResultSet rs5 = selectStatement.executeQuery();

	    while (rs5.next()) {
		contractPeriod = rs5.getInt(1);
	    }

	    Double dfinanceValuePerMonth = financeValue / contractPeriod;
	    Double dserviceValuePerMonth = ((serviceValue + otherValue) / contractPeriod);

	    retVal.setProfitPercentage(new BigDecimal(percentagProfit));
	    retVal.setFinanceRental(new BigDecimal(dfinanceValuePerMonth));
	    retVal.setServiceRental(new BigDecimal(dserviceValuePerMonth));
	    retVal.setTotalRental(retVal.getFinanceRental().add(retVal.getServiceRental()));

	    relaseConnection(connection,  selectStatement);

	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    relaseConnection(connection,  selectStatement);
	}

	return retVal;
    }
    
    
    public RentalCalcsCompareVO testCalculateBaselineRental(Long qmdId) {

	RentalCalcsCompareVO retVal = new RentalCalcsCompareVO();
	Connection connection = null;
	PreparedStatement selectStatement = null;
	
	try {
	    System.out.println("qmdId===" + qmdId);
	    DataSource  dataSource  =  (DataSource)SpringAppContext.getBean("dataSource");	
	    connection = dataSource.getConnection();
	    connection.setAutoCommit(false);
	    Double percentagProfit = 00.00;

	    selectStatement = connection.prepareStatement("SELECT WILLOW2K.quotation1.get_profitability(?, ?, ?) from dual ");
	    selectStatement.setLong(1, qmdId);
	    selectStatement.setString(2, "P");
	    selectStatement.setString(3, "N");

	    ResultSet rs = selectStatement.executeQuery();
	    while (rs.next()) {
		percentagProfit = rs.getDouble(1);
	    }

	    BigDecimal dbIRR = new BigDecimal(String.valueOf(percentagProfit));
	    // dbIRR = dbIRR.setScale(2, BigDecimal.ROUND_HALF_UP);

	    BigDecimal monthlyRental = new BigDecimal(0);

	    QuotationModel quotationModel = rentalCalculationService.getCalculatedQuotationModel(qmdId, true,null);
	    monthlyRental = profitabilityService.calculateMonthlyRental(quotationModel, quotationModel.getCalculatedCapCost(),
	    quotationModel.getResidualValue(), dbIRR);

	    BigDecimal mthlyService = quotationElementDAO.getSumOfService(qmdId);
	    BigDecimal monthlyService;

	    // check for value before dividing for monthLy service charge

	    if (mthlyService != null) {

		BigDecimal dcontractPeriod = new BigDecimal(String.valueOf(quotationModel.getContractPeriod()));
		monthlyService = mthlyService.divide(dcontractPeriod, CommonCalculations.MC);
	    } else {
		monthlyService = new BigDecimal(0);
	    }

	    // add Vision rental and service values together

	    retVal.setVisionServiceRental(monthlyService);
	    retVal.setVisionFinanceRental(monthlyRental);
	    retVal.setVisionTotalRental(monthlyRental.add(monthlyService));
	    relaseConnection(connection,  selectStatement);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    relaseConnection(connection,  selectStatement);
	}

	return retVal;
    }
    
    private void relaseConnection(Connection connection, PreparedStatement selectStatement) {
	try {
	    if (selectStatement != null) {
		selectStatement.close();
	    }
	    if (connection != null) {
		connection.commit();
		connection.close();
	    }

	} catch (Exception e2) {
	}

    }

}
