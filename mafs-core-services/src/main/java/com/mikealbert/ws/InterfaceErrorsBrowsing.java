package com.mikealbert.ws;

import java.util.ArrayList;

import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.mikealbert.data.vo.InterfaceErrorsVO;
import com.mikealbert.exception.MalException;

@WebService
@XmlSeeAlso({com.mikealbert.data.vo.StoreLocationVO.class, com.mikealbert.data.vo.VendorMaintCodeVO.class})
public interface InterfaceErrorsBrowsing {
	public ArrayList<InterfaceErrorsVO> getInterfaceErrors(String interfaceType) throws MalException;
}
