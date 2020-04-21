/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.commons.utils.IUtils.CTypes;
import com.synectiks.dynModel.DynamicModelApplication;
import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 */
public class ModalParser {

	private static final Logger logger = LoggerFactory.getLogger(ModalParser.class);

	private String absName;
	private String clsName;
	private Class<?> cls;
	private JSONObject json;
	private String srcPath;
	private boolean overwrite;
	
	public ModalParser(String clsName, JSONObject json) {
		this(clsName, json, false);
	}
	
	public ModalParser(String clsName, JSONObject json, boolean overwrite) {
		this.clsName = clsName;
		this.absName = Utils.pkg + "." + clsName;
		this.json = json;
		this.srcPath = Utils.getSrcPath(absName);
		createClassModal();
	}

	/**
	 * Method to create class modal into file system.
	 */
	public Class<?> createClassModal() {
		if (overwrite || !isClassExists()) {
			List<String> keys = IUtils.getJsonKeys(json);
			createModel(keys);
			String repoPath = Utils.getSrcPath(cls.getName() + "Repository");
			Utils.createRepositoryClass(repoPath, getClassName());
			String[] source = new String[] { srcPath , repoPath };
			boolean success = Utils.compileAllSources(source);
			if (success) {
				cls = IUtils.loadClass(absName);
				DynamicModelApplication.registerBean(cls);
				cls = IUtils.loadClass(absName + "Repository");
				DynamicModelApplication.registerBean(cls);
			}
			logger.info("Compilation res: " + success);
		}
		return cls;
	}

	/**
	 * Method to create a Model class object in file system.
	 * @param keys
	 */
	private void createModel(List<String> keys) {
		try (FileWriter fileWriter = new FileWriter(srcPath, false)) {
			fileWriter.write(Utils.getImportHeaders());
			fileWriter.write(Utils.getEntity(clsName));
			for (String key : keys) {
				addFieldInModel(fileWriter, key, json.opt(key));
			}
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * Method to identify vale type and create field into class file.
	 * @param fileWriter
	 * @param key
	 * @param val
	 * @throws IOException
	 */
	private void addFieldInModel(
			FileWriter fileWriter, String key, Object val) throws IOException {
		CTypes type = IUtils.getValueClassType(val);
		if (CTypes.Object == type) {
			String clz = StringUtils.capitalize(key);
			ModalParser parser = new ModalParser(clz, (JSONObject) val, overwrite);
			Utils.addField(fileWriter, clsName,
					parser.getClassName(), key, false, false, true);
		} else if (CTypes.Array == type) {
			CTypes tp = IUtils.getArrValType(val);
			String inClsName = tp.name();
			if (CTypes.Object == tp) {
				String clz = StringUtils.capitalize(key);
				ModalParser parser = new ModalParser(clz, (JSONObject) val, overwrite);
				inClsName = parser.getClassName();
			} else if (CTypes.Array == tp) {
				// TODO: check if this case (Array of Array's) required.
				// If we get such object the parse will fail.
				throw new RuntimeException("Invalid Json, can't parse Array of array");
			} else {
				logger.warn("Found an array type of: " + inClsName);
			}
			Utils.addField(fileWriter, clsName, inClsName, key, false/* , false */, true);
		} else if (IUtils.isNull(val) || "null".equalsIgnoreCase((String) val)) {
			Utils.addField(fileWriter, clsName, type.name(), key/* , false */, true);
		} else {
			Utils.addField(fileWriter, clsName, type.name(), key);
		}
	}

	/**
	 * Checks if java file exists with this name.
	 * @return
	 */
	public boolean isClassExists() {
		return IUtils.isFileExists(srcPath);
	}

	public Class<?> getClazz() {
		return cls;
	}

	public String getClassName() {
		return cls.getSimpleName();
	}

	public String getFullClassName() {
		return cls.getName();
	}
}
