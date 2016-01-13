package com.twm.ips.log4j.appender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

public class RedisClusterAppenderHelper {

	protected JedisCluster getRedisClusterConfig(RedisClusterAppender appender) {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		if (appender.isLifo()) {
			poolConfig.setLifo(appender.isLifo());
		}
		if (appender.isTestOnBorrow()) {
			poolConfig.setTestOnBorrow(appender.isTestOnBorrow());
		}
		if (appender.isTestWhileIdle()) {
			poolConfig.setTestWhileIdle(appender.isTestWhileIdle());
		}
		if (appender.isTestOnReturn()) {
			poolConfig.setTestOnReturn(appender.isTestOnReturn());
		}
		if (appender.getTimeBetweenEvictionRunsMillis() > 0) {
			poolConfig.setTimeBetweenEvictionRunsMillis(appender.getTimeBetweenEvictionRunsMillis());
		}
		if (appender.getEvictionPolicyClassName() != null && appender.getEvictionPolicyClassName().length() > 0) {
			poolConfig.setEvictionPolicyClassName(appender.getEvictionPolicyClassName());
		}
		if (appender.isBlockWhenExhaused()) {
			poolConfig.setBlockWhenExhausted(appender.isBlockWhenExhaused());
		}
		if (appender.getMinIdle() > 0) {
			poolConfig.setMinIdle(appender.getMinIdle());
		}
		if (appender.getMaxIdle() > 0) {
			poolConfig.setMaxIdle(appender.getMaxIdle());
		}
		if (appender.getNumTestsPerEvictionRun() > 0) {
			poolConfig.setNumTestsPerEvictionRun(appender.getNumTestsPerEvictionRun());
		}
		if (appender.getMaxTotal() != 8) {
			poolConfig.setMaxTotal(appender.getMaxTotal());
		}
		if (appender.getMinEvictableIdleTimeMillis() > 0) {
			poolConfig.setMinEvictableIdleTimeMillis(appender.getMinEvictableIdleTimeMillis());
		}
		poolConfig.setMaxWaitMillis(appender.getTimeout());
		
		return new JedisCluster(getClusterNodes(appender.getHosts()),poolConfig);
	}
	
	protected void populateEvent(LoggingEvent event) {
		event.getThreadName();
		event.getRenderedMessage();
		event.getNDC();
		event.getMDCCopy();
		event.getThrowableStrRep();
		event.getLocationInformation();
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

	public void archiveLog(String archivePath, Queue<LoggingEvent> events,Layout layout) {
		try {
			LoggingEvent event;
			List<String> messageList = new ArrayList<String>();
			while ((event = events.poll()) != null) {
				String message = layout.format(event);
				messageList.add(message);
				if (StringUtils.isEmpty(message))
					continue;
			}
			if(messageList.size() > 0)
				FileUtils.writeLines(new File(archivePath),messageList,true);

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void readArchiveLog(String archivePath,JedisCluster jedisCluster,String key) {
		File archiveFile = new File(archivePath);
		if(archiveFile.exists()) {
			try {
				List<String> messageList = FileUtils.readLines(archiveFile);
				byte[][] batch = new byte[messageList.size()][];
				int messageIndex = 0;
				for(String message : messageList) {
					batch[messageIndex++] = SafeEncoder.encode(message);
				}
				if(messageIndex > 0) {
					jedisCluster.lpush(SafeEncoder.encode(key),batch);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			archiveFile.delete();
		}
	}
}
