package com.mikealbert.data.vo;

import java.util.List;

import com.mikealbert.data.entity.Contact;
import com.mikealbert.data.entity.DriverGrade;
import com.mikealbert.data.entity.ExternalAccount;
import com.mikealbert.data.entity.QuoteRequest;
import com.mikealbert.data.entity.QuoteRequestQuote;

public class QuoteRequestVO {
	
	private ExternalAccount client;
	private List<ExternalAccount> childAccounts;
	private List<String> clientConfigurations;
	private List<Contact> contacts;
	private List<DriverGrade> driverGradeGroups;
	private List<ClientPrefSupplierVO> clientPrefSuppliers;
	private List<ClientFleetCodeVO> clientFleetCodes;
	private List<ClientConsultantsVO> clientConsultants;
	private List<ClientQuoteRequestServiceElementVO> clientServiceElements;
	private QuoteRequest quoteRequest;
	private ClientCreditLimitsVO clientCreditLimits;
	private List<QuoteRequestClientProfilesVO> clientProfiles;
	private QuoteRequestQuoteVO quote;
	private List<QuoteRequestQuote> quoteRequestQuotes;

	public ExternalAccount getClient() {
		return client;
	}

	public void setClient(ExternalAccount client) {
		this.client = client;
	}

	public List<ExternalAccount> getChildAccounts() {
		return childAccounts;
	}

	public void setChildAccounts(List<ExternalAccount> childAccounts) {
		this.childAccounts = childAccounts;
	}

	public List<String> getClientConfigurations() {
		return clientConfigurations;
	}

	public void setClientConfigurations(List<String> clientConfigurations) {
		this.clientConfigurations = clientConfigurations;
	}

	public List<DriverGrade> getDriverGradeGroups() {
		return driverGradeGroups;
	}

	public void setDriverGradeGroups(List<DriverGrade> driverGradeGroups) {
		this.driverGradeGroups = driverGradeGroups;
	}

	public List<ClientPrefSupplierVO> getClientPrefSuppliers() {
		return clientPrefSuppliers;
	}

	public void setClientPrefSuppliers(List<ClientPrefSupplierVO> clientPrefSuppliers) {
		this.clientPrefSuppliers = clientPrefSuppliers;
	}

	public List<ClientFleetCodeVO> getClientFleetCodes() {
		return clientFleetCodes;
	}

	public void setClientFleetCodes(List<ClientFleetCodeVO> clientFleetCodes) {
		this.clientFleetCodes = clientFleetCodes;
	}

	public List<ClientConsultantsVO> getClientConsultants() {
		return clientConsultants;
	}

	public void setClientConsultants(List<ClientConsultantsVO> clientConsultants) {
		this.clientConsultants = clientConsultants;
	}
	
	public List<ClientQuoteRequestServiceElementVO> getClientServiceElements() {
		return clientServiceElements;
	}

	public void setClientServiceElements(List<ClientQuoteRequestServiceElementVO> clientServiceElements) {
		this.clientServiceElements = clientServiceElements;
	}

	public QuoteRequest getQuoteRequest() {
		return quoteRequest;
	}

	public void setQuoteRequest(QuoteRequest quoteRequest) {
		this.quoteRequest = quoteRequest;
	}

	public ClientCreditLimitsVO getClientCreditLimits() {
		return clientCreditLimits;
	}

	public void setClientCreditLimits(ClientCreditLimitsVO clientCreditLimits) {
		this.clientCreditLimits = clientCreditLimits;
	}

	public List<QuoteRequestClientProfilesVO> getClientProfiles() {
		return clientProfiles;
	}

	public void setClientProfiles(List<QuoteRequestClientProfilesVO> clientProfiles) {
		this.clientProfiles = clientProfiles;
	}

	public QuoteRequestQuoteVO getQuote() {
		return quote;
	}

	public void setQuote(QuoteRequestQuoteVO quote) {
		this.quote = quote;
	}

	public List<QuoteRequestQuote> getQuoteRequestQuotes() {
		return quoteRequestQuotes;
	}

	public void setQuoteRequestQuotes(List<QuoteRequestQuote> quoteRequestQuotes) {
		this.quoteRequestQuotes = quoteRequestQuotes;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

}
