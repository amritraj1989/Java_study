package com.mikealbert.websvc.client;

import javax.xml.bind.JAXBElement;

import com.mikealbert.service.tal.websvc.transaction.CancelTransactionRequestType;
import com.mikealbert.service.tal.websvc.transaction.CancelTransactionResponseType;
import com.mikealbert.service.tal.websvc.transaction.CreateNotificationRequestType;
import com.mikealbert.service.tal.websvc.transaction.CreateNotificationResponseType;
import com.mikealbert.service.tal.websvc.transaction.CreateTransactionRequestType;
import com.mikealbert.service.tal.websvc.transaction.CreateTransactionResponseType;
import com.mikealbert.service.tal.websvc.transaction.GetTransactionsRequestType;
import com.mikealbert.service.tal.websvc.transaction.GetTransactionsResponseType;
import com.mikealbert.service.tal.websvc.transaction.ObjectFactory;

public class TALTransactionWebsvcClient extends WebServicesClient {

	private static final String TRANSACTION_WEBSVC_CONTEXT_PATH = "com.mikealbert.service.tal.websvc.transaction";
//	private static final String TRANSACTION_WSDL_URL = "http://localhost:7001/tal/Transaction?WSDL";

	public TALTransactionWebsvcClient(String wsdlURL ) {
		super(wsdlURL,TRANSACTION_WEBSVC_CONTEXT_PATH);
	}

	@SuppressWarnings("unchecked")
	public CreateNotificationResponseType sendCreateNotificationRequest(CreateNotificationRequestType request) {

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<CreateNotificationRequestType> fianlRequest = objectFactory.createCreateNotification(request);
		JAXBElement<CreateNotificationResponseType> jaxCreateNotificationResponseType = (JAXBElement<CreateNotificationResponseType>) marshalSendAndReceive(fianlRequest);

		return jaxCreateNotificationResponseType.getValue();
	}
	
	
	@SuppressWarnings("unchecked")
	public CreateTransactionResponseType sendForceCreateTransactionRequest(CreateTransactionRequestType request) {

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<CreateTransactionRequestType> fianlRequest = objectFactory.createCreateTransaction(request);
		JAXBElement<CreateTransactionResponseType> jaxCreateTransactionResponseType = (JAXBElement<CreateTransactionResponseType>) marshalSendAndReceive(fianlRequest);

		return jaxCreateTransactionResponseType.getValue();
	}

	@SuppressWarnings("unchecked")
	public CancelTransactionResponseType sendCancelTransactionRequest(CancelTransactionRequestType request) {

		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<CancelTransactionRequestType> fianlRequest = objectFactory.createCancelTransaction(request);
		JAXBElement<CancelTransactionResponseType> jaxCreateTransactionResponseType = (JAXBElement<CancelTransactionResponseType>) marshalSendAndReceive(fianlRequest);

		return jaxCreateTransactionResponseType.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public GetTransactionsResponseType getTransactionsRequest(GetTransactionsRequestType request) {
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<GetTransactionsRequestType> fianlRequest = objectFactory.createGetTransactions(request);
		JAXBElement<GetTransactionsResponseType> jaxCreateTransactionResponseType = (JAXBElement<GetTransactionsResponseType>) marshalSendAndReceive(fianlRequest);
		return jaxCreateTransactionResponseType.getValue();
	}
	

}
