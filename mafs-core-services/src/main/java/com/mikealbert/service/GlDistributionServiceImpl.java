package com.mikealbert.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.DistDAO;
import com.mikealbert.data.dao.GlCodeDAO;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.Doc;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.util.MALUtilities;

@Service
public class GlDistributionServiceImpl implements GlDistributionService{
	@Resource DistDAO distDAO;
	@Resource GlCodeDAO glCodeDAO;
	
	@Transactional
	public List<Dist> getGlDistByDocId(Long docId) throws MalBusinessException {
		try {
			List<Dist> glDistList= distDAO.findDistByDocId(docId);
			setTransientDistDescription(glDistList);
			return glDistList;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	private void setTransientDistDescription(List<Dist> glDistList){
		List<GlCode> glCodes = glCodeDAO.findGlCodeByDist(glDistList);
		for(Dist dist : glDistList){
			  for(GlCode glCode : glCodes){
				  if(glCode.getId().getCode().equalsIgnoreCase(dist.getGlCode()) &&
						  glCode.getId().getCorpId() == dist.getCorpId()){
					  dist.setGlCodeDescription(glCode.getDescription());
				  }
			  }
		}
	}
	
	@Transactional
	public void saveOrUpdateGLDist(List<Dist> distList) throws MalBusinessException{
		validateGlCodeOnSave(distList);
		
		for(Dist dist : distList){
			distDAO.saveAndFlush(dist);
		}
	}
	
	private void validateGlCodeOnSave(List<Dist> glDistList) throws MalBusinessException{
		ArrayList<String> messages = new ArrayList<String>();
		List<GlCode> glCodes = glCodeDAO.findGlCodeByDist(glDistList);
		boolean found = false;
		
		for(Dist dist : glDistList){
			  found = false;
			  
			  for(GlCode glCode : glCodes){
				  if(glCode.getId().getCode().equalsIgnoreCase(dist.getGlCode()) &&
						  glCode.getId().getCorpId() == dist.getCorpId()){
					  found = true;
				  }
			  }
			  
			  if(found == false){
				  messages.add("Invalid GL Code: " + dist.getGlCode() + " for the provided Internal Company");
			  }
			  if(MALUtilities.isEmpty(dist.getCdbCode4())){
				  messages.add("Category is required");
			  }
		}
		
		if(messages.size() > 0)
			throw new MalBusinessException("service.validation", messages.toArray(new String[messages.size()]));
	}
}
