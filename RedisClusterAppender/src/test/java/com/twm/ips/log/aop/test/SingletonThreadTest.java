package com.twm.ips.log.aop.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.twm.ips.log4j.appender.NamedThreadFactory;

public class SingletonThreadTest {
	
	private ScheduledFuture<?> pushEventTask;
	private ScheduledExecutorService pushEventExecutor;
	
	@Test
	public void test(){
		
		Runner r1 = new Runner();
		r1.setName("r1");
		Runner r2 = new Runner();
		r2.setName("r2");
		pushEventExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.getClass().getName(), true));
		pushEventTask = pushEventExecutor.scheduleAtFixedRate(r1, 1, 1, TimeUnit.MILLISECONDS);
		while(true){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pushEventExecutor.execute(r2);
			
		}
		
		
		
		
	}
	
	@Test
	public void test2(){
		Runner r1 = new Runner();
		r1.setName("r1");
		Runner r2 = new Runner();
		r2.setName("r2");
		while(true){
			Thread t1 = new Thread(r1);
			t1.start();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Thread t2 = new Thread(r2);
			t2.start();
			
		}
	}

}
;