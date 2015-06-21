package org.jianyi.yibuyiqu.servers.command;

import org.jianyi.yibuyiqu.command.Command;
import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.command.Result;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

public class LoginCommandService extends CommandService {
	
	public static final String USERNAME="username";
	public static final String PASSWORD="password";
	

	public void start() {
		vertx.eventBus().registerHandler("server.command.login",
				new Handler<Message<String>>() {
					@Override
					public void handle(Message<String> message) {
						execute(message);
					}
				});

	}

	@Override
	public void execute(final Message<String> message) {
		// TODO Auto-generated method stub
		container.logger().info("execute Login Command");
		final Command command = CommandUtil.createCommand(message.body());

		JsonObject loginMsg = new JsonObject();
		loginMsg.putString(CommandUtil.CMD_SESSIONID, command.getSessionId());
		loginMsg.putString(USERNAME, command.getParamStringValue(USERNAME));
		loginMsg.putString(PASSWORD, command.getParamStringValue(PASSWORD));
		loginMsg.putString(CommandUtil.CMD_PROXYNAME,
				command.getParamStringValue(CommandUtil.CMD_PROXYNAME));
		vertx.eventBus().send("server.author.login", loginMsg,
				new Handler<Message<Boolean>>() {
					@Override
					public void handle(Message<Boolean> reply) {
						if (reply.body().booleanValue()) {
							Result result = new Result(command.getSessionId(),
									"登录成功", Result.SUCCESS);
							message.reply(result.toJsonObject());
							sendMsg(result.toJsonObject());
						} else {
							Result result = new Result(command.getSessionId(),
									"登录失败", Result.ERROR);
							message.reply(result.toJsonObject());
							sendMsg(result.toJsonObject());
						}
					}
				});

	}

}
