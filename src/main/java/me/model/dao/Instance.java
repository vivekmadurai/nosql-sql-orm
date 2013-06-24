package me.model.dao;

import me.metadata.Attribute;
import me.metadata.Model;

/**
 * @author VivekMadurai
 *
 */
public interface Instance{

	public String getId();
	
	public String getName();
	
	public String getTenantId();
	
	public Model getModel();
	
	public String getMetadataId();
	
	public String getMetadataName();

	public Object get(Attribute attr);
	
	public void set(Attribute attr, Object value);
	
	public Object getValue(String attrName);
	
	public Object getDBInstance();

}
