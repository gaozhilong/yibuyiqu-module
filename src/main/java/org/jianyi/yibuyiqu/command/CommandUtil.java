/**
 * 
 */
package org.jianyi.yibuyiqu.command;

import org.vertx.java.core.json.JsonObject;

/**
 * @author zhilong_Gao
 *
 */
public class CommandUtil {
	
	public static final String CMD_SESSIONID = "sessionID";
	
	public static final String CMD_LOGIN = "login";
	
	public static final String CMD_LOGOUT = "logout";
	
	public static final String CMD_RESULT = "result";
	
	public static final String CMD_MESSAGE = "message";
	
	public static JsonObject getCommands(String message) {
		JsonObject cmd = null;
		try {
			cmd = new JsonObject(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cmd;
	}
	
	public static JsonObject getCommands(String message, String sessionId) {
		JsonObject cmd = null;
		try {
			cmd = new JsonObject(message);
			cmd.putString("sessionID", sessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cmd;
	}

}
