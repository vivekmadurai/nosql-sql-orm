package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface Attribute extends Metadata {
	public Model getModel();
	
	public Model getReferredModel();
	
	public boolean isReferenceAttribute();
	
	public Formula getFormula();
	
	public List<Attribute> getDependentAttributeList(); 
	
	public boolean isSystem();
	
	public boolean isCascade();
	
	public boolean isString();
	
	public String getDataType();
	
	public String getFormat();
}
