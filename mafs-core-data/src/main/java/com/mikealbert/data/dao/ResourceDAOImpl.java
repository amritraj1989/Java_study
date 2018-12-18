package com.mikealbert.data.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Query;

import com.mikealbert.data.entity.AppResource;
import com.mikealbert.data.dao.GenericDAOImpl;


public  class ResourceDAOImpl extends GenericDAOImpl<AppResource, Long> implements ResourceDAOCustom {

	@Resource
	private ResourceDAO resourceDAO;
	
	private static final long serialVersionUID = 1L;
	
	public Map<String, List<String>> getResourceRoleMap(){
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String stmt = "SELECT r.resource_name, ps.permission_set "
                      + "FROM resources r INNER JOIN resource_permissions rp ON r.res_id = rp.res_res_id "
                      + "INNER JOIN permission_sets ps ON rp.prs_prs_id = ps.prs_id "
                      + "ORDER BY 1 ASC";
		Query query = entityManager.createNativeQuery(stmt);
		List<Object[]> results = query.getResultList();
		String resourceId = null;
		
		
		for(Object[] record : results){			
			//When there resource id is null, it is the start of the iteration.
			//However, there is a change, we have the resource id and all of its
			//roles.
			if(resourceId == null || !((String)record[0]).equals(resourceId)){
				resourceId = (String)record[0];					
				map.put(resourceId, new ArrayList<String>());					
			}
			
			map.get(resourceId).add((String)record[1]);			
									
		}
		
		return map;
	}
	
}
