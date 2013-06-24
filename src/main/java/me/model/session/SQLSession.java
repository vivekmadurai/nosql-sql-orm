package me.model.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.model.dao.MapInstance;
import me.model.session.Criteria.Condition;
import me.util.Constant;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * @author VivekMadurai
 *
 */
public class SQLSession extends AbstractSession {
	private Session hibernateSession;
	
	public SQLSession(Project project, Session hibernateSession) {
		super(project);
		//http://www.coderanch.com/t/218534/ORM/databases/Difference-commit-flush-Hibernate
		//http://www.tutorialspoint.com/hibernate/hibernate_mapping_types.htm
		this.hibernateSession = hibernateSession;
		hibernateSession.getTransaction().begin();
	}
	
	@Override
	public Instance createInstance(Model model, String uniqueId) {
		Map<String, Object> values = new HashMap<String, Object>();
		//update the base values into the entity
		values.put(model.getAttributeByName("Id").getId(), uniqueId);
		values.put(model.getAttributeByName("Tenant").getId(), getCurrentUser().getTenantId());
		try {
			hibernateSession.save(model.getName(), values);
		} catch(RuntimeException e) {
			throw e;
		}
		Instance instance = new MapInstance(values, model);
		return instance;
	}

	@Override
	public Instance readInstance(String instanceId, Model model) {
		Map<String, Object> dbMap = (Map<String, Object>) hibernateSession.get(model.getName(), instanceId);
		if (dbMap == null) {
			logger.info(String.format("Instance not found for the given id %s for the model %s", instanceId, model.getName()));
			return null;
		}
		//TODO need to change the below code when we move to multiple schema based tenency
		Instance instance = new MapInstance(dbMap, model);
		//TODO temp code to allow for anonymous user, since while login the read will be from anonymous user
		if(!getCurrentUser().getTenantId().equals(Constant.ANONYMOUS) && !instance.getTenantId().equals(getCurrentUser().getTenantId()))
			throw new RuntimeException(String.format("The instance %s of the model %s does not belong to the tenant %s", instanceId, model.getName(), getCurrentUser().getTenantId()));
		
		return instance;
	}

	@Override
	public void updateInstance(Instance instance) {
		hibernateSession.update(instance.getModel().getName(), instance.getDBInstance());
		
	}

	@Override
	public void deleteInstance(Instance instance) {
		hibernateSession.delete(instance.getModel().getName(), instance.getDBInstance());
		
	}

	@Override
	public List<Instance> queryInstance(Criteria criteria) {
		Model model = criteria.getModel();
		org.hibernate.Criteria filter = hibernateSession.createCriteria(model.getName());
		List<Instance> instanceList = new ArrayList<Instance>();
		logger.debug("select * from "+model.getName());
		//adding filters to the data store query
		addFilter(filter, criteria);
		
		//adding limit and offset
		int limit = criteria.getLimit();
		int offset = criteria.getOffset();
		
		filter.setMaxResults(limit);
		filter.setFirstResult(offset);
		logger.debug(" limit = "+limit);
		logger.debug(" offset = "+offset);
		
		//adding orderby
		for(Map.Entry<String, Boolean> entry: criteria.getOrder().entrySet()) {
			String orderAttrName = entry.getKey();
			filter.addOrder(entry.getValue()? Order.desc(orderAttrName): Order.asc(orderAttrName));
		}
		
		List<Map<String, Object>> result = filter.list();
		for(Map<String, Object> instanceMap: result) {
			instanceList.add(new MapInstance(instanceMap, model));
		}
		return instanceList;
		
	}
	
	private void addFilter(org.hibernate.Criteria filter, Criteria criteria) {
		for (Condition cond: criteria.getConditionList()) {
			logger.debug(cond.getLhsName() +"  "+cond.getOperator()+"   "+ cond.getRhsValue());
			switch (cond.getOperator()) {
				case EQUAL:
					filter.add(Restrictions.eq(cond.getLhsName(), cond.getRhsValue()));
					break;
				case NOT_EQUAL:
					filter.add(Restrictions.ne(cond.getLhsName(), cond.getRhsValue()));
					break;
				case LESS_THAN:
					filter.add(Restrictions.lt(cond.getLhsName(), cond.getRhsValue()));
					break;
				case GREATER_THAN:
					filter.add(Restrictions.gt(cond.getLhsName(), cond.getRhsValue()));
					break;
				case LESS_THAN_OR_EQUAL:
					filter.add(Restrictions.le(cond.getLhsName(), cond.getRhsValue()));
					break;
				case GREATER_THAN_OR_EQUAL:
					filter.add(Restrictions.ge(cond.getLhsName(), cond.getRhsValue()));
					break;
				case LIKE:
					filter.add(Restrictions.like(cond.getLhsName(), cond.getRhsValue()));
					break;
				case IN:
					Collection rhsVal = (Collection)cond.getRhsValue();
					if (rhsVal.size() > 0)
						filter.add(Restrictions.in(cond.getLhsName(), rhsVal));
					else
						logger.warn("The size of RHS value for IN filter is 0, Ignoring it LHS attribute "+cond.getLhsName()+" from the query criteria");
					break;
				default:
					break;
			}
		}
	}

	@Override
	public void commitInstance() {
		try {
			hibernateSession.flush();
			//hibernateSession.clear();
			hibernateSession.getTransaction().commit();
		} catch(RuntimeException e) {
			hibernateSession.getTransaction().rollback();
			throw e;
		}
		
	}

	@Override
	public void closeSession() {
		hibernateSession.close();
	}
	
}
