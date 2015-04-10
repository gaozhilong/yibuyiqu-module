package org.jianyi.yibuyiqu.command;

import java.io.Serializable;
import java.util.Map;

public class Command implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sessionId;
	
	private String type;
	
	private Map<String, Object> parameters;
		
	public Command(String sessionId, String type, Map<String, Object> parameters) {
		super();
		this.sessionId = sessionId;
		this.type = type;
		this.parameters = parameters;
	}

	public Object getParamValue(String param) {
		return parameters.get(param);
	}
	
	public String getParamStringValue(String param) {
		return parameters.get(param).toString();
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
