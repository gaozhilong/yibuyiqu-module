/**
 * 
 */
package org.jianyi.yibuyiqu.integration.java;

import java.io.IOException;
import java.net.URL;

import org.jianyi.yibuyiqu.servers.socket.SocketJSServer;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

/**
 * @author zhilong_Gao
 *
 */
public class DepolyVerticle {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		
		PlatformManager pm = PlatformLocator.factory.createPlatformManager();
		
		/*pm.deployVerticle(LogServer.class.getName(), null, new URL[] {}, 1,	null, doneHandler);
		pm.deployVerticle(MongodbSessionManager.class.getName(), null, new URL[] {}, 1,	null, doneHandler);
		pm.deployVerticle(AuthServer.class.getName(), null, new URL[] {}, 1,	null, doneHandler);
		pm.deployVerticle(CommandServer.class.getName(), null, new URL[] {}, 1,	null, doneHandler);*/
		pm.deployVerticle(SocketJSServer.class.getName(), null, new URL[] {}, 1,	null, doneHandler);
		//pm.deployWorkerVerticle(false, SocketJSServer.class.getName(), null, new URL[] {}, 1, null, doneHandler);
		//pm.deployModule("org.jianyi~yimud-module~0.1", null, 1, doneHandler);
		/*pm.deployVerticle(LogServer.class.getName(), null, new URL[] {}, 1,	null, doneHandler);*/
		// Prevent the JVM from exiting
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
