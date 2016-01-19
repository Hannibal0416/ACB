package com.twm.ips.web;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.web.dto.HelloRequest;
import com.twm.ips.web.dto.HelloResponse;

@Controller
@RequestMapping(consumes={"application/json"}, produces={"application/json"})
public class RedisController {
	private final AtomicLong counter = new AtomicLong();
	@RequestMapping(method=RequestMethod.POST, value = "/acb/helloRedis")
    public @ResponseBody HelloResponse helloRedis(@RequestBody @LogRequest HelloRequest hello) {
		System.out.println(hello);
        return new HelloResponse(counter.incrementAndGet(),
        		hello.getContent());
    }
}
