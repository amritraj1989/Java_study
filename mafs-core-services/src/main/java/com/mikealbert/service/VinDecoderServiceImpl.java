package com.mikealbert.service;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

import com.chrome.services.description7a.AccountInfo;
import com.chrome.services.description7a.ResponseStatus.Status;
import com.chrome.services.description7a.VehicleDescription;
import com.chrome.services.description7a.VehicleDescriptionRequest;
import com.mikealbert.common.MalLogger;
import com.mikealbert.common.MalLoggerFactory;
import com.mikealbert.common.MalMessage;
import com.mikealbert.data.dao.FleetMasterDAO;
import com.mikealbert.data.dao.FleetMasterVinDetailsDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.FleetMasterVinDetails;
import com.mikealbert.service.vo.VinDecoderRequestVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.util.SpringAppContext;
import com.mikealbert.websvc.client.WebServicesClient;

@Service("vinDecoderService")
public class VinDecoderServiceImpl extends WebServicesClient implements	VinDecoderService {

	@Resource
	private FleetMasterDAO fleetMasterDAO;
	@Resource 
	VinDecoderService vinDecoderService;
	@Resource
	FleetMasterVinDetailsDAO fleetMasterVinDetailsDAO;
	@Resource 
	protected MalMessage malMessage;
	
	@Value("${chrome.enableService:false}")
	private String enableChromeService;
	
	private MalLogger logger = MalLoggerFactory.getLogger(this.getClass());
	//private static final String CONTEXT_PATH = "com.chrome.services.description7a";
	//private static final String  wsdlURL = "http://services.chromedata.com/Description/7a?wsdl";


	public void initilizeResource(String wsdlURL , String contextPath) {
		super.setDefaultContext(wsdlURL, contextPath);
	}
	
	
	public String getVehicleDescriptionXMLResponse(VinDecoderRequestVO vinDecoderRequestVO) throws Exception {
		initilizeResource(vinDecoderRequestVO.getWsdlURL() ,vinDecoderRequestVO.getContectPath());
		
		VehicleDescriptionRequest vehRequest = new VehicleDescriptionRequest();
		
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setNumber(vinDecoderRequestVO.getAccountNumber());
		accountInfo.setSecret(vinDecoderRequestVO.getAccountSecret());
		accountInfo.setCountry(vinDecoderRequestVO.getAccountCountry());
		accountInfo.setLanguage(vinDecoderRequestVO.getAccountLanguage());
		accountInfo.setBehalfOf(vinDecoderRequestVO.getAccountName());
		
		vehRequest.setAccountInfo(accountInfo);
		vehRequest.setVin(vinDecoderRequestVO.getVin());
		
		return getXMLResponse(vehRequest,null);
	}
	

	private Object getResponseObjectFromXML(String xmlResponse) throws Exception {

		Object response = null;
	
		try {
			StreamSource resSource = new StreamSource(new StringReader(xmlResponse));
			response = getUnmarshaller().unmarshal(resSource);
		} catch (Exception e) {
			logger.error(e, "Error in getting  soap Response Object::");
		}
		return response;

	}

	private String getXMLResponse(Object request, String soapActionCallback)throws Exception {

		String xmlResponse = null;
	
		String vin = null;
		try {
			vin = ((VehicleDescriptionRequest) request).getVin();
			final StringWriter out = new StringWriter();
			getMarshaller().marshal(request, new StreamResult(out));
			StreamSource requestSource = new StreamSource(new StringReader(out.toString()));
			logger.debug("Soap Request::" + out.toString());
			StringResult stringResult = new StringResult();
			SoapActionCallback sac = new SoapActionCallback(soapActionCallback);
			boolean success = sendSourceAndReceiveToResult(requestSource, sac,stringResult);
			if (success) {
				xmlResponse = stringResult.toString();
				xmlResponse = xmlResponse.replaceAll("<response xmlns=\"\">", "<response>");
				logger.debug("Soap Response::" + xmlResponse);
			}

		} catch (Exception e) {
			logger.error(e, "Error in getting  soap Response for vin ::" + vin);
			throw e;
		}
		return xmlResponse;

	}

