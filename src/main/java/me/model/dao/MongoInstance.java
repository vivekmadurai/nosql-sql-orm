package me.model.dao;

import me.metadata.Attribute;
import me.metadata.Model;

import com.mongodb.DBObject;

/**
 * @author VivekMadurai
 *
 */
public class MongoInstance implements Instance {
	private DBObject dbInstance;
	private Model model;
	
	public MongoInstance(DBObject dbInstance, Model model) {
		this.dbInstance = dbInstance;
		this.model = model;
	}
	
	@Override
	public String getId() {
		return dbInstance.get("_id").toString();
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

	@Override
	public DBObject getDBInstance() {
		return this.dbInstance;
	}
	
	private Attribute getAttribute(String attrName) {
		return getModel().getAttributeByName(attrName);
	}

}
