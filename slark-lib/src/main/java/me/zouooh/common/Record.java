package me.zouooh.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.json.Json;

public class Record{

	private Map<String, Object> map;
	
	public Record() {
		this(new LinkedHashMap<String, Object>());
	}
	
	public Record(Map<String, Object> map) {
		this.map = map;
	}

	public int getInt(String key) {
		if (getMap().containsKey(key)) {
			return Castors.me().castTo(getMap().get(key), Integer.class);
		}
		return 0;
	}

	public double getDouble(String key) {
		if (getMap().containsKey(key)) {
			return Castors.me().castTo(getMap().get(key), Double.class);
		}
		return 0.0;
	}

	public boolean getBoolean(String key) {
		if (getMap().containsKey(key)) {
			return Castors.me().castTo(getMap().get(key), Boolean.class);
		}
		return false;
	}
	
	public boolean getBooleanDefalutTrue(String key) {
		if (getMap().containsKey(key)) {
			return Castors.me().castTo(getMap().get(key), Boolean.class);
		}
		return true;
	}
	
	public String getString(String key) {
		return getString(key, "");
	}

	public String getString(String key, String defalut) {
		Object object = getMap().get(key);
		if (object == null) {
			return defalut;
		}
		return Castors.me().castTo(object, String.class);
	}

	public Date getDateStr(String key) {
		if (getMap().containsKey(key)) {
			return Castors.me().castTo(getMap().get(key), Date.class);
		}
		return null;
	}

	public <T> List<T> getEntityList(String key, Class<T> clazz) {
		Object list = getMap().get(key);
		if (list instanceof List) {
			List<?> list2 = (List<?>) list;
			List<T> list3 = new ArrayList<T>();
			for (Object object : list2) {
				T t = Castors.me().castTo(object, clazz);
				list3.add(t);
			}
			return list3;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getAsList(String key, Class<T> clazz){
		Object object = getMap().get(key);
		if (object == null) {
			return null;
		}
		if (object instanceof List<?>) {
			return (List<T>)object;
		}
		String json = (String)object;
		List<T> list = Json.fromJsonAsList(clazz, json);
		return list;
	}

	public <T> T getEntity(String key, Class<T> clazz) {
		return Castors.me().castTo(getMap().get(key), clazz);
	}

	@SuppressWarnings("unchecked")
	public Record getRecord(String key) {
		Object object = getMap().get(key);
		if (object instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) object;
			Record record = new Record(map);
			return record;
		}
		return new Record();
	}

	@SuppressWarnings("unchecked")
	public List<Record> getRecordList(String key) {
		Object list = getMap().get(key);
		if (list instanceof List) {
			List<?> list2 = (List<?>) list;
			List<Record> list3 = new ArrayList<Record>();
			for (Object object : list2) {
				if (object instanceof Map) {
					Map<String, Object> map = (Map<String, Object>) object;
					Record record = new Record(map);
					list3.add(record);
				}
			}
			return list3;
		}
		return null;
	}

	public Map<String, Object> getMap() {
		return map;
	}
	
}
