package org.jianyi.yibuyiqu.command;

import org.vertx.java.core.json.JsonObject;

public class Result {
	
	private String sessionID;
	private String message;
	private String result;
	private String scoap;
	private boolean immediately;
	
	public Result(String sessionID, String message, String result) {
		super();
		this.sessionID = sessionID;
		this.message = message;
		this.result = result;
		immediately = true;
	}
	
	public Result(String sessionID, String scoap, String message, String result) {
		super();
		this.sessionID = sessionID;
		this.scoap = scoap;
		this.message = message;
		this.result = result;
	}
	
	public Result(String sessionID, String message, String result,
			String scoap, boolean immediately) {
		super();
		this.sessionID = sessionID;
		this.message = message;
		this.result = result;
		this.scoap = scoap;
		this.immediately = immediately;
	}

	public JsonObject toJsonObject() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.putString(CommandUtil.CMD_SESSIONID, sessionID);
		jsonObject.putString(CommandUtil.CMD_RESULT,result);
		jsonObject.putString(CommandUtil.CMD_MESSAGE,message);
		return jsonObject;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getScoap() {
		return scoap;
	}

	public void setScoap(String scoap) {
		this.scoap = scoap;
	}

	public boolean isImmediately() {
		return immediately;
	}

	public void setImmediately(boolean immediately) {
		this.immediately = immediately;
	}

}
