package com.twm.ips.log4j.appender;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

public class RedisClusterDAO {
	//config
	private String hosts;
	private String password;
	private String key;
	private int timeout;
    private long minEvictableIdleTimeMillis;
    private long timeBetweenEvictionRunsMillis;
    private int numTestsPerEvictionRun;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private boolean blockWhenExhaused;
    private String evictionPolicyClassName;
    private boolean lifo;
    private boolean testOnBorrow;
    private boolean testWhileIdle;
    private boolean testOnReturn;
    
    private JedisCluster jedisCluster = null;
    

	protected void initRedisCluster() {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		if (key == null) 
			throw new InvalidParameterException("Must set 'key'");
		if (lifo) {
			poolConfig.setLifo(lifo);
		}
		if (testOnBorrow) {
			poolConfig.setTestOnBorrow(testOnBorrow);
		}
		if (testWhileIdle) {
			poolConfig.setTestWhileIdle(testWhileIdle);
		}
		if (testOnReturn) {
			poolConfig.setTestOnReturn(testOnReturn);
		}
		if (timeBetweenEvictionRunsMillis > 0) {
			poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		}
		if (evictionPolicyClassName != null && evictionPolicyClassName.length() > 0) {
			poolConfig.setEvictionPolicyClassName(evictionPolicyClassName);
		}
		if (blockWhenExhaused) {
			poolConfig.setBlockWhenExhausted(blockWhenExhaused);
		}
		if (minIdle > 0) {
			poolConfig.setMinIdle(minIdle);
		}
		if (maxIdle > 0) {
			poolConfig.setMaxIdle(maxIdle);
		}
		if (numTestsPerEvictionRun > 0) {
			poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		}
		if (maxTotal != 8) {
			poolConfig.setMaxTotal(maxTotal);
		}
		if (minEvictableIdleTimeMillis > 0) {
			poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		}
		poolConfig.setMaxWaitMillis(timeout);
		jedisCluster = new JedisCluster(getClusterNodes(hosts),poolConfig);
	}
	
	private Set<HostAndPort> getClusterNodes(String hosts) {
		Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
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
			
			String portArr[] =  ports.split(",");
			for (String port : portArr) {
				System.out.println(host+":"+port);
				jedisClusterNode.add(new HostAndPort(host,Integer.parseInt(port)));
			}
			
		}
		return jedisClusterNode;
	}
	
	protected void push(byte[] key, byte[][] batch){
		jedisCluster.lpush(key, batch);
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

	public String getEvictionPolicyClassName() {
		return evictionPolicyClassName;
	}

	public void setEvictionPolicyClassName(String evictionPolicyClassName) {
		this.evictionPolicyClassName = evictionPolicyClassName;
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

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public void setJedisCluster(JedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
	
}
