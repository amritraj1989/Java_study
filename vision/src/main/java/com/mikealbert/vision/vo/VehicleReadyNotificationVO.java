package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.mikealbert.data.enumeration.LogBookTypeEnum;

/**
 * Represents the content of the Vehicle Ready Notification, which is
 * triggered from the order to delivery business process.
 * 
 * @author sibley
 *
 */
public class VehicleReadyNotificationVO implements Serializable {
	private static final long serialVersionUID = -6303825383819821011L;
	
	private String unitNumber;
	private String fleetReferenceNumber;
	private String driverName;
	
	public VehicleReadyNotificationVO(){}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getFleetReferenceNumber() {
		return fleetReferenceNumber;
	}

	public void setFleetReferenceNumber(String fleetReferenceNumber) {
		this.fleetReferenceNumber = fleetReferenceNumber;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

    
}