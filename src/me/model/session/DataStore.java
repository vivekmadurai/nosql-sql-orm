package me.model.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VivekMadurai
 *
 */
public class DataStore {
	private static Map<String, Map<String, Map<String, Object>>> dataStore = new HashMap<String, Map<String,  Map<String, Object>>>();

	public static Map<String, Object> create(String modelId, String instanceId) {
		Map<String,  Map<String, Object>> instanceMap = dataStore.get(modelId);
		if (instanceMap == null) {
			instanceMap = new HashMap<String,  Map<String, Object>>();
			dataStore.put(modelId, instanceMap);
		}
		Map<String, Object> instance = new HashMap<String, Object>();
		instanceMap.put(instanceId, instance);
		instance.put("Id", instanceId);
		instance.put("ModelId", modelId);
		return instance;
	}

	public static Map<String, Object> read(String instanceId, String modelId) {
		Map<String,  Map<String, Object>> instanceMap = dataStore.get(modelId);

		return instanceMap != null ? instanceMap.get(instanceId) : null;
	}

	public static void put(List<Map<String, Object>> instanceList) {
		for (Map<String, Object> instance: instanceList) {
			Map<String,  Map<String, Object>> instanceMap = dataStore.get(instance.get("ModelId"));
			instanceMap.put((String) instance.get("Id"), instance);
		}
	}

	public static void delete(List<Map<String, Object>> instanceList) {
		for (Map<String, Object> instance: instanceList) {
			Map<String,  Map<String, Object>> instanceMap = dataStore.get(instance.get("ModelId"));
			instanceMap.remove(instance.get("Id"));
		}
	}

	public static Map<String, Map<String, Map<String, Object>>> getDataStore() {
		return dataStore;
	}

	public static void clearDataStore() {
		dataStore = new HashMap<String, Map<String,  Map<String, Object>>>();
	}

}
