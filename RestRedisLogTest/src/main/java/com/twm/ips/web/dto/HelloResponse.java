package com.twm.ips.web.dto;

import com.twm.ips.log.annotation.LogField;

public class HelloResponse {
	
	private long helloResponseID;
	
	@LogField("responseResult")
	private String result;

	public long getHelloResponseID() {
		return helloResponseID;
	}

	public void setHelloResponseID(long helloResponseID) {
		this.helloResponseID = helloResponseID;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
