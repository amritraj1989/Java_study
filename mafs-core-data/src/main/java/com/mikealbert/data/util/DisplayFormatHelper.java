package com.mikealbert.data.util;

import java.util.List;

import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.entity.ServiceProviderDiscount;
import com.mikealbert.data.enumeration.ServiceProviderDiscountApplicationTypes;
import com.mikealbert.util.MALUtilities;

// TODO: longer term this should be moved into a core-ui/core-view project along with the LOVs and composite controls that we want to share (core user interface and display code)
public class DisplayFormatHelper {
	public static String formatClientForTable(String clientName, String clientNumber, String newLine){
		StringBuilder clientFormattedBuilder = new StringBuilder();
		if(!MALUtilities.isEmptyString(clientName)){
			clientFormattedBuilder.append(clientName);
		}
		if(!MALUtilities.isEmptyString(clientNumber)){
			clientFormattedBuilder.append(newLine);
			clientFormattedBuilder.append("Client No: "); 
			clientFormattedBuilder.append(clientNumber);
		}
		return clientFormattedBuilder.toString();
	}
	public static String formatDriverForTable(String firstName, String lastNumber, boolean poolManager, String newLine){
		StringBuilder driverFormattedBuilder = new StringBuilder();
		if(!MALUtilities.isEmptyString(lastNumber)){
			driverFormattedBuilder.append(lastNumber);
		}
		if(!MALUtilities.isEmptyString(firstName)){
			driverFormattedBuilder.append(", "); 
			driverFormattedBuilder.append(firstName);
		}
		if(poolManager){
			driverFormattedBuilder.append(" (Pool Mgr)");
		}
		
		return driverFormattedBuilder.toString();
	}
	public static String formatAddressForTable(String businessAddress, String addressLine1, String addressLine2, String addressLine3, String addressLine4, String city, String stateProvince, String postalCode, String newLine){
		StringBuilder addressFormattedBuilder = new StringBuilder();
		if(!MALUtilities.isEmptyString(businessAddress)){
			addressFormattedBuilder.append(businessAddress);
			addressFormattedBuilder.append(newLine);
		}
		if(!MALUtilities.isEmptyString(addressLine1)){
			addressFormattedBuilder.append(addressLine1);
		}
		if(!MALUtilities.isEmptyString(addressLine2)){
			addressFormattedBuilder.append(newLine);
			addressFormattedBuilder.append(addressLine2);
		}
		if(!MALUtilities.isEmptyString(addressLine3)){
			addressFormattedBuilder.append(newLine);
			addressFormattedBuilder.append(addressLine3);
		}
		if(!MALUtilities.isEmptyString(addressLine4)){
			addressFormattedBuilder.append(newLine);
			addressFormattedBuilder.append(addressLine4);
		}
		if(!MALUtilities.isEmptyString(city) && !MALUtilities.isEmptyString(stateProvince) && !MALUtilities.isEmptyString(postalCode)){
			addressFormattedBuilder.append(newLine);
			addressFormattedBuilder.append(city);
			addressFormattedBuilder.append(", ");
			addressFormattedBuilder.append(stateProvince);
			addressFormattedBuilder.append(" ");
			addressFormattedBuilder.append(postalCode);
		}
		
		return addressFormattedBuilder.toString();		
		
	}
	public static String formatAddressForSingleLineLabel(String businessAddress, String addressLine1, String addressLine2, String addressLine3, String addressLine4, String city, String stateProvince, String postalCode){
		StringBuilder addressFormattedBuilder = new StringBuilder();
		if(!MALUtilities.isEmptyString(businessAddress)){
			addressFormattedBuilder.append(businessAddress);
		}
		if(!MALUtilities.isEmptyString(addressLine1)){
			addressFormattedBuilder.append(addressFormattedBuilder.length() > 0 ? ", ".concat(addressLine1) : addressLine1);
		}
		if(!MALUtilities.isEmptyString(addressLine2)){
			addressFormattedBuilder.append(addressFormattedBuilder.length() > 0 ? ", ".concat(addressLine2) : addressLine2);
		}
		if(!MALUtilities.isEmptyString(addressLine3)){
			addressFormattedBuilder.append(addressFormattedBuilder.length() > 0 ? ", ".concat(addressLine3) : addressLine3);
		}
		if(!MALUtilities.isEmptyString(addressLine4)){
			addressFormattedBuilder.append(addressFormattedBuilder.length() > 0 ? ", ".concat(addressLine4) : addressLine4);
		}
		if(!MALUtilities.isEmptyString(city) && !MALUtilities.isEmptyString(stateProvince) && !MALUtilities.isEmptyString(postalCode)){
			addressFormattedBuilder.append(", ");
			addressFormattedBuilder.append(city);
			addressFormattedBuilder.append(", ");
			addressFormattedBuilder.append(stateProvince);
			addressFormattedBuilder.append(" ");
			addressFormattedBuilder.append(postalCode);
		}
		
		return addressFormattedBuilder.toString();		
		
	}	
	public static String formatPhoneNumberForTable(String areaCode, String phoneNumber, String extension, String newLine){
		StringBuilder phoneFormattedBuilder = new StringBuilder();
		if(!MALUtilities.isEmptyString(areaCode)){
			phoneFormattedBuilder.append("(");
			phoneFormattedBuilder.append(areaCode);
			phoneFormattedBuilder.append(") ");
		}
		if(!MALUtilities.isEmptyString(phoneNumber)){ 
			phoneFormattedBuilder.append(phoneNumber);
		}
		if(!MALUtilities.isEmptyString(extension)){
			phoneFormattedBuilder.append(" Ext:");
			phoneFormattedBuilder.append(extension);
		}
		
		return phoneFormattedBuilder.toString();
	}
	
