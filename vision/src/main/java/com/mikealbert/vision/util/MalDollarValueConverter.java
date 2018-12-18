package com.mikealbert.vision.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import com.mikealbert.util.MALUtilities;

@FacesConverter("malDollarValueConverter")
public class MalDollarValueConverter implements Converter {
	private String fieldName;
	//Need to put mapping for id on screen
	private Map<String,String> fieldMapping = new HashMap<String, String>(); 
	{
		fieldMapping.put("baseresidual", "Base Residual");
		fieldMapping.put("irr", "IRR");
	}
	
	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		BigDecimal amountAsBigDecimal = null;
		boolean negativeValue = false;
		setFieldName(fieldMapping.get(uiComponent.getClientId()));
		try {
			if (!MALUtilities.isEmpty(value)) {
				DecimalFormat format = new DecimalFormat("$#,##0.00;-$#,##0.00");
				format.parse(value);
				if (value.startsWith("-")) {
					value = value.substring(value.indexOf("-")+1);
					negativeValue = true;
				}
				int indexOfDollar = value.indexOf("$");
				if (indexOfDollar == 0) {
					value = value.substring(value.indexOf("$") + 1);
				}
				value = value.replaceAll(",", "");
				if (negativeValue) {
					value = "-" + value;
				}
				amountAsBigDecimal = new BigDecimal(value);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesMessage msg = new FacesMessage("Amount Conversion error.", "Invalid amount in "+getFieldName());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}

		return amountAsBigDecimal;
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
		String amountAsString = null;
		setFieldName(fieldMapping.get(uiComponent.getClientId()));
		try {
			if (!MALUtilities.isEmpty(object)) {
				DecimalFormat format = new DecimalFormat("$#,##0.00;-$#,##0.00");
				amountAsString = format.format(object);
			}
		} catch (NumberFormatException nfe) {
			FacesMessage msg = new FacesMessage("Amount Conversion error.", "Invalid amount in "+getFieldName());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		} catch (Exception ex) {
			FacesMessage msg = new FacesMessage("Amount Conversion error.",  "Invalid amount in "+getFieldName());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
		return amountAsString;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	

}
