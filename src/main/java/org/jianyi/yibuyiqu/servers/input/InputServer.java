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
											vertx.eventBus().send(
													commandMap.get(type),
													command);
										} else {
											String msg = "命令没有相应的处理服务";
											if (type == null) {
												msg = "命令类型为空";
											}
											Result result = new Result(
													sessionId, msg, "error");
											JsonObject log = new JsonObject();
											log.putString("type", "用户命令-" + msg);
											log.putString("result", msg);
											vertx.eventBus().send("server.log",
													log);
											sendMsg(result.toJsonObject());
										}
									}
								});
					}
				});
	}

	public void sendMsg(JsonObject msg) {
		vertx.eventBus().send("server.proxy.send", msg);
	}

}