	public Map<String, Object> validateVINAndReturnMessage(String inputVIN, Long fmsId) {
		Map<String, Object> resultWithMessage = new HashMap<String, Object>();
		if (MALUtilities.isEmpty(inputVIN)) {
			// return false;
		}
		inputVIN = inputVIN.toUpperCase();
		if (inputVIN.length() != 17) {
			// message for length
			resultWithMessage.put(ERROR_TYPE_BLOCKER, true);
			resultWithMessage.put(MESSAGE, "VIN must be 17 characters long");
			return resultWithMessage;
		}
		Long existingFleetMasterCount = fleetMasterDAO.countFleetMasterByVIN(inputVIN);
		if (existingFleetMasterCount != null) {
			List<FleetMaster> fleetMasters = fleetMasterDAO.findByVIN(inputVIN);
			if (existingFleetMasterCount.longValue() == 1) {
				FleetMaster fms = fleetMasters.get(0);
				if (MALUtilities.isEmpty(fmsId) || (fms.getFmsId().longValue() != fmsId.longValue())) {

					// message for duplicate
					resultWithMessage.put(ERROR_TYPE_WARNING, true);
					resultWithMessage.put(MESSAGE, "This VIN number already exists");
					return resultWithMessage;
				}
			} else {
				if (existingFleetMasterCount.longValue() > 1) {

					// message for duplicate
					resultWithMessage.put(ERROR_TYPE_WARNING, true);
					resultWithMessage.put(MESSAGE, "This VIN already exists more than once");
					return resultWithMessage;
				}
			}
		}

		boolean ifInvalidCharsInVin = false;
		int index = inputVIN.indexOf("I");
		if (index >= 0) {
			ifInvalidCharsInVin = true;
		}
		index = inputVIN.indexOf("O");
		if (index >= 0) {
			ifInvalidCharsInVin = true;
		}
		index = inputVIN.indexOf("Q");
		if (index >= 0) {
			ifInvalidCharsInVin = true;
		}
		if (ifInvalidCharsInVin) {
			// message for invalid chars
			resultWithMessage.put(ERROR_TYPE_BLOCKER, true);
			resultWithMessage.put(MESSAGE, "VIN must not contain the characters I,O,Q");
			return resultWithMessage;
		}
		int numericCode = 0;
		int numericMultiplier = 0;
		int total = 0;
		for (int vCount = 1; vCount <= 17; vCount++) {
			numericCode = 0;
			numericMultiplier = 0;
			if (vCount != 9) {
				numericCode = decodeLetter(inputVIN.substring(vCount - 1, vCount));
				numericMultiplier = decodeNumber(vCount);

				if (numericCode == -1 || numericMultiplier == -1) {
					// error for decode
					resultWithMessage.put(ERROR_TYPE_BLOCKER, true);
					resultWithMessage.put(MESSAGE, "VIN does not pass the checksum validation");
					return resultWithMessage;
				}
				total = total + (numericCode * numericMultiplier);
			}
		}
		int vMod = total % 11;
		if (vMod == 10) {
			String str = inputVIN.substring(8, 9);
			if (!str.equals("X")) {
				// error
				resultWithMessage.put(ERROR_TYPE_BLOCKER, true);
				resultWithMessage.put(MESSAGE, "VIN does not pass the checksum validation");
				return resultWithMessage;
			}
		} else {
			int newDecode = decodeLetter(inputVIN.substring(8, 9));
			if (newDecode != vMod) {
				// error
				resultWithMessage.put(ERROR_TYPE_BLOCKER, true);
				resultWithMessage.put(MESSAGE, "VIN does not pass the checksum validation");
				return resultWithMessage;
			}
		}	
		return resultWithMessage;
	}
	

	private int decodeNumber(int number) {
		if (number == 1)
			return 8;
		if (number == 2)
			return 7;
		if (number == 3)
			return 6;
		if (number == 4)
			return 5;
		if (number == 5)
			return 4;
		if (number == 6)
			return 3;
		if (number == 7)
			return 2;
		if (number == 8)
			return 10;
		if (number == 9)
			return -1;
		if (number == 10)
			return 9;
		if (number == 11)
			return 8;
		if (number == 12)
			return 7;
		if (number == 13)
			return 6;
		if (number == 14)
			return 5;
		if (number == 15)
			return 4;
		if (number == 16)
			return 3;
		if (number == 17)
			return 2;

		return -1;
	}

