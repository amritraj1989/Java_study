package com.mikealbert.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mikealbert.data.dao.CapitalElementGroupMappingDAO;
import com.mikealbert.data.dao.QuotationModelDAO;
import com.mikealbert.data.entity.CapitalElement;
import com.mikealbert.data.entity.CapitalElementGroup;
import com.mikealbert.data.entity.CapitalElementGroupMapping;
import com.mikealbert.data.entity.QuotationDealerAccessory;
import com.mikealbert.data.entity.QuotationModel;
import com.mikealbert.data.entity.QuotationModelAccessory;
import com.mikealbert.exception.MalBusinessException;
import com.mikealbert.service.vo.QuoteCostElementVO;
import com.mikealbert.service.enumeration.NonCapitalElementEnum;

@Service("capitalCostElementService")
@Transactional(readOnly = true)
public class CapitalCostElementServiceImpl implements  CapitalCostElementService{	
	@Resource private CapitalElementGroupMappingDAO capitalElementGroupMappingDAO;
	@Resource private QuotationModelDAO quotationModelDAO;

	
	/**
	 * This method load all capital elements from database and non capital elements from NonCapitalElementEnum file.
	 * It merges them together and returns a list of objects sorted on group and display sequence. 
	 *  
	 */
	@Override
	public List<QuoteCostElementVO> getCapitalCostElementList(Long qmdId) throws MalBusinessException {
		 List<QuoteCostElementVO>  costElementlist = new ArrayList<QuoteCostElementVO>() ;
		 Map<Integer,List<QuoteCostElementVO>> nonCapitalElementMap =  new HashMap<Integer,List<QuoteCostElementVO>>();
		 
		 List<CapitalElementGroupMapping> capitalElementGroupList = capitalElementGroupMappingDAO.getAllElementGroupMapping();
		 List<Long> baseCapitalElements = null;

		 baseCapitalElements = quotationModelDAO.getBaseCapitalElementList(qmdId);
		 QuotationModel quotationModel =  quotationModelDAO.findById(qmdId).orElse(null);
			
		for (CapitalElementGroupMapping capitalElementGroupMapping : capitalElementGroupList) {
			if(baseCapitalElements != null) {
				CapitalElement capitalElement  = capitalElementGroupMapping.getCapitalElement();
				if(capitalElementApplies(baseCapitalElements, capitalElement.getCelId())) {
					CapitalElementGroup  capitalElementGroup  = capitalElementGroupMapping.getCapitalElementGroup();
					costElementlist.add(this.createCapitalElementVO(capitalElementGroup.getGroupName(), capitalElementGroup.getGroupDisplaySeq(), capitalElementGroupMapping.getGroupElementDisplaySeq(), capitalElement));
				}
			}
		 }		
		
		 for (NonCapitalElementEnum nonCapitalElement : NonCapitalElementEnum.values()) {
			 
			 if(! (nonCapitalElement.isModelAccessories() || nonCapitalElement.isDealerAccessories())){
				 	QuoteCostElementVO costElementVO  = this.createNonCapitalElementVO(nonCapitalElement);
				 	
					 if(nonCapitalElementMap.containsKey(nonCapitalElement.getGroupOrder())){
						 nonCapitalElementMap.get(nonCapitalElement.getGroupOrder()).add(costElementVO);
					 }else{
						 List<QuoteCostElementVO> list =  new ArrayList<QuoteCostElementVO>();
						 list.add(costElementVO);
						 nonCapitalElementMap.put(nonCapitalElement.getGroupOrder(),list);
					 }	
			 	}else{
					 if(nonCapitalElement.isModelAccessories()){
						 List<QuotationModelAccessory> modelAccessoriesList = quotationModel.getQuotationModelAccessories();
						 Collections.sort(modelAccessoriesList, new Comparator<QuotationModelAccessory>() {
							    public int compare(QuotationModelAccessory access1, QuotationModelAccessory access2) {
							        return access1.getOptionalAccessory().getAccessoryCode().getDescription().compareTo(
							        		access2.getOptionalAccessory().getAccessoryCode().getDescription());
							    }
							});
						 for (QuotationModelAccessory modelAccessory : modelAccessoriesList) {					
							 QuoteCostElementVO newCostElementVO = this.createModelAccessoryVO(nonCapitalElement.getGroupOrder(), nonCapitalElement.getElementOrderInGroup(), modelAccessory);

							 if(nonCapitalElementMap.containsKey(nonCapitalElement.getGroupOrder())){
								 nonCapitalElementMap.get(nonCapitalElement.getGroupOrder()).add(newCostElementVO);
							 }else{
								 List<QuoteCostElementVO> list =  new ArrayList<QuoteCostElementVO>();
								 list.add(newCostElementVO);
								 nonCapitalElementMap.put(nonCapitalElement.getGroupOrder(),list);
							 }	
						 }
					}
					 
					 if(nonCapitalElement.isDealerAccessories()){
						 List<QuotationDealerAccessory> dealerAccessoriesList = quotationModel.getQuotationDealerAccessories();
						 
								 Collections.sort(dealerAccessoriesList, new Comparator<QuotationDealerAccessory>() {
									    public int compare(QuotationDealerAccessory access1, QuotationDealerAccessory access2) {
									        return access1.getDealerAccessory().getDealerAccessoryCode().getDescription().compareTo(
									        			access2.getDealerAccessory().getDealerAccessoryCode().getDescription());
									    }
									});
						 
						 for (QuotationDealerAccessory dealerAccessory : dealerAccessoriesList) {					
							 	QuoteCostElementVO newCostElementVO = this.createDealerAccessoryVO(nonCapitalElement.getGroupOrder(), nonCapitalElement.getElementOrderInGroup(), dealerAccessory);

								 if(nonCapitalElementMap.containsKey(nonCapitalElement.getGroupOrder())){
									 nonCapitalElementMap.get(nonCapitalElement.getGroupOrder()).add(newCostElementVO);
								 }else{
									 List<QuoteCostElementVO> list =  new ArrayList<QuoteCostElementVO>();
									 list.add(newCostElementVO);
									 nonCapitalElementMap.put(nonCapitalElement.getGroupOrder(),list);
								 }	
								
						 }
					}
			 }
		 } 
		 
		 for (int i =0; i < costElementlist.size(); i++) {
			 QuoteCostElementVO capitalElement  = costElementlist.get(i);
			 if(nonCapitalElementMap.containsKey(capitalElement.getElementGroupOrder())){
				 List<QuoteCostElementVO> list = nonCapitalElementMap.remove(capitalElement.getElementGroupOrder());
				 costElementlist.addAll(i, list);
				 i= i + list.size();
			 }
		}
		
	 
		 return costElementlist;
		
	}
	
