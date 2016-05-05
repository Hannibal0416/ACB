package com.twm.ips.web.departure;

import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;

/**
 * 
 * @author HannibalHan
 * 
 */
public interface HelloDeparture {
	
    public HelloResponse helloRedis(@LogRequest HelloRequest hello) throws Exception ;
	
    public HelloResponse helloRedisWithID(@LogRequest HelloRequest hello,@LogID String id) throws Exception;
    
    public HelloResponse helloRedisWithIDAndFunctionName(@LogRequest HelloRequest hello,@LogID String id,@LogFunctionName String functionName) throws Exception;
}
