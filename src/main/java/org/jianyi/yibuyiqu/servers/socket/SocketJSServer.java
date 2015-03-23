/**
 * 
 */
package org.jianyi.yibuyiqu.servers.socket;

import java.io.File;
import java.util.Set;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.servers.auth.AuthServer;
import org.jianyi.yibuyiqu.servers.command.CommandServer;
import org.jianyi.yibuyiqu.servers.log.LogServer;
import org.jianyi.yibuyiqu.session.MongodbSessionServer;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;
import org.vertx.java.platform.Verticle;

/**
 * @author zhilong_Gao
 *
 */
public class SocketJSServer extends Verticle {
	
	JsonObject serverCfg, httpserverCfg, sockJsCfg;

	public void start() {
		serverCfg = container.config().getObject("server");
		httpserverCfg = container.config().getObject("http");
		sockJsCfg = container.config().getObject("sock");
		
		container.deployVerticle(LogServer.class.getName(),null, serverCfg.getInteger("LogServer"), doneHandler);
		container.deployWorkerVerticle(MongodbSessionServer.class.getName(),null, serverCfg.getInteger("MongodbSessionServer"), true, doneHandler);
		container.deployWorkerVerticle(AuthServer.class.getName(), null, serverCfg.getInteger("AuthServer"),
				true, doneHandler);
		container.deployWorkerVerticle(CommandServer.class.getName(), null, serverCfg.getInteger("CommandServer"),
				true, doneHandler);

		HttpServer server = vertx.createHttpServer();
		RouteMatcher httpRouteMatcher = new RouteMatcher();

		httpRouteMatcher.get(httpserverCfg.getString("path"), new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest request) {
				request.response().sendFile(httpserverCfg.getString("index"));
			}
		});
		
		httpRouteMatcher.get(httpserverCfg.getString("staticsuffix"), new Handler<HttpServerRequest>() {
			@Override
			public void handle(final HttpServerRequest request) {
				request.response().sendFile(httpserverCfg.getString("static") + new File(request.path()));
			}
		});

		server.requestHandler(httpRouteMatcher);

		SockJSServer sockServer = vertx.createSockJSServer(server);

		sockServer.installApp(new JsonObject().putString("prefix", sockJsCfg.getString("prefix")),
				new Handler<SockJSSocket>() {
					public void handle(final SockJSSocket sock) {
						container.logger().info(
								"A client has connected! SID:"
										+ sock.writeHandlerID());
						JsonObject log = new JsonObject();
						log.putString("type", "客户端连接！");
						log.putString("ip", String.valueOf(sock.remoteAddress()));
						vertx.eventBus().send("server.log",log);
						
						//hornetqServer.createQueue(sock.writeHandlerID());
						Set<String> set = vertx.sharedData().getSet("allusers");
						set.add(sock.writeHandlerID());
						sock.dataHandler(new Handler<Buffer>() {
							public void handle(Buffer data) {
								JsonObject cmd = CommandUtil.getCommands(
										data.toString(), sock.writeHandlerID());
								vertx.eventBus().send("server.command.execute",
										cmd,
										new Handler<Message<JsonObject>>() {
											@Override
											public void handle(
													Message<JsonObject> reply) {
												if (reply.body().size() > 0) {
													sock.write(new Buffer(reply
														.body().toString()));
												}
											}
										});
							}
						});

					}
				});
		
		server.listen(httpserverCfg.getInteger("port"));

		/*long timerID = vertx.setPeriodic(10000, new Handler<Long>() {
			public void handle(Long timerID) {
				Set<String> set = vertx.sharedData().getSet("allusers");
				for (String usr : set) {
					String msg = "dsdasdasd";
					if (msg != null) {
						vertx.eventBus().send(usr,
								new Buffer(msg));
					}
				}
				container.logger().info("And every second this is printed");
			}
		});*/
		
		vertx.eventBus().registerHandler("server.proxy.send",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							vertx.eventBus().send(message.body().getString(CommandUtil.CMD_SESSIONID), new Buffer(message.body().getString(CommandUtil.CMD_MESSAGE)));
						}
					}
				});
		vertx.eventBus().registerHandler("server.proxy.sendloginuser",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							Set<String> set = vertx.sharedData().getSet("users");
							for (String usr : set) {
								vertx.eventBus().send(usr, new Buffer(message.body().getString(CommandUtil.CMD_MESSAGE)));
							}
						}
					}
				});
		vertx.eventBus().registerHandler("server.proxy.sendalluser",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							Set<String> set = vertx.sharedData().getSet("allusers");
							for (String usr : set) {
								vertx.eventBus().send(usr, new Buffer(message.body().getString(CommandUtil.CMD_MESSAGE)));
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
						.println("The verticle has been deployed, deployment ID is rewrwerewrwe "
								+ asyncResult.result());
			} else {
				asyncResult.cause().printStackTrace();
			}
		}
	};

}
