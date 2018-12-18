package com.mikealbert.vision.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.mikealbert.data.enumeration.LogBookTypeEnum;

/**
 * Configuration that defines how a log book is to be displayed.
 * 
 * See also: <br />
 * <pre>
 * {@link LogBookbean} <br />
 * {@link LogBookTypeEnum} <br />
 * </pre>
 * 
 * @author sibley
 *
 */
public class LogBookTypeVO implements Serializable {
	private static final long serialVersionUID = -8653405301696778711L;
	
	private LogBookTypeEnum logBookType;
	private List<LogBookTypeEnum> relatedLogBookTypes;
	private Object entity;
	private String label;
	private boolean combineLogBookEntries;
	private boolean readOnly;
	private boolean renderZeroEntries;
	private boolean renderEntrySource;
        
	/**
	 * Constructor for most use cases
	 * @param logBookType The log book type
	 * @param readOnly Whether the log book should render in edit or read only mode
	 */
    public LogBookTypeVO(LogBookTypeEnum logBookType, boolean readOnly){
    	setLogBookType(logBookType);
    	setRelatedLogBookTypes(new ArrayList<LogBookTypeEnum>());
    	setCombineLogBookEntries(false);
    	setReadOnly(readOnly); 
    	setRenderZeroEntries(true);
    	setRenderEntrySource(false);
    }
    
    /**
     * Constructor for use case where the entity for this particular log book type will
     * be different than the entity passed to the component. For example, when passing
     * a maintenance request to a component, it will display the specified log books
     * attached to the entity. However, a type can be defined to display a log book that
     * is attached to the maintenance request's unit. In this case, the log book type entity
     * would be assigned the fleet master.
     * @param logBookType The log book type
     * @param entity The entity the log book is attached to
     * @param readOnly Whether the log book should render in edit or read only mode
     */
    public LogBookTypeVO(LogBookTypeEnum logBookType, Object entity, boolean readOnly){
    	setLogBookType(logBookType);
    	setEntity(entity);
    	setRelatedLogBookTypes(new ArrayList<LogBookTypeEnum>());
    	setCombineLogBookEntries(false);
    	setReadOnly(readOnly); 
    	setRenderZeroEntries(true);   
    	setRenderEntrySource(false);    	
    }    
    
    /**
     * Constructor for a use case where multiple log books will be mashed up into one. 
     * The entries of the related log book types will be fused into the entries of the
     * parent log book type. However, when in edit mode, new entries will be added to 
     * the parent log book.
     * @param logBookType The log book type
     * @param relatedLogBookType Related log books whom entries will be fused with the parent log book 
     * @param label Overrides the label of the parent log book
     * @param readOnly Whether the log book should render in edit or read only mode
     */
    public LogBookTypeVO(LogBookTypeEnum logBookType, LogBookTypeEnum relatedLogBookType, String label, boolean readOnly){
    	setRelatedLogBookTypes(new ArrayList<LogBookTypeEnum>());
    	setLogBookType(logBookType);
    	getRelatedLogBookTypes().add(relatedLogBookType);
    	setCombineLogBookEntries(true);
    	setLabel(label);
    	setReadOnly(readOnly);
    	setRenderZeroEntries(true);   
    	setRenderEntrySource(false);    	
    }

    public List<LogBookTypeEnum> getCombinedTypes(){
    	List<LogBookTypeEnum> combinedTypes = new ArrayList<LogBookTypeEnum>();
    	combinedTypes.add(getLogBookType());
    	combinedTypes.addAll(getRelatedLogBookTypes());
    	return combinedTypes;
    }
    
	public LogBookTypeEnum getLogBookType() {
		return logBookType;
	}

	public void setLogBookType(LogBookTypeEnum logBookType) {
		this.logBookType = logBookType;
	}

	public List<LogBookTypeEnum> getRelatedLogBookTypes() {
		return relatedLogBookTypes;
	}

	public void setRelatedLogBookTypes(List<LogBookTypeEnum> relatedLogBookTypes) {
		this.relatedLogBookTypes = relatedLogBookTypes;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isCombineLogBookEntries() {
		return combineLogBookEntries;
	}

	public void setCombineLogBookEntries(boolean combineLogBookEntries) {
		this.combineLogBookEntries = combineLogBookEntries;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public boolean isRenderZeroEntries() {
		return renderZeroEntries;
	}

	public void setRenderZeroEntries(boolean renderZeroEntries) {
		this.renderZeroEntries = renderZeroEntries;
	}

	public boolean isRenderEntrySource() {
		return renderEntrySource;
	}

	public void setRenderEntrySource(boolean renderEntrySource) {
		this.renderEntrySource = renderEntrySource;
	}
    
}