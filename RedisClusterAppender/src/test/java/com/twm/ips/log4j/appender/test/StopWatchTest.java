package com.twm.ips.log4j.appender.test;

import org.apache.commons.lang.time.StopWatch;

public class StopWatchTest {
	public static void main(String[] args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stopWatch.stop();
		
		System.out.println(stopWatch.getStartTime());
		System.out.println(stopWatch.getTime());
		System.out.println(System.currentTimeMillis());
	}
}
