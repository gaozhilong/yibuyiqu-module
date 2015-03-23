/**
 * 
 */
package org.jianyi.yibuyiqu.servers.command;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

/**
 * @author zhilong_Gao
 *
 */
public class CommandServer extends Verticle {

	public void start() {
		vertx.eventBus().registerHandler("server.command.execute",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						execute(message);
					}
				});

	}

	private void execute(final Message<JsonObject> message) {
		final JsonObject cmd = message.body();
		if (cmd != null) {
			String commandType = cmd.getString("command");
			if (commandType != null) {
				if (commandType.equals(CommandUtil.CMD_LOGIN)) {
					JsonObject loginMsg = new JsonObject();
					loginMsg.putString("sessionID", cmd.getString("sessionID"));
					loginMsg.putString("username", cmd.getString("username"));
					loginMsg.putString("password", cmd.getString("password"));
					vertx.eventBus().send("server.author.login", loginMsg,
								new Handler<Message<Boolean>>() {
									JsonObject result = new JsonObject();
									@Override
									public void handle(Message<Boolean> reply) {
										if (reply.body().booleanValue()) {
											result.putString(
													CommandUtil.CMD_SESSIONID,
													cmd.getString("sessionID"));
											result.putString(
													CommandUtil.CMD_RESULT,
													"completed");
											result.putString(
													CommandUtil.CMD_MESSAGE,
													"登录成功");
											JsonObject log = new JsonObject();
											log.putString("type", "用户命令-登录");
											log.putString("username", cmd.getString("username"));
											log.putString("sessionID", cmd.getString("sessionID"));
											log.putString("result", "登录成功");
											vertx.eventBus().send("server.log",log);
											sendMsg(result);
										} else {
											result.putString(
													CommandUtil.CMD_SESSIONID,
													cmd.getString("sessionID"));
											result.putString(
													CommandUtil.CMD_RESULT,
													"error");
											result.putString(
													CommandUtil.CMD_MESSAGE,
													"登录失败");
											JsonObject log = new JsonObject();
											log.putString("type", "用户命令-登录");
											log.putString("username", cmd.getString("username"));
											log.putString("sessionID", cmd.getString("sessionID"));
											log.putString("result", "登录失败");
											vertx.eventBus().send("server.log",log);
											sendMsg(result);
										}
									}
								});
				} else if (commandType.equals(CommandUtil.CMD_LOGOUT)) {
					JsonObject logoutMsg = new JsonObject();
					logoutMsg.putString("sessionID", cmd.getString("username"));
					vertx.eventBus().send("server.author.logout", logoutMsg,
							new Handler<Message<Boolean>>() {
								JsonObject result = new JsonObject();
								@Override
								public void handle(Message<Boolean> reply) {
									if (reply.body().booleanValue()) {
										result.putString(
												CommandUtil.CMD_SESSIONID,
												cmd.getString("sessionID"));
										result.putString(
												CommandUtil.CMD_RESULT,
												"completed");
										result.putString(
												CommandUtil.CMD_MESSAGE,
												"登出成功");
										JsonObject log = new JsonObject();
										log.putString("type", "用户命令-登出");
										log.putString("username", cmd.getString("username"));
										log.putString("sessionID", cmd.getString("sessionID"));
										log.putString("result", "登出成功");
										vertx.eventBus().send("server.log",log);
										sendMsg(result);
									} else {
										result.putString(
												CommandUtil.CMD_SESSIONID,
												cmd.getString("sessionID"));
										result.putString(
												CommandUtil.CMD_RESULT, "error");
										result.putString(
												CommandUtil.CMD_MESSAGE,
												"登出失败");
										JsonObject log = new JsonObject();
										log.putString("type", "用户命令-登出");
										log.putString("username", cmd.getString("username"));
										log.putString("sessionID", cmd.getString("sessionID"));
										log.putString("result", "登出失败");
										vertx.eventBus().send("server.log",log);
										sendMsg(result);
									}
								}
							});
				} else {
					JsonObject result = new JsonObject();
					result.putString(CommandUtil.CMD_SESSIONID,
							cmd.getString("sessionID"));
					result.putString(CommandUtil.CMD_RESULT, "error");
					result.putString(CommandUtil.CMD_MESSAGE, "未知命令");
					JsonObject log = new JsonObject();
					log.putString("type", "用户命令-未知");
					log.putString("username", cmd.getString("username"));
					log.putString("sessionID", cmd.getString("sessionID"));
					log.putString("result", "未知命令");
					vertx.eventBus().send("server.log",log);
					sendMsg(result);
				}
			} else {
				JsonObject result = new JsonObject();
				result.putString(CommandUtil.CMD_SESSIONID,
						cmd.getString("sessionID"));
				result.putString(CommandUtil.CMD_RESULT, "error");
				result.putString(CommandUtil.CMD_MESSAGE, "命令类型为空");
				JsonObject log = new JsonObject();
				log.putString("type", "用户命令-类型为空");
				log.putString("username", cmd.getString("username"));
				log.putString("sessionID", cmd.getString("sessionID"));
				log.putString("result", "命令类型为空");
				vertx.eventBus().send("server.log",log);
				sendMsg(result);
			}
		} else {
			JsonObject result = new JsonObject();
			//result.putString(CommandUtil.CMD_SESSIONID,	cmd.getString("sessionID"));
			result.putString(CommandUtil.CMD_RESULT, "error");
			result.putString(CommandUtil.CMD_MESSAGE, "命令为空");
			JsonObject log = new JsonObject();
			log.putString("type", "用户命令-命令为空");
			/*log.putString("username", cmd.getString("username"));
			log.putString("sessionID", cmd.getString("sessionID"));*/
			log.putString("result", "命令为空");
			vertx.eventBus().send("server.log",log);
			sendMsg(result);
		}
	}

	private void sendMsg(JsonObject msg) {
		vertx.eventBus().send("server.proxy.send", msg);
	}

}
