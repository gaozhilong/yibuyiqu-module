package org.jianyi.yibuyiqu.servers.input;

import java.util.Map;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.command.Result;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import rx.Observable;
import rx.functions.Action1;

public class InputServer extends Verticle {

	public void start() {

		final Map<String, String> commandMap = vertx.sharedData().getMap(
				"commandMap");

		vertx.eventBus().registerHandler("server.input",
				new Handler<Message<String>>() {
					@Override
					public void handle(Message<String> message) {
						container.logger().info("server.input");
						String command = message.body();
						Observable.just(command).subscribe(
								new Action1<String>() {
									@Override
									public void call(String command) {
										JsonObject message = new JsonObject(
												command);
										String sessionId = message
												.getString(CommandUtil.CMD_SESSIONID);
										String type = message
												.getString(CommandUtil.COMMAND_TYPE);
										if (type != null
												&& commandMap.containsKey(type)) {
											logCall(message);
											vertx.eventBus().send(
													commandMap.get(type),
													command,new Handler<Message<JsonObject>>() {
														@Override
														public void handle(Message<JsonObject> reply) {
															// TODO Auto-generated method stub
															log(reply.body());
														}
													});
										} else {
											String msg = "命令没有相应的处理服务";
											if (type == null) {
												msg = "命令类型为空";
											}
											Result result = new Result(
													sessionId, msg, "error");
											log(result);
											sendMsg(result.toJsonObject());
										}
									}
								});
					}
				});
	}
	
	private void logCall(JsonObject message) {
		JsonObject log = new JsonObject();
		String type = message.getString(CommandUtil.COMMAND_TYPE);
		String sessionId = message.getString(CommandUtil.CMD_SESSIONID);
		log.putString("type", type);
		//log.putString("username", message.getString("username"));
		log.putString("sessionID", sessionId);
		log.putString("result",	"call");
		vertx.eventBus().send("server.log", log);
	}
	
	private void log(Result result) {
		JsonObject log = new JsonObject();
		log.putString("type", "用户命令-" + result.getResult());
		log.putString("sessionID", result.getSessionID());
		log.putString("result", result.getMessage());
		vertx.eventBus().send("server.log",
				log);
	}
	
	private void log(JsonObject result) {
		JsonObject log = new JsonObject();
		log.putString("type", "用户命令执行结果");
		log.putString("sessionID", result.getString(CommandUtil.CMD_SESSIONID));
		log.putString("result", result.getString(CommandUtil.CMD_RESULT));
		log.putString("message", result.getString(CommandUtil.CMD_MESSAGE));
		vertx.eventBus().send("server.log",	log);
	}

	public void sendMsg(JsonObject msg) {
		vertx.eventBus().send("server.proxy.send", msg);
	}

}
