package com.twm.ips.log4j.appender.test;

import com.twm.ips.log.annotation.LogClientIP;
import com.twm.ips.log.annotation.LogEncryption;
import com.twm.ips.log.annotation.LogField;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogIgnored;
import com.twm.ips.log.annotation.enums.EncryptionAlogrithm;

public class TestBean {
	@LogID
	private String a;
	
	@LogClientIP
	private String b;
	
	@LogField
	private String c;
	
	@LogField("ChangeNameD")
	private String d;
	
	@LogIgnored
	private String e;
	
	@LogEncryption(EncryptionAlogrithm.MD5)
	private String f;
	
	private String g;
	
	private InnerTestBean bean;

	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public String getB() {
		return b;
	}
	public void setB(String b) {
		this.b = b;
	}
	public String getC() {
		return c;
	}
	public void setC(String c) {
		this.c = c;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getE() {
		return e;
	}
	public void setE(String e) {
		this.e = e;
	}
	public String getF() {
		return f;
	}
	public void setF(String f) {
		this.f = f;
	}
	public String getG() {
		return g;
	}
	public void setG(String g) {
		this.g = g;
	}
	public InnerTestBean getBean() {
		return bean;
	}
	public void setBean(InnerTestBean bean) {
		this.bean = bean;
	}
	
	

}
