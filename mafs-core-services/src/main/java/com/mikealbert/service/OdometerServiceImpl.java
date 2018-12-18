package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.OdometerDAO;
import com.mikealbert.data.dao.OdometerReadingDAO;
import com.mikealbert.data.dao.TelematicsMileageDAO;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.LatestOdometerReadingV;
import com.mikealbert.data.entity.MaintenanceRequest;
import com.mikealbert.data.entity.Odometer;
import com.mikealbert.data.entity.OdometerReading;
import com.mikealbert.data.entity.TelematicsMileage;
import com.mikealbert.data.entity.UomCode;
import com.mikealbert.exception.MalException;
import com.mikealbert.util.MALUtilities;

/**
 * Implementation of {@link com.mikealbert.vision.service.OdometerService}
 */
@Service("odometerService")
public class OdometerServiceImpl implements OdometerService {
	@Resource OdometerDAO 			odometerDAO;	
	@Resource OdometerReadingDAO	odometerReadingDAO;
	@Resource LookupCacheService 	lookupCacheService;
	@Resource TelematicsMileageDAO 	telematicsMileageDAO;
	
	/**
	 * Find the highest odometer reading for the provided fleet master; Used by Driver Allocation screen to retrieve 
	 * odometer reading
	 * @param fleetMaster Fleet Master
	 * @return Odometer 
	 */
	@Transactional
	public Odometer getCurrentOdometer(FleetMaster fleetMaster){
		Odometer currentOdometer = null;
		try{
			if(!MALUtilities.isEmpty(fleetMaster)){
				currentOdometer = odometerDAO.findMaxByFmsId(fleetMaster.getFmsId());
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting the latest odometer reading for Fleet Master id: " + fleetMaster.getFmsId() }, ex);				
		}		
		return currentOdometer;
	}
	
	/**
	 * Find the highest odometer reading for the provided fleet master; Used by Driver Allocation screen to retrieve 
	 * odometer reading
	 * @param fleetMaster Fleet Master
	 * @return Odometer 
	 */
	@Transactional
	public Odometer getCurrentOdometer(Long fmsId){
		Odometer currentOdometer = null;
		try{
			if(!MALUtilities.isEmpty(fmsId)){
				currentOdometer = odometerDAO.findMaxByFmsId(fmsId);
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", 
					new String[] { "getting the latest odometer reading for Fleet Master id: " + fmsId }, ex);				
		}		
		return currentOdometer;
	}
	
	/**
	 * Retrieves the latest odometer reading from the view, LASTEST_ODOMETER_READING_V.
	 * 
	 * The view wraps the algorithm used to determined the latest reading from various sources.
	 * @return LatestOdometerReadingV
	 */
	public LatestOdometerReadingV getCurrentOdometerReading(Long fmsId) {
		LatestOdometerReadingV latestReading;
		latestReading = odometerReadingDAO.findLatestOdometerReading(fmsId);
	    return latestReading;
	}
	
	/**
	 * Retrieves the odometer reading based on the maintenance request id.
	 * This is "SERVICE" reading captured while created or editing the 
	 * maintenance purchase order.
	 */
	@Transactional
	public OdometerReading getOdometerReading(MaintenanceRequest source){
		OdometerReading reading = null;
		reading = odometerReadingDAO.findByMrqId(source.getMrqId());
		return reading;		
	}
	
	
	/**
	 * Set up and save the Odometer Reading; Used when saving a new driver allocation in Driver Allocation screen
	 * @param fleetMaster Fleet Master to save reading against
	 * @param readingCode New odometer amount
	 * @param reading Updated odometer reading number
	 * @param user User providing the reading
	 * @return OdometerReading that has been saved.
	 */
	@Transactional	
    public OdometerReading saveOdometerReading(FleetMaster fleetMaster, String readingCode, Long reading, String user){
		Odometer odometer = null;
		OdometerReading odometerReading = null;
		
		try{
			odometer = odometerDAO.findMaxByFmsId(fleetMaster.getFmsId());
			odometerReading = new OdometerReading();
			odometerReading.setOdometer(odometer);
			odometerReading.setReadingCode(readingCode);
			odometerReading.setReading(reading);
			odometerReading.setUserId(user);
			odometerReading.setReadingDate(Calendar.getInstance().getTime());
			odometerReading.setStmntInd("N");			

			odometerReading = odometerReadingDAO.saveAndFlush(odometerReading);			
						
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "Saving the odometer reading" }, ex);				
		}	
		
