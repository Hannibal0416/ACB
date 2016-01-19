package com.twm.ips.log.aop.test.impl;

import com.twm.ips.log.annotation.LogID;
import com.twm.ips.log.annotation.LogFunctionName;
import com.twm.ips.log.annotation.LogRequest;
import com.twm.ips.log.aop.test.Service;
import com.twm.ips.log4j.appender.test.TestBean;

public class ServiceImpl implements Service {

	


	@Override
	public TestBean request(@LogRequest TestBean bean,@LogID String id, @LogFunctionName String abc , String def) {
		// TODO Auto-generated method stub
		return bean;
	}}
