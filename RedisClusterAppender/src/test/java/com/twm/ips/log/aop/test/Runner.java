package com.twm.ips.log.aop.test;

public class Runner implements Runnable {

	private int i;
	private String name;
	@Override
	public void run() {
		int i = StaticInt.i++;
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println( i  +   Thread.currentThread().getName() + Thread.currentThread().getId() + this.getName());
	}

	public int getI() {
		return i++;
	}

	public void setI(int i) {
		this.i = i;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
