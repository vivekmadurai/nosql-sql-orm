package me.model.session;

import java.util.List;

import me.metadata.Attribute;
import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.model.session.Criteria.Operator;
import me.util.Util;

import org.apache.log4j.Logger;

/**
 * @author VivekMadurai
 *
 */
public abstract class AbstractSession implements Session {
	
	protected static final Logger logger = Logger.getLogger("Session");
	
	private Project project;
	private User userDetail;
	
	public AbstractSession(Project project) {
		this.project = project;
		
	}
	
	@Override
	public Project getProject() {
		return project;
	}
	
	@Override
	public User getCurrentUser() {
		return userDetail;
	}
	
	@Override
	public void setCurrentUser(User userDetails) {
		this.userDetail = userDetails;
	}
	
	public abstract Instance createInstance(Model model, String uniqueId);
	
	public abstract Instance readInstance(String instanceId, Model model);
	
	public abstract void updateInstance(Instance instance);
	
	public abstract void deleteInstance(Instance instance);
	
	public abstract List<Instance> queryInstance(Criteria criteria);
	
	public abstract void commitInstance();
	
	public abstract void closeSession();
	
	/**
	 * Calls the corresponding session implementation to creates a object for the given model and given Id
	 */
	@Override
	public Instance create(Model model, String uniqueId) {
		logger.debug("Creating new instance for the model ".concat(model.getName()).concat(" with the id ").concat(uniqueId));
		Instance instance = createInstance(model, uniqueId);
		return instance;
	}
	
	/**
	 * Calls the overloaded create method 
	 */
	@Override
	public Instance create(Model model) {
		String uniqueId = Util.generateUUID(model.getName());
		return create(model, uniqueId);
	}
	
	/**
	 * Reads the instance directly from data store
	 */
	@Override
	public Instance read(String instanceId, Model model) {
		logger.debug("Fetching instance ".concat(instanceId).concat( "for the model ").concat(model.getName()));
		return readInstance(instanceId, model);
	}
	
	/**
	 * Updates the instance back to data store
	 */
	@Override
	public void update(Instance instance) {
		updateInstance(instance);
	}
	
	/**
	 * delete the instance from to data store
	 */
	@Override
	public void delete(Instance instance) {
		deleteInstance(instance);
	}

	@Override
	public List<Instance> readList(Instance instance, Attribute attr) {
		Model model = attr.getModel();
		Criteria criteria = new Criteria(model, this);
		criteria.addCondition(attr.getId(), Operator.EQUAL, instance.getId());
		List<Instance> instanceList = query(criteria);
		return instanceList;
	}
	
	/**
	 * gets the resultmap from specific implmentation
	 */
	@Override
	public List<Instance> query(Criteria criteria) {
		List<Instance> instanceList =  queryInstance(criteria);
		
		return instanceList;
	}
	
	/**
	 * Calls the corresponding session implementation to commit all the instance
	 */
	@Override
	public void commit() {
		commitInstance();
	}
	
	/**
	 * close the current session
	 */
	@Override
	public void close() {
		closeSession();
	}

}
