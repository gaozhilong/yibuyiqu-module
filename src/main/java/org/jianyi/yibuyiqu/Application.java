package org.jianyi.yibuyiqu;

import java.util.Map;
import java.util.Set;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.servers.socket.SocketJSServer;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public class Application extends Verticle {
	
	JsonObject httpserverCfg, sockJsCfg;
	
	JsonArray proxyCfg,serverCfg, commandCfg;
	
	public void start() {
		proxyCfg = container.config().getArray("proxyserver");
		serverCfg = container.config().getArray("server");
		httpserverCfg = container.config().getObject("http");
		sockJsCfg = container.config().getObject("sock");
		commandCfg = container.config().getArray("commands");
		
		int servivesize = serverCfg.size();
		for (int i = 0; i < servivesize; i++) {
			JsonObject config = serverCfg.get(i);
			if (config.getBoolean("worker")) {
				container.deployWorkerVerticle(config.getString("verticlefile"), null, config.getInteger("instance"),
						true, doneHandler);
			} else {
				container.deployVerticle(config.getString("verticlefile"), null, config.getInteger("instance"), doneHandler);
			}
		}		
		
		int proxysize = proxyCfg.size();
		for (int i = 0; i < proxysize; i++) {
			JsonObject config = proxyCfg.get(i);
			container.deployVerticle(SocketJSServer.class.getName(),config, 1, doneHandler);
		}
		
		
		Map<String, String> commands = vertx.sharedData().getMap("commandMap");
		int commandsize = commandCfg.size();
		for (int i = 0; i < commandsize; i++) {
			JsonObject config = commandCfg.get(i);
			container.deployWorkerVerticle(config.getString("verticlefile"), null, config.getInteger("instance"),
					true, doneHandler);
			commands.put(config.getString("name"), config.getString("address"));
		}
		
		vertx.eventBus().registerHandler(
				"server.all.sendalluser",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							Map<String,String> map = vertx.sharedData().getMap("allusers");
							Set<String> set = map.keySet();
							for (String usr : set) {
								JsonObject json = new JsonObject(map.get(usr));
								vertx.eventBus().send("server." + json.getString(CommandUtil.CMD_PROXYNAME) + ".send",	message.body());
							}
						}
					}
				});
	}
	
	public void stop() {
		
	}

	AsyncResultHandler<String> doneHandler = new AsyncResultHandler<String>() {
		public void handle(AsyncResult<String> asyncResult) {
			if (asyncResult.succeeded()) {
				System.out
						.println("The verticle has been deployed, deployment ID is "
								+ asyncResult.result());
			} else {
				asyncResult.cause().printStackTrace();
			}
		}
	};

}
