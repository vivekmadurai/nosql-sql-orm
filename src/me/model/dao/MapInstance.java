package me.model.dao;

import java.util.Map;

import me.metadata.Attribute;
import me.metadata.Model;

/**
 * @author VivekMadurai
 *
 */
public class MapInstance implements Instance {
	private Map<String, Object> dbInstance;
	private Model model;
	
	public MapInstance(Map<String, Object> dbInstance, Model model){
		this.dbInstance = dbInstance;
		this.model = model;
	}

	@Override
	public String getId() {
		return (String) getValue("Id");
	}

	@Override
	public String getName() {
		return (String) getValue("Name");
	}

	@Override
	public String getTenantId() {
		return (String) getValue("Tenant");
	}

	/**
	 * 
	 * @return the instance meta data
	 */
	@Override
	public Model getModel() {
		return this.model;
	}

	@Override
	public String getMetadataId() {
		return getModel().getId();
	}

	@Override
	public String getMetadataName() {
		return getModel().getName();
	}

	@Override
	public Object get(Attribute attr) {
		return getDBInstance().get(attr.getId());
	}

	@Override
	public void set(Attribute attr, Object value) {
		getDBInstance().put(attr.getId(), value);
		
	}

	@Override
	public Object getValue(String attrName) {
		return get(getAttribute(attrName));
	}

	/**
	 * @return the actual data access object
	 */
	@Override
	public Map<String, Object> getDBInstance() {
		return dbInstance;
	}
	
	private Attribute getAttribute(String attrName){
		return getModel().getAttributeByName(attrName);
	}

}
