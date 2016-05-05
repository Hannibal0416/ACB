package com.twm.ips.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twm.ips.web.departure.CommonDeparture;
import com.twm.ips.web.departure.HelloDeparture;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;
import com.twm.ips.web.service.MyService;

@Service
public class MyServiceImpl implements MyService {

	@Autowired
	private HelloDeparture departure;

	@Autowired
	private CommonDeparture<HelloRequest, HelloResponse> commonDeparture;

	@Override
	public HelloResponse doSomething(HelloRequest hello) throws Exception {
		return departure.helloRedis(hello);
	}

	@Override
	public HelloResponse doSomethingWithID(HelloRequest hello, String id)
			throws Exception {
		return departure.helloRedisWithID(hello, id);
	}

	@Override
	public HelloResponse doSomethingWithIDAndFunctionName(HelloRequest hello,
			String id, String functionName) throws Exception {
		return departure.helloRedisWithIDAndFunctionName(hello, id,
				functionName);

	}

	@Override
	public HelloResponse helloGenericType(HelloRequest hello) throws Exception {
		return commonDeparture.helloGenericType(hello,HelloResponse.class);
	}

	@Override
	public HelloResponse helloGenericTypeWithID(HelloRequest hello, String id)
			throws Exception {
		return commonDeparture.helloGenericTypeWithID(hello,HelloResponse.class, id);
	}

	@Override
	public HelloResponse helloGenericTypeWithIDAndFunctionName(
			HelloRequest hello, String id, String functionName)
			throws Exception {
		return commonDeparture.helloGenericTypeWithIDAndFunctionName(hello,HelloResponse.class, id, functionName);
	}

}
