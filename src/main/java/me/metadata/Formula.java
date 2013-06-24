package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface Formula extends Metadata {
	
	public Attribute getAttribute();
	
	public Node getNode();
	
	public FlowNode getFlowNode();
}

