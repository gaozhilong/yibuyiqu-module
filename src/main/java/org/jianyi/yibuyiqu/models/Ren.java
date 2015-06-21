package org.jianyi.yibuyiqu.models;

import java.util.Set;

import org.jianyi.yibuyiqu.utils.JsonUril;
import org.vertx.java.core.json.JsonObject;

public class Ren {
	
	private String id;
	
	private String name;
	
	private Area area;
	
	private Set<Wu> wu;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public Set<Wu> getWu() {
		return wu;
	}

	public void setWu(Set<Wu> wu) {
		this.wu = wu;
	}
	
	public JsonObject toJsonObject() {
		return JsonUril.objectToJson(this);
	}

}
