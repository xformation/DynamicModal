package com.synectiks.dynModel.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.dynModel.DynamicModelApplication;
import com.synectiks.dynModel.handlers.ConfigWrapper;
import com.synectiks.dynModel.handlers.ModalWrapper;
import com.synectiks.dynModel.repositories.PsqlRepository;
import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 */
@RestController
@RequestMapping(path = "/dynModel", method = RequestMethod.POST)
@CrossOrigin
public class DynModelController {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DynModelController.class);

	/**
	 * Method to create data models using config objects.
	 * @param request
	 * @param config
	 * <pre>
	 * {
	 *	"className": {
	 *	"cloudName": "AWS, AZure, Kubernete ...",
	 *	"groupName": "Networking, Cloud ...",
	 *	"fields": [{
	 *		"key": "key",
	 *		"type": "String|Text|Integer|Long|Double|Date|Boolean|Object",
	 *		"default": "value",
	 *		"isArray": true|false,
	 *		"clsName": "",
	 *		"isNullable": true|false,
	 *		"length": n,
	 *		"validations": true|false,
	 *		"isRequire": true|false,
	 *		"min": n,
	 *		"max": n,
	 *		"regex": "^$",
	 *		"regexMsg": "Regex validation failed",
	 *		"dateFromat": "dd-MM-YYYY",
	 *		"isCompositeKeyMem": true|false
	 *	}]
	 * }
	 *}
	 * </pre>
	 * @param overwrite
	 * @return
	 */
	@RequestMapping(path = "/create")
	public ResponseEntity<Object> createConfig(HttpServletRequest request,
			@RequestParam String config, @RequestParam boolean overwrite) {
		Object entities = null;
		try {
			ConfigWrapper wrapper = new ConfigWrapper(
					new JSONObject(config), overwrite);
			entities = wrapper.writeClasses();
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(th);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entities);
	}

	@RequestMapping(path = "/createModelByJson")
	public ResponseEntity<Object> createByJson(HttpServletRequest request,
			@RequestBody String entity) {
		return createModel(request, entity, false);
	}

	//@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(path = "/createModel")
	public ResponseEntity<Object> createModel(HttpServletRequest request,
			@RequestParam String strJson, @RequestParam boolean overwrite) {
		Object entities = null;
		List<String> innerClses = null;
		logger.info("json: " + strJson);
		try {
			ModalWrapper wrapper = new ModalWrapper(strJson, overwrite);
			List<String> dynCls = wrapper.getClasses();
			innerClses = wrapper.getInnerClasses();
			//JSONObject json = IUtils.getJSONObject(strJson);
			//Class<?> dynCls = Utils.createDynModels(json, overwrite);
			if (!IUtils.isNull(dynCls)) {
				entities = "{result: \"success\", classesName: \"" +
						dynCls + "\"}";
			} else {
				throw new Exception("Failed to create class form input json!");
			}
		} catch (Throwable th) {
			Utils.deleteDynFiles(innerClses);
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(th);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entities);
	}

	@RequestMapping(path = "/populateData")
	public ResponseEntity<Object> populateData(HttpServletRequest request,
			@RequestParam String strJson) {
		Object entities = null;
		try {
			ModalWrapper wrapper = new ModalWrapper(strJson, false, true);
			if (!IUtils.isNull(wrapper)) {
				entities = wrapper.getSavedEntities();
				logger.info("Saved entity: " + IUtils.getStringFromValue(entities));
			} else {
				throw new Exception("Class Not Found for input json!");
			}
		} catch (Throwable th) {
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(th);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entities);
	}

	@RequestMapping(path = "/restart", method = RequestMethod.GET)
	public void restart() {
		Thread restartThread = new Thread(() -> {
			try {
				Thread.sleep(1000);
				DynamicModelApplication.restart();
			} catch (InterruptedException ignored) {
			}
		});
		restartThread.setDaemon(false);
		restartThread.start();
	}

	@RequestMapping(path = "/getAll", method = RequestMethod.GET)
	public ResponseEntity<Object> getAll(String cls) {
		Object result = null;
		if (!IUtils.isNullOrEmpty(cls)) {
			Class<?> clz = IUtils.loadClass(cls);
			result = Utils.loadRepoGetAllList(clz);
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}

	@RequestMapping(path = "/getOne", method = RequestMethod.GET)
	public ResponseEntity<Object> getOne(String cls, Long id) {
		Object result = null;
		if (!IUtils.isNullOrEmpty(cls)) {
			Class<?> clz = IUtils.loadClass(cls);
			result = Utils.loadRepoGetById(clz, id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
