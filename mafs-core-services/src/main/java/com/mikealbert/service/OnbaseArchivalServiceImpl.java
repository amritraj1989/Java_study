package com.mikealbert.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.OnbaseUploadedDocsDAO;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.exception.MalException;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;

@Service("onbaseArchivalService")
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
public class OnbaseArchivalServiceImpl implements OnbaseArchivalService {

	@Resource OnbaseUploadedDocsDAO onbaseUploadedDocsDAO;
	
	private static final long serialVersionUID = 1L;
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());

	private static final String SEPARATOR = "|";
	private static final String NEW_LINE_CHAR = "\n";

	@Value("${onbase.archive.stage.dir:NOT_DEFINED}")
	private String onbaseArchiveDir;	
	
	
	@PostConstruct
	public void initilizeResource() {
		onbaseArchiveDir = onbaseArchiveDir +File.separator;
	}
	
	public  List<OnbaseKeywordVO> getOnbaseKeywords(OnbaseDocTypeEnum docTypeEnum){
		
		List<OnbaseKeywordVO>  indexList = new ArrayList<OnbaseKeywordVO>();
		
		if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE.getValue() , OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE.getValue()));
			indexList.add(new OnbaseKeywordVO(2, OnbaseIndexEnum.UPLOAD_ID.getName()));
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.FILE_EXT.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.VENDOR_NAME.getName()));
			indexList.add(new OnbaseKeywordVO(5,OnbaseIndexEnum.VENDOR_CODE.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.VENDOR_QUOTE_NO.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.QUOTE_DATE.getName()));
		
		}else if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue() , OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue()));
			indexList.add(new OnbaseKeywordVO(2,OnbaseIndexEnum.UPLOAD_ID.getName()));
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.FILE_EXT.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.UNIT_NO.getName()));
			indexList.add(new OnbaseKeywordVO(5,OnbaseIndexEnum.VIN_LAST_8.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.CUSTOMER_NO.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.CUSTOMER_NAME.getName()));			
			indexList.add(new OnbaseKeywordVO(8,OnbaseIndexEnum.VIN.getName()));
			
		}else if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_CLIENT_ORDER_CONFIRMATION)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_CLIENT_ORDER_CONFIRMATION.getValue() , OnbaseDocTypeEnum.TYPE_CLIENT_ORDER_CONFIRMATION.getValue()));
			indexList.add(new OnbaseKeywordVO(2,OnbaseIndexEnum.UPLOAD_ID.getName()));
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.FILE_EXT.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.UNIT_NO.getName()));
			indexList.add(new OnbaseKeywordVO(5,OnbaseIndexEnum.VIN_LAST_8.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.CUSTOMER_NO.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.CUSTOMER_NAME.getName()));			
			indexList.add(new OnbaseKeywordVO(8,OnbaseIndexEnum.VIN.getName()));
			
		}else if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST.getValue() , OnbaseDocTypeEnum.TYPE_QUOTE_REQUEST.getValue()));
			indexList.add(new OnbaseKeywordVO(2,OnbaseIndexEnum.UPLOAD_ID.getName()));
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.FILE_EXT.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.QUOTE_REQUEST_TYPE.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.CUSTOMER_NO.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.CUSTOMER_NAME.getName()));	
			
		}else if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_SCHEDULE_A)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_SCHEDULE_A.getValue() , OnbaseDocTypeEnum.TYPE_SCHEDULE_A.getValue()));	
			indexList.add(new OnbaseKeywordVO(2,OnbaseIndexEnum.UPLOAD_ID.getName()));			
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.UNIT_NO.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.VIN_LAST_8.getName()));
			indexList.add(new OnbaseKeywordVO(5,OnbaseIndexEnum.CUSTOMER_NO.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.CUSTOMER_NAME.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.CORPORATE_NO.getName()));
			indexList.add(new OnbaseKeywordVO(8,OnbaseIndexEnum.CORPORATE_NAME.getName()));
			indexList.add(new OnbaseKeywordVO(9,OnbaseIndexEnum.VIN.getName()));	
			
		}else if(docTypeEnum.equals(OnbaseDocTypeEnum.TYPE_AMORTIZATION_SCHEDULE)){
			
			indexList.add(new OnbaseKeywordVO(1, OnbaseDocTypeEnum.TYPE_AMORTIZATION_SCHEDULE.getValue() , OnbaseDocTypeEnum.TYPE_AMORTIZATION_SCHEDULE.getValue()));
			indexList.add(new OnbaseKeywordVO(2,OnbaseIndexEnum.UPLOAD_ID.getName()));			
			indexList.add(new OnbaseKeywordVO(3,OnbaseIndexEnum.UNIT_NO.getName()));
			indexList.add(new OnbaseKeywordVO(4,OnbaseIndexEnum.VIN_LAST_8.getName()));
			indexList.add(new OnbaseKeywordVO(5,OnbaseIndexEnum.CUSTOMER_NO.getName()));
			indexList.add(new OnbaseKeywordVO(6,OnbaseIndexEnum.CUSTOMER_NAME.getName()));
			indexList.add(new OnbaseKeywordVO(7,OnbaseIndexEnum.VIN.getName()));	
			
		}
		
		return indexList;
	}
	
	public String generateOnbaseKeywordsIndex(List<OnbaseKeywordVO> onbaseKeywordList) {

		Collections.sort(onbaseKeywordList, new Comparator<OnbaseKeywordVO>() {
			public int compare(OnbaseKeywordVO onkw1, OnbaseKeywordVO onkw2) {
				return onkw1.getPosition().compareTo(onkw1.getPosition());
			}
		});

		StringBuilder indexBuilder = new StringBuilder();
		for (OnbaseKeywordVO OnbaseKeywordVO : onbaseKeywordList) {
			indexBuilder = indexBuilder.append(OnbaseKeywordVO.getKeywordValue()).append("|");
		}
		indexBuilder = indexBuilder.delete(indexBuilder.length() - 1,indexBuilder.length());
		
		return indexBuilder.toString();
	}
	
	
	public void archiveDocumentInOnBase(final OnbaseUploadedDocs onbaseUploadedDoc) throws MalException {
		
		List<OnbaseUploadedDocs>  onbaseArchiveVOList = new ArrayList<OnbaseUploadedDocs>();
		onbaseArchiveVOList.add(onbaseUploadedDoc);
		archiveDocumentInOnBase(onbaseArchiveVOList);
	}
	
	public void archiveDocumentInOnBase(List<OnbaseUploadedDocs> onbaseUploadedDocList) throws MalException {
		
		StringBuilder indexFileNameBuilder = new StringBuilder(onbaseArchiveDir).append("dip_files").append(File.separator).
												append(onbaseUploadedDocList.get(0).getObdId()).append("...").append(".txt");
		
		StringBuilder indexBuilder = new StringBuilder();	
		for (OnbaseUploadedDocs onbaseUploadedDocs : onbaseUploadedDocList) {//adding file name with absolute path in last of index 
			
			indexBuilder =  indexBuilder.append(onbaseUploadedDocs.getIndexKey()).append(SEPARATOR).append(onbaseArchiveDir)
										.append(onbaseUploadedDocs.getObdId() + "-" + onbaseUploadedDocs.getFileNameWithExt()).append(NEW_LINE_CHAR);
		}
		
		createDoumentsInOnbaseArchiveDir(onbaseUploadedDocList);
		createIndexForDocuments(indexFileNameBuilder.toString(), indexBuilder.toString());
	}

	public Long getNextPK() throws MalException{
		return onbaseUploadedDocsDAO.getNextPK();
	}
	
	private void createIndexForDocuments(String indexFile, String indexInfo) throws MalException {

		try {
			File file = new File(indexFile);
			file.createNewFile();
			FileWriter fstream = new FileWriter(indexFile, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(indexInfo);
			out.close();
			logger.info("Index file :" + indexFile + " created successfully in Onbase archive dir");
		} catch (Exception e) {			
			String msg = " Could not create dip file : " + indexFile + " in Onbase archive dir  : " + e.getMessage();
			throw new MalException(msg, e);
		}
	}

	private void createDoumentsInOnbaseArchiveDir(List<OnbaseUploadedDocs> onbaseUploadedDocList) throws MalException {

		File archiveFile = null;
		String fileName = "";
		OutputStream outStream = null;

		try {
			
			for (OnbaseUploadedDocs onbaseUploadedDocs : onbaseUploadedDocList) {
				fileName = onbaseArchiveDir + onbaseUploadedDocs.getObdId() +"-" +onbaseUploadedDocs.getFileNameWithExt();
				archiveFile = new File(fileName);
				logger.info("Going to Archive File :" + fileName + " : File exist : " + archiveFile.exists());	
				archiveFile.createNewFile();
				outStream = new FileOutputStream(archiveFile);			
				outStream.write(onbaseUploadedDocs.getFileData());
				logger.info("File :" + archiveFile.getName() + " created successfully in Onbase archive dir");			
				outStream.close();
				archiveFile = null;
				outStream = null;

			}

		} catch (Exception e) {
			archiveFile = null;
			if(outStream != null){
				try {
					outStream.close();
				} catch (IOException e1) {
					logger.error(e1, "File Stream close error :" + fileName);					
				}
				outStream = null;
			}
			
			String msg = "Could not create document : " + fileName + " in Onbase archive dir :" + e.getMessage();
			logger.error(e,msg);	
			throw new MalException(msg, e);
		}
	}

}