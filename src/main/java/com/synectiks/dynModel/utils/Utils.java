package com.synectiks.dynModel.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

import com.synectiks.commons.entities.SourceEntity;
import com.synectiks.commons.utils.IUtils;
import com.synectiks.commons.utils.IUtils.CTypes;
import com.synectiks.dynModel.DynamicModelApplication;
import com.synectiks.dynModel.handlers.ClassConfig;
import com.synectiks.commons.entities.PSqlEntity;

/**
 * @author Rajesh Upadhyay
 */
public class Utils {

	private static final Logger logger = LoggerFactory.getLogger(Utils.class);

	public static final String CLASSPATH = System.getProperty("java.class.path");
	public static final String UNIC_KEY = "uniqueKeysList";
	public static final String UNIC_METHOD = "getUniqueObject";
	public static final String pkg = "com.synectiks.dynModel.models";
	public static final String src = "src/main/java";
	public static final String dest = "target/classes";

	private static final String input = "{\n" +
			"	dir: {\n" + 
			"		\"uniqueKeysList\": [" +
			"			\"drive\", \"serial\", \"fullPath\"" +
			"		],\n" +
			"		\"drive\": \"D\",\n" + 
			"		\"serial\": \"56DC-72DB\",\n" +
			"		\"fullPath\": \"D:\\\\Projects\",\n" +
			"		\"dirs\": [\n" +
			"			{\n" +
			"				\"modifiedAt\": \"18-Nov-19  12:42 PM\",\n" +
			"				\"name\": \".\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"18-Nov-19  12:42 PM\",\n" +
			"				\"name\": \"..\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"13-Jun-19  11:22 AM\",\n" +
			"				\"name\": \"Aws-Git\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"06-Apr-18  06:09 PM\",\n" +
			"				\"name\": \"Docs\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"01-Mar-19  12:16 PM\",\n" +
			"				\"name\": \"girnarsoft-react-native\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"26-Dec-19  12:53 PM\",\n" +
			"				\"name\": \"Git\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"20-Dec-19  05:33 PM\",\n" +
			"				\"name\": \"LMS\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"02-May-19  08:48 PM\",\n" +
			"				\"name\": \"Samples\"\n" +
			"			}, {\n" +
			"				\"modifiedAt\": \"02-May-19  08:48 PM\",\n" +
			"				\"name\": \"SVN\"\n" +
			"			}\n" +
			"		],\n" +
			"		\"files\": [\n" +
			"			{\n" +
			"				\"size\": \"54,619,076\",\n" +
			"				\"modifiedAt\": \"12-Mar-18  02:14 AM\",\n" +
			"				\"name\": \"AUD-20180312-WA0038.mp3\"\n" +
			"			}, {\n" +
			"				\"size\": \"73\",\n" +
			"				\"modifiedAt\": \"21-Feb-18  03:59 PM\",\n" +
			"				\"name\": \"GitHub-GirnarEmail-Auth-Token.txt\"\n" +
			"			}, {\n" +
			"				\"size\": \"221\",\n" +
			"				\"modifiedAt\": \"11-Jun-19  06:11 PM\",\n" +
			"				\"name\": \"github-recovery-codes.txt\"\n" +
			"			}\n" +
			"		],\n" +
			"		\"fileCount\": \"3\",\n" +
			"		\"filesSize\": \"54,619,370\",\n" +
			"		\"dirCount\": \"9\",\n" +
			"		\"freeSpace\": \"51,751,751,680\"\n" +
			"	}\n" +
			"}";

