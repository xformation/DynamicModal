/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.commons.utils.IUtils.CTypes;
import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 */
public class ModalWrapper {

	private static final Logger logger = LoggerFactory.getLogger(ModalWrapper.class);

	private JSONObject json;
	private boolean save;
	private boolean overwrite;
	private List<String> classes;
	private List<Object> savedEntities;
	private Map<Class<?>, JSONObject> map;
	
	public ModalWrapper(String str) {
		this(str, false);
	}
	
	public ModalWrapper(String str, boolean overwrite) {
		this(str, overwrite, false);
	}
	
	public ModalWrapper(String str, boolean overwrite, boolean save) {
		this.json = IUtils.getJSONObject(str);
		this.overwrite = overwrite;
		this.save = save;
		this.setClassMap();
	}

	/**
	 * Method to extract all classes from input json.
	 */
	private void setClassMap() {
		if (!IUtils.isNull(json)) {
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = json.opt(key);
				logger.info("Val: " + val);
				CTypes type = IUtils.getValueClassType(val);
				if (type == CTypes.Object) {
					JSONObject jObj = (JSONObject) val;
					ModalParser parser = new ModalParser(
							StringUtils.capitalize(key), jObj, overwrite);
					classes.add(parser.getFullClassName());
					map.put(parser.getClazz(), jObj);
					if (this.save) {
						saveEntity(parser.getClazz(), jObj, true);
					}
				} else {
					logger.warn("Property: " + key + " ignored!");
				}
			}
		}
	}

	/**
	 * Method to save an entity class object into db.
	 * @param clz
	 * @param jObj
	 * @param save
	 * return
	 */
	private Object saveEntity(Class<?> clz, JSONObject jObj, boolean save) {
		Object clsObj = null;
		if (!IUtils.isNull(clz) && !IUtils.isNull(jObj)) {
			Map<String, Object> inCls = getInnerObjects(jObj);
			String str = IUtils.getStringFromValue(jObj);
			clsObj = IUtils.getObjectFromValue(str, clz);
			clsObj = Utils.updateValuesInJavaObject(clsObj, inCls);
			logger.info("final Object: " + IUtils.getStringFromValue(clsObj));
			clsObj = Utils.loadRepoAndSave(clz, clsObj, save);
			if (save) {
				savedEntities.add(clsObj);
			}
		}
		return clsObj;
	}

	/**
	 * Method to check inner class objects in json
	 * @param jObj
	 * @return
	 */
	private Map<String, Object> getInnerObjects(JSONObject jObj) {
		Map<String, Object> map = null;
		if (!IUtils.isNull(json)) {
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			map = new HashMap<>();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = json.opt(key);
				logger.info("Val: " + val);
				CTypes type = IUtils.getValueClassType(val);
				if (type == CTypes.Object) {
					Class<?> clz = Utils.getJsonValueJavaClass(
							key, (JSONObject) val);
					map.put(key, saveEntity(clz, (JSONObject) val, false));
				} else if (type == CTypes.Array) {
					CTypes tp = IUtils.getArrValType(val);
					if (CTypes.Object == tp) {
						Set<Object> sets = getJavaObjects(key, (JSONArray) val);
						map.put(key, sets);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Method to get all java objects from json array.
	 * @param key
	 * @param arr
	 * @return
	 */
	private Set<Object> getJavaObjects(String key, JSONArray arr) {
		Set<Object> set = null;
		if (!IUtils.isNull(arr)) {
			set = new HashSet<>();
			for (int i = 0; i < arr.length(); i++) {
				Object val = arr.opt(i);
				Class<?> clz = Utils.getJsonValueJavaClass(
						key, (JSONObject) val);
				Object jObj = saveEntity(clz, (JSONObject) val, false);
				set.add(jObj);
			}
		}
		return set;
	}

	/**
	 * Method to return list of classes in input json.
	 * @return
	 */
	public List<String> getClasses() {
		return classes;
	}

	/**
	 * Method to return saved entities list.
	 * @return
	 */
	public List<Object> getSavedEntities() {
		return savedEntities;
	}
}
