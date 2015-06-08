package org.jianyi.yibuyiqu.db;

import org.vertx.java.core.json.JsonObject;

public interface DataAccessManager {
	
	public boolean saveObject(String collection, JsonObject jsonObject);
	
	public boolean updateObject(String collection, JsonObject jsonObject, String key);
	
	public boolean deleteObject(String collection, JsonObject jsonObject, String key);
	
	public JsonObject queryObject(String collection, JsonObject jsonObject, String key);

}