	public static void main(String[] args) {
		//java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
		try {
			Class<SourceEntity> cls = SourceEntity.class;
			if (cls.isAnnotationPresent(Document.class)) {
				Document doc = cls.getAnnotation(Document.class);
				logger.info("index name: " + doc.indexName());
				logger.info("index type: " + doc.type());
			}
//			logger.info("54619076: " + nf.parse("54619076"));
//			logger.info("54619076.06: " + nf.parse("54619076.06"));
//			logger.info("54,619,076: " + nf.parse("54,619,076"));
//			logger.info("54,619,076.06: " + nf.parse("54,619,076.06"));
//			logger.info("Long: " + nf.parse("54619076").longValue());
//			logger.info("Int: " + nf.parse("54619076").intValue());
//			logger.info("double: " + nf.parse("54619076.06").doubleValue());
//			logger.info("float: " + nf.parse("54619076.06").floatValue());
//			logger.info("long: " + nf.parse("54619076.06").longValue());
//			logger.info("int: " + nf.parse("54619076.06").intValue());
//			logger.info("short: " + nf.parse("54619076.06").shortValue());
//			logger.info("Long: " + nf.parse("54,619,076").longValue());
//			logger.info("int: " + nf.parse("54,619,076").intValue());
//			logger.info("short: " + nf.parse("54,619,076").shortValue());
//			logger.info("double: " + nf.parse("54,619,076.06").doubleValue());
//			logger.info("float: " + nf.parse("54,619,076.06").floatValue());
//			logger.info("long: " + nf.parse("54,619,076.06").longValue());
//			logger.info("int: " + nf.parse("54,619,076.06").intValue());
//			logger.info("short: " + nf.parse("54,619,076.06").shortValue());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//Date d = new Date();
		//ThaiBuddhistDate td = ThaiBuddhistDate.now();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//SimpleDateFormat sdful = new SimpleDateFormat("yyyy-MM-dd G z");
		//logger.info(sdf.format(d));
		//logger.info(sdful.format(d));
		//logger.info(td.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		//logger.info(td.format(DateTimeFormatter.ofPattern("yyyy-MM-dd G")));
		
		//logger.info("1.0: " + "1.0".matches("^[\\d\\.,]+$"));
		//logger.info("1,000: " + "1,000".matches("^[\\d\\.,]+$"));
		//logger.info("a100: " + "a100".matches("^[\\d\\.,]+$"));
		//logger.info("12,005.56.565: " + "12,005.56.565".matches("^[\\d\\.,]+$"));
		//logger.info("4,45,454: " + "4,45,454".matches("^[\\d\\.,]+$"));
		System.exit(0);
		//boolean success = createDynModels("Hello", pkg);
		JSONObject json = IUtils.getJSONObject(input);
		Class<?> lst = createDynModels(json, true);
		Object obj = putDataIntoModel(json);
		logger.info("Object: " + IUtils.getStringFromValue(obj));
		if (!IUtils.isNull(lst)) {
			try {
				Class<?>[] strParam = new Class[] { String.class };
				logger.info("Class: " + Class.forName(pkg + "." + "Dir"));
				logger.info("Class: " + Class.forName(pkg + "." + "Dirs"));
				logger.info("Class: " + Class.forName(pkg + "." + "Files"));
				Class<?> cls = Class.forName(pkg + "." + "Hello");
				Method method = cls.getDeclaredMethod("execute", strParam);
				Object res = method.invoke(cls.newInstance(), new String("mkdir"));
				logger.info("Res: " + res);
				// Register model into spring bean
				DynamicModelApplication.registerBean(
						Class.forName(pkg + "." + "HelloRepository"));
			} catch (ClassNotFoundException e) {
				logger.error("Class not found: " + e);
			} catch (NoSuchMethodException e) {
				logger.error("No such method: " + e);
			} catch (IllegalAccessException e) {
				logger.error("Illegal access: " + e);
			} catch (InvocationTargetException e) {
				logger.error("Invocation target: " + e);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	public static Object putDataIntoModel(JSONObject json) {
		if (!IUtils.isNull(json)) {
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = json.opt(key);
				logger.info("Val: " + val);
				CTypes type = IUtils.getValueClassType(val);
				if (type == CTypes.Object) {
					String absName = pkg + "." + StringUtils.capitalize(key);
					try {
						Class<?> cls = Class.forName(absName);
						return IUtils.getObjectFromValue(val.toString(), cls);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	public static Object createAndSaveInnerModels(JSONObject json) {
		if (!IUtils.isNull(json)) {
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				JSONObject val = json.optJSONObject(key);
				logger.info(key + ", Val: " + val);
				CTypes type = IUtils.getValueClassType(val);
				if (type == CTypes.Object) {
					Class<?> cls = getJsonValueJavaClass(key, val);
					Map<String, Object> inCls = checkForInnerObjects(val);
					//val = updateEntitiesInJson(val, inCls);
					String str = IUtils.getStringFromValue(val);
					Object clsObj = IUtils.getObjectFromValue(str, cls);
					clsObj = updateValuesInJavaObject(clsObj, inCls);
					logger.info("final Object: " + IUtils.getStringFromValue(clsObj));
					return loadRepoAndSave(cls, clsObj, true);
				}
			}
		}
		return null;
	}

	public static Object updateValuesInJavaObject(Object clsObj,
			Map<String, Object> map) {
		if (!IUtils.isNull(clsObj)) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String key = entry.getKey();
				Object val = entry.getValue();
				setFieldValue(clsObj,
						"set" + StringUtils.capitalize(key), val);
			}
		}
		return clsObj;
	}

	public static boolean setFieldValue(Object object, String fieldName, Object fieldValue) {
	    Class<?> clazz = object.getClass();
	    while (clazz != null) {
	        try {
	            Field field = clazz.getDeclaredField(fieldName);
	            field.setAccessible(true);
	            field.set(object, fieldValue);
	            return true;
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return false;
	}

	public static JSONObject updateEntitiesInJson(JSONObject val, Map<String, Object> map) {
		if (!IUtils.isNull(val)) {
			for (Entry<String, Object> entry : map.entrySet()) {
				if (val.has(entry.getKey())) {
					try {
						val.put(entry.getKey(), entry.getValue());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return val;
	}

	public static Map<String, Object> checkForInnerObjects(JSONObject json) {
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
					map.put(key, getJavaObject(key, val));
				} else if (type == CTypes.Array) {
					CTypes tp = IUtils.getArrValType(val);
					if (CTypes.Object == tp) {
						Set<Object> sets = getJavaObjects(key, (JSONArray) val, false);
						map.put(key, sets);
					}
				}
			}
		}
		return map;
	}

	public static Set<Object> getJavaObjects(String key, JSONArray arr, boolean isJson) {
		Set<Object> set = null;
		if (!IUtils.isNull(arr)) {
			set = new HashSet<>();
			for (int i = 0; i < arr.length(); i++) {
				Object jObj = getJavaObject(key, arr.opt(i));
				if (isJson) {
					set.add(IUtils.getJSONObject(IUtils.getStringFromValue(jObj)));
				} else {
					set.add(jObj);
				}
			}
		}
		return set;
	}

	public static Object getJavaObject(String key, Object val) {
		Class<?> dynCls = getJsonValueJavaClass(key, (JSONObject) val);
		Object obj = IUtils.getObjectFromValue(val.toString(), dynCls);
		if (!isIdExistsInObject(obj)) {
			// Check for duplicate
			obj = loadRepoAndSave(dynCls, obj, false);
		}
		return obj;
	}

	public static boolean isIdExistsInObject(Object obj) {
		if (!IUtils.isNull(obj) && obj instanceof PSqlEntity) {
			Long id = ((PSqlEntity) obj).getId();
			if (!IUtils.isNull(id) && id > 0l) {
				return true;
			}
		}
		return false;
	}

	public static Class<?> createDynModels(JSONObject json, boolean forceWrite) {
		if (!IUtils.isNull(json)) {
			@SuppressWarnings("rawtypes")
			Iterator it = json.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object val = json.opt(key);
				logger.info("Val: " + val);
				CTypes type = IUtils.getValueClassType(val);
				if (type == CTypes.Object) {
					return createModelClass(
							StringUtils.capitalize(key), (JSONObject) val, forceWrite);
				}
			}
		}
		return null;
	}

	/**
	 * Method to create java class source path on file system.
	 * @param clsName java class simple name
	 * @return src path in file system.
	 */
	public static String getJavaSrcPath(String clsName) {
		return getSrcPath(pkg + "." + clsName);
	}

	/**
	 * Method to create java class source path on file system.
	 * @param absName java class full name with package.
	 * @return
	 */
	public static String getSrcPath(String absName) {
		return src + "/" + absName.replace(".", "/") + ".java";
	}

	public static Class<?> createModelClass(String clsName,
			JSONObject json, boolean forceWrite) {
		String absName = pkg + "." + clsName;
		String srcPath = getSrcPath(absName);
		if (!IUtils.isFileExists(srcPath) || forceWrite) {
			List<String> keys = IUtils.getJsonKeys(json);
			/*if (!keys.contains("id")) {
				keys.add(0, "id");
			}*/
			createModel(clsName, srcPath, keys, json, forceWrite);
			String repoPath = createRepository(clsName);
			String[] source = new String[] { srcPath , repoPath };
			boolean success = compileAllSources(source);
			if (success) {
				Class<?> cls = IUtils.loadClass(absName);
				DynamicModelApplication.registerBean(cls);
				cls = IUtils.loadClass(absName + "Repository");
				DynamicModelApplication.registerBean(cls);
			}
			logger.info("Compilation res: " + success);
		}
		return IUtils.loadClass(absName);
	}

	public static String createRepository(String clsName) {
		String repoPath = src + "/" + pkg.replace(".", "/") + "/" + clsName
				+ "Repository.java";
		createRepositoryClass(repoPath, clsName);
		return repoPath;
	}

	public static void createModel(String clsName, String srcPath, List<String> keys,
			JSONObject json, boolean forceWrite) {
		try (FileWriter fileWriter = new FileWriter(srcPath, false)) {
			fileWriter.write(getImportHeaders());
			fileWriter.write(getEntity(clsName));
			for (String key : keys) {
				addFieldInModel(fileWriter, clsName, key, json.opt(key), forceWrite);
			}
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	public static void addFieldInModel(FileWriter fileWriter, String cls,
			String key, Object val, boolean forceWrite) throws IOException {
		CTypes type = IUtils.getValueClassType(val);
		/*if ("id".equals(key)) {
			addField(fileWriter, cls, CTypes.Long.name(), key, true);
		} else*/ if (CTypes.Object == type) {
			String clz = StringUtils.capitalize(key);
			createModelClass(clz, (JSONObject) val, forceWrite);
			addField(fileWriter, cls, clz, key/* , false */, false, false, true);
		} else if (CTypes.Array == type) {
			if (UNIC_KEY.equals(key)) {
				addUnicKeysMethod(fileWriter, cls, val);
			} else {
				CTypes tp = IUtils.getArrValType(val);
				String clsName = tp.name();
				if (CTypes.Object == tp) {
					String clz = StringUtils.capitalize(key);
					createModelClass(clz,
							(JSONObject) IUtils.getArrFirstVal(val), forceWrite);
					clsName = clz;
				} else if (CTypes.Array == tp) {
					// TODO: check if this case (Array of Array's) required.
				}
				addField(fileWriter, cls, clsName, key, false/* , false */, true);
			}
		} else if (IUtils.isNull(val) || "null".equalsIgnoreCase((String) val)) {
			addField(fileWriter, cls, type.name(), key/* , false */, true);
		} else {
			addField(fileWriter, cls, type.name(), key);
		}
	}

	private static void addUnicKeysMethod(FileWriter fileWriter,
			String cls, Object val) throws IOException {
		if (!IUtils.isNull(val) && val instanceof JSONArray) {
			JSONArray arr = (JSONArray) val;
			List<String> lst = new ArrayList<>();
			for (int i = 0; i < arr.length(); i++) {
				lst.add(arr.optString(i));
			}
			addUnicKeysMethod(fileWriter, cls, lst);
		}
	}

	public static void addUnicKeysMethod(FileWriter fileWriter,
			String cls, List<String> arr) throws IOException {
		fileWriter.write("\n\n\t@JsonIgnore\n");
		fileWriter.write("\tpublic " + cls + " " + UNIC_METHOD + "() {\n");
		fileWriter.write("\t\t" + cls + " obj = new " + cls + "();\n");
		for (String key : arr) {
			fileWriter.write("\t\tobj.set" +
					StringUtils.capitalize(key) + "(this." + key + ");\n");
		}
		fileWriter.write("\t\treturn obj;\n");
		fileWriter.write("\t}\n\n");
	}

	public static String getEntity(String clsName) {
		StringBuilder sb = new StringBuilder();
		sb.append("@Entity\n" + "@Table(name = \""+ clsName +"\")\n");
		sb.append("public class " + clsName + " extends PSqlEntity {\n\n");
		sb.append("\tprivate static final long serialVersionUID = -1;\n");
		return sb.toString();
	}

	public static String getImportHeaders() {
		StringBuilder sb = new StringBuilder();
		sb.append("package " + pkg + ";\n\n");
		sb.append("import java.util.Date;\n" +
				"import java.util.Set;\n" +
				"\n" + 
				"import javax.persistence.CascadeType;\n" +
				"import javax.persistence.Column;\n" +
				"import javax.persistence.ElementCollection;\n" +
				"import javax.persistence.Entity;\n" +
				"import javax.persistence.FetchType;\n" +
				"import javax.persistence.GeneratedValue;\n" +
				"import javax.persistence.GenerationType;\n" +
				"import javax.persistence.Id;\n" +
				"import javax.persistence.ManyToMany;\n" +
				"import javax.persistence.ManyToOne;\n" +
				"import javax.persistence.SequenceGenerator;\n" +
				"import javax.persistence.Table;\n\n" +
				"import com.fasterxml.jackson.annotation.JsonIgnore;\n" +
				"import com.synectiks.commons.entities.PSqlEntity;\n" +
				"\n");
		return sb.toString();
	}

	public static boolean createDynModels(String fileName, String pkg) {
		boolean success = false;
		String srcPath = src + "/" + pkg.replace(".", "/") + "/" + fileName + ".java";
		createModelClass(srcPath, fileName);
		String repoPath = src + "/" + pkg.replace(".", "/") + "/" + fileName
				+ "Repository.java";
		createRepositoryClass(repoPath, fileName);
		String[] source = new String[] { srcPath, repoPath };
		success = compileAllSources(source);
		return success;
	}

	public static boolean compileAllSources(String[] source) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null,
				null, null);
		// set class file destination
		File file = new File(dest);
		file.mkdirs();

		//ClassLoader loader = ClassUtils.getDefaultClassLoader();
		String[] compileOptions = new String[] { "-classpath", CLASSPATH };
		Iterable<String> compilationOptions = Arrays.asList(compileOptions);
		logger.info("Dest: " + file.getAbsolutePath());
		try {
			stdFileManager.setLocation(StandardLocation.CLASS_OUTPUT,
					Arrays.asList(file));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		Iterable<? extends JavaFileObject> srcFiles = stdFileManager
				.getJavaFileObjects(source);
		CompilationTask task = compiler.getTask(null, stdFileManager, diagnostics,
				compilationOptions, null, srcFiles);
		boolean success = task.call();
		logger.info("Success: " + success);
		logger.info("Diagnostic: ");
		for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
			logger.info("Code: " + diagnostic.getCode());
			logger.info("Kind: " + diagnostic.getKind());
			logger.info("Pos: " + diagnostic.getPosition());
			logger.info("StartPos: " + diagnostic.getStartPosition());
			logger.info("EndPos: " + diagnostic.getEndPosition());
			logger.info("Source: " + diagnostic.getSource());
			logger.info("Msg: " + diagnostic.getMessage(null));
		}
		return success;
	}

	public static void createRepositoryClass(String repoPath, String fileName) {
		try (FileWriter fileWriter = new FileWriter(repoPath, false)) {
			fileWriter.write("package com.synectiks.dynModel.models;\n\n");
			fileWriter.write("import org.springframework.data.jpa.repository.JpaRepository;\n");
			fileWriter.write("import org.springframework.stereotype.Repository;\n");
			fileWriter.write("\n@Repository\n");
			fileWriter.write("public interface " + fileName + "Repository extends "
					+ "JpaRepository<" + fileName + ", Long> {\n\n");
			fileWriter.write("}");
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	}

	public static void createModelClass(String srcPath, String fileName) {
		String methodName = "execute";
		String parameterName = "strParam";
		// String destPath = dest
		// Creates DynamicTestClass.java file
		try (FileWriter fileWriter = new FileWriter(srcPath, false)) {
			fileWriter.write("package " + pkg + ";\n\n");
			fileWriter.write("import java.io.Serializable;\n");
			fileWriter.write("import java.util.Set;\n" +
					"\n" + 
					"import javax.persistence.CascadeType;\n" +
					"import javax.persistence.Column;\n" +
					"import javax.persistence.ElementCollection;\n" +
					"import javax.persistence.Entity;\n" +
					"import javax.persistence.FetchType;\n" +
					"import javax.persistence.GeneratedValue;\n" +
					"import javax.persistence.GenerationType;\n" +
					"import javax.persistence.Id;\n" +
					"import javax.persistence.ManyToMany;\n" +
					"import javax.persistence.ManyToOne;\n" +
					"import javax.persistence.SequenceGenerator;\n" +
					"import javax.persistence.Table;\n" +
					"\n");
			fileWriter.write("@Entity\n" + 
					"@Table(name = \""+ fileName +"\")\n");
			fileWriter.write("public class " + fileName + " implements Serializable {\n\n");
			fileWriter.write("\tprivate static final long serialVersionUID = -1;\n");
			addField(fileWriter, fileName, "long", "id", true);
			String clsType = "String";
			String fldName = "msg";
			addField(fileWriter, fileName, clsType, fldName);
			fileWriter.write(
					"\n\tpublic String " + methodName + "(String " + parameterName + ") {\n");
			fileWriter.write("\t\tSystem.out.println(\" Testing\");\n");
			fileWriter.write("\t\treturn " + parameterName + " + \" is dump\";\n\t}");
			fileWriter.write("\n}");
			fileWriter.flush();
			fileWriter.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}

	/*private static void addField(FileWriter fileWriter, String cls,
			String clsType, String fldName) throws IOException {
		addField(fileWriter, cls, clsType, fldName, false);
	}*/

	public static void addField(FileWriter fileWriter, String cls,
			String clsType, String fldName/* , boolean isPrimary */) throws IOException {
		addField(fileWriter, cls, clsType, fldName/* , isPrimary */, false);
	}

	public static void addField(FileWriter fileWriter, String cls, String clsType,
			String fldName/* , boolean isPrimary */, boolean isNullable) throws IOException {
		addField(fileWriter, cls, clsType, fldName/* , isPrimary */, isNullable, false);
	}

	public static void addField(FileWriter fileWriter, String cls, String clsType,
			String fldName/* , boolean isPrimary */, boolean isNullable,
			boolean isArray) throws IOException {
		addField(fileWriter, cls, clsType, fldName/* , isPrimary */,
				isNullable, isArray, false);
	}

	public static void addField(FileWriter fileWriter, String cls, String clsType,
			String fldName/* , boolean isPrimary */, boolean isNullable,
			boolean isArray, boolean isInnerClass) throws IOException {
		addField(fileWriter, cls, clsType, fldName/* , isPrimary */,
				isNullable, isArray, isInnerClass, 0, null);
	}

	public static void addField(FileWriter fileWriter, String cls, String clsType,
			String fldName/* , boolean isPrimary */, boolean isNullable,
			boolean isArray, boolean isInnerClass, int len, String defVal) throws IOException {
		String type = addClassField(fileWriter, cls, "private",
				clsType, fldName/* , isPrimary */, isNullable,
				isArray, isInnerClass, len, defVal);
		addGetMethod(fileWriter, "public", type, fldName);
		addSetMethod(fileWriter, "public", type, fldName);
	}

	public static String addClassField(FileWriter fileWriter, String cls, String acModi,
			String clsType, String fldName, /* boolean isPrimary, */ boolean isNullable,
			boolean isArray, boolean isInnerClass, int len, String defVal) throws IOException {
		logger.info("Add field:\n" + cls + ", " + clsType + ", " + fldName
				+ ", " + isNullable + ", " + isArray + ", " + isInnerClass);
		/*if (isPrimary) {
			fileWriter.write("\tprivate static final String ATTR_ID_SEQ = \"psqlDyn" + cls + "Seq\";\n\n");
			fileWriter.write("\t@Id\n" + 
					"	@SequenceGenerator(name = ATTR_ID_SEQ,\n" + 
					"		sequenceName = ATTR_ID_SEQ,\n" + 
					"		initialValue = 1,\n" + 
					"		allocationSize = 1)\n" + 
					"	@GeneratedValue(strategy = GenerationType.SEQUENCE,\n" + 
					"		generator = ATTR_ID_SEQ)\n");
		}*/
		clsType = setColumnAnnotation(fileWriter, isNullable, clsType, len);
		if (isArray) {
			clsType = addArrAnnotation(fileWriter, clsType, cls);
		}
		if (isInnerClass) {
			fileWriter.write("\n\t@ManyToOne(cascade = CascadeType.PERSIST)\n");
			//Below line commented because its causing issue in mappings.
			//clsType = cls;
		}
		fileWriter.write("\t" + acModi + " " + clsType + " " + fldName);
		if (!IUtils.isNullOrEmpty(defVal)) {
			fileWriter.write(" = \"" + defVal + "\"");
		}
		fileWriter.write(";\n");
		return clsType;
	}

	public static String addArrAnnotation(FileWriter fileWriter,
			String clsType, String cls) throws IOException {
		if (CTypes.isNativeType(clsType)) {
			fileWriter.write("\n\t@ElementCollection(targetClass = " + clsType + ".class)");
		} else {
			if (CTypes.Object.name().equals(clsType) &&
					!IUtils.isNullOrEmpty(cls)) {
				clsType = cls;
			}
			fileWriter.write("\n\t@ManyToMany(targetEntity = " + clsType
					+ ".class,\n\t\t\tfetch = FetchType.EAGER, cascade = CascadeType.ALL)\n");
		}
		clsType = "Set<" + clsType + ">";
		return clsType;
	}

	public static String setColumnAnnotation(FileWriter fileWriter,
			boolean isNullable, String clsType, int len) throws IOException {
		if (!isNullable && len <= 0 && !CTypes.Text.name().equals(clsType)) {
			return clsType;
		}
		fileWriter.write("\n\t@Column(");
		boolean fAdded = false;
		if (CTypes.Text.name().equals(clsType)) {
			fileWriter.write("columnDefinition = \"TEXT\"");
			clsType = CTypes.String.name();
			fAdded = true;
		}
		if (isNullable) {
			if (fAdded) {
				fileWriter.write(", ");
			} else {
				fAdded = true;
			}
			fileWriter.write("nullable = true");
		}
		if (len > 0 && CTypes.String.name().equals(clsType)) {
			if (fAdded) {
				fileWriter.write(", ");
			}
			fileWriter.write("length = " + len);
		}
		fileWriter.write(")\n");
		return clsType;
	}

	public static void addGetMethod(FileWriter fileWriter, String acesModifier,
			String clsType, String fldName) throws IOException {
		fileWriter.write("\n\t" + acesModifier + " " + clsType);
		if (clsType != null && "boolean".equals(clsType.toLowerCase())) {
			fileWriter.write(" is");
		} else {
			fileWriter.write(" get");
		}
		fileWriter.write(StringUtils.capitalize(fldName) + "() {\n");
		fileWriter.write("\t\treturn " + fldName + ";\n");
		fileWriter.write("\t}\n");
	}

	public static void addSetMethod(FileWriter fileWriter, String acesModifier,
			String clsType, String fldName) throws IOException {
		fileWriter.write("\n\t" + acesModifier + " void " + "set");
		fileWriter.write(StringUtils.capitalize(fldName) + "(");
		fileWriter.write(clsType + " " + fldName + ") {\n");
		fileWriter.write("\t\tthis." + fldName + " = " + fldName + ";\n");
		fileWriter.write("\t}\n");
	}

	public static void addSetStrDate(FileWriter fileWriter, String dformat,
			String clsType, String fldName) throws IOException {
		fileWriter.write("\n\tpublic void " + "set");
		fileWriter.write(StringUtils.capitalize(fldName) + "(");
		fileWriter.write("String " + fldName + ") {\n");
		fileWriter.write("\t\tjava.text.DateFormat df = "
				+ "new java.text.SimpleDateFormat(\"" + dformat + "\");\n");
		fileWriter.write("\t\ttry {\n");
		fileWriter.write("\t\t\tthis." + fldName + " = df.parse(" + fldName + ");\n");
		fileWriter.write("\t\t} catch (java.text.ParseException e) {\n");
		fileWriter.write("\t\t\t// ignore it\n");
		fileWriter.write("\t\t}\n");
		fileWriter.write("\t}\n");
	}

	public static void addSetStrNum(FileWriter fileWriter,
			String clsType, String name) throws IOException {
		fileWriter.write("\n\tpublic void " + "set");
		fileWriter.write(StringUtils.capitalize(name) + "(");
		fileWriter.write("String " + name + ") {\n");
		fileWriter.write("\t\tjava.text.NumberFormat nf ="
				+ " java.text.NumberFormat.getInstance();\n");
		fileWriter.write("\t\ttry {\n");
		fileWriter.write("\t\t\tthis." + name + " = nf.parse(" + name + ")");
		if (CTypes.Double.name().equals(clsType)) {
			fileWriter.write(".doubleValue();\n");
		} else if (CTypes.Long.name().equals(clsType)) {
			fileWriter.write(".longValue();\n");
		} else {
			fileWriter.write(".intValue();\n");
		}
		fileWriter.write("\t\t} catch (java.text.ParseException e) {\n");
		fileWriter.write("\t\t\t// ignore it\n");
		fileWriter.write("\t\t}\n");
		fileWriter.write("\t}\n");
	}

	public static class CompileSourceInMemory {
		public static void compileSourceInMemory() throws IOException {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			out.println("package com.synectiks.dynModel.Models;\n");
			out.println("public class HelloWorld {");
			out.println("  public static void main(String args[]) {");
			out.println("    System.out.println(\"This is in another java file\");");
			out.println("  }");
			out.println("}");
			out.close();
			JavaFileObject file = new JavaSourceFromString(
					"com.synectiks.dynModel.Models.HelloWorld", writer.toString());

			Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
			CompilationTask task = compiler.getTask(null, null, diagnostics, null, null,
					compilationUnits);

			boolean success = task.call();
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
				logger.info("Code: " + diagnostic.getCode());
				logger.info("Kind: " + diagnostic.getKind());
				logger.info("Pos: " + diagnostic.getPosition());
				logger.info("StartPos: " + diagnostic.getStartPosition());
				logger.info("EndPos: " + diagnostic.getEndPosition());
				logger.info("Source: " + diagnostic.getSource());
				logger.info("Msg: " + diagnostic.getMessage(null));

			}
			logger.info("Success: " + success);

			if (success) {
				try {
					Class.forName("HelloWorld")
							.getDeclaredMethod("main", new Class[] { String[].class })
							.invoke(null, new Object[] { null });
				} catch (ClassNotFoundException e) {
					logger.error("Class not found: " + e);
				} catch (NoSuchMethodException e) {
					logger.error("No such method: " + e);
				} catch (IllegalAccessException e) {
					logger.error("Illegal access: " + e);
				} catch (InvocationTargetException e) {
					logger.error("Invocation target: " + e);
				}
			}
		}
	}

	public static class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		JavaSourceFromString(String name, String code) {
			super(URI.create(
					"string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
					Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

	/**
	 * Method to load repository class and save entities.
	 * @param dynCls
	 * @param entity
	 * @param save set true if you want to update existing record.
	 * @return 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object loadRepoAndSave(Class<?> dynCls, Object entity, boolean save) {
		if (!IUtils.isNull(entity)) {
			String repo = dynCls.getPackage().getName()
					+ "." + dynCls.getSimpleName() + "Repository";
			Class<?> repoCls = IUtils.getClass(repo);
			Object repository = DynamicModelApplication.getBean(repoCls);
			if (repository instanceof JpaRepository) {
				JpaRepository tmpRepo = ((JpaRepository) repository);
				if (!isIdExistsInObject(entity)) {
					Object exObj = getExampleObj(dynCls, entity);
					List lst = tmpRepo.findAll(Example.of(exObj));
					if (!IUtils.isNull(lst) && lst.size() > 0) {
						Object oldObj = lst.get(0);
						// Found duplicate row
						logger.warn("Duplicate:: " + IUtils.getStringFromValue(oldObj));
						if (hasUnicObjMethod(dynCls, UNIC_METHOD)) {
							BeanUtils.copyProperties(entity, oldObj, "id");
							logger.warn("Updated:: " + IUtils.getStringFromValue(oldObj));
						}
						// always replace matched object as entity to update it.
						entity = oldObj;
					}
				}
				if (save) {
					entity = tmpRepo.save(entity);
				}
			}
			logger.info("Saved: " + IUtils.getStringFromValue(entity));
			return entity;
		}
		return null;
	}

	/**
	 * Method to return list of all object from repository
	 * @param <T>
	 * @param clz
	 * @return
	 */
	public static <T> List<T> loadRepoGetAllList(Class<T> clz) {
		List<T> entities = null;
		if (!IUtils.isNull(clz)) {
			String repo = clz.getPackage().getName() + "." + clz.getSimpleName() + "Repository";
			Class<?> repoCls = IUtils.getClass(repo);
			Object repository = DynamicModelApplication.getBean(repoCls);
			if (repository instanceof JpaRepository) {
				JpaRepository tmpRepo = ((JpaRepository) repository);
				entities = tmpRepo.findAll();
			}
		}
		logger.info("Item: " + IUtils.getStringFromValue(entities));
		return entities;
	}

	/**
	 * Method to return list of all object from repository
	 * @param <T>
	 * @param clz
	 * @return
	 */
	public static <T> T loadRepoGetById(Class<T> clz, Object id) {
		T entity = null;
		if (!IUtils.isNull(clz)) {
			String repo = clz.getPackage().getName() + "." + clz.getSimpleName() + "Repository";
			Class<?> repoCls = IUtils.getClass(repo);
			Object repository = DynamicModelApplication.getBean(repoCls);
			if (repository instanceof JpaRepository) {
				JpaRepository tmpRepo = ((JpaRepository) repository);
				entity = (T) tmpRepo.findById(id);
			}
		}
		logger.info("Item: " + IUtils.getStringFromValue(entity));
		return entity;
	}

	/**
	 * Method to get entity ExampleMatcher object.
	 * @param dynCls
	 * @param entity
	 * @return
	 */
	private static Object getExampleObj(Class<?> dynCls, Object entity) {
		Object exObj = null;
		if (hasUnicObjMethod(dynCls, UNIC_METHOD)) {
			exObj = getMethodResult(dynCls, entity, UNIC_METHOD);
		} else {
			try {
				exObj = dynCls.newInstance();
				BeanUtils.copyProperties(entity, exObj);
			} catch (InstantiationException | IllegalAccessException e) {
				// ignore
			}
		}
		logger.info("Example Object: " + IUtils.getStringFromValue(exObj));
		return exObj;
	}

	/**
	 * Method to invoke a method to get result object.
	 * @param dynCls
	 * @param entity
	 * @param mName
	 * @return
	 */
	private static Object getMethodResult(Class<?> dynCls, Object entity,
			String mName) {
		try {
			Method method = dynCls.getDeclaredMethod(mName);
			return method.invoke(entity);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Method to check if class has unique object method for ExampleMatcher
	 * @param dynCls
	 * @param mName 
	 * @return
	 */
	private static boolean hasUnicObjMethod(Class<?> dynCls, String mName) {
		try {
			Method method = dynCls.getDeclaredMethod(mName);
			if (!IUtils.isNull(method)) {
				return true;
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// ignore
		}
		return false;
	}

	public static Class<?> getJsonValueJavaClass(String key, JSONObject val) {
		Class<?> cls = null;
		String absName = pkg + "." + StringUtils.capitalize(key);
		try {
			cls = Class.forName(absName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return cls;
	}

	public static Object getJsonValueJavaObject(String key, JSONObject val) {
		return IUtils.getObjectFromValue(
				val.toString(), getJsonValueJavaClass(key, val));
	}

	public static List<ClassConfig> getClsConfigList(JSONObject json) {
		List<ClassConfig> classes = new ArrayList<>();
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
					ClassConfig cls = IUtils.getObjectFromValue(jObj.toString(), ClassConfig.class);
					logger.info("Creating new Class for key: " + key);
					cls.setClassName(StringUtils.capitalize(key));
					classes.add(cls);
				} else {
					logger.warn("Property: " + key + " ignored!");
				}
			}
		}
		return classes;
	}

	/**
	 * Delete newly created files if we failed to create.
	 * @param innerClses
	 */
	public static void deleteDynFiles(String... innerClses) {
		deleteDynFiles(Arrays.asList(innerClses));
	}

	/**
	 * Delete newly created files if we failed to create.
	 * @param innerClses
	 */
	public static void deleteDynFiles(List<String> innerClses) {
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
}
