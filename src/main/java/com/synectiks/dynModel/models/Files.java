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
@Table(name = "Files")
public class Files extends PSqlEntity {

	private static final long serialVersionUID = -1;
	private String size;

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}
	private String modifiedAt;

	public String getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(String modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}