		return odometerReading;
	}
	
	/**
	 * Saves the odometer reading captured on a given fleet maintenance purchase order.
	 * An odometer reading is either created or updated if one exist for the PO.
	 * @param source Fleet Maintenance Purchase order
	 * @param username The username of the user that initiated the transaction.
	 */
	@Transactional
	public OdometerReading saveOrUpdateReading(MaintenanceRequest source, String username){
		Odometer odometer = null;		
		OdometerReading odometerReading = null;
		
		try{
			//Check if an Odo Reading exist for the maintenance request
			odometerReading = odometerReadingDAO.findByMrqId(source.getMrqId());
			if(MALUtilities.isEmpty(odometerReading)){
				//create new odometer reading
				odometer = odometerDAO.findMaxByFmsId(source.getFleetMaster().getFmsId());
				odometerReading = new OdometerReading();
//				odometer.setFinalOdoReading(source.getCurrentOdo());
				odometerReading.setOdometer(odometer);
				odometerReading.setReadingCode(OdometerService.ODO_READING_TYPE_SERVICE);
				odometerReading.setReading(source.getCurrentOdo());
				odometerReading.setUserId(username);
				odometerReading.setReadingDate(source.getActualEndDate());// Changed Start date to End Date for Bug 16247
				odometerReading.setStmntInd("N");
				//odometerReading.setMrqId(source.getMrqId());	
				odometerReading.setMaintenanceRequest(source);
			} else {
				//update existing reading
				odometerReading = odometerReadingDAO.findByMrqId(source.getMrqId());
				odometerReading.setReading(source.getCurrentOdo());
				odometerReading.setReadingDate(source.getActualEndDate());// Changed Start date to End Date for Bug 16247	
				odometerReading.setUserId(username);
			}
			
			odometerReading = odometerReadingDAO.saveAndFlush(odometerReading);
			
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "Saving the Maintenance Purchase Order's odometer reading" }, ex);				
		}	
		return odometerReading;
	}
	
	/**
	 * This method will update the Odometer and OdometerReading  objects associated with mainteance Request object so that it can get cascade save or update on save or updating maintenance Request. 
	 * @param source
	 * @param username
	 */
	@Transactional
	public void saveOrUpdateOdoReading(MaintenanceRequest source, String username){
		Odometer odometer = null;		
		OdometerReading odometerReading = null;
		
		try{
			if(!MALUtilities.isEmpty(source.getOdometerReadings()) && source.getOdometerReadings().size() > 0){
				odometerReading = source.getOdometerReadings().get(0); 
			}
			odometer = odometerDAO.findMaxByFmsId(source.getFleetMaster().getFmsId());
			if(!MALUtilities.isEmpty(odometer)){
				odometer.setFinalOdoReading(source.getCurrentOdo());	
			}
			if(MALUtilities.isEmpty(odometerReading)){
				//create new odometer reading
				odometerReading = new OdometerReading();
				odometerReading.setOdometer(odometer);
				odometerReading.setReadingCode(OdometerService.ODO_READING_TYPE_SERVICE);
				odometerReading.setReading(source.getCurrentOdo());
				odometerReading.setUserId(username);
				odometerReading.setReadingDate(source.getActualEndDate());// Changed Start date to End Date for Bug 16247
				odometerReading.setStmntInd("N");
				odometerReading.setMaintenanceRequest(source);
				List<OdometerReading> odometerReadings = new ArrayList<OdometerReading>();
				odometerReadings.add(odometerReading);
				source.setOdometerReadings(odometerReadings);
			} else {
				//update existing reading
				odometerReading.setOdometer(odometer);
				odometerReading.setReading(source.getCurrentOdo());
				odometerReading.setReadingDate(source.getActualEndDate());// Changed Start date to End Date for Bug 16247	
				odometerReading.setUserId(username);
			}
		} catch(Exception ex) {
			throw new MalException("generic.error.occured.while", new String[] { "Saving the Maintenance Purchase Order's odometer reading" }, ex);
		}	
	}	
	
	public UomCode convertOdoUOMCode(String code){
		List<UomCode> uomCodes = lookupCacheService.getUomCodes();
		UomCode uomCode = null;
		
		for(UomCode uom : uomCodes){
			if(uom.getUomCode().equals(code) && uom.getUomTYpe().equals("DISTANCE")){
				uomCode = uom;
				break;
			}
		}
		
		return uomCode;		
	}

	/**
	 * Save or update a record within the TelematicsMileage table
	 * @param telematicsMileage entity holding the appropriate
	 */
	@Transactional	
	public void saveOrUpdateOdoReading(TelematicsMileage telematicsMileage) {
		try {
			telematicsMileageDAO.saveAndFlush(telematicsMileage);
		} catch(Exception ex) {
			throw new MalException("generic.error", new String[] { ex.getMessage() }, ex);
		}
	}
	
}