	public static String formatSupplierDiscountForDisplay(ServiceProvider serviceProvider, List<ServiceProviderDiscount> serviceProviderDiscounts, String newLine){
		StringBuilder supplierDiscountFormattedBuilder = new StringBuilder();
		ServiceProviderDiscount serviceProviderDiscount = null;
		
		String discountApplication = null;
		if(!MALUtilities.isEmpty(serviceProvider) && serviceProvider.getServiceProviderId() != null){
			if (serviceProvider.getNetworkVendor().equals("Y")) {
				supplierDiscountFormattedBuilder.append("Network");
				supplierDiscountFormattedBuilder.append(newLine);
				if (serviceProviderDiscounts.size() == 1) {
					serviceProviderDiscount = serviceProviderDiscounts.get(0);
					if (!MALUtilities.isEmpty(serviceProviderDiscount)) {
						if (serviceProviderDiscount.getDiscAppl().equals(ServiceProviderDiscountApplicationTypes.DISC_APPL_REBATE.getCode())) {
							discountApplication = ServiceProviderDiscountApplicationTypes.DISC_APPL_REBATE.getDescription();
						} else if (serviceProviderDiscount.getDiscAppl().equals(ServiceProviderDiscountApplicationTypes.DISC_APPL_OFF_INVOICE.getCode())) {
							discountApplication = ServiceProviderDiscountApplicationTypes.DISC_APPL_OFF_INVOICE.getDescription();
						} else {
							discountApplication = "Unknown";
						}
						
						if(!MALUtilities.isEmpty(serviceProviderDiscount.getPartsDisc())){
							supplierDiscountFormattedBuilder.append("Parts Disc ");
							supplierDiscountFormattedBuilder.append(serviceProviderDiscount.getPartsDisc());
							supplierDiscountFormattedBuilder.append(" % ");
							supplierDiscountFormattedBuilder.append(discountApplication);
							supplierDiscountFormattedBuilder.append(newLine);
						}
						
						if(!MALUtilities.isEmpty(serviceProviderDiscount.getLabourDisc())){
							supplierDiscountFormattedBuilder.append("Labor Disc ");
							supplierDiscountFormattedBuilder.append(serviceProviderDiscount.getLabourDisc());
							supplierDiscountFormattedBuilder.append(" % ");
							supplierDiscountFormattedBuilder.append(discountApplication);
							supplierDiscountFormattedBuilder.append(newLine);
						}
						
						if(!MALUtilities.isEmpty(serviceProviderDiscount.getTyreDisc())){
							supplierDiscountFormattedBuilder.append("Tires Disc ");
							supplierDiscountFormattedBuilder.append(serviceProviderDiscount.getTyreDisc());
							supplierDiscountFormattedBuilder.append(" % ");
							supplierDiscountFormattedBuilder.append(discountApplication);
							supplierDiscountFormattedBuilder.append(newLine);
						}
	
					}
				} else if (serviceProviderDiscounts.size() > 1) {
					
					supplierDiscountFormattedBuilder.append("Parts Disc %");
					supplierDiscountFormattedBuilder.append(" ERROR");
					supplierDiscountFormattedBuilder.append(newLine);
					
					supplierDiscountFormattedBuilder.append("Labor Disc %");
					supplierDiscountFormattedBuilder.append(" ERROR");
					supplierDiscountFormattedBuilder.append(newLine);
					
					supplierDiscountFormattedBuilder.append("Tires Disc %");
					supplierDiscountFormattedBuilder.append(" ERROR");
					supplierDiscountFormattedBuilder.append(newLine);
					
				}
			} else {
				supplierDiscountFormattedBuilder.append("Non-Network");
			}
		}
		
		return supplierDiscountFormattedBuilder.toString();
		
	}
	
	public static String formatTrimForTable(String trim, String engine, String newLine){
		StringBuilder formattedText = new StringBuilder();
		
		formattedText.append(trim);
		formattedText.append(newLine);
		formattedText.append(engine);		
		
		return formattedText.toString();
	}
}
