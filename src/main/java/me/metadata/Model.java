package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface Model extends Metadata {
	
	public Project getProject();
	
	public Service getService();
	
	public boolean isSystem();
	
	public boolean isWorkflow();
	
	public List<Attribute> getAttributeList();
	
	public List<Attribute> getOneToManyList();
	
	public List<Attribute> getManytoOneList();
	
	public Attribute getAttributeByName(String name);
	
	public Attribute getAttributeById(String id);
	
	public ProcessDefinition getWorkflowProcess();

	public ProcessDefinition getProcessDefinitionById(String processDefinitionId);
	
	public FlowNode getFlowNodeById(String flowNodeId);

}
