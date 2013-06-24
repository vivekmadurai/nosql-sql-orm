package me.model.session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import me.metadata.Attribute;
import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.model.session.Criteria.Condition;

/**
 * @author VivekMadurai
 *
 */
public abstract class TransactionSession extends AbstractSession {
	private Map<String, Map<String, Instance>> updatedModel;
	private Map<String, Map<String, Instance>> deletedModel;
	
	public abstract Map<String, Instance> queryInstanceAsMap(Criteria criteria);
	
	/**
	 * Initializing the dirty graph in session
	 */
	public TransactionSession(Project project) {
		super(project);
		updatedModel = new HashMap<String, Map<String, Instance>>();
		deletedModel = new HashMap<String, Map<String, Instance>>();
	}
	
	/**
	 * overwritten to add entries into dirty graph
	 */
	@Override
	public Instance create(Model model, String uniqueId) {
		logger.debug("Creating new instance for the model ".concat(model.getName()).concat(" with the id ").concat(uniqueId));
		Instance instance = createInstance(model, uniqueId);
		setUpdatedModel(instance);
		return instance;
	}
	
	/**
	 * Reads the instance from dirty graph and then tries in data store
	 */
	@Override
	public Instance read(String instanceId, Model model) {
		logger.debug("Fetching instance ".concat(instanceId).concat( "for the model ").concat(model.getName()));
		//fetching from updated graph
		Map<String, Instance> updatedInstanceMap = getUpdatedModel().get(model.getId());
		if (updatedInstanceMap != null && updatedInstanceMap.get(instanceId) != null) {
			return updatedInstanceMap.get(instanceId);
		}
		
		//checking in deleted graph,If yes then returns null
		Map<String, Instance> deleteInstanceMap = getDeletedModel().get(model.getId());
		if (deleteInstanceMap != null && deleteInstanceMap.get(instanceId) != null) {
			return null;
		}
		
		return readInstance(instanceId, model);
	}
	
	/**
	 * Updates the instance in dirty graph
	 */
	@Override
	public void updateInstance(Instance instance) {
		setUpdatedModel(instance);
	}
	
	/**
	 * Update the instance in delete graph, and if it part of update graph then remove it
	 */
	@Override
	public void deleteInstance(Instance instance) {
		Model model = instance.getModel();
		String instanceId = instance.getId();
		setDeletedModel(instance);
		Map<String, Instance> updatedMap = getUpdatedModel().get(model.getId());
		if ( updatedMap != null && updatedMap.get(instanceId) != null) {
			updatedMap.remove(instanceId);
		}
	}
	
	@Override
	public List<Instance> queryInstance(Criteria criteria) {
		return null;
	}
	
	/**
	 * queries the result from specific implementation and merges with the dirty graph
	 */
	@Override
	public List<Instance> query(Criteria criteria) {
		Model model = criteria.getModel();
		Map<String, Instance> instanceMap =  queryInstanceAsMap(criteria);
		Map<String, Instance> updatedMap = getUpdatedModel().get(model.getId());
		Map<String, Instance> deletedMap = getDeletedModel().get(model.getId());
		
		//construct query string only if updated and deleted cache has the value.
		String queryString = "";
		if (updatedMap != null && updatedMap.size() > 0 || deletedMap != null && deletedMap.size() > 0) {
			queryString = constructQueryString(model, criteria);
		}
		
		if (updatedMap != null && updatedMap.size() > 0) {
			
			Map<String, Instance> filteredMap = parseMap(updatedMap, queryString); 
			//If the updated instance is present in the queried map then remove the entry from queried instance map.
			for (String key : filteredMap.keySet()) {
				instanceMap.remove(key);
			}
			
			for (Map.Entry<String, Instance> entry: filteredMap.entrySet()) {
				   instanceMap.put(entry.getKey(), entry.getValue());
			}
		}
		
		if (deletedMap != null && deletedMap.size() > 0) {
			Map<String, Instance> filteredMap = parseMap(deletedMap, queryString); 
			//If the deleted instance is present in the queried map then remove the entry from queried instance map.
			for (String key : filteredMap.keySet()) {
				instanceMap.remove(key);
			}
		}
		
		List<Instance> instanceList = new ArrayList<Instance>(instanceMap.values());
		return instanceList;
	}
	
