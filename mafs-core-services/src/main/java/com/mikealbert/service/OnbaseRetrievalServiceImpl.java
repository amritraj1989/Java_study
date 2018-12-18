package com.mikealbert.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.exception.MalException;
import com.mikealbert.imaging.GetDoc;
import com.mikealbert.imaging.GetDoc.Request.GetDocReq;
import com.mikealbert.imaging.GetDocResponse;
import com.mikealbert.imaging.GetDocResponse.GetDocResult;
import com.mikealbert.imaging.GetList;
import com.mikealbert.imaging.GetList.Request.GetListReq;
import com.mikealbert.imaging.GetList.Request.GetListReq.KwList;
import com.mikealbert.imaging.GetList.Request.GetListReq.KwList.Keyword;
import com.mikealbert.imaging.GetListResponse;
import com.mikealbert.imaging.GetListResponse.GetListResult;
import com.mikealbert.imaging.GetListResponse.GetListResult.Response.ResponseData.DocList;
import com.mikealbert.imaging.GetListResponse.GetListResult.Response.ResponseData.DocList.Doc;
import com.mikealbert.imaging.ObjectFactory;
import com.mikealbert.service.enumeration.OnbaseDocTypeEnum;
import com.mikealbert.service.vo.OnbaseKeywordVO;
import com.mikealbert.websvc.client.WebServicesClient;

@Service("onbaseRetrievalService")
public class OnbaseRetrievalServiceImpl extends WebServicesClient  implements OnbaseRetrievalService {
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 1L;
	
	public  static final String ONBASE_RES_KEYWORD_ID = "ID";
	public  static final String ONBASE_RES_KEYWORD_VALUE = "VALUE";
	private static final String ONBASE_RES_TYPE_DOC = "DOC";
	private static final String ONBASE_RES_TYPE_LIST = "LIST";
	private static final String ONBASE_RES_TYPE_ERR = "ERR";
	private static final String ONBASE_RES_TYPE_NODATA = "NODATA";	
	private static final String LOG_LEVEL = "NONE";
	private static final String TIME_OUT = "10000";
	private static final String	DOC_SOAP_ACTION_CALLBACK = "http://www.mikealbert.com/imaging/GetDoc";
	private static final String	LIST_SOAP_ACTION_CALLBACK = "http://www.mikealbert.com/imaging/GetList";
	
	
	
	@Value("${onbase.enableService:NOT_DEFINED}")
	private String enableOnbaseService;
	
	@Value("${onbase.wsdlURL:NOT_DEFINED}")
	private String wsdlURL;
		
	@Value("${onbase.contextPath:NOT_DEFINED}")
	private String contextPath;
	
	@PostConstruct
	public void initilizeResource() {
		super.setDefaultContext(wsdlURL, contextPath);
	}
    
	private Comparator<Doc> docComparator = new Comparator<Doc>() {
		public int compare(Doc doc1, Doc doc2) {
			Long docId1 = doc1.getId().longValue();
			Long docId2 = doc2.getId().longValue();
			return docId2.compareTo(docId1);
		}
	}; 

