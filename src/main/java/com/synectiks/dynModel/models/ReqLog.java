package com.synectiks.dynModel.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ReqLog")
public class ReqLog extends PSqlEntity {

	private static final long serialVersionUID = -6392485475675005674L;

	private String reqRemoteAddr;
	private String reqMethod;
	private String reqUri;
	private String reqQueryStr;
	@Column(columnDefinition = "TEXT")
	private String reqHeaders;
	private String reqSessionId;
	@Column(columnDefinition = "TEXT")
	private String reqContent;
	private String reqContentType;
	private String reqEncoding;
	private String resStatus;
	@Column(columnDefinition = "TEXT")
	private String resHeaders;
	@Column(columnDefinition = "TEXT")
	private String resContent;
	private String resContentType;
	private String resEncoding;
	private int reqContLen;
	private int resContLen;

	public String getReqRemoteAddr() {
		return reqRemoteAddr;
	}

	public void setReqRemoteAddr(String reqRemoteAddr) {
		this.reqRemoteAddr = reqRemoteAddr;
	}

	public String getReqMethod() {
		return reqMethod;
	}

	public void setReqMethod(String reqMethod) {
		this.reqMethod = reqMethod;
	}

	public String getReqUri() {
		return reqUri;
	}

	public void setReqUri(String reqUri) {
		this.reqUri = reqUri;
	}

	public String getReqQueryStr() {
		return reqQueryStr;
	}

	public void setReqQueryStr(String reqQueryStr) {
		this.reqQueryStr = reqQueryStr;
	}

	public String getReqHeaders() {
		return reqHeaders;
	}

	public void setReqHeaders(String reqHeaders) {
		this.reqHeaders = reqHeaders;
	}

	public String getReqSessionId() {
		return reqSessionId;
	}

	public void setReqSessionId(String reqSessionId) {
		this.reqSessionId = reqSessionId;
	}

	public String getReqContent() {
		return reqContent;
	}

	public void setReqContent(String reqContent) {
		this.reqContent = reqContent;
	}

	public String getReqContentType() {
		return reqContentType;
	}

	public void setReqContentType(String reqContentType) {
		this.reqContentType = reqContentType;
	}

	public String getReqEncoding() {
		return reqEncoding;
	}

	public void setReqEncoding(String reqEncoding) {
		this.reqEncoding = reqEncoding;
	}

	public String getResStatus() {
		return resStatus;
	}

	public void setResStatus(String resStatus) {
		this.resStatus = resStatus;
	}

	public String getResHeaders() {
		return resHeaders;
	}

	public void setResHeaders(String resHeaders) {
		this.resHeaders = resHeaders;
	}

	public String getResContent() {
		return resContent;
	}

	public void setResContent(String resContent) {
		this.resContent = resContent;
	}

	public String getResContentType() {
		return resContentType;
	}

	public void setResContentType(String resContentType) {
		this.resContentType = resContentType;
	}

	public String getResEncoding() {
		return resEncoding;
	}

	public void setResEncoding(String resEncoding) {
		this.resEncoding = resEncoding;
	}

	public int getReqContLen() {
		return reqContLen;
	}

	public void setReqContLen(int reqContLen) {
		this.reqContLen = reqContLen;
	}

	public int getResContLen() {
		return resContLen;
	}

	public void setResContLen(int resContLen) {
		this.resContLen = resContLen;
	}

}