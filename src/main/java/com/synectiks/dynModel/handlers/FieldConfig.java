/**
 * 
 */
package com.synectiks.dynModel.handlers;

import java.io.FileWriter;
import java.io.IOException;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.commons.utils.IUtils.CTypes;
import com.synectiks.dynModel.utils.Utils;

/**
 * @author Rajesh Upadhyay
 */
public class FieldConfig {

	private String name;
	private String type;
	private String defaultValue;
	private boolean isArray;
	private String clsName;
	private boolean isNullable;
	private int length;
	private boolean validations;
	private boolean isRequire;
	private long max = -1l;
	private long min = -1l;
	private String regex;
	private String regexMsg;
	private String dateFromat;
	private boolean isCompositeKeyMem;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean getIsArray() {
		return isArray;
	}

	public void setIsArray(boolean isArray) {
		this.isArray = isArray;
	}

	public String getClsName() {
		return clsName;
	}

	public void setClsName(String clsName) {
		this.clsName = clsName;
	}

	public boolean getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(boolean isNull) {
		this.isNullable = isNull;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public boolean getIsRequire() {
		return isRequire;
	}

	public void setIsRequire(boolean isRequire) {
		this.isRequire = isRequire;
	}

	public boolean getValidations() {
		return validations;
	}

	public void setValidations(boolean validation) {
		this.validations = validation;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getRegexMsg() {
		return regexMsg;
	}

	public void setRegexMsg(String regexMsg) {
		this.regexMsg = regexMsg;
	}

	public String getDateFromat() {
		return dateFromat;
	}

	public void setDateFromat(String dateFromat) {
		this.dateFromat = dateFromat;
	}

	public boolean getIsCompositeKeyMem() {
		return isCompositeKeyMem;
	}

	public void setIsCompositeKeyMem(boolean isCompositeKeyMem) {
		this.isCompositeKeyMem = isCompositeKeyMem;
	}

	public void write(FileWriter fileWriter) throws IOException {
		String clsType = createField(fileWriter);
		Utils.addGetMethod(fileWriter, "public", clsType, name);
		Utils.addSetMethod(fileWriter, "public", clsType, name);
		if (CTypes.Date.name().equals(clsType) &&
				!IUtils.isNullOrEmpty(dateFromat)) {
			Utils.addSetStrDate(fileWriter, dateFromat, clsType, name);
		}
		if (CTypes.isNumber(clsType)) {
			Utils.addSetStrNum(fileWriter, clsType, name);
		}
	}

	private String createField(FileWriter fileWriter) throws IOException {
		String clsType = Utils.setColumnAnnotation(
				fileWriter, isNullable, type, length);
		if (isArray) {
			clsType = Utils.addArrAnnotation(fileWriter, clsType, clsName);
		} else if (CTypes.Object.name().equals(type)) {
			fileWriter.write("\n\t@ManyToOne\n");
			clsType = clsName;
		}
		// Add validation Annotations
		if (validations) {
			addValidations(fileWriter);
		}
		fileWriter.write("\tprivate " + clsType + " " + name);
		if (!IUtils.isNullOrEmpty(defaultValue)) {
			fileWriter.write(" = \"" + defaultValue + "\"");
		}
		fileWriter.write(";\n");
		return clsType;
	}

	private void addValidations(FileWriter fileWriter) throws IOException {
		if (isRequire) {
			fileWriter.write("\t@javax.validation.constraints.NotBlank\n");
			fileWriter.write("\t@javax.validation.constraints.NotNull\n");
		}
		if (!IUtils.isNullOrEmpty(regex)) {
			fileWriter.write("\t@javax.validation.constraints.Pattern(regexp = \""
					+ regex + "\"");
			if (!IUtils.isNullOrEmpty(regexMsg)) {
				fileWriter.write(", message = \"" + regexMsg + "\"");
			}
			fileWriter.write(")\n");
		}
		if (min > -1 && max >= -1) {
			fileWriter.write("\t@javax.validation.constraints.Size(min = "
					+ min + ", max = " + max + ")\n");
		} else if (min > -1) {
			fileWriter.write("@javax.validation.constraints.Min(" + min + ")\n");
		} else if (max > -1) {
			fileWriter.write("@javax.validation.constraints.Max(" + max + ")\n");
		}
	}

}
