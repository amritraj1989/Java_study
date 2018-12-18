package com.mikealbert.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.data.dao.DocDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.OnbaseUploadedDocs;
import com.mikealbert.schema.kvpair.KeyValuePairType;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.enumeration.OnbaseIndexEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.testing.BaseTest;

public class OnbaseOperationsTest extends BaseTest {

	private static MalLogger logger = MalLoggerFactory .getLogger(OnbaseOperationsTest.class);
	
	@Resource  OnbaseRetrievalService onbaseRetrievalService;
	@Resource  OnbaseArchivalService onbaseArchivalService;
	@Resource  FleetMasterService fleetMasterService;
	@Resource  QuotationModelDAO quotationModelDAO;
	@Resource  DocDAO docDAO;
	
	@Value("${onbase.enableService}")
	private String enableOnbaseService;
	
	
	/*
	 *  Below test case tell us how to interact with onbase for Archival/Retrieval of any file. 
	 *  The dev/qa url is configured as http://devImgWSsvc.mikealbert.corp/imaging/obunitysvc.asmx?wsdl
	 *  and context path is com.mikealbert.imaging
	 */
	
	@Ignore
	public void testMainPOArchive() {
	
		List<OnbaseKeywordVO>  keywordVOList  = onbaseArchivalService.getOnbaseKeywords(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER);
		
		for (OnbaseKeywordVO onbaseKeywordVO : keywordVOList) {				
			if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UPLOAD_ID.getName())){
				onbaseKeywordVO.setKeywordValue( "DUMMY_UPLOAD_ID") ;//dummy id since it is not generated yet. It will replace later.
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.FILE_EXT.getName())){
				onbaseKeywordVO.setKeywordValue( "TXT");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.UNIT_NO.getName())){
				onbaseKeywordVO.setKeywordValue( "UNIT_NO");				
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN_LAST_8.getName())){
				onbaseKeywordVO.setKeywordValue( "VIN_LAST_8");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NO.getName())){
				onbaseKeywordVO.setKeywordValue( "CUSTOMER_NO");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.CUSTOMER_NAME.getName())){
				onbaseKeywordVO.setKeywordValue( "CUSTOMER_NAME");
			}else if(onbaseKeywordVO.getKeywordName().equals(OnbaseIndexEnum.VIN.getName())){
				onbaseKeywordVO.setKeywordValue( "VIN");
			}			
		}
		
		String indexKeyData = onbaseArchivalService.generateOnbaseKeywordsIndex(keywordVOList);
		
		
		OnbaseUploadedDocs onbaseUploadedDocs = new  OnbaseUploadedDocs();
		onbaseUploadedDocs.setObdId(1001L);
		onbaseUploadedDocs.setObjectId(String.valueOf(5009));
		onbaseUploadedDocs.setObjectType("Doc");
		onbaseUploadedDocs.setFileName("Vehicle_Order_Summary_Doc");
		onbaseUploadedDocs.setDocType(OnbaseDocTypeEnum.TYPE_PURCHASE_ORDER.getValue());
		onbaseUploadedDocs.setDocSubType(OnbaseDocTypeEnum.TYPE_SUB_MAIN_PURCHASE_ORDER.getValue());		
		onbaseUploadedDocs.setObsoleteYn("N");
		onbaseUploadedDocs.setFileData("Dummy main po content".getBytes());
		onbaseUploadedDocs.setFileType("TXT");
		indexKeyData = indexKeyData.replaceAll("DUMMY_UPLOAD_ID", String.valueOf( onbaseUploadedDocs.getObdId()));
		onbaseUploadedDocs.setIndexKey(indexKeyData);
		
		List<OnbaseUploadedDocs> onbaseArchiveVOListForUpload  = new ArrayList<OnbaseUploadedDocs>();
		onbaseArchiveVOListForUpload.add(onbaseUploadedDocs);
		onbaseArchivalService.archiveDocumentInOnBase(onbaseArchiveVOListForUpload);
	}

	@Test
	public void testGetDocList() {

		try {
			if(enableOnbaseService != null && enableOnbaseService.equals("true")){
				List<OnbaseKeywordVO> keyWordVOList = new ArrayList<OnbaseKeywordVO>();
				keyWordVOList.add( new OnbaseKeywordVO("Customer #", "00006813"));
				List<Map<String, String>> res = onbaseRetrievalService.getList(OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE, keyWordVOList);
				Assert.assertNotNull(res);
				Assert.assertTrue(res.size() > 0);
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}

	}
	
	@Ignore
	public void testGetDoc1() {

		try {
			int ONBASE_UPLOADED_DOCS_ID  = 1001;// PK of ONBASE_UPLOADED_DOCS tabe
			List<OnbaseKeywordVO> keyWordVOList = new ArrayList<OnbaseKeywordVO>();
			keyWordVOList.add( new OnbaseKeywordVO(OnbaseIndexEnum.UPLOAD_ID.getName(), String.valueOf(ONBASE_UPLOADED_DOCS_ID)));
			byte[] byteArray = onbaseRetrievalService.getDoc(OnbaseDocTypeEnum.TYPE_VENDOR_QUOTE ,keyWordVOList);
			boolean condition = byteArray != null && byteArray.length >= 0;			
			if (condition) {
				Assert.assertTrue("Receive Data--" + byteArray.length,condition);
			} else {
				Assert.assertFalse("Receive Data--" + byteArray.length,condition);
			}
		} catch (Exception e) {
		//	e.printStackTrace();
		}

	}
	
	@Ignore
	public void testGetDoc2() {

		try {
			
			if(enableOnbaseService != null && enableOnbaseService.equals("true")){
				byte[] byteArray = onbaseRetrievalService.getDoc("5944497");
				boolean condition = byteArray != null && byteArray.length >= 0;
				
				if (condition) {
					Assert.assertTrue("Receive Data--" + byteArray.length,condition);
				} else {
					Assert.assertFalse("Receive Data--" + byteArray.length,condition);
				}
			}

		} catch (Exception e) {
		//	e.printStackTrace();
		}

	}
	

	protected void print(List<Map<String, String>> respListMap) {
		for (Map<String, String> map : respListMap) {
			print(map);
		}
	}

	protected void print(Map<String, String> respMap) {
		for (String key : respMap.keySet()) {
			logger.debug("key--" + key + "--value--" + respMap.get(key));
		}
	}

	protected List<KeyValuePairType> populateReqKeyValueList(
			Map<String, String> beanMap) {
		List<KeyValuePairType> kvList = new ArrayList<KeyValuePairType>();
		for (String key : new TreeSet<String>(beanMap.keySet())) {
			KeyValuePairType keyValuePairType = new KeyValuePairType();
			keyValuePairType.setKey(key);
			keyValuePairType.setValue(beanMap.get(key));
			kvList.add(keyValuePairType);
		}

		return kvList;
	}

	protected Map<String, String> populateBeanMap(List<KeyValuePairType> kvList) {

		Map<String, String> txnMapReq = new HashMap<String, String>();

		for (KeyValuePairType kv : kvList) {
			txnMapReq.put(kv.getKey(), kv.getValue());

		}

		return txnMapReq;
	}

	protected void verboseKeyValueList(List<KeyValuePairType> kvList) {
		for (KeyValuePairType typeKV : kvList) {
			logger.debug(typeKV.getKey() + "----" + typeKV.getValue());

		}

	}

}
