package com.twm.ips.web.dto;

import com.twm.ips.log.annotation.LogClientIP;
import com.twm.ips.log.annotation.LogEncryption;
import com.twm.ips.log.annotation.LogField;
import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogIgnored;
import com.twm.ips.log.annotation.enums.EncryptionAlogrithm;

/**
 * 
 * @author HannibalHan
 * LogID 及 LogFunctionName是必要註釋，若因某些條件下無法放置於Bean物件內，則可將註釋放置於進入功能的參數之中
 */
public class HelloRequest {

	//必要
	@LogID
	private String helloID;
	
	//必要
	@LogFunctionName
	private String functionName;

	@LogClientIP
	private String location;
	
	@LogField
	private String goesField;
	
	@LogField("nameHasBeenChanged")
	private String changeName;
	
	@LogIgnored
	private String secret;
	
	@LogEncryption(EncryptionAlogrithm.MD5)
	private String password;
	
	private String content;

	public String getHelloID() {
		return helloID;
	}

	public void setHelloID(String helloID) {
		this.helloID = helloID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getGoesField() {
		return goesField;
	}

	public void setGoesField(String goesField) {
		this.goesField = goesField;
	}

	public String getChangeName() {
		return changeName;
	}

	public void setChangeName(String changeName) {
		this.changeName = changeName;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}