package com.mikealbert.vision.view.bean.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import net.sf.cglib.core.CollectionUtils;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.chrome.services.description7a.Models;
import com.mikealbert.data.DataUtilities;
import com.mikealbert.data.entity.Model;
import com.mikealbert.data.vo.ModelSearchResultVO;
import com.mikealbert.util.MALUtilities;
import com.mikealbert.vision.view.bean.BaseBean;

@Component
@Scope("view")
public class modelDisplayBean extends BaseBean {
	private static final long serialVersionUID = -6711113468255150013L;

	private List<Model> trims;
	private Model trim;
	private boolean tableDisplay;
	
	/**
	 * Initializes the bean
	 */
    @SuppressWarnings("unchecked")
    @PostConstruct    
	public void init(){     	
    	try {
    		FacesContext fc = FacesContext.getCurrentInstance();
    		trims = fc.getApplication().evaluateExpressionGet(fc,"#{cc.attrs.entities}", new ArrayList<Model>().getClass());
    		    		
    		// The UI relies on this logic to determine
    		// when to display a table or a set of fields.
    		if(getTrims().size() == 1){
    			setTrim(getTrims().get(0));
    			setTableDisplay(false);
    		} else {
    			setTableDisplay(true);
    		}
    		
		} catch(Exception e) {	
			super.addErrorMessage("generic.error", e.getMessage());
		}    	
      	    	    	
    }


	public List<Model> getTrims() {
		return trims;
	}


	public void setTrims(List<Model> trims) {
		this.trims = trims;
	}


	public Model getTrim() {
		return trim;
	}


	public void setTrim(Model trim) {
		this.trim = trim;
	}


	public boolean isTableDisplay() {
		return tableDisplay;
	}

	public void setTableDisplay(boolean tableDisplay) {
		this.tableDisplay = tableDisplay;
	}

		
}