	private int decodeLetter(String string) {
		if (string.equals("A"))
			return 1;
		if (string.equals("B"))
			return 2;
		if (string.equals("C"))
			return 3;
		if (string.equals("D"))
			return 4;
		if (string.equals("E"))
			return 5;
		if (string.equals("F"))
			return 6;
		if (string.equals("G"))
			return 7;
		if (string.equals("H"))
			return 8;
		if (string.equals("J"))
			return 1;
		if (string.equals("K"))
			return 2;
		if (string.equals("L"))
			return 3;
		if (string.equals("M"))
			return 4;
		if (string.equals("N"))
			return 5;
		if (string.equals("P"))
			return 7;
		if (string.equals("R"))
			return 9;
		if (string.equals("S"))
			return 2;
		if (string.equals("T"))
			return 3;
		if (string.equals("U"))
			return 4;
		if (string.equals("V"))
			return 5;
		if (string.equals("W"))
			return 6;
		if (string.equals("X"))
			return 7;
		if (string.equals("Y"))
			return 8;
		if (string.equals("Z"))
			return 9;
		if (string.equals("1"))
			return 1;
		if (string.equals("2"))
			return 2;
		if (string.equals("3"))
			return 3;
		if (string.equals("4"))
			return 4;
		if (string.equals("5"))
			return 5;
		if (string.equals("6"))
			return 6;
		if (string.equals("7"))
			return 7;
		if (string.equals("8"))
			return 8;
		if (string.equals("9"))
			return 9;
		if (string.equals("0"))
			return 0;

		return -1;

	}

