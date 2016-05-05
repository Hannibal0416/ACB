package com.twm.ips.web.entry;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;


@RequestMapping(consumes={"application/json"}, produces={"application/json"})
public interface HelloEntry {
	
	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloRedis")
    public @ResponseBody HelloResponse helloRedis(@RequestBody @LogRequest HelloRequest hello) throws Exception ;
	
	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloRedisWithID/{id}")
    public @ResponseBody HelloResponse helloRedisWithID(@RequestBody @LogRequest HelloRequest hello,@LogID @PathVariable("id")  String id) throws Exception;
	
	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloRedisWithIDAndFunctionName/{id}/{functionName}")
    public @ResponseBody HelloResponse helloRedisWithIDAndFunctionName(@RequestBody @LogRequest HelloRequest hello,@LogID @PathVariable("id") String id, @LogFunctionName @PathVariable("functionName") String functionName) throws Exception;

	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloCommonRequest")
    public @ResponseBody HelloResponse helloCommonRequest(@RequestBody @LogRequest HelloRequest hello) throws Exception ;
	
	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloCommonRequestWithID/{id}")
    public @ResponseBody HelloResponse helloCommonRequestWithID(@RequestBody @LogRequest HelloRequest hello,@LogID @PathVariable("id") String id) throws Exception;
	
	@RequestMapping(method=RequestMethod.POST, value = "/acb/CommonRequestWithIDAndFunctionName/{id}/{functionName}")
    public @ResponseBody HelloResponse helloCommonRequestWithIDAndFunctionName(@RequestBody @LogRequest HelloRequest hello,@LogID @PathVariable("id") String id, @LogFunctionName @PathVariable("functionName") String functionName) throws Exception;
	
}
