/**
 * 
 */
package org.jianyi.yibuyiqu.session;

import java.net.UnknownHostException;
import java.util.Set;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

/**
 * @author zhilong_Gao
 *
 */
public class MongodbSessionServer extends Verticle {

	private MongoClient mongoClient = null;
	private DBCollection coll = null;

	public void start() {
		try {
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("yi");
			coll = db.getCollection("sessions");

			vertx.eventBus().registerHandler("server.session.create",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							if (create(message)) {
								message.reply(true);
							} else {
								message.reply(false);
							}
						}
					});
			vertx.eventBus().registerHandler("server.session.get",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							JsonObject val = get(message);
							if (val != null) {
								message.reply(val);
							} else {
								JsonObject msg = new JsonObject();
								msg.putString("sessionID", "null");
								message.reply(msg);
							}
						}
					});
			vertx.eventBus().registerHandler("server.session.clear",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							if (clear(message)) {
								message.reply(true);
							} else {
								message.reply(false);
							}
						}
					});
			vertx.eventBus().registerHandler("server.session.destroy",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							if (destroy(message)) {
								message.reply(true);
							} else {
								message.reply(false);
							}
						}
					});
			vertx.eventBus().registerHandler("server.session.getbyusername",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							JsonObject val = getByUserName(message);
							if (val != null) {
								message.reply(val);
							} else {
								JsonObject msg = new JsonObject();
								msg.putString("sessionID", "null");
								message.reply(msg);
							}
						}
					});

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void stop() {
		mongoClient.close();
	}

	private boolean create(Message<JsonObject> message) {
		boolean sucess = false;
		BasicDBObject doc = new BasicDBObject(message.body().toMap());
		WriteResult result = coll.insert(doc);
		if (result.getN() == 0) {
			Set<String> set = vertx.sharedData().getSet("users");
			set.add(message.body().getString("sessionID"));
			sucess = true;
		}
		return sucess;
	}

	private JsonObject get(Message<JsonObject> message) {
		BasicDBObject query = new BasicDBObject("sessionID", message.body()
				.getString("sessionID"));
		DBCursor cursor = coll.find(query);
		JsonObject sessionVal = null;
		try {
			while (cursor.hasNext()) {
				sessionVal = new JsonObject(cursor.next().get("sessionVal").toString());
			}
		} finally {
			cursor.close();
		}
		return sessionVal;
	}

	private boolean clear(Message<JsonObject> message) {
		boolean sucess = false;
		BasicDBObject query = new BasicDBObject("sessionID", message.body()
				.getString("sessionID"));
		WriteResult result = coll.remove(query);
		result = coll.remove(query);
		if (result.getN() == 0) {
			Set<String> set = vertx.sharedData().getSet("users");
			set.remove(message.body().getString("sessionID"));
			sucess = true;
		}
		return sucess;
	}

	private boolean destroy(Message<JsonObject> message) {
		boolean sucess = false;
		BasicDBObject query = new BasicDBObject("sessionID", message.body()
				.getString("sessionID"));
		WriteResult result = coll.remove(query);
		if (result.getN() == 1) {
			Set<String> set = vertx.sharedData().getSet("users");
			set.remove(message.body().getString("sessionID"));
			sucess = true;
		}
		return sucess;
	}

	private JsonObject getByUserName(Message<JsonObject> message) {
		BasicDBObject query = new BasicDBObject("username", message.body()
				.getString("username"));
		DBCursor cursor = coll.find(query);
		JsonObject sessionVal = null;
		try {
			while (cursor.hasNext()) {
				sessionVal = new JsonObject(cursor.next().get("sessionVal").toString());
			}
		} finally {
			cursor.close();
		}
		return sessionVal;
	}

}
