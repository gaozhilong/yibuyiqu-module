/**
 * 
 */
package org.jianyi.yibuyiqu.servers.log;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

/**
 * @author zhilong_Gao
 *
 */
public class LogServer extends Verticle {
	
	private MongoClient mongoClient = null;
	private DBCollection coll = null;

	public void start() {
		
		try {
			mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("yi");
			coll = db.getCollection("logs");
			
			vertx.eventBus().registerHandler("server.log",
					new Handler<Message<JsonObject>>() {
						@Override
						public void handle(Message<JsonObject> message) {
							saveLog(message);
							
						}
					});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void saveLog(Message<JsonObject> message) {
		JsonObject log = message.body();
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		log.putString("ts", timeStamp);
		BasicDBObject doc = new BasicDBObject(log.toMap());
		coll.insert(doc);
	}

}
