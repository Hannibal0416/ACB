package com.twm.ips.log4j.appender;

public class ReadArchivedLog implements Runnable {
	private RedisClusterService service;

	@Override
	public void run() {
		service.readArchivedEvent();
	}

	public RedisClusterService getService() {
		return service;
	}

	public void setService(RedisClusterService service) {
		this.service = service;
	}

	
}
