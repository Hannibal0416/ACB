package com.twm.ips.log4j.appender;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.beans.BeanUtils;

import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

/**
 * 
 * @author hannibal
 *
 */
public class RedisClusterAppender extends AppenderSkeleton {

	private String hosts = "localhost(7000)";
	private String password;
	private String key;
	private int timeout = 2000;
    private long minEvictableIdleTimeMillis = 60000L;
    private long timeBetweenEvictionRunsMillis = 30000L;
    private int numTestsPerEvictionRun = -1;
    private int maxTotal = 8;
    private int maxIdle = 0;
    private int minIdle = 0;
    private boolean blockWhenExhaused = false;
    private String evictionPolicyClassName = "";
    private boolean lifo = false;
    private boolean testOnBorrow = false;
    private boolean testWhileIdle = false;
    private boolean testOnReturn = false;
//    private int connectionPoolRetryCount = 2;
    
    //Task
    private ScheduledExecutorService pushEventExecutor;
    private ScheduledFuture<?> pushEventTask;
	private PushEvent pushEvent;
	private ReadArchivedLog readArchivedLog;
    private ScheduledExecutorService archiveExecutor;
    private ScheduledFuture<?> archiveTask;
    private boolean batchPush = true;
    private boolean purgeOnFailure = true;
	private boolean daemonThread = true;
	private long period = 30000;
	private long archivePeriod = 600000;
	private int batchSize = 20;
	private String archiveOnFailure = "";

	private Queue<LoggingEvent> events;
	private RedisClusterDAO dao;
	private RedisClusterService service;
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void activateOptions() {
		try {
			super.activateOptions();
			events = new ConcurrentLinkedQueue<LoggingEvent>();
			service = new RedisClusterService();
			dao = new RedisClusterDAO();
			pushEvent = new PushEvent();
			readArchivedLog = new ReadArchivedLog();
			BeanUtils.copyProperties(this, dao);
			BeanUtils.copyProperties(this, service);
			dao.initRedisCluster();
			pushEvent.setService(service);
			readArchivedLog.setService(service);
			
			if (batchPush) {
				if (pushEventTask != null && !pushEventTask.isDone()) 
					pushEventTask.cancel(true);
				if (pushEventExecutor == null) 
					pushEventExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(pushEvent.getClass().getName(), daemonThread));
				pushEventTask = pushEventExecutor.scheduleWithFixedDelay(pushEvent, period, period, TimeUnit.MILLISECONDS);
			}
			if(StringUtils.isNotEmpty(archiveOnFailure)) {
				if (archiveTask != null && !archiveTask.isDone()) 
					archiveTask.cancel(true);
				if (archiveExecutor == null) 
					archiveExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(readArchivedLog.getClass().getName(), daemonThread));
				archiveTask = archiveExecutor.scheduleWithFixedDelay(readArchivedLog, archivePeriod, archivePeriod, TimeUnit.MILLISECONDS);
			}
			Runtime.getRuntime().addShutdownHook(new Thread(pushEvent));
		} catch (Exception e) {
			LogLog.error(this.getClass().getName() + ": Error during activateOptions", e);
		}
	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	
	@Override
	protected void append(LoggingEvent event) {
		try {
			populateEvent(event);
			events.add(event);
			if(events.size() >= batchSize) 
				pushEventExecutor.execute(pushEvent);
		} catch (Exception e) {
			errorHandler.error("Error populating event and adding Redis to queue", e, ErrorCode.GENERIC_FAILURE, event);
		}
	}
	
	private void populateEvent(LoggingEvent event) {
		event.getThreadName();
		event.getRenderedMessage();
		event.getNDC();
		event.getMDCCopy();
		event.getThrowableStrRep();
		event.getLocationInformation();
	}
	

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	public boolean isBatchPush() {
		return batchPush;
	}

	public void setBatchPush(boolean batchPush) {
		this.batchPush = batchPush;
	}

	public boolean isPurgeOnFailure() {
		return purgeOnFailure;
	}

	public void setPurgeOnFailure(boolean purgeOnFailure) {
		this.purgeOnFailure = purgeOnFailure;
	}

	public boolean isDaemonThread() {
		return daemonThread;
	}

	public void setDaemonThread(boolean daemonThread) {
		this.daemonThread = daemonThread;
	}

	public long getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public long getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public int getNumTestsPerEvictionRun() {
		return numTestsPerEvictionRun;
	}

	public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
		this.numTestsPerEvictionRun = numTestsPerEvictionRun;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public boolean isBlockWhenExhaused() {
		return blockWhenExhaused;
	}

	public void setBlockWhenExhaused(boolean blockWhenExhaused) {
		this.blockWhenExhaused = blockWhenExhaused;
	}

	public boolean isLifo() {
		return lifo;
	}

	public void setLifo(boolean lifo) {
		this.lifo = lifo;
	}

	public boolean isTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public boolean isTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public boolean isTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

//	public int getConnectionPoolRetryCount() {
//		return connectionPoolRetryCount;
//	}
//
//	public void setConnectionPoolRetryCount(int connectionPoolRetryCount) {
//		this.connectionPoolRetryCount = connectionPoolRetryCount;
//	}

	public String getEvictionPolicyClassName() {
		return evictionPolicyClassName;
	}

	public void setEvictionPolicyClassName(String evictionPolicyClassName) {
		this.evictionPolicyClassName = evictionPolicyClassName;
	}

	public String getArchiveOnFailure() {
		return archiveOnFailure;
	}

	public void setArchiveOnFailure(String archiveOnFailure) {
		this.archiveOnFailure = archiveOnFailure;
	}

	public Queue<LoggingEvent> getEvents() {
		return events;
	}

	public void setEvents(Queue<LoggingEvent> events) {
		this.events = events;
	}
	
	public Layout getLayout() {
		return this.layout;
	}

	public long getArchivePeriod() {
		return archivePeriod;
	}

	public void setArchivePeriod(long archivePeriod) {
		this.archivePeriod = archivePeriod;
	}

	public RedisClusterDAO getDao() {
		return dao;
	}

	public void setDao(RedisClusterDAO dao) {
		this.dao = dao;
	}

	
}
