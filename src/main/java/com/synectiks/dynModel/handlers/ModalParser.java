/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
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
	private static final String UNIC_KEY = "uniqueKeysList";

	private String absName;
	private String clsName;
	private Class<?> cls;
	private JSONObject json;
	private String srcPath;
	private boolean overwrite;
	private List<String> innerClses;
	
	public ModalParser(String clsName, JSONObject json) {
		this(clsName, json, false);
	}
	
	public ModalParser(String clsName, JSONObject json, boolean overwrite) {
		this.clsName = clsName;
		this.absName = Utils.pkg + "." + clsName;
		this.json = json;
		this.srcPath = Utils.getSrcPath(absName);
		this.innerClses = new ArrayList<>();
		createClassModal();
	}

	/**
	 * Method to create class modal into file system.
	 */
	public Class<?> createClassModal() {
		logger.info("Overwrite: " + overwrite + ", exists: " + isClassExists());
		logger.info("absName: " + absName);
		if (overwrite || !isClassExists()) {
			List<String> keys = IUtils.getJsonKeys(json);
			createModel(keys);
			String repoPath = Utils.getSrcPath(absName + "Repository");
			Utils.createRepositoryClass(repoPath, clsName);
			String[] source = new String[] { srcPath , repoPath };
			boolean success = Utils.compileAllSources(source);
			if (success) {
				cls = IUtils.loadClass(absName);
				DynamicModelApplication.registerBean(cls);
				Class<?> repo = IUtils.loadClass(absName + "Repository");
				DynamicModelApplication.registerBean(repo);
			}
			logger.info("Compilation res: " + success);
			this.innerClses.add(getFullClassName());
		} else {
			cls = IUtils.loadClass(absName);
			logger.info("Class found: " + cls.getName());
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
				if (UNIC_KEY.equals(key)) {
					addUnicExampleObj(fileWriter, json.opt(key));
				} else {
					addFieldInModel(fileWriter, key, json.opt(key));
				}
			}
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * Method to add a method to create object of type 
	 * @param fileWriter
	 * @param val
	 * @throws IOException 
	 */
	private void addUnicExampleObj(FileWriter fileWriter, Object val) throws IOException {
		CTypes type = IUtils.getValueClassType(val);
		CTypes tp = IUtils.getArrValType(val);
		if (CTypes.Array == type && CTypes.String == tp) {
			JSONArray arr = (JSONArray) val;
			if (IUtils.isNull(arr) || arr.length() < 1) {
				return;
			}
			fileWriter.write("\n\tpublic " + clsName + " " + Utils.UNIC_METHOD);
			fileWriter.write("() {\n");
			fileWriter.write("\t\t" + clsName + " obj = new " + clsName + "();\n");
			for (int i = 0; i < arr.length(); i++) {
				String key = arr.optString(i);
				if (!IUtils.isNullOrEmpty(key)) {
					fileWriter.write("\t\tobj.set"
							+ StringUtils.capitalize(key) + "(" + key + ");\n");
				}
			}
			fileWriter.write("\t\treturn obj;\n");
			fileWriter.write("\t}\n");
		} else {
			logger.warn("Invalid value for unique keys list. Expected String Array");
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
			if (!parser.getInnerClasses().isEmpty()) {
				this.innerClses.addAll(parser.getInnerClasses());
			}
		} else if (CTypes.Array == type) {
			CTypes tp = IUtils.getArrValType(val);
			String inClsName = tp.name();
			if (CTypes.Object == tp) {
				String clz = StringUtils.capitalize(key);
				ModalParser parser = new ModalParser(clz,
						(JSONObject) IUtils.getArrFirstVal(val), overwrite);
				inClsName = parser.getClassName();
				if (!parser.getInnerClasses().isEmpty()) {
					this.innerClses.addAll(parser.getInnerClasses());
				}
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

	public List<String> getInnerClasses() {
		return this.innerClses;
	}
}
