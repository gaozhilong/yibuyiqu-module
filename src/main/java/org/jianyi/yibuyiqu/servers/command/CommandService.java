package org.jianyi.yibuyiqu.servers.command;

import org.jianyi.yibuyiqu.command.Result;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public abstract class CommandService extends Verticle{
	
	public void execute(String commandJson) {}
	//发送消息到制定客户端
	public void sendMsg(JsonObject msg) {
		vertx.eventBus().send("server.proxy.send", msg);
	}
	
	public void sendNullCommandMsg() {
		Result result = new Result(null, "命令为空", "error");
		JsonObject log = new JsonObject();
		log.putString("type", "用户命令-命令为空");
		log.putString("result", "命令为空");
		vertx.eventBus().send("server.log", log);
		sendMsg(result.toJsonObject());
	}
}