	/*
	 * This service can be called by passing vin and fms_id(Optional) .
	 * The null value for fmdId can be passed if we want to validate vin without having fleet masters record. 
	 */
	@Override
	public FleetMasterVinDetails validateVINByChrome(String inputVIN, Long fmsId) {
		
		boolean decodeVIN = false;
		FleetMasterVinDetails fleetMasterVinDetailByFmsId = null;
		if(fmsId != null){
			fleetMasterVinDetailByFmsId = fleetMasterVinDetailsDAO.findFleetMasterVinDetailsByFmsId(fmsId);
		}
		
		if(MALUtilities.isEmpty(fleetMasterVinDetailByFmsId) || MALUtilities.isEmpty(fleetMasterVinDetailByFmsId.getVin()) 
															 || !fleetMasterVinDetailByFmsId.getVin().equalsIgnoreCase(inputVIN)){
			decodeVIN = true; // no VIN found or vin is different. so search by vin and decode it.
			fleetMasterVinDetailByFmsId = null;			
		}
		if(decodeVIN){
			// check for VIN in DB.
			List<FleetMasterVinDetails> fleetMasterVinDetailByVin = fleetMasterVinDetailsDAO.findFleetMasterVinDetailsByVin(inputVIN);
			if(fleetMasterVinDetailByVin == null || fleetMasterVinDetailByVin.size() == 0){
				// no record found for vin in DB, so decode it.
				if(enableChromeService != null && Boolean.parseBoolean(enableChromeService)){
					VinDecoderRequestVO vinDecoderRequestVO = (VinDecoderRequestVO)SpringAppContext.getBean("vinDecoderRequestVO");
					vinDecoderRequestVO.setVin(inputVIN);
					FleetMasterVinDetails fleetMasterVinDetailsResponse = null;
					try {
						fleetMasterVinDetailsResponse =  getVehicleDescriptionByChrome(vinDecoderRequestVO);
					} catch (Exception e) {
						logger.error(e, malMessage.getMessage("vin.ws.error"));						
						return null;
					}
					if(fleetMasterVinDetailsResponse != null){
						fleetMasterVinDetailByFmsId = new FleetMasterVinDetails();
						
						fleetMasterVinDetailByFmsId.setFleetMaster(fmsId != null ? fleetMasterDAO.findById(fmsId).orElse(null) : null);
						fleetMasterVinDetailByFmsId.setVin(inputVIN);
						fleetMasterVinDetailByFmsId.setYear(fleetMasterVinDetailsResponse.getYear());
						fleetMasterVinDetailByFmsId.setMakeDesc(fleetMasterVinDetailsResponse.getMakeDesc());
						fleetMasterVinDetailByFmsId.setModelDesc(fleetMasterVinDetailsResponse.getModelDesc());
						fleetMasterVinDetailByFmsId.setModelTypeDesc(fleetMasterVinDetailsResponse.getModelTypeDesc());
						fleetMasterVinDetailByFmsId.setEngineDesc(fleetMasterVinDetailsResponse.getEngineDesc());
						fleetMasterVinDetailByFmsId.setFuelType(fleetMasterVinDetailsResponse.getFuelType());
						fleetMasterVinDetailByFmsId.setStyleDesc(fleetMasterVinDetailsResponse.getStyleDesc());
						fleetMasterVinDetailByFmsId.setTrimDesc(fleetMasterVinDetailsResponse.getTrimDesc());
					}
				}else{
					logger.debug("Chrome web-service is disabled");
					return null;
				}
			}else{
				// found record for VIN. Create entry in DB for VIN & current FMS ID.
				fleetMasterVinDetailByFmsId = fleetMasterVinDetailByVin.get(0);
				fleetMasterVinDetailByFmsId.setFleetMaster(fmsId != null ? fleetMasterDAO.findById(fmsId).orElse(null) : null);
			}
		}		
		
		return fleetMasterVinDetailByFmsId;
		
	}


	
	public FleetMasterVinDetails getVehicleDescriptionByChrome(
			VinDecoderRequestVO vinDecoderRequestVO) throws Exception {
	
		String xmlResponse =  getVehicleDescriptionXMLResponse(vinDecoderRequestVO);
		
		VehicleDescription responseObj =  (VehicleDescription)getResponseObjectFromXML(xmlResponse);
		
		FleetMasterVinDetails  fleetMasterVinDetails  = new FleetMasterVinDetails();
		fleetMasterVinDetails.setVin(vinDecoderRequestVO.getVin());
		
		fleetMasterVinDetails.setMakeDesc(responseObj.getBestMakeName());
		fleetMasterVinDetails.setModelDesc(responseObj.getBestModelName());

	    if (responseObj.getVinDescription() != null) {
//	    	fleetMasterVinDetails.setBodyType(responseObj.getVinDescription().getBodyType()); TODO
	    	fleetMasterVinDetails.setYear(String.valueOf(responseObj.getVinDescription().getModelYear()));
	    	fleetMasterVinDetails.setStyleDesc(responseObj.getVinDescription().getStyleName());
	    }

	    if (responseObj.getVinDescription() != null && responseObj.getVinDescription().getMarketClass() != null
		    && responseObj.getVinDescription().getMarketClass().size() > 0) {

	    	fleetMasterVinDetails.setModelTypeDesc(responseObj.getVinDescription().getMarketClass().get(0).getValue());
	    }
	 
	    if (responseObj.getEngine() != null && responseObj.getEngine().size() > 0) {
	    	fleetMasterVinDetails.setEngineDesc( responseObj.getEngine().get(0).getEngineType().getValue());
	    	fleetMasterVinDetails.setFuelType(responseObj.getEngine().get(0).getFuelType().getValue());
	    }
	    
	    if (responseObj.getStyle() != null && responseObj.getStyle().size() > 0) {
	    	fleetMasterVinDetails.setTrimDesc(responseObj.getStyle().get(0).getTrim());
//	    	fleetMasterVinDetails.setMfrModelCode(responseObj.getStyle().get(0).getMfrModelCode()); TODO
	    }
			
	    
	    List<Status> statusList = responseObj.getResponseStatus().getStatus();
	    String soapError = "";
	    for (Status status : statusList) {
	    	soapError = soapError + "Code:-" + status.getCode() + "-Value:-" + status.getValue() + "\n";
	    }
	    if( (responseObj.getResponseStatus().getResponseCode()).equalsIgnoreCase("Unsuccessful") ){
	    	logger.debug(soapError);
	    	return null;
	    }
	    return fleetMasterVinDetails;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public FleetMasterVinDetails saveFleetMasterVinDetail(FleetMasterVinDetails fleetMasterVinDetails) throws Exception{
		return fleetMasterVinDetailsDAO.saveAndFlush(fleetMasterVinDetails);
	}


	@Override
	public FleetMasterVinDetails getFleetMasterVinDetailByFmsId(Long fmsId) {
		return fleetMasterVinDetailsDAO.findFleetMasterVinDetailsByFmsId(fmsId);
	}

}