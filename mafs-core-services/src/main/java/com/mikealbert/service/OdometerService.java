package com.mikealbert.service;

import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.data.entity.TelematicsMileage;
import com.mikealbert.data.entity.UomCode;

/**
 * Public Interface implemented by {@link com.mikealbert.vision.service.OdometerServiceImpl} for interacting with business service methods concerning {@link com.mikealbert.data.entity.Odometer}(s) and {@link com.mikealbert.data.entity.OdometerReading}(s).
 *
 * @see com.mikealbert.data.entity.Odometer
 * @see com.mikealbert.data.entity.OdometerReading
 * @see com.mikealbert.vision.service.OdometerServiceImpl
 **/
public interface OdometerService {
	final static String ODO_READING_TYPE_ESTODO = "ESTODO";
	final static String ODO_READING_TYPE_SERVICE = "SERVICE";
	
	public Odometer getCurrentOdometer(FleetMaster fleetMaster);
	public Odometer getCurrentOdometer(Long fmsId);
	public LatestOdometerReadingV getCurrentOdometerReading(Long fmsId);
    public OdometerReading saveOdometerReading(FleetMaster fleetMaster, String readingCode, Long reading, String user);        
	public UomCode convertOdoUOMCode(String code);	
	public OdometerReading getOdometerReading(MaintenanceRequest source);
	public OdometerReading saveOrUpdateReading(MaintenanceRequest source, String username);
	public void saveOrUpdateOdoReading(MaintenanceRequest source, String username);
	public void saveOrUpdateOdoReading(TelematicsMileage telematicsMileage);
}
