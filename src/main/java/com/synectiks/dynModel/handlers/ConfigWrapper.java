/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 */
public class ConfigWrapper {

	private static final Logger logger = LoggerFactory.getLogger(ConfigWrapper.class);

	private boolean overwrite;

	private List<ClassConfig> classes;

	public ConfigWrapper(JSONObject json, boolean overwrite) {
		this.overwrite = overwrite;
		this.setClassesList(json);
	}

	private void setClassesList(JSONObject json) {
		setClasses(Utils.getClsConfigList(json));
	}

	public List<ClassConfig> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassConfig> classes) {
		this.classes = classes;
	}

	public List<String> writeClasses() {
		List<String> clzes = new ArrayList<>();
		List<String> srcs = new ArrayList<>();
		//List<String> absCls = new ArrayList<>();
		for (ClassConfig config : classes) {
			String srcPath = config.writeClass(overwrite);
			clzes.add(srcPath);
			//absCls.add(Utils.pkg + "." + config.getClassName());
			String repoPath = Utils.createRepository(config.getClassName());
			srcs.add(srcPath);
			srcs.add(repoPath);
			//absCls.add(Utils.pkg + "." + config.getClassName() + "Repository");
		}
		boolean result = Utils.compileAllSources(srcs.toArray(new String[srcs.size()]));
		logger.info("Compilation success: " + result);
//		if (result) {
//			for (String clz : absCls) {
//				Class<?> cls = IUtils.loadClass(clz);
//				DynamicModelApplication.registerBean(cls);
//			}
//		}
		return clzes;
	}

}
