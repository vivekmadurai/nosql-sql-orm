package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface FlowNode extends Metadata {
	
	public ProcessDefinition getProcessDefinition();
	
	public FlowNode getNextNode();
	
	public List<FlowNode> getPreviousNodeList();
	
	public List<ProcessDefinition> getProcessDefinitionList();
	
	public String getType();
	
	public String getNodeType();
	
	public String getRoleName();
	
	public Formula getFormula();
}
