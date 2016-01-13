package com.twm.ips.log4j.appender.test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.twm.ips.log.aop.test.Service;

public class RedisClusterAppenderTest {

	 Logger log = Logger.getLogger("redis");
	 
	 
	 
	 @Test
	 public void test() {
		 for(int i=0 ;i < 100 ; i++){
		 TestBean bean = new TestBean();
		 bean.setA("@LogID");
		 bean.setB("@LogLocation");
		 bean.setC("@LogField");
		 bean.setD("@LogField(changeName)");
		 bean.setE("@LogIgnored");
		 bean.setF("@LogEncryption");
		 bean.setG("No Annotation");
		 
		 InnerTestBean bean2 = new InnerTestBean();
		 bean2.setA("@LogID");
		 bean2.setB("@LogLocation");
		 bean2.setC("@LogField");
		 bean2.setD("@LogField(changeName)");
		 bean2.setE("@LogIgnored");
		 bean2.setF("@LogEncryption");
		 bean2.setG("No Annotation");
		 
		 innerTestBean2 bean3 = new innerTestBean2();
		 bean3.setA("@LogID");
		 bean3.setB("@LogLocation");
		 bean3.setC("@LogField");
		 bean3.setD("@LogField(changeName)");
		 bean3.setE("@LogIgnored");
		 bean3.setF("@LogEncryption");
		 bean3.setG("No Annotation");
		 
		 bean2.setBean(bean3);
		 bean.setBean(bean2);
		 Map<String,Object> obj = new HashMap<String,Object>();
		 obj.put("request", bean);
		
			 log.debug(obj);
			 try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	 }

	@Test
	public void testHost() {
		String hosts = "twm.ips.com[7000,7001,7002,7003]";
		String hostArr[] = hosts.split("\\|");
		
		for (String host : hostArr) {
			
			Pattern pattern = Pattern.compile("(\\[)(.*?)(\\])");
			Matcher matcher = pattern.matcher(host);
			String ports = "";
//			if (!matcher.matches())
//			        throw new IllegalArgumentException("Malformed Hosts");
			while (matcher.find()) {
				ports = matcher.group(0);
			}
			host = host.substring(0, host.indexOf(ports));
			ports = ports.replace("[", "");
			ports = ports.replace("]", "");
			
			System.out.println(host);
			String portArr[] =  ports.split(",");
			for (String port : portArr) {
				System.out.println(port);
			}
			
		}
	}

}
