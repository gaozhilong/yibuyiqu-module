package org.jianyi.yibuyiqu.servers.command;

import org.jianyi.yibuyiqu.command.Command;
import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.command.Result;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class LoginCommandService extends CommandService{
	
	public void start() {
		vertx.eventBus().registerHandler("server.command.login",
				new Handler<Message<String>>() {
					@Override
					public void handle(Message<String> message) {
						execute(message.body());
					}
				});

	}

	@Override
	public void execute(String commandJson) {
		// TODO Auto-generated method stub
		//container.logger().info("execute Login Command");
		final Command command = CommandUtil.createCommand(commandJson);
		Observable.just(command).filter(new Func1<Command, Boolean>() {
            @Override
            public Boolean call(Command command) {
                return command == null;
            }
        }).subscribe(new Action1<Command>() {  
	        @Override  
	        public void call(Command command) {
	        	sendNullCommandMsg();
	        }  
	    });
		
		Observable.just(command).filter(new Func1<Command, Boolean>() {
            @Override
            public Boolean call(Command command) {
                return command != null;
            }
        }).subscribe(new Action1<Command>() {  
	        @Override  
	        public void call(final Command command) {
	        	JsonObject loginMsg = new JsonObject();
				loginMsg.putString("sessionID",
						command.getSessionId());
				loginMsg.putString("username",
						command.getParamStringValue("username"));
				loginMsg.putString("password",
						command.getParamStringValue("password"));
				vertx.eventBus().send("server.author.login",
						loginMsg,
						new Handler<Message<Boolean>>() {
							@Override
							public void handle(
									Message<Boolean> reply) {
								if (reply.body().booleanValue()) {
									Result result = new Result(
											command.getSessionId(),
											"登录成功", "completed");
									
									JsonObject log = new JsonObject();
									log.putString("type",
											"用户命令-登录");
									log.putString(
											"username",
											command.getParamStringValue("username"));
									log.putString(
											"sessionID",
											command.getSessionId());
									log.putString("result",
											"登录成功");
									vertx.eventBus().send(
											"server.log", log);
									sendMsg(result
												.toJsonObject());
								} else {
									Result result = new Result(
											command.getSessionId(),
											"登录失败", "error");
									JsonObject log = new JsonObject();
									log.putString("type",
											"用户命令-登录");
									log.putString(
											"username",
											command.getParamStringValue("username"));
									log.putString(
											"sessionID",
											command.getSessionId());
									log.putString("result",
											"登录失败");
									vertx.eventBus().send(
											"server.log", log);
									sendMsg(result
												.toJsonObject());
								}
							}
						});
	        }  
	    });
		
	}

}
