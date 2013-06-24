package me.model.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import me.metadata.Attribute;
import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.model.dao.GAEInstance;
import me.model.session.Criteria.Condition;
import me.model.session.Criteria.Operator;
import me.util.Constant;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


/**
 * @author VivekMadurai
 *
 */
public class GAESession extends TransactionSession{
	
	private DatastoreService datastore;
	private AsyncDatastoreService asynDataStore;
	
	private final static Map<Operator, FilterOperator> OPERATOR_MAP = new HashMap<Operator, FilterOperator>() {{
        put(Operator.EQUAL, FilterOperator.EQUAL);
        put(Operator.NOT_EQUAL, FilterOperator.NOT_EQUAL);
        put(Operator.LESS_THAN, FilterOperator.LESS_THAN);
        put(Operator.GREATER_THAN, FilterOperator.GREATER_THAN);
        put(Operator.LESS_THAN_OR_EQUAL, FilterOperator.LESS_THAN_OR_EQUAL);
        put(Operator.GREATER_THAN_OR_EQUAL, FilterOperator.GREATER_THAN_OR_EQUAL);
        put(Operator.IN, FilterOperator.IN);
    }}; 
	
	public GAESession(Project project) {
		super(project);
		datastore = DatastoreServiceFactory.getDatastoreService();
		asynDataStore = DatastoreServiceFactory.getAsyncDatastoreService();
	}

	/**
	 * Creates a object for the given model using GAE low level api's
	 */
	@Override
	public Instance createInstance(Model model, String uniqueId) {
		com.google.appengine.api.datastore.Entity dbInstance = new com.google.appengine.api.datastore.Entity(model.getId(), uniqueId);
		Instance instance = new GAEInstance(dbInstance, model);
		//update the base values into the entity
		instance.set(model.getAttributeByName("Id"), uniqueId);
		instance.set(model.getAttributeByName("Tenant"), getCurrentUser().getTenantId());
		update(instance);
		return instance;
	}
	
	/**
	 * checks in the  dirty graph else queries from the data store
	 */
	@Override
	public Instance readInstance(String instanceId, Model model) {
		
		Instance instance = null;
		Key dataKey = KeyFactory.createKey(model.getId(), instanceId);
		try {
			com.google.appengine.api.datastore.Entity dbInstance = datastore.get(dataKey);
			instance = new GAEInstance(dbInstance, model);
		} catch (EntityNotFoundException e) {
			logger.info("Instance not found for the given id ".concat(instanceId).concat(" for the model ").concat(model.getName()));
			return null;
		}
		return instance;
	}
	
	@Override
	public Map<String, Instance> queryInstanceAsMap(Criteria criteria) {
		Model model = criteria.getModel();
		int limit = Constant.DBLIMIT;
		int offset = Constant.DBOFFSET;
		Map<String, Instance> resultMap = new HashMap<String, Instance>();
		Query query = new Query(model.getId());
		//adding filters to the data store query
		addFilter(query, criteria);
		limit = criteria.getLimit();
		offset = criteria.getOffset();
		
		//to retrieve db entities from data store
		PreparedQuery preparedQuery = datastore.prepare(query);
		//asList will actually fetch the result from data store
		
		List<com.google.appengine.api.datastore.Entity> dbInstanceList = preparedQuery.asList(FetchOptions.Builder.withOffset(offset).limit(limit));
		for (com.google.appengine.api.datastore.Entity dbInstance : dbInstanceList) {
			Instance instance = new GAEInstance(dbInstance, model);
			resultMap.put(instance.getId(), instance);
		}
		return resultMap;
	}
	
	private void addFilter(Query query, Criteria criteria) {
		for (Condition cond: criteria.getConditionList()) {
			String lhsName = cond.getLhsName();
			Operator operator = cond.getOperator();
			Object rhsValue = cond.getRhsValue();
			query.addFilter(lhsName, OPERATOR_MAP.get(operator), rhsValue);
		}
	}
	
	/**
	 * commit into big table, uses asynchronous commit
	 */
	@Override
	public void commitInstance() {
		List<com.google.appengine.api.datastore.Entity> toBeUpdatedList = new ArrayList<com.google.appengine.api.datastore.Entity>(); 
		List<Key> toBeDeletedList = new ArrayList<Key>(); 
		//TODO need to add logging
		try {
			//commiting updated entries
			for (Map.Entry<String, Map<String, Instance>> updatedEntry : getUpdatedModel().entrySet()) {
				for (Map.Entry<String, Instance> entry : updatedEntry.getValue().entrySet()) {
					toBeUpdatedList.add((com.google.appengine.api.datastore.Entity) entry.getValue().getDBInstance());
				}
			}
			
			if (toBeUpdatedList.size() > 0) {
				logger.debug("Commiting ".concat(new StringBuilder(toBeUpdatedList.size()).toString()).concat(" instance into data store"));
				asynDataStore.put(toBeUpdatedList);
			}
			
			//commiting deleted entries
			for (Map.Entry<String, Map<String, Instance>> deletedEntry : getDeletedModel().entrySet()) {
				for (Map.Entry<String, Instance> entry : deletedEntry.getValue().entrySet()) {
					com.google.appengine.api.datastore.Entity dbInstance = (com.google.appengine.api.datastore.Entity) entry.getValue().getDBInstance();
					Key instanceKey = dbInstance.getKey();
					toBeDeletedList.add(instanceKey);
				}
			}
			
			if (toBeDeletedList.size() > 0) {
				logger.debug("Deleting ".concat(new StringBuilder(toBeDeletedList.size()).toString()).concat( "instance from data store"));
				asynDataStore.delete(toBeDeletedList);
			}
		}catch(RuntimeException e) {
			throw e;
		}
	}
	
}
