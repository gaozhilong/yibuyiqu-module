/**
 * 
 */
package org.jianyi.yibuyiqu.servers.auth;

import java.net.UnknownHostException;
import java.util.Map;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * @author zhilong_Gao
 *
 */
public class MongoAuthServer extends Verticle {
	
	public void start() {
		vertx.eventBus().registerHandler("server.author.login",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						login(message);
					}
				});

		vertx.eventBus().registerHandler("server.author.logout",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						logout(message);
					}
				});

		vertx.eventBus().registerHandler("server.author.authorise",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						authorise(message);
					}
				});
	}

	public void stop() {
		
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> findUser(String username, String password) {
		Map<String, Object> usr = null;
		try {
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			DB db = mongoClient.getDB("yi");
			DBCollection coll = db.getCollection("users");
			DBObject query = BasicDBObjectBuilder.start().add("username", username).add("password", password).get();
			DBCursor cursor = coll.find(query);
	        while(cursor.hasNext()){
	        	usr = cursor.next().toMap();
	        }
	        mongoClient.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    return usr;
	}
	
	protected void login(final Message<JsonObject> message) {
		String sessionID = message.body().getString("sessionID");
		String username = message.body().getString("username");
		String password = message.body().getString("password");
		String proxy = message.body().getString(CommandUtil.CMD_PROXYNAME);
		if (username != null || password != null) {
			Map<String, Object> user = findUser(username, password);
			if (user != null) {
				// 判断是否已经登录
				JsonObject userinfo = new JsonObject();
				userinfo.putString("username", username);
				synchronized (this) {
					vertx.eventBus().send("server.session.getbyusername", userinfo,
							new Handler<Message<JsonObject>>() {
								@Override
								public void handle(Message<JsonObject> reply) {
									// TODO Auto-generated method stub
									if (reply != null && reply.body().getString(
											"sessionID") != null && !reply.body().getString(
											"sessionID").equals("null")) {
										// 清除原有登录信息
										JsonObject session = new JsonObject();
										session.putString("sessionID", reply.body().getString("sessionID"));
										vertx.eventBus().send("server.session.destroy", session,
												new Handler<Message<Boolean>>() {
													@Override
													public void handle(Message<Boolean> reply) {
														// TODO Auto-generated method stub
														if (reply.body().booleanValue()) {
															container.logger().info(
																	"Session信息销毁成功 ");
														} else {
															container.logger().info(
																	"Session信息销毁失败！ ");
														}
													}
												});
									}
								}
							});
				}
				
				JsonObject session = new JsonObject();
				session.putString("sessionID", sessionID);
				session.putString("username", username);
				JsonObject sessionVal = new JsonObject();
				sessionVal.putString("username", username);
				session.putString("sessionVal", sessionVal.toString());
				session.putString("ct", String.valueOf(System.nanoTime()));
				session.putString(CommandUtil.CMD_PROXYNAME, proxy);
				vertx.eventBus().send("server.session.create", session,
						new Handler<Message<Boolean>>() {
							@Override
							public void handle(Message<Boolean> reply) {
								// TODO Auto-generated method stub
								if (reply.body().booleanValue()) {
									container.logger().info("Session信息存储成功 ");
									message.reply(true);
								} else {
									container.logger().info("Session信息存储失败！ ");
									message.reply(false);
								}
							}
						});
	
			}
		}
	}

	protected void logout(final Message<JsonObject> message) {
		JsonObject session = new JsonObject();
		session.putString("sessionID", message.body().getString("sessionID"));
		vertx.eventBus().send("server.session.destroy", session,
				new Handler<Message<Boolean>>() {
					@Override
					public void handle(Message<Boolean> reply) {
						// TODO Auto-generated method stub
						if (reply.body().booleanValue()) {
							
							message.reply(true);
							container.logger().info("Session信息销毁成功 ");
						} else {
							
							message.reply(false);
							container.logger().info("Session信息销毁失败！ ");
						}
					}
				});
	}

	protected void authorise(final Message<JsonObject> message) {
		JsonObject session = new JsonObject();
		session.putString("sessionID", message.body().getString("sessionID"));
		vertx.eventBus().send("server.session.get", session,
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> reply) {
						// TODO Auto-generated method stub
						if (reply.body().getString("sessionID") != null && !reply.body().getString("sessionID").equals("null")) {
							message.reply(true);
						} else {
							message.reply(false);
						}
					}
				});
	}

}
