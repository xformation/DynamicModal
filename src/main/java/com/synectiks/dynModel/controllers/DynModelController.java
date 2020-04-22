package com.synectiks.dynModel.controllers;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.dynModel.DynamicModelApplication;
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

	@Autowired
	private PsqlRepository genRepo;

	//@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(path = "/createModel")
	public ResponseEntity<Object> createModel(HttpServletRequest request,
			@RequestParam String strJson, @RequestParam boolean overwrite) {
		Object entities = null;
		List<String> innerClses = null;
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
			deleteDynFiles(innerClses);
			logger.error(th.getMessage(), th);
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(th);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(entities);
	}

	/**
	 * Delete newly created files if we failed to create.
	 * @param innerClses
	 */
	private void deleteDynFiles(List<String> innerClses) {
		if (!IUtils.isNull(innerClses)) {
			for (String absName : innerClses) {
				try {
					File f = new File(Utils.getSrcPath(absName));
					if (f.exists()) {
						logger.info("Removing file: " + absName);
						f.delete();
					}
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
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
}
