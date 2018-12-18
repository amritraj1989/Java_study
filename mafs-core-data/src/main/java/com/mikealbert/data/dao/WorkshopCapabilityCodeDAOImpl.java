package com.mikealbert.data.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import com.mikealbert.data.entity.WorkshopCapabilityCode;

public class WorkshopCapabilityCodeDAOImpl extends GenericDAOImpl<WorkshopCapabilityCode, String> implements WorkshopCapabilityCodeDAOCustom {
	private static final long serialVersionUID = -5275500471183955278L;

	public List<WorkshopCapabilityCode> findWorkshopCapabilityCodesByGroupName(String groupName) {
		String sql = " SELECT WCC.WORKSHOP_CAPABILITY, WCC.DESCRIPTION" +
					 " FROM WILLOW2k.WORKSHOP_CAPABILITY_CODES WCC" +
					 "  JOIN WILLOW2k.WORKSHOP_CAPABILITY_LINKS WCL ON WCL.WORKSHOP_CAPABILITY = WCC.WORKSHOP_CAPABILITY" +
					 "  JOIN WILLOW2k.WORKSHOP_CAPABILITY_GROUPS WCG ON WCG.WCG_ID = WCL.WCG_WCG_ID" +
					 " WHERE WCG.GROUP_NAME = :groupName" +
					 " ORDER BY WCC.DESCRIPTION";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("groupName", groupName);
		List<WorkshopCapabilityCode> workshopCapabilityCodes = new ArrayList<WorkshopCapabilityCode>();
	
		@SuppressWarnings("unchecked")
		List<Object[]> objectList  = (List<Object[]>) query.getResultList();
		if (objectList != null && objectList.size() > 0) {	
			for (Iterator<Object[]> iterator = objectList.iterator(); iterator.hasNext();) {
				Object[] object = iterator.next();
				WorkshopCapabilityCode workshopCapabilityCode = new WorkshopCapabilityCode();
	
				workshopCapabilityCode.setWorkshopCapability((String) object[0]);
				workshopCapabilityCode.setDescription((String) object[1]);
	
				workshopCapabilityCodes.add(workshopCapabilityCode);
			}
		}
		return workshopCapabilityCodes;
	}
}
