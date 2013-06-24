package me.model.dao;

import me.metadata.Attribute;
import me.metadata.Model;

/**
 * @author VivekMadurai
 *
 */
public class GAEInstance implements Instance {
	private com.google.appengine.api.datastore.Entity dbInstance;
	private Model model;
	
	public GAEInstance(com.google.appengine.api.datastore.Entity dbInstance, Model model){
		this.dbInstance = dbInstance;
		this.model = model;
	}

	@Override
	public String getId() {
		return getDBInstance().getKey().getName();
	}
	
	@Override
	public String getName() {
		return (String) getValue("Name");
	}
	
	@Override
	public String getTenantId() {
		return (String) getValue("Tenant");
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
		return getDBInstance().getProperty(attr.getId());
	}

	@Override
	public void set(Attribute attr, Object value) {
		getDBInstance().setProperty(attr.getId(), value);
	}
	
	@Override
	public Object getValue(String attrName) {
		return get(getAttribute(attrName));
	}

	/**
	 * @return the actual data access object
	 */
	@Override
	public com.google.appengine.api.datastore.Entity getDBInstance() {
		return dbInstance;
	}
	
	/**
	 * 
	 * @return the model meta data
	 */
	@Override
	public Model getModel() {
		return this.model;
	}
	
	private Attribute getAttribute(String attrName){
		return getModel().getAttributeByName(attrName);
	}

}
