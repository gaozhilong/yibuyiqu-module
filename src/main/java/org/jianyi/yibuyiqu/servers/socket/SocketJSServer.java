/**
 * 
 */
package org.jianyi.yibuyiqu.servers.socket;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jianyi.yibuyiqu.command.CommandUtil;
import org.jianyi.yibuyiqu.utils.JsonUril;
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

	public void start() {
		final JsonObject httpserverCfg = container.config();
		final String servername = httpserverCfg.getString("name");
		HttpServer server = vertx.createHttpServer();
		RouteMatcher httpRouteMatcher = new RouteMatcher();

		httpRouteMatcher.get(httpserverCfg.getString("path"),
				new Handler<HttpServerRequest>() {
					@Override
					public void handle(final HttpServerRequest request) {
						request.response().sendFile(
								httpserverCfg.getString("static")+httpserverCfg.getString("index"));
					}
				});

		httpRouteMatcher.get(httpserverCfg.getString("staticsuffix"),
				new Handler<HttpServerRequest>() {
					@Override
					public void handle(final HttpServerRequest request) {
						request.response().sendFile(
								httpserverCfg.getString("static")
										+ new File(request.path()));
					}
				});

		server.requestHandler(httpRouteMatcher);

		SockJSServer sockServer = vertx.createSockJSServer(server);

		sockServer.installApp(
				new JsonObject().putString("prefix",
						httpserverCfg.getString("prefix")),
				new Handler<SockJSSocket>() {
					public void handle(final SockJSSocket sock) {
						container.logger().info(
								"A client has connected! SID:"
										+ sock.writeHandlerID());
						JsonObject log = new JsonObject();
						log.putString("type", "客户端连接！");
						log.putString("ip",
								String.valueOf(sock.remoteAddress()));
						vertx.eventBus().send("server.log", log);

						// hornetqServer.createQueue(sock.writeHandlerID());
						Map<String,String> map = vertx.sharedData().getMap("allusers");
						Map<String, String> clientMap = new HashMap<String, String>();
						clientMap.put(CommandUtil.CMD_PROXYNAME, servername);
						map.put(sock.writeHandlerID(), JsonUril.objectToJsonStr(clientMap));
						sock.dataHandler(new Handler<Buffer>() {
							public void handle(Buffer data) {
								JsonObject message = null;
								try {
									message = new JsonObject(data.toString());
									message.putString(CommandUtil.CMD_SESSIONID, sock.writeHandlerID());
									message.putString(CommandUtil.CMD_PROXYNAME, servername);
									vertx.eventBus().send("server.input",
											message.toString());
								} catch (Exception e) {
									JsonObject log = new JsonObject();
									log.putString("type",
											"用户命令-命令格式错误，不是有效的JSON字符串");
									log.putString("result",
											"用户命令-命令格式错误，不是有效的JSON字符串");
									vertx.eventBus().send("server.log", log);
									vertx.eventBus()
											.send(sock.writeHandlerID(),
													new Buffer(
															"用户命令-命令格式错误，不是有效的JSON字符串"));
								}
							}
						});

					}
				});

		server.listen(httpserverCfg.getInteger("port"));

		/*
		 * long timerID = vertx.setPeriodic(10000, new Handler<Long>() { public
		 * void handle(Long timerID) { Set<String> set =
		 * vertx.sharedData().getSet("allusers"); for (String usr : set) {
		 * String msg = "dsdasdasd"; if (msg != null) {
		 * vertx.eventBus().send(usr, new Buffer(msg)); } }
		 * container.logger().info("And every second this is printed"); } });
		 */

		vertx.eventBus().registerHandler("server." + servername + ".send",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							JsonObject msg = message.body();
							String sessionId = msg.getString(CommandUtil.CMD_SESSIONID);
							msg.removeField(CommandUtil.CMD_SESSIONID);
							System.out.println(msg.toString());
							vertx.eventBus().send(sessionId, new Buffer(msg.toString()));
						}
					}
				});
		/*vertx.eventBus().registerHandler(
				"server." + servername + ".sendloginuser",
				new Handler<Message<JsonObject>>() {
					@Override
					public void handle(Message<JsonObject> message) {
						if (message.body().size() > 0) {
							Set<String> set = vertx.sharedData()
									.getSet("users");
							for (String usr : set) {
								vertx.eventBus().send(
										usr,
										new Buffer(message.body().getString(
												CommandUtil.CMD_MESSAGE)));
							}
						}
					}
				});*/
		

		/*
		 * vertx.setPeriodic(100, new Handler<Long>() { public void handle(Long
		 * timerID) { vertx.eventBus().send("server.command.execute",
		 * String.valueOf(timerID)); } });
		 */

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
