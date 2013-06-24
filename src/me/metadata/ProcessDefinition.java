package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface ProcessDefinition extends Metadata {
	
	public Model getModel();
	
	public FlowNode getFlowNode();

	public FlowNode getStartNode();
	
	public FlowNode getEndNode();
	
	public List<FlowNode> getFlowNodeList();
	
	public String getProcessType();
}

