package com.mikealbert.vision.view.bean;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.vision.view.ViewConstants;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;


/**
 * The purpose of this bean is to abstract the implementation details
 * for rendering reports to a JSF front-end. Backing beans should inject 
 * this bean and use the methods herein directly to render the report. 
 *
 */
//TODO Refactor exception handling so that the user sees a friendly message and help desk gets the information they need when an exception is thrown
@Component
public class JasperReportBean extends BaseBean {
	private static final long serialVersionUID = 259707184579464357L;

	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	public void displayPDFReport(JasperPrint print) throws IOException, JRException{
		HttpServletResponse response;
		ServletOutputStream outputStream;
		
		try {					
			response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();	
			response.reset();  
			response.setContentType("application/pdf"); 

			outputStream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(print, outputStream);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			logger.error(e, print.getName());
			super.addErrorMessage("generic.error","Error in displaying PDF report" + e.getMessage());
			forwardToURL(ViewConstants.ERROR);	
		}
	}	
}
