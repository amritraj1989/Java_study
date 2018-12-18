package com.mikealbert.vision.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import com.mikealbert.util.MALUtilities;

public class XLSUtil {
	
	private Map<String, CellStyle>  style = new HashMap<String, CellStyle>();
	
	public static final String TITLE = "title";
	public static final String HEADER = "header";
	public static final String CELL = "cell";
	public static final String CELL_DATE = "dateCell";	
	public static final String CELL_AMOUNT = "amountCell";
	public static final String PRIME_CELL = "primeCell";
	
	public static CellStyle createStyle(Workbook wb, String styleName) {

		CellStyle style = wb.createCellStyle();
		CreationHelper createHelper = wb.getCreationHelper();

		if (styleName.equalsIgnoreCase(TITLE)) {
			
			Font titleFont = wb.createFont();
			titleFont.setFontHeightInPoints((short) 18);
			titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);			
			style.setAlignment(CellStyle.ALIGN_CENTER);
			style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
			style.setFont(titleFont);

		} else if (styleName.equalsIgnoreCase(HEADER)) {
			
		    Font monthFont = wb.createFont();
		    monthFont.setFontHeightInPoints((short)15);
		    monthFont.setColor(IndexedColors.WHITE.getIndex());		
		    style.setAlignment(CellStyle.ALIGN_CENTER);
		    style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		    style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    style.setFont(monthFont);

		} else if (styleName.equalsIgnoreCase(CELL)) {
			  style.setWrapText(true);
		    style.setBorderRight(CellStyle.BORDER_THIN);
		    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		    style.setBorderLeft(CellStyle.BORDER_THIN);
		    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		    style.setBorderTop(CellStyle.BORDER_THIN);
		    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		    style.setBorderBottom(CellStyle.BORDER_THIN);
		    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		} else if (styleName.equalsIgnoreCase(CELL_DATE)) {
			style.setWrapText(true);
			style.setBorderRight(CellStyle.BORDER_THIN);
			style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			style.setAlignment(CellStyle.ALIGN_RIGHT);
			style.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
		} else if (styleName.equalsIgnoreCase(PRIME_CELL)) {		  
		     style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		    style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		    style.setWrapText(true);
		}else if (styleName.equalsIgnoreCase(CELL_AMOUNT)) {
			  style.setWrapText(true);
			    style.setBorderRight(CellStyle.BORDER_THIN);
			    style.setRightBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderLeft(CellStyle.BORDER_THIN);
			    style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderTop(CellStyle.BORDER_THIN);
			    style.setTopBorderColor(IndexedColors.BLACK.getIndex());
			    style.setBorderBottom(CellStyle.BORDER_THIN);
			    style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
			    style.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
		}

		return style;
	}
	
}
