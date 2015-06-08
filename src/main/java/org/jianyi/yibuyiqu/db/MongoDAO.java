package org.jianyi.yibuyiqu.db;

import java.net.UnknownHostException;

import org.vertx.java.core.json.JsonObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoDAO implements DataAccessManager {
	
	private MongoClient mongoClient = null;
	private DBCollection coll = null;
	private DB db = null;
	
	public MongoDAO() {
		try {
			mongoClient = new MongoClient("localhost", 27017);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db = mongoClient.getDB("yi");
	}

	@Override
	public boolean saveObject(String collection, JsonObject jsonObject) {
		// TODO Auto-generated method stub
		coll = db.getCollection(collection);
		boolean sucess = false;
		BasicDBObject doc = new BasicDBObject(jsonObject.toMap());
		WriteResult result = coll.insert(doc);
		if (result.getN() == 0) {
			sucess = true;
		}
		return sucess;
	}

	@Override
	public boolean updateObject(String collection, JsonObject jsonObject, String key) {
		// TODO Auto-generated method stub
		coll = db.getCollection(collection);
		BasicDBObject query = new BasicDBObject(key, jsonObject.getString(key));
		BasicDBObject doc = new BasicDBObject(jsonObject.toMap());
		WriteResult result = coll.update(query, doc);
		return result.isUpdateOfExisting();
	}

	@Override
	public boolean deleteObject(String collection, JsonObject jsonObject, String key) {
		// TODO Auto-generated method stub
		coll = db.getCollection(collection);
		boolean sucess = false;
		BasicDBObject query = new BasicDBObject(key, jsonObject.getString(key));
		WriteResult result = coll.remove(query);
		result = coll.remove(query);
		if (result.getN() == 0) {
			sucess = true;
		}
		return sucess;
	}

	@Override
	public JsonObject queryObject(String collection, JsonObject jsonObject, String key) {
		// TODO Auto-generated method stub
		coll = db.getCollection(collection);
		BasicDBObject query = new BasicDBObject(key, jsonObject.getString(key));
		DBCursor cursor = coll.find(query);
		JsonObject result = null;
		try {
			while (cursor.hasNext()) {
				result = new JsonObject(cursor.next().toString());
				result.removeField("_id");
			}
		} finally {
			cursor.close();
		}
		return result;
	}

}
