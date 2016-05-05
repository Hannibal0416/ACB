package com.twm.ips.web.entry.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;
import com.twm.ips.web.entry.HelloEntry;
import com.twm.ips.web.service.MyService;

@Controller
public class HelloEntryImpl implements HelloEntry{
	private Logger log  = Logger.getLogger(HelloEntryImpl.class);
	
	@Autowired
	private MyService service;
	
	@Override
    public  HelloResponse helloRedis( HelloRequest hello) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
        return service.doSomething(hello);
    }
	
	@Override
    public HelloResponse helloRedisWithID(HelloRequest hello,String id) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
        return service.doSomethingWithID(hello, id);
    }

	@Override
	public HelloResponse helloRedisWithIDAndFunctionName(HelloRequest hello,
			String id, String functionName) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
		return service.doSomethingWithID(hello, id);
	}

	@Override
	public HelloResponse helloCommonRequest(HelloRequest hello)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
		return service.helloGenericType(hello);
	}

	@Override
	public HelloResponse helloCommonRequestWithID(HelloRequest hello, String id)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
		return service.helloGenericTypeWithID(hello, id);
	}

	@Override
	public HelloResponse helloCommonRequestWithIDAndFunctionName(
			HelloRequest hello, String id, String functionName)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String helloStr = mapper.writeValueAsString(hello);
		log.info(helloStr);
		return service.helloGenericTypeWithIDAndFunctionName(hello, id, functionName);
	}


	
}
