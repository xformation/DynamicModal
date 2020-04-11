package com.synectiks.dynModel.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.synectiks.dynModel.models.PSqlEntity;

@Entity
@Table(name = "Dir")
public class Dir extends PSqlEntity {

	private static final long serialVersionUID = -1;
	private String drive;

	public String getDrive() {
		return drive;
	}

	public void setDrive(String drive) {
		this.drive = drive;
	}
	private String serial;

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
	private String fullPath;

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	@ManyToMany(targetEntity = Dirs.class,
			fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Dirs> dirs;

	public Set<Dirs> getDirs() {
		return dirs;
	}

	public void setDirs(Set<Dirs> dirs) {
		this.dirs = dirs;
	}

	@ManyToMany(targetEntity = Files.class,
			fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Files> files;

	public Set<Files> getFiles() {
		return files;
	}

	public void setFiles(Set<Files> files) {
		this.files = files;
	}
	private String fileCount;

	public String getFileCount() {
		return fileCount;
	}

	public void setFileCount(String fileCount) {
		this.fileCount = fileCount;
	}
	private String filesSize;

	public String getFilesSize() {
		return filesSize;
	}

	public void setFilesSize(String filesSize) {
		this.filesSize = filesSize;
	}
	private String dirCount;

	public String getDirCount() {
		return dirCount;
	}

	public void setDirCount(String dirCount) {
		this.dirCount = dirCount;
	}
	private String freeSpace;

	public String getFreeSpace() {
		return freeSpace;
	}

	public void setFreeSpace(String freeSpace) {
		this.freeSpace = freeSpace;
	}
}