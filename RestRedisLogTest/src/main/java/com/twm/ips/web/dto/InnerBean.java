package com.twm.ips.web.dto;

import com.twm.ips.log.annotation.LogField;

public class InnerBean {
	
	@LogField
	private String innerBeanGoesField; 
	
	private String content;

	public String getInnerBeanGoesField() {
		return innerBeanGoesField;
	}

	public void setInnerBeanGoesField(String innerBeanGoesField) {
		this.innerBeanGoesField = innerBeanGoesField;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
}
