package com.mikealbert.vision.view.bean.components;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.mikealbert.data.entity.CostDatabaseCategoryCodes;
import com.mikealbert.data.entity.Dist;
import com.mikealbert.data.entity.FleetMaster;
import com.mikealbert.data.entity.GlCode;
import com.mikealbert.data.entity.ServiceProvider;
import com.mikealbert.data.enumeration.CorporateEntity;
import com.mikealbert.data.vo.GlCodeLOVVO;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.FleetMasterService;
import com.mikealbert.service.GlCategoryService;
import com.mikealbert.service.GlCodeService;
import com.mikealbert.service.GlDistributionService;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class glDistributionBean extends BaseBean{
	private static final long serialVersionUID = -7243110837187947560L;
	
	@Resource GlDistributionService glDistributionService;
	@Resource GlCodeService glCodeService; 
	@Resource GlCategoryService glCategoryService;
	@Resource FleetMasterService fleetMasterService;
	
	static final String VIEW_NAME = "glDistribution";
	
	private List<Dist> glDistList;
	private Long docId;
	private String unitNo;
	private CorporateEntity[] corporateEntityList;
	
	/**
	 * Initializes the bean
	 */
    public void initDialog(Long docId, String unitNo){ 
    	try {
    	    this.unitNo = unitNo;
    	    this.docId = docId;
    	    getGLDistData();
    	}catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}   
    }
	
	private void getGLDistData(){
    	try{
	    	if(docId != null){
	    		setGlDistList(glDistributionService.getGlDistByDocId(docId));
	    	}
    	}catch(Exception ex) {
			super.addErrorMessage("generic.error", ex.getMessage());
    	}
    }
    
    public void saveDist(){
    	try {
			glDistributionService.saveOrUpdateGLDist(glDistList);
		} catch (MalBusinessException e) {
			super.addErrorMessage("generic.error", e.getMessage());
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam("failure", true);
		}
    }
    
    public void decodeGlCode(String code, Long corpId, int index){
    	try{
	    	PageRequest page = null;
			List<GlCodeLOVVO> glCodeList = glCodeService.getGlCodes(code, corpId, page);
			
			if(glCodeList.size() == 1){
				glDistList.get(index).setGlCodeDescription(glCodeList.get(0).getDescription());
				glDistList.get(index).setGlCode(glCodeList.get(0).getCode());
			}else if (glCodeList.size() == 0) {
				super.addErrorMessage("glCodeDataTable:"+index+":glCode", "decode.noMatchFound.msg", "GL Code: " + code);
			}else {
				super.addErrorMessage("glCodeDataTable:"+index+":glCode", "decode.multipleMatchesFound.msg", "GL Code: " + code);
			}
    	}catch(MalBusinessException ex){
    		super.addErrorMessage("generic.error", ex.getMessage());
    	}
	}
    
    public void decodeGlCategory(String category, int index){
    	try{
	    	PageRequest page = null;
	    	if(!MALUtilities.isEmpty(category)){
	    		List<CostDatabaseCategoryCodes> glCategoryList = glCategoryService.getGlCategories(category, page);
				
				if(glCategoryList.size() == 1){
					glDistList.get(index).setCdbCode4(glCategoryList.get(0).getCategory());
				}else if (glCategoryList.size() == 0) {
					super.addErrorMessage("glCategoryDataTable:"+index+":glCategory", "decode.noMatchFound.msg", "GL Category: " + category);
				}else {
					super.addErrorMessage("glCategoryDataTable:"+index+":glCategory", "decode.multipleMatchesFound.msg", "GL Category: " + category);
				}
	    	}else{
	    		//nothing to do as of now
	    	}
			
    	}catch(MalBusinessException ex){
    		super.addErrorMessage("generic.error", ex.getMessage());
    	}
	}    
    
    public boolean hasPermission(){
    	return true;
		//TODO: After Fleet Maint Security Story: return super.hasPermission(VIEW_NAME);
	}

	public CorporateEntity[] getCorporateEntityList() {
		return CorporateEntity.values();
	}

	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public List<Dist> getGlDistList() {
		return glDistList;
	}

	public void setGlDistList(List<Dist> glDistList) {
		this.glDistList = glDistList;
	}
}
