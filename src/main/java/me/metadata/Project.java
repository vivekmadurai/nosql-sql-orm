package me.metadata;

import java.util.List;
import java.util.Map;

/**
 * @author VivekMadurai
 *
 */
public interface Project extends Metadata {
	
	public Map<String, Object> getSerializationBoundary();

	public Map<String, Object> getCommandMapping();

	public List<Service> getServiceList();
	
	public Service getServiceById(String serviceId);
	
	public Service getServiceByName(String serviceName);
	
	public Service getUserService();
	
	public Service getWorkflowService();
	
}
