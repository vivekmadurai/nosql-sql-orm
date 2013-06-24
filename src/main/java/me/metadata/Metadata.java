package me.metadata;

import java.util.List;

/**
 * @author VivekMadurai
 *
 */
public interface Metadata {
	
	public String getId();
	
	public String getName();
	
	public String getMetadataId();
	
	public String getMetadataName();
	
	public Object getValue(String attributeName);
	
	public Metadata get(String refAttributeName);
	
	public Model getModel(String refAttributeName);
	
	public List<Metadata> getList(String refAttributeName, String childModelName);
	
	public Metadata getMetadata(String id);
	
	public Metadata getMetadata(String id, String modelId);
	
}
