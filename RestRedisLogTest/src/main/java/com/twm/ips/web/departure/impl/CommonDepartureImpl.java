package com.twm.ips.web.departure.impl;

import org.springframework.stereotype.Component;

import com.twm.ips.web.departure.CommonDeparture;
@Component
public class CommonDepartureImpl<REQ, RESP> implements CommonDeparture<REQ, RESP> {


	@Override
	public RESP helloGenericType(REQ hello,Class<RESP> resp) throws Exception {
		return resp.newInstance();
	}

	@Override
	public RESP helloGenericTypeWithID(REQ hello,Class<RESP> resp, String id) throws Exception {
		return resp.newInstance();
	}

	@Override
	public RESP helloGenericTypeWithIDAndFunctionName(REQ hello,Class<RESP> resp, String id,
			String functionName) throws Exception {
		return resp.newInstance();
	}

}
