package org.jianyi.yibuyiqu.servers.command;

import java.util.Map;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

public abstract class CommandService extends Verticle{
	
	//发送消息到制定客户端
	public void sendMsg(JsonObject msg) {
		Map<String,String> map = vertx.sharedData().getMap("allusers");
		JsonObject json = new JsonObject(map.get(msg.getString(CommandUtil.CMD_SESSIONID)));
		vertx.eventBus().send("server."+json.getString(CommandUtil.CMD_PROXYNAME)+".send", msg);
	}
	public void execute(Message<String> message) {}
	
}
