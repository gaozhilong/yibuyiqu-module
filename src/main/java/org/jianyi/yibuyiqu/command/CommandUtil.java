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
	
	public static final String COMMAND_TYPE = "command";
	
	public static final String CMD_SESSIONID = "sessionID";
	
	public static final String CMD_PROXYNAME = "proxy";
	
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
	
	public static Command createCommand(JsonObject message) {
		Command command = null;
		if (message != null) {
			String type = message.getString(COMMAND_TYPE);
			String sessionId = message.getString(CMD_SESSIONID);
			message.removeField(COMMAND_TYPE);
			command = new Command(sessionId, type, message.toMap());
		}
		return command;
	}
	
	public static Command createCommand(String input) {
		Command command = null;
		try {
			JsonObject message = new JsonObject(input);
			String type = message.getString(COMMAND_TYPE);
			String sessionId = message.getString(CMD_SESSIONID);
			message.removeField(COMMAND_TYPE);
			command = new Command(sessionId, type, message.toMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	}

}
