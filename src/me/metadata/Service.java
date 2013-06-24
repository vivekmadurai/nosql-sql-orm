package me.metadata;

import java.util.List;
import java.util.Map;

/**
 * @author VivekMadurai
 */
public interface Service extends Metadata {

    public Project getProject();

    public Model getRootModel();

    public List<Model> getModelList();

    public boolean isSystem();

    public boolean isWorkflow();
    
	public Model getModelByName(String modelName);
}
