package me.model.session;

import java.util.List;
import java.util.Map;

import me.metadata.Attribute;
import me.metadata.Model;
import me.metadata.Project;
import me.model.dao.Instance;
import me.security.auth.User;

/**
 * @author VivekMadurai
 *
 */
public interface Session {
	
	public Project getProject();
	
	public User getCurrentUser();
	
	public void setCurrentUser(User userDetail);	
	
	public Instance create(Model model);
	
	public Instance create(Model model, String uniqueId);
	
	public void update(Instance instance);
	
	public void delete(Instance instance);
	
	public Instance read(String instanceId, Model model);
	
	public List<Instance> readList(Instance instance, Attribute attr);
	
	public List<Instance> query(Criteria criteria);
	
	public void commit();
	
	public void close();
	
}
