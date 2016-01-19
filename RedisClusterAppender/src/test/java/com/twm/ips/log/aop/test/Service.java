package com.twm.ips.log.aop.test;

import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.log4j.appender.test.TestBean;


public interface Service {
	
	public TestBean request(@LogRequest TestBean bean,@LogID String id, @LogFunctionName String abc , String def); 
	

}
