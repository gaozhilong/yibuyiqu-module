package org.jianyi.yibuyiqu.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.json.JsonObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUril {

	public static JsonObject objectToJson(Object obj) {
		ObjectWriter ow = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		JsonObject json = null;
		try {
			json = new JsonObject(ow.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	
	public static Map<String, Object> jsonToMapObject(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
			};
			map = mapper.readValue(json, typeRef);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static Map<String, String> jsonToMapString(String json) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			JsonFactory factory = new JsonFactory();
			ObjectMapper mapper = new ObjectMapper(factory);
			TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
			};
			map = mapper.readValue(json, typeRef);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