	/**
	 * parses the dirty graph using spring expression builder
	 * @param cachedMap
	 * @param queryString
	 * @return
	 */
	protected Map<String, Instance> parseMap(Map<String, Instance> cachedMap, String queryString) {
		
		if (queryString.length() > 9) {
			logger.debug("inmemory querystring while parsing the dirty graph is ".concat(queryString));
			Map<String, Instance> filteredMap;
			ExpressionParser PARSER = new SpelExpressionParser();
			
			try {
				filteredMap = (Map<String, Instance>) PARSER.parseExpression(queryString).getValue(cachedMap);
			} catch(SpelEvaluationException e) {
				throw e;
			}
			return filteredMap;
		}
		return cachedMap;
		
	}
	
	/**
	 * Its only implemented for flat AND condition alone.
	 * @param criteria
	 * @return
	 */
	protected String constructQueryString(Model model, Criteria criteria) {
		StringBuilder queryString = new StringBuilder();
		queryString.append("#root.?[");
		int count = criteria.getConditionList().size();
		for (Condition cond: criteria.getConditionList()) {
			
			if (count != criteria.getConditionList().size())
				queryString.append(" && ");
			
			String lhsName = cond.getLhsName();
			Attribute attr = model.getAttributeById(lhsName);
			String operator = cond.getOperator().toString();
			Object rhsValue = cond.getRhsValue();
			
			//TODO need to handle LIKE operator 
			//handling IN operator as or conditions
			if (operator.equals("IN")) {
				queryString.append("(");
				int listCount = 0;
				for (Object val : (List)rhsValue) {
					if (listCount > 0)
						queryString.append("||");
					queryString.append(getLhsValue(attr.getName()));
					queryString.append("==");
					queryString.append(getRhsValue(val));
					
					listCount++;
				}
				//adding dummy condition to satisfy the empty list.
				if(listCount == 0)
					queryString.append("1 == 1");
				queryString.append(")");
			} else {
				//handling = to == for expresion builder
				if (operator.equals("=")) {
					operator = "==";
				}
				queryString.append(getLhsValue(attr.getName()));
				queryString.append(operator);
				queryString.append(getRhsValue(rhsValue));
			}
			
			count--;
		}
		queryString.append("]");
		return queryString.toString();
	}
	
	private String getLhsValue(String lhsName) {
		return "value.getValue('".concat(lhsName).concat("')");
	}
	private Object getRhsValue(Object rhsValue) {
		if (rhsValue instanceof String) {
			return "'"+rhsValue+"'";
		}
		return rhsValue;
	}
	
	/**
	 * Calls the corresponding session implementation to commit all the model present in dirty graph
	 * Clears the dirty graph 
	 */
	public void commit() {
		commitInstance();
		
		updatedModel.clear();
		deletedModel.clear();
	}
	
	@Override
	public void closeSession() { }
	
	public Map<String, Map<String, Instance>> getUpdatedModel() {
		return updatedModel;
	}
	
	/**
	 * updating the update dirty graph
	 * @param model
	 * @param dataId
	 * @param data
	 */
	public void setUpdatedModel(Instance instance) {
		Model model = instance.getModel();
		String instanceId = instance.getId();
		
		Map<String, Instance> updatedMap = updatedModel.get(model.getId());
		if(updatedMap == null) {
			updatedMap = new HashMap<String, Instance>();
			updatedModel.put(model.getId(), updatedMap);
		}
		updatedMap.put(instanceId, instance);
	}
	
	public Map<String, Map<String, Instance>> getDeletedModel(){
		return deletedModel;
	}
	
	/**
	 * updating the delete dirty graph
	 * @param model
	 * @param dataId
	 * @param data
	 */
	public void setDeletedModel(Instance instance) {
		Model model = instance.getModel();
		String instanceId = instance.getId();
		
		Map<String, Instance> deletedMap = deletedModel.get(model.getId());
		if(deletedMap == null) {
			deletedMap = new HashMap<String, Instance>();
			deletedModel.put(model.getId(), deletedMap);
		}
		deletedMap.put(instanceId, instance);
	}

}
