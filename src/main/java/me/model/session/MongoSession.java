package me.model.session;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.model.dao.MongoInstance;
import me.model.session.Criteria.Condition;
import me.model.session.Criteria.Operator;
import me.util.Constant;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

/**
 * @author VivekMadurai
 *
 */
public class MongoSession extends TransactionSession {
	private MongoClient mongo;
	private DB db;
	
	private final static Map<Operator, String> OPERATOR_MAP = new HashMap<Operator, String>() {{
        put(Operator.NOT_EQUAL, "$ne");
        put(Operator.LESS_THAN, "$lt");
        put(Operator.GREATER_THAN, "$gt");
        put(Operator.LESS_THAN_OR_EQUAL, "$lte");
        put(Operator.GREATER_THAN_OR_EQUAL, "$gte");
        put(Operator.IN, "$in");
    }}; 
	
	public MongoSession(Project project) {
		super(project);
		try {
			/**** Connect to MongoDB ****/
			this.mongo = new MongoClient("localhost", 27017);
			
			// if database doesn't exists, MongoDB will create it for you
			this.db = mongo.getDB("testdb");
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Instance createInstance(Model model, String uniqueId) {
		DBObject dbInstance = new BasicDBObject();
		//Overriding the auto generated Id
		dbInstance.put("_id", uniqueId);
		
		Instance instance = new MongoInstance(dbInstance, model);
		//update the base values into the object
		instance.set(model.getAttributeByName("Id"), uniqueId);
		instance.set(model.getAttributeByName("Tenant"), getCurrentUser().getTenantId());
		update(instance);
		return instance;
	}

	@Override
	public Instance readInstance(String instanceId, Model model) {
		// if collection doesn't exists, MongoDB will create it for you
		DBCollection dbTable = db.getCollection(model.getId());
		DBObject dbInstance = dbTable.findOne(instanceId);
		if (dbInstance != null) {
			Instance instance = new MongoInstance(dbInstance, model);
			return instance;
		}
		return null;
	}
	
	@Override
	public Map<String, Instance> queryInstanceAsMap(Criteria criteria) {
		Model model = criteria.getModel();
		int limit = Constant.DBLIMIT;
		int offset = Constant.DBOFFSET;
		Map<String, Instance> resultMap = new HashMap<String, Instance>();
		
		BasicDBObject searchQuery = new BasicDBObject();
		//adding filters to the data store query
		addFilter(searchQuery, criteria);
		DBCollection dbTable = db.getCollection(model.getId());
		
		DBCursor cursor = dbTable.find(searchQuery).limit(limit).skip(offset);
		while (cursor.hasNext()) {
			DBObject dbInstance = cursor.next();
			Instance instance = new MongoInstance(dbInstance, model);
			resultMap.put(instance.getId(), instance);
		}
		return resultMap;
	}
	
	private void addFilter(BasicDBObject query, Criteria criteria) {
		for (Condition cond: criteria.getConditionList()) {
			String lhsName = cond.getLhsName();
			Operator operator = cond.getOperator();
			Object rhsValue = cond.getRhsValue();
			if(operator.equals(Operator.EQUAL)) {
				query.put(lhsName, rhsValue);
			} else {
				query.put(lhsName, new BasicDBObject(OPERATOR_MAP.get(operator), rhsValue));
			}
		}
	}

	@Override
	public void commitInstance() {
		//commiting updated entries
		//TODO need to use batch update and insert
		for (Map.Entry<String, Map<String, Instance>> updatedEntry : getUpdatedModel().entrySet()) {
			DBCollection dbTable = db.getCollection(updatedEntry.getKey());
			for (Map.Entry<String, Instance> entry : updatedEntry.getValue().entrySet()) {
				DBObject dbInstance = (DBObject) entry.getValue().getDBInstance();
				dbTable.save(dbInstance);
			}
		}
		
		//commiting deleted entries
		//TODO need to use batch remove
		for (Map.Entry<String, Map<String, Instance>> deletedEntry : getDeletedModel().entrySet()) {
			DBCollection dbTable = db.getCollection(deletedEntry.getKey());
			for (Map.Entry<String, Instance> entry : deletedEntry.getValue().entrySet()) {
				DBObject dbInstance = (DBObject) entry.getValue().getDBInstance();
				dbTable.remove(dbInstance);
			}
		}
	}
}
