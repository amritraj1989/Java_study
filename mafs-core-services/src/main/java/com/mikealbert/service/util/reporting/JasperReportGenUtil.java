package com.mikealbert.service.util.reporting;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mikealbert.data.enumeration.ReportNameEnum;
import com.mikealbert.util.MALUtilities;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;


public class JasperReportGenUtil {
	
	private static JasperPrint fillFromJRTableList(String reportName, List<JRTableModelDataSource> dataSources, HashMap<String,String> reportParams) throws JRException, FileNotFoundException{
		JasperReport report;
		JasperPrint print = null;
		Map<String, Object> parameters;

		//TODO: make the lookup more robust
		URL jasperResURL = JasperReportGenUtil.class.getResource("/reports/" + reportName + ".jasper");
		 
		report = (JasperReport) JRLoader.loadObject(jasperResURL);
		
		parameters = new HashMap<String, Object>();
		
		for(int i = 1; i <= dataSources.size(); i++){
            parameters.put("Table" + i + "DS", dataSources.get(i -1));
        }
		
		if(reportParams != null && reportParams.size() > 0){
			for (String key : reportParams.keySet()) {
				parameters.put(key,reportParams.get(key));				
			}
		}
		
		print = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource(1));

		return print;
	}
	
	
	public static ByteArrayOutputStream exportAsPdf(String reportName, List<JRTableModelDataSource> dataSources) throws FileNotFoundException, JRException{
		return JasperReportGenUtil.exportAsPdf(reportName, dataSources, null);
	}
	
	public static ByteArrayOutputStream exportAsPdf(String reportName, List<JRTableModelDataSource> dataSources, HashMap<String,String> reportParams) throws FileNotFoundException, JRException{
		JasperPrint print;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		print = JasperReportGenUtil.fillFromJRTableList(reportName, dataSources,reportParams);
		
		JasperExportManager.exportReportToPdfStream(print, outputStream);
		
		return outputStream;
	}
	
	public static JasperPrint FillReport(JasperReport report, List<?> reportVOs,  Map<String, Object> reportParameterMap) throws Exception{
		JasperPrint print;
				
		if(MALUtilities.isEmpty(reportParameterMap)){
			print = JasperFillManager.fillReport(report, new HashMap<String, Object>(), new JRBeanCollectionDataSource(reportVOs));			
		} else {
			print = JasperFillManager.fillReport(report, reportParameterMap, new JRBeanCollectionDataSource(reportVOs));			
		}
		
		return print;
	}
	
	
	public static JasperPrint FillReport(ReportNameEnum reportName, List<?> reportVOs,  Map<String, Object> reportParameterMap) throws Exception{
		JasperPrint print;
		URL jasperResURL;
		JasperReport report;		
		
		jasperResURL = JasperReportGenUtil.class.getResource("/reports/" + reportName.getFileName() + ".jasper");		 
		report = (JasperReport) JRLoader.loadObject(jasperResURL);
		
		if(MALUtilities.isEmpty(reportParameterMap)){
			print = JasperFillManager.fillReport(report, new HashMap<String, Object>(), new JRBeanCollectionDataSource(reportVOs));			
		} else {
			print = JasperFillManager.fillReport(report, reportParameterMap, new JRBeanCollectionDataSource(reportVOs));			
		}
		
		return print;
	}	
	
}
