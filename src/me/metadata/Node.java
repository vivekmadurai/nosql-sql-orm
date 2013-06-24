package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface Node extends Metadata {
	
	public Formula getFormula();
	
	public Attribute getAttribute();
	
	public List<Node> getNodeList();
	
	public Node getNode();
	
	public String getType();
	
	public String getStringValue();
	
	public Double getDoubleValue();
}

