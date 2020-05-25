/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 *
 */
public class ClassConfig {

	private String className;
	private List<FieldConfig> fields;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<FieldConfig> getFields() {
		return fields;
	}

	public void setFields(List<FieldConfig> fields) {
		this.fields = fields;
	}

	public String writeClass(boolean overwrite) {
		String srcPath = Utils.getJavaSrcPath(className);
		if (!IUtils.isFileExists(srcPath) || overwrite) {
			try (FileWriter fileWriter = new FileWriter(srcPath, false)) {
				fileWriter.write(Utils.getImportHeaders());
				fileWriter.write(Utils.getEntity(className));
				List<String> unKeys = new ArrayList<>();
				for (FieldConfig field : fields) {
					if (field.getIsCompositeKeyMem()) {
						unKeys.add(field.getName());
					}
					field.write(fileWriter);
				}
				if (!unKeys.isEmpty()) {
					Utils.addUnicKeysMethod(fileWriter, className, unKeys);
				}
				fileWriter.write("}");
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return srcPath;
	}
}
