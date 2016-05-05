package com.twm.ips.web.departure.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.twm.ips.web.departure.HelloDeparture;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;
import com.twm.ips.web.service.MyService;

@Component
public class HelloDepartureImpl implements HelloDeparture {
	
	private Logger log  = Logger.getLogger(HelloDepartureImpl.class);
	private final AtomicLong counter = new AtomicLong();

	@Override
    public  HelloResponse helloRedis( HelloRequest hello) throws Exception {
		HelloResponse response = new HelloResponse();
		response.setHelloResponseID(counter.getAndIncrement());
		response.setResult("SUCCESS");
        return response;
    }
	
	@Override
    public HelloResponse helloRedisWithID(HelloRequest hello,String id) throws Exception {
		HelloResponse response = new HelloResponse();
		response.setHelloResponseID(counter.getAndIncrement());
		response.setResult("SUCCESS");
        return response;
    }

	@Override
	public HelloResponse helloRedisWithIDAndFunctionName(HelloRequest hello,
			String id, String functionName) throws Exception {
		HelloResponse response = new HelloResponse();
		response.setHelloResponseID(counter.getAndIncrement());
		response.setResult("SUCCESS");
		return response;
	}

}
