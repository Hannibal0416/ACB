package com.twm.ips.web.departure;

import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogRequest;

public interface CommonDeparture<REQ, RESP> {

	
	public RESP helloGenericType(@LogRequest REQ hello,Class<RESP> resp) throws Exception;

	public RESP helloGenericTypeWithID(@LogRequest REQ hello,Class<RESP> resp, @LogID String id) throws Exception;

	public RESP helloGenericTypeWithIDAndFunctionName(@LogRequest REQ hello,Class<RESP> resp, @LogID String id, @LogFunctionName String functionName) throws Exception;
	
	
}