	public QuoteCostElementVO createCapitalElementVO(String elementGroupName, int groupOrder, int orderInGroup, CapitalElement capitalElement){
		QuoteCostElementVO costElementVO = new QuoteCostElementVO();			
		costElementVO.setElementGroupName(elementGroupName);
		costElementVO.setElementGroupOrder(groupOrder);			
		costElementVO.setElementOrderInGroup(orderInGroup);			
		costElementVO.setElementName(capitalElement.getDescription());			
		costElementVO.setElementcode(capitalElement.getCode());
		costElementVO.setOnInvoice(capitalElement.getOnInvoice());
		costElementVO.setLfMarginOnly(capitalElement.getLfMarginOnly());
		costElementVO.setElementId(capitalElement.getCelId());
		costElementVO.setElementcode(capitalElement.getCode());
		costElementVO.setCapitalElements(true);
		costElementVO.setElementType(capitalElement.getCapElementType());
		costElementVO.setQuoteConcealed(capitalElement.getQuoteConcealed());
		costElementVO.setQuoteCapital(capitalElement.getQuoteCapital());
		costElementVO.setRechargable(capitalElement.getRechargeable());
		
		return costElementVO;
	}

	public QuoteCostElementVO createModelAccessoryVO(int groupOrder, int orderInGroup, QuotationModelAccessory modelAccessory){
		QuoteCostElementVO newCostElementVO = new QuoteCostElementVO(); 
		newCostElementVO.setElementGroupOrder(groupOrder);			
		newCostElementVO.setElementOrderInGroup(orderInGroup);
		newCostElementVO.setModelAccessories(true);
		newCostElementVO.setElementName(modelAccessory.getOptionalAccessory().getAccessoryCode().getDescription());
		newCostElementVO.setElementId(modelAccessory.getOptionalAccessory().getOacId());
		return newCostElementVO;
	}

	public QuoteCostElementVO createDealerAccessoryVO(int groupOrder, int orderInGroup, QuotationDealerAccessory dealerAccessory){
		QuoteCostElementVO newCostElementVO = new QuoteCostElementVO(); 
		newCostElementVO.setElementGroupOrder(groupOrder);
		newCostElementVO.setElementOrderInGroup(orderInGroup);
		newCostElementVO.setDealerAccessories(true);
		newCostElementVO.setElementName(dealerAccessory.getDealerAccessory().getDealerAccessoryCode().getDescription());
		newCostElementVO.setElementId(dealerAccessory.getDealerAccessory().getDacId());							
		return newCostElementVO;
	}
	
	public QuoteCostElementVO createNonCapitalElementVO(NonCapitalElementEnum nonCapitalElement){
		QuoteCostElementVO costElementVO = new QuoteCostElementVO();
		costElementVO.setElementGroupOrder(nonCapitalElement.getGroupOrder());
		costElementVO.setElementOrderInGroup(nonCapitalElement.getElementOrderInGroup());
		costElementVO.setElementName(nonCapitalElement.getElementName());
		return costElementVO;
	}
	
	private Boolean capitalElementApplies(List<Long> baseCapitalElements, Long celId) {
		for(Long element : baseCapitalElements) {
			if(element.equals(celId)) {
				return true;
			}
		}
		return false;
	}
	


}
