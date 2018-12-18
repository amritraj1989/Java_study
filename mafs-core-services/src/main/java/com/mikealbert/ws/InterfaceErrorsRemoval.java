package com.mikealbert.ws;

import javax.jws.WebService;

import com.mikealbert.exception.MalException;

@WebService
public interface InterfaceErrorsRemoval {
	public void removeInterfaceError(String interfaceType, String messageId) throws MalException;
}
