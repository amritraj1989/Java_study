package com.mikealbert.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.mikealbert.service.vo.VinDecoderRequestVO;
import com.mikealbert.service.vo.VinDecoderResponseVO;
import com.mikealbert.testing.BaseTest;

public class VinDecoderServiceTest extends BaseTest {

	@Resource
	VinDecoderService vinDecoderService;

	@Test
	public void decodeVin() throws Exception {
		try {

			VinDecoderRequestVO vinRequest = new VinDecoderRequestVO();
			vinRequest.setVin("1FTRF3AT9BEB16173");
			vinRequest.setAccountNumber("295916");
			vinRequest.setAccountSecret("1021c6df92994083");
			vinRequest.setAccountCountry("US");
			vinRequest.setAccountLanguage("en");
			vinRequest.setAccountName("MAFS-DEMO");
			vinRequest.setWsdlURL("http://services.chromedata.com/Description/7a?wsdl");
			vinRequest.setContectPath("com.chrome.services.description7a");

			VinDecoderResponseVO vinDecoderResponseVO = null; //= vinDecoderService.getVehicleDescription(vinRequest);

			System.out.println("Response ==" + vinDecoderResponseVO);
		} catch (Exception e) {
			System.out.println("");
		}

	}

}