	public List<Map<String, String>> getList(OnbaseDocTypeEnum docTypeEnum, List<OnbaseKeywordVO> keyWordVOList) throws MalException {
		
		List<Map<String, String>> resList = new ArrayList<Map<String, String>>();

		GetListResponse response = (GetListResponse) callWebServices(generateListRequest(docTypeEnum.getValue(), keyWordVOList, true) ,LIST_SOAP_ACTION_CALLBACK );
		GetListResult getListResult = response.getGetListResult();
		GetListResult.Response res = getListResult.getResponse();
		String respType = res.getRespType();
		if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_LIST)) {
			DocList docListObj = res.getResponseData().getDocList();
			if (docListObj != null) {
				List<Doc> listDoc = docListObj.getDoc();
				Collections.sort(listDoc, docComparator);
				if (listDoc != null) {
					for (Doc doc : listDoc) {
						Map<String, String> map = new HashMap<String, String>();
						map.put(ONBASE_RES_KEYWORD_ID, doc.getId().toString());
						map.put(ONBASE_RES_KEYWORD_VALUE, doc.getValue());
						resList.add(map);
					}
				}
			}
		} else if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_ERR)) {
			throw new MalException("OnBase API response error");
		}
		return resList;

	}


	public byte[]  getDoc(OnbaseDocTypeEnum docTypeEnum, List<OnbaseKeywordVO> keyWordVOList) throws MalException {
		
		byte[] byteArray = null;
		GetListResponse response = (GetListResponse) callWebServices(generateListRequest(docTypeEnum.getValue(), keyWordVOList,false) ,LIST_SOAP_ACTION_CALLBACK );
		GetListResult getListResult = response.getGetListResult();
		GetListResult.Response res = getListResult.getResponse();
		String respType = res.getRespType();
		if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_DOC)) {
			boolean isBase64 = Base64.isBase64(res.getResponseData().getDocData().getBytes());
			if (isBase64) {
				byteArray = Base64.decode(res.getResponseData().getDocData().getBytes());
			}
		
		} else if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_LIST)) {
			throw new MalException("Unable to get unique doc with search criteria");
		}else if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_NODATA)) {
			throw new MalException("No data found in onbase");
		}else if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_ERR)) {
			throw new MalException("OnBase API response error");
		}else {
			throw new MalException("Unknown onbase response type");
		}
		
		return byteArray;

	}
	
	/**
	 * Retrieves document from OnBase using OnBase's doc id
	 * @param String OnBase doc id
	 */
	public byte[] getDoc(String docID) throws MalException {
		byte[] byteArray = null;
		byteArray = Base64.decode(this.getDocAsBase64(docID).getBytes());
		return byteArray;
	}

	@Override
	public String getDocAsBase64(String docID) throws MalException {
		String base64 = null;
		GetDocResponse response = (GetDocResponse) callWebServices(generateDocRequest(docID),DOC_SOAP_ACTION_CALLBACK);
		GetDocResult docResult = response.getGetDocResult();
		GetDocResponse.GetDocResult.Response res = docResult.getResponse();
		String respType = res.getRespType();
		if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_DOC)) {
			boolean isBase64 = Base64.isBase64(res.getResponseData().getDocData().getBytes());
			if (isBase64) {
				base64 = res.getResponseData().getDocData();
			}
		} else if (respType.equalsIgnoreCase(ONBASE_RES_TYPE_ERR) || base64 == null || base64.length() == 0 ) {
			throw new MalException("OnBase API response error");
		}

		return base64;
	}
	
	public GetListResponse getList(GetList request) throws MalException {
		
		return (GetListResponse) callWebServices(request,LIST_SOAP_ACTION_CALLBACK );

	}

	
	public GetDocResponse getDoc(GetList request) throws MalException {

		return (GetDocResponse) callWebServices(request,LIST_SOAP_ACTION_CALLBACK );
	}

	public Object callWebServices(Object request,String  soapActionCallback) throws MalException {

		Object response = null;
		
		try {

			
			final StringWriter out = new StringWriter();
			getMarshaller().marshal(request, new StreamResult(out));
			StreamSource requestSource = new StreamSource(new StringReader(out.toString()));
			logger.info("Onbase Soap Request::"+out.toString());			
			StringResult stringResult = new StringResult();
			SoapActionCallback sac = new SoapActionCallback(soapActionCallback);
			boolean success = sendSourceAndReceiveToResult(requestSource, sac, stringResult);
			if (success) {
				String xmlResponse = stringResult.toString();
				xmlResponse = xmlResponse.replaceAll("<response xmlns=\"\">", "<response>");// Tune the response to unmarshal.Cann't expect name space.			
				logger.debug("Onbase Soap Response::"+xmlResponse);
				StreamSource resSource = new StreamSource(new StringReader(xmlResponse));
				response = getUnmarshaller().unmarshal(resSource);				
			}

		} catch (Exception e) {			
			throw new MalException("OnBase API response error", e);
		}
		return response;

	}

	private GetList generateListRequest(String docType, List<OnbaseKeywordVO> keyWordVOList , boolean returnSingleDocAsList) {
		
		ObjectFactory objectFactory = new ObjectFactory();
		
		GetList getList = objectFactory.createGetList();
		
		GetList.Request req = new GetList.Request();		
		GetListReq request = new GetListReq();
		request.setDocType(docType);
		request.setLogLevel(LOG_LEVEL);
		request.setReturnSingleDocAsList(returnSingleDocAsList);
		BigInteger timeout = new BigInteger(TIME_OUT);
		request.setTimeout(timeout);
		KwList kwList = new KwList();
		for (OnbaseKeywordVO keywordVO : keyWordVOList) {
			Keyword keyword = new Keyword();
			keyword.setName(keywordVO.getKeywordName());
			keyword.setValue(keywordVO.getKeywordValue());
			kwList.getKeyword().add(keyword);
		}

		request.setKwList(kwList);
		req.setGetListReq(request);
		getList.setRequest(req);

		return getList;
	}

	private GetDoc generateDocRequest(String docID) {
		
		ObjectFactory objectFactory = new ObjectFactory();
		GetDoc doc = objectFactory.createGetDoc();
		GetDoc.Request req = new GetDoc.Request();	
		GetDocReq request = new GetDocReq();
		request.setLogLevel(LOG_LEVEL);
		request.setDocID(new BigInteger(docID));
		BigInteger timeout = new BigInteger(TIME_OUT);
		request.setTimeout(timeout);
		req.setGetDocReq(request);
		doc.setRequest(req);

		return doc;
	}
}