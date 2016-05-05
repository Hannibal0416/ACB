package com.twm.ips.web.service;

import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;

public interface MyService {

	/**
	 * 
	 * @param hello
	 * @return
	 * @throws Exception
	 */
	public HelloResponse doSomething(HelloRequest hello) throws Exception;

	/**
	 * 
	 * @param hello
	 * @param id
	 */
	public HelloResponse doSomethingWithID(HelloRequest hello, String id)
			throws Exception;

	/**
	 * 
	 * @param hello
	 * @param id
	 * @param functionName
	 */
	public HelloResponse doSomethingWithIDAndFunctionName(HelloRequest hello,
			String id, String functionName) throws Exception;
	
	public HelloResponse helloGenericType(HelloRequest hello) throws Exception;

	public HelloResponse helloGenericTypeWithID(HelloRequest hello, String id) throws Exception;

	public HelloResponse helloGenericTypeWithIDAndFunctionName(HelloRequest hello, String id,String functionName) throws Exception;

}
