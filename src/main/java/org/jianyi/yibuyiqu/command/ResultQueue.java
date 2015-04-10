package org.jianyi.yibuyiqu.command;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.vertx.java.core.json.JsonObject;

public class ResultQueue extends ConcurrentLinkedQueue<JsonObject> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4195452533343698682L;
	private static ResultQueue instance;
	
	private ResultQueue() {
		super();
	}
	
	public static ResultQueue getInstance() {
		if (instance == null) {
			instance = new ResultQueue();
		}
		return instance;
	}

}
