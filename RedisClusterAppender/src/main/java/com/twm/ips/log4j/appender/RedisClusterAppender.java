package com.twm.ips.log4j.appender;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.SafeEncoder;

/**
 * 
 * @author hannibal
 *
 */
public class RedisClusterAppender extends AppenderSkeleton implements Runnable{

	private String hosts = "localhost(7000)";
	private String password;
	private String key;
	private int timeout = 2000;
	private int batchSize = 20;
	private long period = 30000;
	private boolean batchPush = true;
	private boolean purgeOnFailure = true;
	private String archiveOnFailure = "";
	private boolean daemonThread = true;
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
    private int connectionPoolRetryCount = 2;
    private JedisCluster jedisCluster = null;
	private Queue<LoggingEvent> events;

	private ScheduledExecutorService executor;
	private ScheduledFuture<?> task;
	private RedisClusterAppenderHelper helper;
	
	private boolean pushLock = false;
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void activateOptions() {
		try {
			super.activateOptions();

			if (key == null) 
				throw new IllegalStateException("Must set 'key'");
			if (batchPush) {
				if (executor == null) executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(this.getClass().getName(), daemonThread));
					task = executor.scheduleWithFixedDelay(this, period, period, TimeUnit.MILLISECONDS);
				if (task != null && !task.isDone()) 
					task.cancel(true);
			}

            events = new ConcurrentLinkedQueue<LoggingEvent>();
			helper = new RedisClusterAppenderHelper();
			jedisCluster = helper.getRedisClusterConfig(this);
			Runtime.getRuntime().addShutdownHook(new Thread(this));
			//Read archived log file on startup.
			if(StringUtils.isNotEmpty(archiveOnFailure)) {
				RedisClusterAppenderHelper helper = new RedisClusterAppenderHelper();
				helper.readArchiveLog(archiveOnFailure, jedisCluster, key);
			}
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
	public void run() {
		push();
//		System.out.println(this.ge);
	}
	
	
	
	@Override
	protected void append(LoggingEvent event) {
		try {
			helper.populateEvent(event);
			events.add(event);
			if(events.size() >= batchSize) 
				executor.execute(this);
//			layout.format(event);
		} catch (Exception e) {
			System.out.println(e);
			errorHandler.error("Error populating event and adding Redis to queue", e, ErrorCode.GENERIC_FAILURE, event);
		}
	}
	
	private void push2(){
		if(StringUtils.isNotEmpty(archiveOnFailure)) {
			LogLog.debug("Archiving Redis event queue");
			RedisClusterAppenderHelper helper = new RedisClusterAppenderHelper();
			helper.archiveLog(archiveOnFailure, events, layout);
		}
	}
	
	private void push() {
		try {
			if(!pushLock) {
				pushLock = true;
				LoggingEvent event;
				byte[][] batch = new byte[events.size()][];
				int messageIndex = 0;
				while ((event = events.poll()) != null) {
					try {
						String message = layout.format(event);
						if(StringUtils.isEmpty(message))
							continue;
						batch[messageIndex++] = SafeEncoder.encode(message);
					} catch (Exception e) {
						errorHandler.error(e.getMessage(), e, ErrorCode.GENERIC_FAILURE, event);
					}
				}
				if(messageIndex > 0) {
					jedisCluster.lpush(SafeEncoder.encode(key),batch);
							
//			                : Arrays.copyOf(batch, messageIndex));
				}
				
				 messageIndex = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.error(e.getMessage(), e, ErrorCode.WRITE_FAILURE);
			if(StringUtils.isNotEmpty(archiveOnFailure)) {
				LogLog.debug("Archiving Redis event queue");
				RedisClusterAppenderHelper helper = new RedisClusterAppenderHelper();
				helper.archiveLog(archiveOnFailure, events, layout);
			}
			if (purgeOnFailure) {
                LogLog.debug("Purging Redis event queue");
                events.clear();
            }
		} finally {
			pushLock = false;
		}
		
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

	public int getConnectionPoolRetryCount() {
		return connectionPoolRetryCount;
	}

	public void setConnectionPoolRetryCount(int connectionPoolRetryCount) {
		this.connectionPoolRetryCount = connectionPoolRetryCount;
	}

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
	

}
