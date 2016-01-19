package com.twm.ips.log4j.appender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.beans.BeanUtils;

import redis.clients.util.SafeEncoder;

public class RedisClusterService {
	private Logger log = Logger.getLogger(PushEvent.class);
	private RedisClusterDAO dao;
	private Queue<LoggingEvent> events;
	private Layout layout;
	private String key;
	private String archiveOnFailure;
	private boolean purgeOnFailure;
	
	protected void readArchivedEvent() {
		File archiveFile = new File(archiveOnFailure);
		if(archiveFile.exists()) {
			try {
				List<String> messageList = FileUtils.readLines(archiveFile);
				byte[][] batch = new byte[messageList.size()][];
				int messageIndex = 0;
				for(String message : messageList) {
					if(StringUtils.isNotEmpty(message))
						batch[messageIndex++] = SafeEncoder.encode(message);
				}
				if(messageIndex > 0) {
					dao.push(SafeEncoder.encode(key),batch);
				}
				archiveFile.delete();
			} catch (IOException e) {
				log.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}

	protected void pushEvent() {
		List<String> eventErrorBuffer = null;
		boolean exceptionFlag = false;
		try {
			
			eventErrorBuffer = new ArrayList<String>();
			int messageIndex = events.size();
			if (messageIndex > 0) {

				byte[][] batch = new byte[messageIndex][];
				for (int i = 0; i < messageIndex; i++) {
					LoggingEvent event = null;
					try {
						event = events.poll();
						String message = layout.format(event);
						log.debug(message);
						if (StringUtils.isEmpty(message))
							continue;
						eventErrorBuffer.add(message);
						batch[i] = SafeEncoder.encode(message);
					} catch (Exception e) {
						log.error(ExceptionUtils.getStackTrace(e));
					}
				}
				dao.push(SafeEncoder.encode(key), batch);
			}

		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));
			exceptionFlag = true;
		} finally {
			if(exceptionFlag) {
				if (StringUtils.isNotEmpty(archiveOnFailure)) {
					LogLog.debug("Archiving Redis event queue");
					if (eventErrorBuffer != null && eventErrorBuffer.size() > 0) {
						archiveEventToFile(eventErrorBuffer);
					}
				}
				if (purgeOnFailure) {
					LogLog.debug("Purging Redis event queue");
					events.clear();
				}
			}
		}
	}
	
	private  void archiveEventToFile(List<String> events) {
		try {
			FileUtils.writeLines(new File(archiveOnFailure),events,true);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}



	public RedisClusterDAO getDao() {
		return dao;
	}

	public void setDao(RedisClusterDAO dao) {
		this.dao = dao;
	}

	public Queue<LoggingEvent> getEvents() {
		return events;
	}

	public void setEvents(Queue<LoggingEvent> events) {
		this.events = events;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getArchiveOnFailure() {
		return archiveOnFailure;
	}

	public void setArchiveOnFailure(String archiveOnFailure) {
		this.archiveOnFailure = archiveOnFailure;
	}

	public boolean isPurgeOnFailure() {
		return purgeOnFailure;
	}

	public void setPurgeOnFailure(boolean purgeOnFailure) {
		this.purgeOnFailure = purgeOnFailure;
	}
	

}
