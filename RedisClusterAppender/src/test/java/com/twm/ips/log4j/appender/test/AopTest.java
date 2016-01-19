package com.twm.ips.log4j.appender.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.twm.ips.log.aop.test.Service;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/spring-aop.xml")
public class AopTest {
	@Autowired
	Service service;

	@Test
	public void testAop() {
		TestBean bean = new TestBean();
		bean.setA("@LogID");
		bean.setB("180.204.176.241");
		bean.setC("@LogField");
		bean.setD("@LogField(changeName)");
		bean.setE("@LogIgnored");
		bean.setF("@LogEncryption");
		bean.setG("No Annotation");

		InnerTestBean bean2 = new InnerTestBean();
		bean2.setA("@LogID");
		bean2.setB("180.204.176.241");
		bean2.setC("@LogField");
		bean2.setD("@LogField(changeName)");
		bean2.setE("@LogIgnored");
		bean2.setF("@LogEncryption");
		bean2.setG("No Annotation");

		innerTestBean2 bean3 = new innerTestBean2();
		bean3.setA("@LogID");
		bean3.setB("180.204.176.241");
		bean3.setC("@LogField");
		bean3.setD("@LogField(changeName)");
		bean3.setE("@LogIgnored");
		bean3.setF("@LogEncryption");
		bean3.setG("No Annotation");

		bean2.setBean(bean3);
		bean.setBean(bean2);
		service.request(bean, "123id","功能(auth)","其他");
	}

}
