package org.jianyi.yibuyiqu.models;

import java.util.Set;

import org.jianyi.yibuyiqu.utils.JsonUril;
import org.vertx.java.core.json.JsonObject;

public class Area {
	
	private String id;
	
	private String name;
	
	private String description;
	
	private Set<Ren> ren;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Ren> getRen() {
		return ren;
	}

	public void setRen(Set<Ren> ren) {
		this.ren = ren;
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
